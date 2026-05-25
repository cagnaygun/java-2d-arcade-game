package com.spacegame;

public class ScoreManager {
    private static ScoreManager instance;
    private int score;
    private int highScore;
    
    private ScoreManager() {}
    
    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }
    
    public void addScore(int points) {
        score += points;
        GameManager.getInstance().notifyScoreChanged(score);
    }
    
    public void reset() {
        if (score > highScore) {
            highScore = score;
        }
        score = 0;
    }
    
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
}