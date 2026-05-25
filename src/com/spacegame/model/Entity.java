package com.spacegame.model;

import java.awt.*;

public abstract class Entity {
    protected double x, y;
    protected int width, height;
    protected boolean active;
    
    public Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = true;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public abstract void update();
    public abstract void draw(Graphics g);
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public boolean collidesWith(Entity other) {
        return active && other.active && getBounds().intersects(other.getBounds());
    }
    
    public void destroy() { active = false; }
    public boolean isActive() { return active; }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

