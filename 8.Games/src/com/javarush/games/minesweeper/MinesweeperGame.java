package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;


    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        } else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y,"");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void countMineNeighbors(){
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (!gameField[i][j].isMine) {
                    GameObject object = gameField[i][j];
                    for (GameObject mine : getNeighbors(object)) {
                        if (mine.isMine) {
                            object.countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    private void openTile(int x, int y) {
        GameObject gameObject = gameField[y][x];
        if (gameObject.isOpen || gameObject.isFlag || isGameStopped) {
            return;
        }
        gameObject.isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.GREEN);
        if (gameObject.isMine) {
            setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);
            gameOver();
            return;
        } else if (gameObject.countMineNeighbors == 0) {
            setCellValue(gameObject.x, gameObject.y, "");
            List<GameObject> neighbors = getNeighbors(gameObject);
            for (GameObject neighbor : neighbors) {
                if (!neighbor.isOpen) {
                    openTile(neighbor.x, neighbor.y);
                }
            }
        } else {
            setCellNumber(x, y, gameObject.countMineNeighbors);
        }

        if (countClosedTiles == countMinesOnField) {
            win();
        }
        score +=5;
        setScore(score);
    }
    private void markTile(int x, int y) {
        GameObject gameObject = gameField[y][x];

        if (gameObject.isOpen || isGameStopped || (countFlags == 0 && !gameObject.isFlag)) {
            return;
        }

        if (gameObject.isFlag) {
            countFlags++;
            gameObject.isFlag = false;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORANGE);
        } else {
            countFlags--;
            gameObject.isFlag = true;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        }
    }
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Game Over", Color.GREEN, 70);
    }
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "You Win!", Color.ORANGE, 80);
    }
    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }
}