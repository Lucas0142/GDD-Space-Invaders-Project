package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int currentSpeed = 2;
    private int speedLevel = 1;
    private int shotLevel = 1;
    private int lives = 100;

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        this.width = ii.getIconWidth() * SCALE_FACTOR;
        setX(START_X);
        setY(START_Y);
    }

    public void upgradeSpeed() {
        if (speedLevel < MAX_SPEED_LEVEL) {
            speedLevel++;
            currentSpeed = speedLevel * 2;
        }
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1;
        }
        this.currentSpeed = speed;
        updateSpeedLevel();
        return currentSpeed;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public int getShotLevel() {
        return shotLevel;
    }

    public void setShotLevel(int level) {
        if (level < 1) {
            this.shotLevel = 1;
        } else if (level > MAX_SHOT_LEVEL) {
            this.shotLevel = MAX_SHOT_LEVEL;
        } else {
            this.shotLevel = level;
        }
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
    }

    public boolean takeDamage() {
        lives--;
        if (lives <= 0) {
            setDying(true);
            return true;
        }
        return false;
    }

    public boolean isOutOfLives() {
        return lives <= 0;
    }

    private void updateSpeedLevel() {
        if (currentSpeed <= 2) {
            speedLevel = 1;
        } else if (currentSpeed <= 4) {
            speedLevel = 2;
        } else if (currentSpeed <= 6) {
            speedLevel = 3;
        } else {
            speedLevel = 4;
        }

        if (speedLevel > MAX_SPEED_LEVEL) {
            speedLevel = MAX_SPEED_LEVEL;
        }
    }

    @Override
    public void act() {
        x += dx;

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - width) {
            x = BOARD_WIDTH - width;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }
}