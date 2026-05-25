package com.spacegame.model;

import java.awt.*;

public class Projectile extends Entity {
    private double speed;
    private Color color;
    private boolean fromPlayer;
    
    public Projectile(double x, double y, boolean fromPlayer) {
        super(x, y, 20, 4);
        this.fromPlayer = fromPlayer;
        this.speed = fromPlayer ? 12 : -8;
        this.color = fromPlayer ? Color.GREEN : Color.RED;
    }
    
    @Override
    public void update() {
        x += speed;
        if (x < -50 || x > 850) active = false;
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, width, height);
        g.setColor(Color.WHITE);
        g.fillRect((int)x + (fromPlayer ? 5 : 5), (int)y + 1, width - 10, height - 2);
    }
    
    public boolean isFromPlayer() { return fromPlayer; }
}