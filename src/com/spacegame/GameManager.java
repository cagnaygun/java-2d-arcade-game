package com.spacegame;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;
    private boolean running;
    private boolean gameOver;
    private int gameSpeed;
    private List<GameObserver> observers;
    
    private GameManager() {
        observers = new ArrayList<>();
        gameSpeed = 2;
    }
    
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
    
    public void addObserver(GameObserver o) {
        observers.add(o);
    }
    
    public void startGame() {
        running = true;
        gameOver = false;
    }
    
    public void notifyGameOver() {
        running = false;
        gameOver = true;
        for (GameObserver o : observers) {
            o.onGameOver();
        }
    }
    
    public void notifyScoreChanged(int score) {
        for (GameObserver o : observers) {
            o.onScoreChanged(score);
        }
    }
    
    public void notifyCollision() {
        for (GameObserver o : observers) {
            o.onCollision();
        }
    }
    
    public boolean isRunning() { return running; }
    public boolean isGameOver() { return gameOver; }
    public int getGameSpeed() { return gameSpeed; }
}