package com.spacegame.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class Meteor extends Entity {
    public enum Size { SMALL, MEDIUM, BIG }
    
    private double speed;
    private Size size;
    private int health;
    private BufferedImage image;
    private double rotation = 0;
    private double rotationSpeed;
    private static Random rand = new Random();
    public Meteor(int x, int y, Size size) {
        super(x, y, 0, 0);
        this.size = size;
        
        switch(size) {
            case SMALL -> { width = 30; height = 30; speed = 4 + rand.nextInt(3); health = 1; }
            case MEDIUM -> { width = 50; height = 50; speed = 3 + rand.nextInt(2); health = 2; }
            case BIG -> { width = 70; height = 70; speed = 2 + rand.nextInt(2); health = 3; }
        }
        
        try {
            image = ImageIO.read(new File("resources/images/meteor.png"));
        } catch (Exception e) {
            image = null;
        }rotationSpeed = 0.2 + rand.nextDouble() * 0.5;
    }
    
    @Override
    public void update() {
        x -= speed;
        rotation += rotationSpeed;
        if (x < -100) active = false;
        if (hitFlash > 0) hitFlash--;
    }
    
    @Override
    public void draw(Graphics g) {

        if (image != null) {
        	Graphics2D g2d = (Graphics2D) g;

        	g2d.rotate(
        	    Math.toRadians(rotation),
        	    x + width / 2,
        	    y + height / 2
        	);

        	g2d.drawImage(
        	    image,
        	    (int)x,
        	    (int)y,
        	    width,
        	    height,
        	    null
        	);

        	g2d.rotate(
        	    -Math.toRadians(rotation),
        	    x + width / 2,
        	    y + height / 2
        	);

        } else {
            g.setColor(new Color(150, 100, 80));
            g.fillOval((int)x, (int)y, width, height);

            g.setColor(new Color(100, 70, 50));
            g.fillOval((int)x + 5, (int)y + 5, width - 10, height - 10);
        }

        // Spark effect
        if (hitFlash > 0) {

            for (int i = 0; i < 5; i++) {

                int sparkX = (int)x + width / 2 + (int)(Math.random() * 20 - 10);
                int sparkY = (int)y + height / 2 + (int)(Math.random() * 20 - 10);

                int size = 4 + (int)(Math.random() * 4);

                g.setColor(i % 2 == 0 ? Color.ORANGE : Color.YELLOW);

                g.fillOval(sparkX, sparkY, size, size);
            }
        }
    }
    private int hitFlash = 0;
    public void hit() {
        health--;
        hitFlash = 6;

        if (health <= 0) active = false;
    }
    
    public Size getSize() { return size; }
    public int getSpeed() { return (int)speed; }
}