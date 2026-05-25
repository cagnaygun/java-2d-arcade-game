package com.spacegame.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Player extends Entity {
    private double velX, velY;
    private final double ACCEL = 0.5;
    private final double MAX_SPEED = 7;
    private final double FRICTION = 0.96;
    
    private boolean up, down, left, right;
    private int health = 100;
    private int ammo = 50;
    private int damageFlash = 0;
    private BufferedImage image;
    
    public Player(int x, int y) {
        super(x, y, 98, 98);
        try {
            image = ImageIO.read(new File("resources/images/spaceship.png"));
        } catch (Exception e) {
            image = null;
        }
    }
    
    public void setUp(boolean b) { up = b; }
    public void setDown(boolean b) { down = b; }
    public void setLeft(boolean b) { left = b; }
    public void setRight(boolean b) { right = b; }
    
    @Override
    public void update() {
        if (up) velY -= ACCEL;
        if (down) velY += ACCEL;
        if (left) velX -= ACCEL;
        if (right) velX += ACCEL;
        
        velX = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, velX));
        velY = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, velY));
        
        if (!left && !right) velX *= FRICTION;
        if (!up && !down) velY *= FRICTION;
        
        x += velX;
        y += velY;
        
        // Ekran sınırları
        if (x < 0) { x = 0; velX = 0; }
        if (x > 800 - width) { x = 800 - width; velX = 0; }
        if (y < 0) { y = 0; velY = 0; }
        if (y > 600 - height) { y = 600 - height; velY = 0; }
        
        if (damageFlash > 0) damageFlash--;
    }
    
    @Override
    public void draw(Graphics g) {
    	if (damageFlash > 0) {

    	    for (int i = 0; i < 6; i++) {

    	        int sparkX = (int)x + width / 2 + (int)(Math.random() * 30 - 15);
    	        int sparkY = (int)y + height / 2 + (int)(Math.random() * 30 - 15);

    	        int size = 6 + (int)(Math.random() * 6);

    	        g.setColor(i % 2 == 0 ? Color.ORANGE : Color.YELLOW);

    	        g.fillOval(sparkX, sparkY, size, size);
    	    }
    	}
    	// Spaceship PNG
        if (image != null) {
            g.drawImage(image, (int)x, (int)y, width, height, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillOval((int)x + 8, (int)y + 4, 32, 40);

            g.setColor(Color.BLUE);
            g.fillOval((int)x + 14, (int)y + 10, 20, 14);
        }
        // İtiş efekti
     // RIGHT movement -> left thruster
        if (right) {

            for (int i = 0; i < 5; i++) {

                int fx = (int)x + 8 + (int)(Math.random() * 8);
                int fy = (int)y + height / 2 + (int)(Math.random() * 20 - 10);

                int size = 6 + (int)(Math.random() * 6);

                g.setColor(i % 2 == 0
                    ? new Color(255, 180, 40)
                    : new Color(255, 120, 0));

                g.fillOval(fx, fy, size, size);
            }
        }

       
        

        if (up) {

            for (int i = 0; i < 6; i++) {

                int fx = (int)x + width / 2 + (int)(Math.random() * 24 - 12);
                int fy = (int)y + height - 25 + (int)(Math.random() * 4);

                int size = 5 + (int)(Math.random() * 5);

                g.setColor(i % 2 == 0
                    ? new Color(255, 200, 60)
                    : new Color(255, 100, 0));

                g.fillOval(fx, fy, size, size);
            }
        }

        if (down) {

            for (int i = 0; i < 4; i++) {

                int fx = (int)x + width / 2 + (int)(Math.random() * 20 - 10);
                int fy = (int)y + 25 + (int)(Math.random() * 4);

                int size = 4 + (int)(Math.random() * 4);

                g.setColor(new Color(255, 140, 50));

                g.fillOval(fx, fy, size, size);
            }
        }
    }
    
    public void drawHUD(Graphics g) {
        // Ammo bar
        g.setColor(Color.DARK_GRAY);
        g.fillRect(20, 20, 100, 14);
        int ammoW = (int)((ammo / 100.0) * 100);
        g.setColor(ammo > 30 ? new Color(0, 200, 255) : Color.RED);
        g.fillRect(20, 20, ammoW, 14);
        g.setColor(Color.WHITE);
        g.drawRect(20, 20, 100, 14);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("LASER", 55, 31);
        
        // Health bar
        g.setColor(Color.DARK_GRAY);
        g.fillRect(20, 38, 100, 10);
        int hpW = (int)((health / 100.0) * 100);
        g.setColor(health > 50 ? Color.GREEN : health > 25 ? Color.YELLOW : Color.RED);
        g.fillRect(20, 38, hpW, 10);
        g.setColor(Color.WHITE);
        g.drawRect(20, 38, 100, 10);
    }
    
    public boolean takeDamage(int dmg) {
        health -= dmg;
        damageFlash = 10;
        return health > 0;
    }
    
    public void heal(int amount) {
        health = Math.min(100, health + amount);
    }
    
    public void addAmmo(int amount) {
        ammo = Math.min(100, ammo + amount);
    }
    @Override
    public Rectangle getBounds() {
        return new Rectangle(
            (int)x + 25,
            (int)y + 25,
            width - 50,
            height - 50
        );
    }
    public boolean canFire() { return ammo >= 2; }
    public void fire() { if (canFire()) ammo -= 2; }
    
    public int getHealth() { return health; }
    public int getAmmo() { return ammo; }
}