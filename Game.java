import java.util.ArrayList;
import java.util.Random;
public class Game {
    private int[][] gameBoard;
    private int score = 0;

    public Game() {
        gameBoard = new int[4][4];
    }

    public void addNewNumbers(){
        ArrayList<int[]> emptySpaces = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                if(gameBoard[i][j] == 0){
                    emptySpaces.add(new int[]{i,j});
                }
            }
        }

        if(!emptySpaces.isEmpty()){
            Random r = new Random();

            int[] cell = emptySpaces.get(r.nextInt(emptySpaces.size()));
            int row = cell [0];
            int col = cell[1];

            gameBoard[row][col] = r.nextInt(10) < 9 ? 2 : 4;
        }
    }

    public int[] mergeLine(int[] line){
        ArrayList<Integer> result = new ArrayList<>();
        for(int val : line){
            if (val != 0) result.add(val);
        }

        for(int i = 0; i < result.size() - 1; i++){
            if (result.get(i).equals(result.get(i+1))){
                int mergedValue = result.get(i) * 2;
                result.set(i, mergedValue);
                score += mergedValue;
                result.remove(i+1);
                result.add(0);
            }
        }

        while(result.size() < 4){
            result.add(0);
        }

        int[] array = new int[4];
        for(int i = 0; i < result.size(); i++){
            array[i] = result.get(i);
        }
        return array;
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
            for (int i = 0; i < 4; i++) column[i] = gameBoard[3 - i][j];
            int[] merged = mergeLine(column);
            for (int i = 0; i < 4; i++) gameBoard[3 - i][j] = merged[i];
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
