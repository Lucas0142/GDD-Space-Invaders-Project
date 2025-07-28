package gdd.sprite;

import static gdd.Global.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class AnimatedAlien2 extends Enemy {

    private static final int STATE_MOVING = 0;
    private static final int STATE_ATTACKING = 1;

    private BufferedImage moveSheet;
    private BufferedImage attackSheet;
    private BufferedImage[] moveFrames;
    private BufferedImage[] attackFrames;

    private int currentFrame = 0;
    private int frameCounter = 0;
    private int animationSpeed = 10;
    private int currentState = STATE_MOVING;

    private int attackTimer = 0;
    private int attackDuration = 20;
    private int attackCooldown = 120;
    private int cooldownTimer = 0;

    private int frameCounterMove = 0;
    private int zigzagDirection = 1;
    private int spiralCounter = 0;

    private List<Alien2LaserProjectile> lasers;

    public AnimatedAlien2(int x, int y) {
        super(x, y);
        this.lasers = new ArrayList<>();
        initAnimatedAlien2();
    }

    private void initAnimatedAlien2() {
        File moveFile = new File("src/images/fighter_move.png");
        File attackFile = new File("src/images/fighter_attack2.png");

        if (moveFile.exists() && attackFile.exists()) {
            try {
                moveSheet = ImageIO.read(moveFile);
                attackSheet = ImageIO.read(attackFile);
                extractFrames();

                if (moveFrames != null && moveFrames.length > 0) {
                    int alien2Width = ALIEN_WIDTH * 3;
                    int alien2Height = ALIEN_HEIGHT * 3;
                    var scaledImage = moveFrames[0].getScaledInstance(
                            alien2Width, alien2Height,
                            java.awt.Image.SCALE_SMOOTH
                    );
                    setImage(scaledImage);
                }
            } catch (IOException e) {
                initFallbackAlien2();
            }
        } else {
            initFallbackAlien2();
        }
    }

    private void extractFrames() {
        if (moveSheet != null) {
            moveFrames = new BufferedImage[6];
            int frameWidth = moveSheet.getWidth();
            int frameHeight = moveSheet.getHeight() / 6;

            for (int i = 0; i < 6; i++) {
                int y = i * frameHeight;
                if (y + frameHeight <= moveSheet.getHeight()) {
                    moveFrames[i] = moveSheet.getSubimage(0, y, frameWidth, frameHeight);
                }
            }
        }

        if (attackSheet != null) {
            attackFrames = new BufferedImage[2];
            int frameWidth = attackSheet.getWidth();
            int frameHeight = attackSheet.getHeight() / 2;

            for (int i = 0; i < 2; i++) {
                int y = i * frameHeight;
                if (y + frameHeight <= attackSheet.getHeight()) {
                    attackFrames[i] = attackSheet.getSubimage(0, y, frameWidth, frameHeight);
                }
            }
        }
    }

    private void initFallbackAlien2() {
        var ii = new javax.swing.ImageIcon(IMG_ENEMY2);
        if (!new File(IMG_ENEMY2).exists()) {
            ii = new javax.swing.ImageIcon(IMG_ENEMY);
        }
        int alien2Width = ALIEN_WIDTH * 3;
        int alien2Height = ALIEN_HEIGHT * 3;
        var scaledImage = ii.getImage().getScaledInstance(
                alien2Width, alien2Height,
                java.awt.Image.SCALE_SMOOTH
        );
        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        super.act(0);
        updateAnimation();
        updateAttackState();
        updateMovement();
        updateLasers();
        attemptLaserAttack();
    }

    private void updateAnimation() {
        if ((currentState == STATE_MOVING && moveFrames != null) ||
                (currentState == STATE_ATTACKING && attackFrames != null)) {

            frameCounter++;

            if (frameCounter >= animationSpeed) {
                if (currentState == STATE_MOVING) {
                    currentFrame = (currentFrame + 1) % moveFrames.length;
                    int alien2Width = ALIEN_WIDTH * 3;
                    int alien2Height = ALIEN_HEIGHT * 3;
                    var scaledImage = moveFrames[currentFrame].getScaledInstance(
                            alien2Width, alien2Height,
                            java.awt.Image.SCALE_SMOOTH
                    );
                    setImage(scaledImage);
                } else if (currentState == STATE_ATTACKING) {
                    currentFrame = (currentFrame + 1) % attackFrames.length;
                    int alien2Width = ALIEN_WIDTH * 3;
                    int alien2Height = ALIEN_HEIGHT * 3;
                    var scaledImage = attackFrames[currentFrame].getScaledInstance(
                            alien2Width, alien2Height,
                            java.awt.Image.SCALE_SMOOTH
                    );
                    setImage(scaledImage);
                }
                frameCounter = 0;
            }
        }
    }

    private void updateAttackState() {
        if (currentState == STATE_ATTACKING) {
            attackTimer++;
            if (attackTimer >= attackDuration) {
                currentState = STATE_MOVING;
                currentFrame = 0;
                attackTimer = 0;
            }
        }

        if (cooldownTimer > 0) {
            cooldownTimer--;
        }
    }

    private void updateMovement() {
        this.y += 2;

        spiralCounter++;

        frameCounterMove++;
        if (frameCounterMove >= 40) {
            zigzagDirection *= -1;
            frameCounterMove = 0;
        }

        double spiralRadius = 15 + (spiralCounter * 0.05);
        double spiralAngle = spiralCounter * 0.05;
        int spiralX = (int)(Math.cos(spiralAngle) * spiralRadius);
        int spiralY = (int)(Math.sin(spiralAngle) * 5);

        this.x += zigzagDirection * 1 + spiralX * 0.1;
        this.y += spiralY * 0.1;

        int alien2Width = ALIEN_WIDTH * 3;
        if (this.x <= BORDER_LEFT) {
            this.x = BORDER_LEFT;
            zigzagDirection = 1;
        }
        if (this.x >= BOARD_WIDTH - BORDER_RIGHT - alien2Width) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - alien2Width;
            zigzagDirection = -1;
        }

        if (y > BOARD_HEIGHT) {
            die();
        }
    }

    private void updateLasers() {
        lasers.removeIf(laser -> {
            if (!laser.isDestroyed()) {
                laser.act();
                return false;
            }
            return true;
        });
    }

    private void attemptLaserAttack() {
        if (cooldownTimer <= 0 && isVisible() && currentState == STATE_MOVING) {
            currentState = STATE_ATTACKING;
            currentFrame = 0;
            attackTimer = 0;
            fireCloseDoubleShot();
            cooldownTimer = attackCooldown;
        }
    }

    private void fireCloseDoubleShot() {
        int alien2Width = ALIEN_WIDTH * 3;
        int centerX = this.x + alien2Width / 2;
        int laserY = this.y + ALIEN_HEIGHT * 3;

        Alien2LaserProjectile leftLaser = new Alien2LaserProjectile(centerX - 2, laserY);
        leftLaser.setDestroyed(false);
        lasers.add(leftLaser);

        Alien2LaserProjectile rightLaser = new Alien2LaserProjectile(centerX + 2, laserY);
        rightLaser.setDestroyed(false);
        lasers.add(rightLaser);
    }

    @Override
    public void tryCreateBomb() {
        // Do nothing - Alien2 uses lasers instead of bombs
    }

    @Override
    public List<Bomb> getBombs() {
        return new ArrayList<>();
    }

    @Override
    public void act() {
        act(0);
    }

    public List<Alien2LaserProjectile> getLasers() {
        return lasers;
    }

    @Override
    public void die() {
        super.die();
    }

    public int getCurrentState() {
        return currentState;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setAnimationSpeed(int speed) {
        this.animationSpeed = Math.max(1, speed);
    }
}