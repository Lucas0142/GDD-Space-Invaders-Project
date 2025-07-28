package gdd.sprite;

import static gdd.Global.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class AnimatedAlien1 extends Enemy {

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
    private int attackCooldown = 180;
    private int cooldownTimer = 0;

    private List<Alien1LaserProjectile> lasers;

    private int waveCounter = 0;
    private double waveAmplitude = 2.0;
    private double waveFrequency = 0.1;

    public AnimatedAlien1(int x, int y) {
        super(x, y);
        this.lasers = new ArrayList<>();
        initAnimatedAlien1();
    }

    private void initAnimatedAlien1() {
        File moveFile = new File("src/images/Bomber_Move.png");
        File attackFile = new File("src/images/Bomber_Attack1.png");

        if (moveFile.exists() && attackFile.exists()) {
            try {
                moveSheet = ImageIO.read(moveFile);
                attackSheet = ImageIO.read(attackFile);
                extractFrames();

                if (moveFrames != null && moveFrames.length > 0) {
                    int alien1Width = ALIEN_WIDTH * 3;
                    int alien1Height = ALIEN_HEIGHT * 3;
                    var scaledImage = moveFrames[0].getScaledInstance(
                            alien1Width, alien1Height,
                            java.awt.Image.SCALE_SMOOTH
                    );
                    setImage(scaledImage);
                }
            } catch (IOException e) {
                initFallbackAlien1();
            }
        } else {
            initFallbackAlien1();
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
            attackFrames = new BufferedImage[3];
            int frameWidth = attackSheet.getWidth();
            int frameHeight = attackSheet.getHeight() / 3;

            for (int i = 0; i < 3; i++) {
                int y = i * frameHeight;
                if (y + frameHeight <= attackSheet.getHeight()) {
                    attackFrames[i] = attackSheet.getSubimage(0, y, frameWidth, frameHeight);
                }
            }
        }
    }

    private void initFallbackAlien1() {
        var ii = new javax.swing.ImageIcon(IMG_ENEMY);
        int alien1Width = ALIEN_WIDTH * 3;
        int alien1Height = ALIEN_HEIGHT * 3;
        var scaledImage = ii.getImage().getScaledInstance(
                alien1Width, alien1Height,
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
                    int alien1Width = ALIEN_WIDTH * 3;
                    int alien1Height = ALIEN_HEIGHT * 3;
                    var scaledImage = moveFrames[currentFrame].getScaledInstance(
                            alien1Width, alien1Height,
                            java.awt.Image.SCALE_SMOOTH
                    );
                    setImage(scaledImage);
                } else if (currentState == STATE_ATTACKING) {
                    currentFrame = (currentFrame + 1) % attackFrames.length;
                    int alien1Width = ALIEN_WIDTH * 3;
                    int alien1Height = ALIEN_HEIGHT * 3;
                    var scaledImage = attackFrames[currentFrame].getScaledInstance(
                            alien1Width, alien1Height,
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

        waveCounter++;
        double waveOffset = Math.sin(waveCounter * waveFrequency) * waveAmplitude;
        this.x += (int)waveOffset;

        int alien1Width = ALIEN_WIDTH * 3;
        if (this.x <= BORDER_LEFT) {
            this.x = BORDER_LEFT;
        }
        if (this.x >= BOARD_WIDTH - BORDER_RIGHT - alien1Width) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - alien1Width;
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
            fireDualLasers();
            cooldownTimer = attackCooldown;
        }
    }

    private void fireDualLasers() {
        int alien1Width = ALIEN_WIDTH * 3;
        int alien1Height = ALIEN_HEIGHT * 3;
        int centerX = this.x + alien1Width / 2;
        int cannonSpacing = alien1Width / 4;
        int leftCannonX = centerX - cannonSpacing / 2 - 5;
        int rightCannonX = centerX + cannonSpacing / 2;
        int laserY = this.y + alien1Height;

        Alien1LaserProjectile leftLaser = new Alien1LaserProjectile(leftCannonX, laserY);
        leftLaser.setDestroyed(false);
        lasers.add(leftLaser);

        Alien1LaserProjectile rightLaser = new Alien1LaserProjectile(rightCannonX, laserY);
        rightLaser.setDestroyed(false);
        lasers.add(rightLaser);
    }

    @Override
    public void tryCreateBomb() {
        // Do nothing - Alien1 uses lasers instead of bombs
    }

    @Override
    public List<Bomb> getBombs() {
        return new ArrayList<>();
    }

    @Override
    public void act() {
        act(0);
    }

    public List<Alien1LaserProjectile> getLasers() {
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