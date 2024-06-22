package com.example.btl.Gameplay;


public class Game {

    private int[][] gameBoard;

    private int player;

    private static Game game;

    private final int colNum = 15;


    private final int rowNum = 17;

    private Game(){
        this.gameBoard=new int[rowNum][colNum];
        this.player=1;
    }


    public static synchronized Game getInstance(){
        if(game==null){
            game=new Game();
        }
        return game;
    }

    public synchronized void destroyInstance(){
        this.game=null;
    }

    public boolean move(int x){

        int[] coord=findCoordinate(x);
        int row=coord[0];
        int col=coord[1];

        if(this.gameBoard[row][col]==0){
            this.gameBoard[row][col]=this.player;

            return true;
        }

        return false;
    }

    public boolean checkWinner(int index){

        int[] coord= findCoordinate(index);
        int row=coord[0];
        int col=coord[1];

        if(
                checkRow(row,col) ||
                checkColumn(row,col)||
                checkDiagonal(row,col)||
                checkReverseDiagonal(row,col)
        ){
            return true;
        }


        return false;
    }

    //check row
    public boolean checkRow(int row,int col){

        int strike=1;

        //check right side
        for (int i = 1; i < 5; i++) {
            if ((col + i)< colNum){
                if (this.gameBoard[row][col + i] == this.gameBoard[row][col]){
                    strike++;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }

        int leftLoop= 5 - strike;
        //check left side
        for (int i=1;i<=leftLoop;i++){
            if (col - i >=0) {
                if (this.gameBoard[row][col - i] == this.gameBoard[row][col]) {
                    strike++;
                }
                else break;
            }
            else {
                break;
            }
        }

        return strike == 5;
    }

    public boolean checkColumn(int row,int col){

        int strike=1;

        for(int i=1;i<5;i++){
            if((row+i)<rowNum){
                if (this.gameBoard[row+i][col]==this.gameBoard[row][col]){
                    strike++;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }

        int loopLeft = 5-strike;

        for (int i = 1; i <= loopLeft; i++) {
            if((row - i)>=0){
                if(this.gameBoard[row-i][col]==this.gameBoard[row][col]){
                    strike++;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }

        return strike==5;
    }

    //check diagonal line /
    public boolean checkReverseDiagonal(int row,int col){
        int strike = 1;

        for(int i=1; i < 5;i++){
            if((row + i) < rowNum&& (col - i) >= 0){
                if(this.gameBoard[row+i][col-i]==this.gameBoard[row][col]){
                    strike++;
                }
                else break;
            }
            else break;
        }

        int loopLeft = 5-strike;

        for(int i=1; i <= loopLeft;i++){
            if((row - i) >= 0&& (col + i) < rowNum){
                if(this.gameBoard[row - i][col + i]==this.gameBoard[row][col]){
                    strike++;
                }
                else break;
            }
            else break;
        }

        return strike==5;
    }

    //check diagonal \
    public boolean checkDiagonal(int row,int col){
        int strike = 1;

        for(int i=1; i < 5;i++){
            if((row + i) < rowNum && (col + i) < colNum){
                if(this.gameBoard[row+i][col+i]==this.gameBoard[row][col]){
                    strike++;
                }
                else break;
            }
            else break;
        }

        int loopLeft = 5-strike;

        for(int i=1; i <= loopLeft;i++){
            if((row - i) >= 0&& (col + i) >= 0){
                if(this.gameBoard[row - i][col - i]==this.gameBoard[row][col]){
                    strike++;
                }
                else break;
            }
            else break;
        }

        return strike==5;
    }
    public int[] findCoordinate(int index){
        int row = index / 15;
        int col = index % 15;
        return new int[]{row, col};

    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int[][] getGameBoard() {
        return gameBoard;
    }

}
