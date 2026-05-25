package com.spacegame.view;

import com.spacegame.*;
import com.spacegame.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener, GameObserver {
    
    public static final int W = 800;
    public static final int H = 600;
    private static final int BOSS_TIME = 4800; // 80 saniye
    
    private Timer timer;
    private Random rand;
    private Player player;
    private List<Meteor> meteors;
    private List<Projectile> projectiles;
    private List<Pickup> pickups;
    private List<Star> bgStars;
    private Boss boss;
    
    private enum State { MENU, PLAYING, BOSS_WARNING, BOSS_TRANSITION, BOSS, GAMEOVER, VICTORY }
    private State state;
    private int frameCount;
    private int waveTimer;
    private int waveNumber;
    private int bossWarningTimer;
    private int bossTransitionTimer;
    
    private boolean up, down, left, right, firing;
    
    public GamePanel() {
        setPreferredSize(new Dimension(W, H));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        rand = new Random();
        meteors = new ArrayList<>();
        projectiles = new ArrayList<>();
        pickups = new ArrayList<>();
        bgStars = new ArrayList<>();
        
        for (int i = 0; i < 150; i++) {
            bgStars.add(new Star());
        }
        
        state = State.MENU;
        
        GameManager.getInstance().addObserver(this);
        ScoreManager.getInstance();
        
        timer = new Timer(16, this);
        timer.start();
        SoundManager.getInstance().playMusic("arcade.wav");
    }
    
    private void startGame() {
        player = new Player(W / 2 - 24, H / 2 - 24);
        meteors.clear();
        projectiles.clear();
        pickups.clear();
        boss = null;
        frameCount = 0;
        waveTimer = 0;
        waveNumber = 1;
        bossWarningTimer = 0;
        bossTransitionTimer = 0;
        state = State.PLAYING;
        
        GameManager.getInstance().startGame();
        ScoreManager.getInstance().reset();
        
        requestFocus();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, W, H);
        
        for (Star s : bgStars) {
            s.move(state == State.PLAYING || state == State.BOSS ? 2 : 1);
            s.draw(g);
        }
        
        switch(state) {
            case MENU -> drawMenu(g);
            case PLAYING -> drawGame(g);
            case BOSS_WARNING -> drawBossWarning(g);
            case BOSS_TRANSITION -> drawBossTransition(g);
            case BOSS -> drawBoss(g);
            case GAMEOVER -> drawGameOver(g);
            case VICTORY -> drawVictory(g);
        }
    }
    
    private void drawMenu(Graphics g) {

        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Courier New", Font.BOLD, 50));

        String title = "GALAXY BLAST";
        FontMetrics fmTitle = g.getFontMetrics();
        int titleX = (W - fmTitle.stringWidth(title)) / 2;

        g.drawString(title, titleX, H/2 - 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Courier New", Font.PLAIN, 20));

        String controls1 = "Move with WASD / Arrow Keys";
        String controls2 = "Press SPACE to shoot";
        String controls3 = "Dodge the meteor shower!";

        FontMetrics fm = g.getFontMetrics();

        g.drawString(
            controls1,
            (W - fm.stringWidth(controls1)) / 2,
            H/2 - 10
        );

        g.drawString(
            controls2,
            (W - fm.stringWidth(controls2)) / 2,
            H/2 + 20
        );

        g.drawString(
            controls3,
            (W - fm.stringWidth(controls3)) / 2,
            H/2 + 50
        );

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Courier New", Font.BOLD, 24));

        String start = ">> PRESS SPACE TO START <<";

        FontMetrics fmStart = g.getFontMetrics();

        g.drawString(
            start,
            (W - fmStart.stringWidth(start)) / 2,
            H/2 + 120
        );
    }
    
    private void drawGame(Graphics g) {
        player.draw(g);
        for (Meteor m : meteors) m.draw(g);
        for (Projectile p : projectiles) p.draw(g);
        for (Pickup pu : pickups) pu.draw(g);
        player.drawHUD(g);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Courier New", Font.BOLD, 18));
        g.drawString("SCORE: " + ScoreManager.getInstance().getScore(), W - 140, 30);
        g.setColor(new Color(100, 220, 255));
       
        int lightYears = Math.min(80, frameCount / 60);

        g.drawString(
            "LIGHT YEARS TRAVELED: " + lightYears,
            W - 300,
            55
        );
        
     // Smooth warning
        if (frameCount > BOSS_TIME - 300) {

            float alpha =
                0.5f + 0.5f * (float)Math.sin(frameCount * 0.2);

            g.setColor(
                new Color(255, 0, 0, (int)(150 + 105 * alpha))
            );

            g.setFont(new Font("Courier New", Font.BOLD, 36));

            String warning = "!!! ALIEN DISCOVERED !!!";

            FontMetrics fmWarning = g.getFontMetrics();

            g.drawString(
                warning,
                (W - fmWarning.stringWidth(warning)) / 2,
                100
            );
        }
    }
    
    private void drawBossWarning(Graphics g) {
        // Oyun devam ediyor ama uyarı gösteriliyor
        drawGame(g);
        
        // Kırmızı overlay
        float progress = bossWarningTimer / 180.0f; // 3 saniye
        int alpha = (int)(100 + 155 * (1 - progress));
        g.setColor(new Color(255, 0, 0, alpha));
        g.fillRect(0, 0, W, H);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Courier New", Font.BOLD, 30));
        String warning = "WARNING: ALIEN STARSHIP ATTACKING!";

        FontMetrics fm = g.getFontMetrics();

        int warningX = (W - fm.stringWidth(warning)) / 2;

        g.drawString(warning, warningX, H/2);        
        g.setFont(new Font("Courier New", Font.BOLD, 24));
        g.drawString("Clearing the asteroid field...", W/2 - 150, H/2 + 50);
    }
    
    private void drawBossTransition(Graphics g) {
        // Gezegen yavaşça beliriyor
        float progress = bossTransitionTimer / 120.0f; // 2 saniye
        
        // Gezegen arka plan - yavaşça
      /*  int planetAlpha = (int)(255 * progress);
        g.setColor(new Color(200, 100, 50, planetAlpha));
        g.fillOval(W - 250, H/2 - 125, 250, 250);
       */ 
        // Boss yavaşça beliriyor
        if (boss != null) {
            g.setColor(new Color(255, 255, 255, (int)(200 * progress)));
            g.setFont(new Font("Courier New", Font.BOLD, 30));
            g.drawString("ALIEN SPACESHIP DETECTED!", W/2 - 180, H/2 - 50);
            boss.draw(g);
        }
        
        player.draw(g);
        for (Projectile p : projectiles) p.draw(g);
        player.drawHUD(g);
    }
    
    private void drawBoss(Graphics g) {
        // Gezegen arka plan
      //  g.setColor(new Color(200, 100, 50));
      //  g.fillOval(W - 250, H/2 - 125, 250, 250);
        
        if (boss != null && !boss.isDead()) boss.draw(g);
        player.draw(g);
        for (Projectile p : projectiles) p.draw(g);
        for (Pickup pu : pickups) pu.draw(g);
        player.drawHUD(g);
        
        if (boss != null) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(W/2 - 150, 10, 300, 20);
            int hpW = (int)((boss.getHealth() / 100.0) * 300);
            g.setColor(new Color(200, 50, 50));
            g.fillRect(W/2 - 150, 10, hpW, 20);
            g.setColor(Color.WHITE);
            g.drawRect(W/2 - 150, 10, 300, 20);
            g.setFont(new Font("Courier New", Font.BOLD, 14));
            g.drawString("ALIEN HEALTH", W/2 - 40, 25);
        }
        for (Meteor m : meteors) {
            m.draw(g);
        }
    }
    
    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, W, H);

        g.setColor(Color.RED);
        g.setFont(new Font("Courier New", Font.BOLD, 50));
        String title = "GAME OVER!";
        FontMetrics fmTitle = g.getFontMetrics();
        g.drawString(title, (W - fmTitle.stringWidth(title)) / 2, H/2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Courier New", Font.PLAIN, 24));
        String scoreText = "Score: " + ScoreManager.getInstance().getScore();
        FontMetrics fmScore = g.getFontMetrics();
        g.drawString(scoreText, (W - fmScore.stringWidth(scoreText)) / 2, H/2 + 20);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Courier New", Font.BOLD, 24));
        String pressText = ">> PRESS SPACE <<";
        FontMetrics fmPress = g.getFontMetrics();
        g.drawString(pressText, (W - fmPress.stringWidth(pressText)) / 2, H/2 + 80);
    }
    
    private void drawVictory(Graphics g) {
       // g.setColor(new Color(255, 215, 0, 100));
       // g.fillRect(0, 0, W, H);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Courier New", Font.BOLD, 50));
        String title = "VICTORY!";
        FontMetrics fmTitle = g.getFontMetrics();
        g.drawString(title, (W - fmTitle.stringWidth(title)) / 2, H/2 - 60);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Courier New", Font.PLAIN, 24));
        String msg = "You saved the world from an alien invasion!";
        FontMetrics fmMsg = g.getFontMetrics();
        g.drawString(msg, (W - fmMsg.stringWidth(msg)) / 2, H/2);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Courier New", Font.BOLD, 24));
        String pressText = ">> PRESS SPACE <<";
        FontMetrics fmPress = g.getFontMetrics();
        g.drawString(pressText, (W - fmPress.stringWidth(pressText)) / 2, H/2 + 60);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(state) {
            case PLAYING -> updatePlaying();
            case BOSS_WARNING -> updateBossWarning();
            case BOSS_TRANSITION -> updateBossTransition();
            case BOSS -> updateBoss();
        }
        repaint();
    }
    
    private void updatePlaying() {
        frameCount++;
        player.setUp(up); player.setDown(down);
        player.setLeft(left); player.setRight(right);
        player.update();
        
        if (firing && frameCount % 8 == 0 && player.canFire()) {
            projectiles.add(new Projectile(player.getX() + 48, player.getY() + 32, true));
            player.fire();
            SoundManager.getInstance().play("laser.wav");
        }
        
        waveTimer++;
        int waveInterval = Math.max(200, 500 - waveNumber * 5);
        if (waveTimer > waveInterval) {
            spawnWave();
            waveTimer = 0;
            waveNumber++;
        }
        
        if (rand.nextInt(100) < 3 + waveNumber / 4) {
            spawnRandomMeteor();
        }
        
        if (frameCount % 350 == 0) {
            spawnPickup();
        }
        
        // Smooth boss geçişi - önce uyarı
        if (frameCount >= BOSS_TIME) {
            state = State.BOSS_WARNING;
            bossWarningTimer = 0;
            return;
        }
        
        updateEntities();
        checkCollisions();
    }
    
    private void updateBossWarning() {
        bossWarningTimer++;
        
        // Meteorları yavaşça temizle
        Iterator<Meteor> mit = meteors.iterator();
        while (mit.hasNext()) {
            Meteor m = mit.next();
            m.update();
            if (!m.isActive() || m.getX() < -100) mit.remove();
        }
        
        // Pickup'ları topla
        for (Pickup pu : pickups) {
            pu.update();
        }
        
        player.update();
        
        // 3 saniye sonra transition
        if (bossWarningTimer > 180) {
            state = State.BOSS_TRANSITION;
            bossTransitionTimer = 0;
            meteors.clear();
            pickups.clear();
            projectiles.clear();
            boss = new Boss(W - 150, H/2 - 30);
        }
    }
    
    private void updateBossTransition() {
        bossTransitionTimer++;
        player.update();
        
        // 2 saniye sonra boss fight başlar
        if (bossTransitionTimer > 120) {
            state = State.BOSS;
            frameCount = 0; // Boss için yeni sayaç
        }
    }
    
    private void updateBoss() {
        frameCount++;
        player.setUp(up); player.setDown(down);
        player.setLeft(left); player.setRight(right);
        player.update();
        
        
        if (boss != null) {
            boss.update();
         // Small easy meteors during boss fight
            if (frameCount % 180 == 0) {

                int meteorY = rand.nextInt(H - 60);

                meteors.add(
                    new Meteor(W, meteorY, Meteor.Size.SMALL)
                );
                for (Meteor m : meteors) {
                    m.update();
                }
            }
            if (boss.shouldShoot()) {
                projectiles.add(new Projectile(boss.getX(), boss.getY() + 30, false));
            }
            if (frameCount % 50 == 0) { // Daha seyrek meteor
                int targetY = (int)(player.getY() + player.getHeight()/2);
                meteors.add(new Meteor((int)boss.getX(), targetY - 25, Meteor.Size.MEDIUM));
            }
         // Boss phase meteor collisions
            Iterator<Meteor> mit = meteors.iterator();

            while (mit.hasNext()) {

                Meteor m = mit.next();

                // Player collision
                if (player.getBounds().intersects(m.getBounds())) {
                	SoundManager.getInstance().play("damage.wav");
                	if (!player.takeDamage(10)) {

                	    state = State.GAMEOVER;

                	    GameManager.getInstance().notifyGameOver();

                	    return;
                	}

                    m.setActive(false);
                }

                // Projectile collision
                for (Projectile p : projectiles) {

                    if (m.getBounds().intersects(p.getBounds())) {

                        m.hit();

                        p.setActive(false);

                        break;
                    }
                }

                if (!m.isActive()) {
                    mit.remove();
                }
            }
        }
        
        // Seyrek ammo pickup
        if (frameCount % 300 == 0) {
            pickups.add(new Pickup(W, 50 + rand.nextInt(H - 100), Pickup.Type.AMMO));
        }
        if (frameCount % 220 == 0) {
            pickups.add(new Pickup(
                W,
                50 + rand.nextInt(H - 100),
                Pickup.Type.HEALTH
            ));
        }
        if (firing && frameCount % 8 == 0 && player.canFire()) {
            projectiles.add(new Projectile(player.getX() + 48, player.getY() + 22, true));
            player.fire();
            SoundManager.getInstance().play("laser.wav");
        }
        
        updateBossEntities();
        checkBossCollisions();
        
        if (boss != null && boss.isDead()) {
            state = State.VICTORY;
            GameManager.getInstance().notifyGameOver();
        }
    }
    
    private void spawnWave() {
        int waveType = rand.nextInt(3);
        switch(waveType) {
            case 0 -> spawnGapWave();
            case 1 -> spawnWallWave();
            case 2 -> spawnChaosWave();
        }
    }
    
    private void spawnGapWave() {
        int gapCount = 1;
        int sectionHeight = H / gapCount;
        
        for (int g = 0; g < gapCount; g++) {
            int gapStart = g * sectionHeight + 60 + rand.nextInt(sectionHeight - 120);
            int gapSize = 120 + rand.nextInt(80);
            
            for (int y = g * sectionHeight; y < (g + 1) * sectionHeight; y += 70 + rand.nextInt(40)) {
                if (y > gapStart && y < gapStart + gapSize) continue;
                
                Meteor.Size size = rand.nextInt(100) < 5 ? Meteor.Size.SMALL :
                                  rand.nextInt(100) < 50 ? Meteor.Size.MEDIUM : Meteor.Size.BIG;
                int x = W + rand.nextInt(200);
                meteors.add(new Meteor(x, y, size));
            }
        }
    }
    
    private void spawnWallWave() {
        int wallY = 80 + rand.nextInt(H - 160);
        int wallHeight = 150 + rand.nextInt(100);
        int holes = 2;
        int holeSize = 90 + rand.nextInt(50);
        
        for (int y = 0; y < H; y += 60 + rand.nextInt(30)) {
            boolean inHole = false;
            for (int h = 0; h < holes; h++) {
                int holeStart = wallY + h * (wallHeight / holes);
                if (y > holeStart && y < holeStart + holeSize) {
                    inHole = true;
                    break;
                }
            }
            if (inHole) continue;
            
            Meteor.Size size = rand.nextInt(100) < 5 ? Meteor.Size.SMALL : 
                              rand.nextInt(100) < 45 ? Meteor.Size.MEDIUM : Meteor.Size.BIG;
            meteors.add(new Meteor(W + rand.nextInt(150), y, size));
        }
    }
    
    private void spawnChaosWave() {
        int count = 3 + rand.nextInt(2) + waveNumber / 5;
        
        for (int i = 0; i < count; i++) {
            int y = rand.nextInt(H - 40);
            Meteor.Size size = rand.nextInt(100) < 5 ? Meteor.Size.SMALL :
                              rand.nextInt(100) < 50 ? Meteor.Size.MEDIUM : Meteor.Size.BIG;
            int x = W + rand.nextInt(500);
            meteors.add(new Meteor(x, y, size));
        }
    }
    
    private void spawnRandomMeteor() {
        int y = rand.nextInt(H - 40);
        Meteor.Size size = rand.nextInt(100) < 5 ? Meteor.Size.SMALL :
                          rand.nextInt(100) < 50 ? Meteor.Size.MEDIUM : Meteor.Size.BIG;
        meteors.add(new Meteor(W + 50, y, size));
    }
    
    private void spawnPickup() {
        Pickup.Type type = rand.nextInt(100) < 70 ? Pickup.Type.STAR : 
                          rand.nextInt(100) < 85 ? Pickup.Type.HEALTH : Pickup.Type.AMMO;
        pickups.add(new Pickup(W, 50 + rand.nextInt(H - 100), type));
    }
    
    private void updateEntities() {
        Iterator<Meteor> mit = meteors.iterator();
        while (mit.hasNext()) {
            Meteor m = mit.next();
            m.update();
            if (!m.isActive()) mit.remove();
        }
        
        Iterator<Projectile> pit = projectiles.iterator();
        while (pit.hasNext()) {
            Projectile p = pit.next();
            p.update();
            if (!p.isActive()) pit.remove();
        }
        
        Iterator<Pickup> puit = pickups.iterator();
        while (puit.hasNext()) {
            Pickup pu = puit.next();
            pu.update();
            if (!pu.isActive()) puit.remove();
        }
    }
    
    private void updateBossEntities() {
        // Meteorlar - boss ekranında yavaş ve seyrek
        Iterator<Meteor> mit = meteors.iterator();
        while (mit.hasNext()) {
            Meteor m = mit.next();
            m.update();
            if (!m.isActive()) mit.remove();
        }
        
        Iterator<Projectile> pit = projectiles.iterator();
        while (pit.hasNext()) {
            Projectile p = pit.next();
            p.update();
            if (!p.isActive()) pit.remove();
        }
        
        Iterator<Pickup> puit = pickups.iterator();
        while (puit.hasNext()) {
            Pickup pu = puit.next();
            pu.update();
            if (!pu.isActive()) puit.remove();
        }
    }
    
    private void checkCollisions() {
        for (Meteor m : meteors) {
            if (m.isActive() && m.collidesWith(player)) {
                GameManager.getInstance().notifyCollision();
                SoundManager.getInstance().play("damage.wav");
                if (!player.takeDamage(m.getSize() == Meteor.Size.BIG ? 20 : m.getSize() == Meteor.Size.MEDIUM ? 15 : 8)) {
                    state = State.GAMEOVER;
                    GameManager.getInstance().notifyGameOver();
                    return;
                }
                m.destroy();
            }
        }
        
        for (Pickup pu : pickups) {
            if (pu.isActive() && pu.collidesWith(player)) {
                switch(pu.getType()) {
                    case STAR -> player.addAmmo(pu.getValue());
                    case HEALTH -> player.heal(pu.getValue());
                    case AMMO -> player.addAmmo(pu.getValue());
                }   SoundManager.getInstance().play("pickup.wav");
                pu.destroy();
            }
        }
        
        for (Projectile p : projectiles) {
            if (!p.isFromPlayer() || !p.isActive()) continue;
            for (Meteor m : meteors) {
                if (m.isActive() && p.collidesWith(m)) {
                    p.destroy();
                    m.hit();
                    // HER meteor vurulduğunda loot
                    if (!m.isActive()) {
                        spawnMeteorLoot(m);
                    }
                    int points = m.getSize() == Meteor.Size.BIG ? 30 : m.getSize() == Meteor.Size.MEDIUM ? 20 : 10;
                    ScoreManager.getInstance().addScore(points);
                    break;
                }
            }
        }
    }
    
    private void spawnMeteorLoot(Meteor m) {
        int x = (int)m.getX();
        int y = (int)m.getY();
        
        // %80 ihtimalle loot çıkar
        if (rand.nextInt(100) < 80) {
            Pickup.Type type;
            int r = rand.nextInt(100);
            if (r < 35) type = Pickup.Type.HEALTH; // %40 can
            else if (r < 50) type = Pickup.Type.AMMO; // %30 ammo
            else type = Pickup.Type.STAR; // %30 star
            
            pickups.add(new Pickup(x, y, type));
        }
    }
    
    private void checkBossCollisions() {
        // Meteor - player
   /*     for (Meteor m : meteors) {
            if (m.isActive() && m.collidesWith(player)) {
                GameManager.getInstance().notifyCollision();
                if (!player.takeDamage(15)) {	
                    state = State.GAMEOVER;
                    GameManager.getInstance().notifyGameOver();
                    return;
                }
                m.destroy();
            }
        }
        */
        // Pickup - player
        for (Pickup pu : pickups) {
            if (pu.isActive() && pu.collidesWith(player)) {
                player.addAmmo(pu.getValue());
                SoundManager.getInstance().play("pickup.wav");
                pu.destroy();
            }
        }
        
        // Player lazer - boss
        for (Projectile p : projectiles) {
            if (p.isFromPlayer() && p.isActive() && boss != null && !boss.isDead() && p.collidesWith(boss)) {
                p.destroy();
                boss.takeDamage(8);
            }
        }
        
        // Boss lazer - player
        for (Projectile p : projectiles) {
            if (!p.isFromPlayer() && p.isActive() && p.collidesWith(player)) {
                GameManager.getInstance().notifyCollision();
                p.destroy();
                SoundManager.getInstance().play("damage.wav");
                if (!player.takeDamage(10)) {
                    state = State.GAMEOVER;
                    GameManager.getInstance().notifyGameOver();
                    return;
                }
            }
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_SPACE) {
            if (state == State.MENU || state == State.GAMEOVER || state == State.VICTORY) {
                startGame();
                return;
            }
            firing = true;
        }
        
        switch(key) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = true;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        switch(key) {
            case KeyEvent.VK_SPACE -> firing = false;
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = false;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void onScoreChanged(int newScore) {
        repaint();
    }
    
    @Override
    public void onGameOver() {
        repaint();
    }
    
    @Override
    public void onCollision() {
    }
    
    private class Star {
        int x, y, speed;
        Star() {
            x = rand.nextInt(W);
            y = rand.nextInt(H);
            speed = 1 + rand.nextInt(3);
        }
        void move(int gameSpeed) {
            x -= speed + gameSpeed / 2;
            if (x < 0) { x = W; y = rand.nextInt(H); }
        }
        void draw(Graphics g) {
            g.setColor(new Color(255, 255, 255, 100 + rand.nextInt(155)));
            g.fillRect(x, y, 2, 2);
        }
    }
}