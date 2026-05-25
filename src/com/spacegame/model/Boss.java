package com.spacegame.model;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.*;

public class Boss extends Entity {
    private int health;
    private final int MAX_HEALTH = 100;
    private boolean movingUp = true;
    private int moveTimer = 0;
    private int shootTimer = 0;
    private int hitFlash = 0;
    private BufferedImage image;
    
    public Boss(int x, int y) {
        super(x, y, 170, 140);
        this.health = MAX_HEALTH;
        try {
            image = ImageIO.read(new File("resources/images/boss.png"));
        } catch (Exception e) {
            image = null;
        }
    }
    
    @Override
    public void update() {
        moveTimer++;
        shootTimer++;
        if (hitFlash > 0) hitFlash--;
        
        if (moveTimer % 120 == 0) movingUp = !movingUp;
        
        if (movingUp) {
            y -= 2;
            if (y < 50) movingUp = false;
        } else {
            y += 2;
            if (y > 500) movingUp = true;
        }
    }
    
    @Override
    public void draw(Graphics g) {
    	if (image != null) {
    	    g.drawImage(image, (int)x, (int)y, width, height, null);
    	} else {
    	    g.setColor(new Color(120, 50, 150));
    	    g.fillOval((int)x, (int)y, width, height);
    	}
        
        // Can barı
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)x, (int)y - 15, width, 8);
        int hpW = (int)((health / 100.0) * width);
        g.setColor(health > 60 ? Color.GREEN : health > 30 ? Color.YELLOW : Color.RED);
        g.fillRect((int)x, (int)y - 15, hpW, 8);
        g.setColor(Color.WHITE);
        g.drawRect((int)x, (int)y - 15, width, 8);
        if (hitFlash > 0) {

            for (int i = 0; i < 12; i++) {

                int sparkX = (int)x + width / 2 + (int)(Math.random() * 80 - 40);
                int sparkY = (int)y + height / 2 + (int)(Math.random() * 80 - 40);

                int size = 8 + (int)(Math.random() * 10);

                if (i < 4) {
                    g.setColor(new Color(255, 220, 120)); // sıcak merkez
                } else if (i < 8) {
                    g.setColor(new Color(255, 120, 40)); // turuncu patlama
                } else {
                    g.setColor(new Color(120, 120, 120, 180)); // duman
                }

                g.fillOval(sparkX, sparkY, size, size);
            }
        }
    }
    
    public boolean shouldShoot() {
        if (shootTimer > 60) {
            shootTimer = 0;
            return true;
        }
        return false;
    }
    
    public void takeDamage(int dmg) {
        health -= dmg;

        hitFlash = 8;

        if (health < 0) health = 0;
    }
    
    public boolean isDead() { return health <= 0; }
    public int getHealth() { return health; }
}