package com.spacegame;

public interface GameObserver {
    void onScoreChanged(int newScore);
    void onGameOver();
    void onCollision();
}