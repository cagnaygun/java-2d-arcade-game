package com.spacegame.model;

import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
public class Pickup extends Entity {
    public enum Type { STAR, HEALTH, AMMO }
    
    private Type type;
    private int bobOffset;
    private int value;
    private BufferedImage image;
    
    public Pickup(int x, int y, Type type) {
        super(x, y, 55, 55);
        this.type = type;
        this.value = switch(type) {
        
            case STAR -> 25;
            case HEALTH -> 20;
            case AMMO -> 15;
        };
        try {
            switch(type) {
                case STAR -> image = ImageIO.read(new File("resources/images/star.png"));
                case HEALTH -> image = ImageIO.read(new File("resources/images/health.png"));
                case AMMO -> image = ImageIO.read(new File("resources/images/ammo.png"));
            }
        } catch (Exception e) {
            image = null;
        }
    }
    
    @Override
    public void update() {
        x -= 2;
        bobOffset = (int)(Math.sin(System.currentTimeMillis() / 200.0) * 5);
        if (x < -50) active = false;
    }
    
    @Override
    public void draw(Graphics g) {
        int drawY = (int)y + bobOffset;
        
        if (image != null) {
            g.drawImage(image, (int)x, drawY, width, height, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval((int)x, drawY, width, height);
        }
    }
    
    public Type getType() { return type; }
    public int getValue() { return value; }
}