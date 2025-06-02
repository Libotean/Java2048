import java.util.ArrayList;
import java.util.Random;
public class Game {
    private int[][] gameBoard;
    private int score = 0;

    public Game() {
        gameBoard = new int[4][4];
    }

    public void addNewNumbers(){
        ArrayList<Pos> emptySpaces = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                if(gameBoard[i][j] == 0){
                    emptySpaces.add(new Pos(i,j));
                }
            }
        }

        if(!emptySpaces.isEmpty()){
            Random r = new Random();

            Pos cell = emptySpaces.get(r.nextInt(emptySpaces.size()));
            int row = cell.x;
            int col = cell.y;

            gameBoard[row][col] = r.nextInt(10) < 9 ? 2 : 4;
        }
    }

    public int[] mergeLine(int[] line) {
        int[] newLine = new int[4];
        int index = 0;

        for (int i = 0; i < 4; i++) {
            if (line[i] != 0) {
                newLine[index] = line[i];
                index++;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (newLine[i] != 0 && newLine[i] == newLine[i + 1]) {
                newLine[i] *= 2;
                score += newLine[i];
                newLine[i + 1] = 0;
            }
        }

        int[] finalLine = new int[4];
        index = 0;
        for (int i = 0; i < 4; i++) {
            if (newLine[i] != 0) {
                finalLine[index] = newLine[i];
                index++;
            }
        }

        return finalLine;
    }


    private int[] reverse(int[] arr){
        int[] reversed = new int[arr.length];
        for(int i = 0; i < arr.length; i++){
            reversed[i] = arr[arr.length - i - 1];
        }
        return reversed;
    }

    public void pushUp(){
        //System.out.println("Push up");
        for(int j = 0; j < 4; j++){
            int[] column = new int[4];
            for (int i = 0; i < 4; i++) column[i] = gameBoard[i][j];
            int[] merged = mergeLine(column);
            for (int i = 0; i < 4; i++) gameBoard[i][j] = merged[i];

        }
    }

    public void pushDown() {
        //System.out.println("Push down");
        for (int j = 0; j < 4; j++) {
            int[] column = new int[4];
            for (int i = 0; i < 4; i++) column[i] = gameBoard[i][j];
            column = reverse(column);
            int[] merged = mergeLine(column);
            merged = reverse(merged);
            for (int i = 0; i < 4; i++) gameBoard[i][j] = merged[i];

        }
    }

    public void pushLeft(){
        //System.out.println("Push left");
        for(int i = 0; i < 4; i++){
            int[] row = gameBoard[i];
            int[] merged = mergeLine(row);
            gameBoard[i] = merged;
        }
    }

    public void pushRight(){
        //System.out.println("Push right");
        for(int i = 0; i < 4; i++){
            int[] row = reverse(gameBoard[i]);
            int[] merged = mergeLine(row);
            gameBoard[i] = reverse(merged);
        }
    }

    public int[][] getGameBoard() {
        return gameBoard;
    }

    public int getScore() {
        return score;
    }

    public boolean has2048(){
        for(int[] row: gameBoard){
            for(int val : row){
                if(val == 2048) return true;
            }
        }
        return false;
    }

    public boolean isGameOver(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(gameBoard[i][j] == 0) return false; // empty space
                if(i < 3 && gameBoard[i][j] == gameBoard[i+1][j]) return false; // vertical merge
                if(j < 3 && gameBoard[i][j] == gameBoard[i][j+1]) return false; // horizontal merge
            }
        }
        return true; // no moves left
    }
}

class Pos{
    int x, y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
