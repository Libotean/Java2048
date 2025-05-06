import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GUI extends JFrame implements KeyListener{
    private Game game;

    private final Color BG_COLOR = new Color(0x121212); // Background
    private final Color BOARD_COLOR = new Color(0x1E1E1E); // Board bg
    private final Color FONT_COLOR = new Color(0xF5F5F5); // Text color
    private final Color RED_HUE = new Color(0x9C3A3A);  // A deep, muted red

    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JLabel nameLabel;
    private int score = 0;
    private String highScoreFile = "highscore.txt";
    private int highScore = loadHighScore();
    private boolean hasWon = false;

    public GUI(){
        game = new Game();
        setFont(new Font("Roboto", Font.BOLD, 20));
        setTitle("2048");
        setSize(450, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel scorePanel = createScorePanel();
        JPanel gamePanel = createGamePanel();

        add(scorePanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();
        setVisible(true);

        game.addNewNumbers();
        game.addNewNumbers();
    }

    private void drawBoard(Graphics g){
        int tileSize = 100;
        int margin = 10;
        int boardSize = tileSize * 4 - margin;

        int startX = (getWidth() - boardSize) / 2 - 8;
        int startY = ((getHeight() - boardSize) / 2) - 35;

        int[][] board = game.getGameBoard();

        g.setColor(BOARD_COLOR);
        g.fillRoundRect(startX - margin/2, startY - margin/2, boardSize + margin, boardSize + margin, 15, 15);

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                int val = board[i][j];
                int x = startX + j * tileSize;
                int y = startY + i * tileSize;

                g.setColor(getTileColor(val));
                g.fillRoundRect(x, y, tileSize-margin, tileSize-margin, 15, 15);

                if(val != 0){
                    g.setColor(FONT_COLOR);
                    g.setFont(new Font("Roboto", Font.BOLD, 28));
                    String text = String.valueOf(val);
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getHeight();

                    g.drawString(text, x + (tileSize - margin - textWidth) / 2, y + (tileSize - margin + textHeight) / 2 - 5);
                }
            }
        }
    }

    private JPanel createGamePanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
                drawBoard(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(getWidth(), getHeight());
            }
        };
    }


    private JPanel createScorePanel(){
        JPanel panel = new JPanel();
        panel.setBackground(RED_HUE);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 10));

        nameLabel = new JLabel("2048");
        scoreLabel = new JLabel("Score: " + score);
        highScoreLabel = new JLabel("High Score: " + highScore);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        nameLabel.setFont(labelFont);
        scoreLabel.setFont(labelFont);
        highScoreLabel.setFont(labelFont);
        nameLabel.setForeground(FONT_COLOR);
        scoreLabel.setForeground(FONT_COLOR);
        highScoreLabel.setForeground(FONT_COLOR);


        panel.add(nameLabel);
        panel.add(scoreLabel);
        panel.add(highScoreLabel);

        return panel;
    }

    private Color getTileColor(int val){
        switch(val){
            case 2: return new Color(0x4B2E2E);
            case 4: return new Color(0x703B3B);
            case 8: return new Color(0xA74C3C);
            case 16: return new Color(0xBF5E36);
            case 32: return new Color(0xD9773A);
            case 64: return new Color(0xEC9C3A);
            case 128: return new Color(0xF4B942);
            case 256: return new Color(0xF2D14A);
            case 512: return new Color(0xF4E285);
            case 1024: return new Color(0xFCE38A);
            case 2048: return new Color(0xFFF5CC);
            default: return new Color(0x2A2A2A);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        boolean moved = false;
        switch(e.getKeyCode()){
            case KeyEvent.VK_UP:
                game.pushUp();
                moved = true;
                break;
            case KeyEvent.VK_DOWN:
                game.pushDown();
                moved = true;
                break;
            case KeyEvent.VK_LEFT:
                game.pushLeft();
                moved = true;
                break;
            case KeyEvent.VK_RIGHT:
                game.pushRight();
                moved = true;
                break;
        }
        if(moved){
            game.addNewNumbers();
            repaint();
        }

        score = game.getScore();
        scoreLabel.setText("Score: " + score);
        if(score > highScore){
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
            saveHighScore(highScore);
        }

        if(!hasWon && game.has2048()){
            hasWon = true;
            int choice = JOptionPane.showConfirmDialog(this, "You won! Play again?", "2048", JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION){
                resetGame();
            }else {
                System.exit(0);
            }
        }

        if(game.isGameOver()){
            int choice = JOptionPane.showConfirmDialog(this, "Game Over! Try again?", "2048", JOptionPane.YES_NO_OPTION);
            if(choice == JOptionPane.YES_OPTION){
                resetGame();
            }else {
                System.exit(0);
            }
        }
    }

    public void resetGame(){
        hasWon = false;
        game = new Game();
        if(score > highScore){
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
        }
        score = 0;
        scoreLabel.setText("Score: " + score);
        game.addNewNumbers();
        game.addNewNumbers();
        repaint();
    }

    private int loadHighScore(){
        File file = new File(highScoreFile);
        if(!file.exists()){
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(highScoreFile))){
            return Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            return 0;
        }
    }

    private void saveHighScore(int score){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile))){
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
