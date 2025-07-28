package gdd.sprite;

import static gdd.Global.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class AnimatedBoss extends Enemy {

    private static final int STATE_MOVE = 0;
    private static final int STATE_ATTACK1 = 1;
    private static final int STATE_ATTACK2 = 2;
    private static final int STATE_ROCKET = 3;
    private static final int STATE_BOOST = 4;
    private static final int STATE_SHIELD = 5;
    private static final int STATE_DESTROYED = 6;

    private BufferedImage moveSheet, attack1Sheet, attack2Sheet, rocketSheet;
    private BufferedImage boostSheet, shieldSheet, destroyedSheet;

    private BufferedImage[] moveFrames;
    private BufferedImage[] attack1Frames;
    private BufferedImage[] attack2Frames;
    private BufferedImage[] rocketFrames;
    private BufferedImage[] boostFrames;
    private BufferedImage[] shieldFrames;
    private BufferedImage[] destroyedFrames;

    private int currentState = STATE_MOVE;
    private int currentFrame = 0;
    private int frameCounter = 0;
    private int animationSpeed = 10;

    private int health;
    private int maxHealth;
    private boolean isEnraged = false;
    private boolean isInvulnerable = false;

    private int stateTimer = 0;
    private int stateDuration = 60;
    private int shieldTimer = 0;
    private int shieldCooldown = 240;
    private int shieldLastUsed = 0;

    private int phaseStep = 0;
    private boolean hasAttackedInCurrentState = false;

    private List<BossLaserCharge1> attack1Lasers;
    private List<BossLaserCharge2> attack2Lasers;
    private List<BossRocket> rockets;

    private int moveCounter = 0;
    private int direction = 1;
    private int baseY;

    public AnimatedBoss(int x, int y) {
        super(x, y);
        this.baseY = y;
        this.health = BOSS_HEALTH;
        this.maxHealth = BOSS_HEALTH;
        this.attack1Lasers = new ArrayList<>();
        this.attack2Lasers = new ArrayList<>();
        this.rockets = new ArrayList<>();
        initAnimatedBoss();
    }

    private void initAnimatedBoss() {
        File moveFile = new File("src/images/Corvette_Move.png");
        File attack1File = new File("src/images/Corvette_Attack1.png");
        File attack2File = new File("src/images/Corvette_Attack2.png");
        File rocketFile = new File("src/images/Corvette_Rocket.png");
        File boostFile = new File("src/images/Corvette_Boost.png");
        File shieldFile = new File("src/images/Corvette_Shield.png");
        File destroyedFile = new File("src/images/Corvette_Destroyed.png");

        if (moveFile.exists() && attack1File.exists() && attack2File.exists() &&
                rocketFile.exists() && boostFile.exists() && shieldFile.exists() && destroyedFile.exists()) {

            try {
                moveSheet = ImageIO.read(moveFile);
                attack1Sheet = ImageIO.read(attack1File);
                attack2Sheet = ImageIO.read(attack2File);
                rocketSheet = ImageIO.read(rocketFile);
                boostSheet = ImageIO.read(boostFile);
                shieldSheet = ImageIO.read(shieldFile);
                destroyedSheet = ImageIO.read(destroyedFile);

                extractFrames();

                if (moveFrames != null && moveFrames.length > 0) {
                    int bossWidth = ALIEN_WIDTH * 3;
                    int bossHeight = ALIEN_HEIGHT * 3;
                    var scaledImage = moveFrames[0].getScaledInstance(
                            bossWidth, bossHeight, java.awt.Image.SCALE_SMOOTH
                    );
                    setImage(scaledImage);
                }
            } catch (IOException e) {
                initFallbackBoss();
            }
        } else {
            initFallbackBoss();
        }
    }

    private void extractFrames() {
        moveFrames = extractVerticalFrames(moveSheet, 6);
        attack1Frames = extractVerticalFrames(attack1Sheet, 4);
        attack2Frames = extractVerticalFrames(attack2Sheet, 4);
        rocketFrames = extractVerticalFrames(rocketSheet, 7);
        boostFrames = extractVerticalFrames(boostSheet, 5);
        shieldFrames = extractVerticalFrames(shieldSheet, 6);
        destroyedFrames = extractVerticalFrames(destroyedSheet, 21);
    }

    private BufferedImage[] extractVerticalFrames(BufferedImage sheet, int frameCount) {
        if (sheet == null) return null;

        BufferedImage[] frames = new BufferedImage[frameCount];
        int frameWidth = sheet.getWidth();
        int frameHeight = sheet.getHeight() / frameCount;

        for (int i = 0; i < frameCount; i++) {
            int y = i * frameHeight;
            if (y + frameHeight <= sheet.getHeight()) {
                frames[i] = sheet.getSubimage(0, y, frameWidth, frameHeight);
            }
        }
        return frames;
    }

    private void initFallbackBoss() {
        var ii = new javax.swing.ImageIcon(IMG_ENEMY);
        int bossWidth = ALIEN_WIDTH * 3;
        int bossHeight = ALIEN_HEIGHT * 3;
        var scaledImage = ii.getImage().getScaledInstance(
                bossWidth, bossHeight, java.awt.Image.SCALE_SMOOTH
        );
        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        super.act(0);
        updateAnimation();
        updatePhaseLogic();
        updateMovement();
        updateProjectiles();
        updateShieldLogic();
    }

    private void updateAnimation() {
        BufferedImage[] currentFrames = getCurrentFrames();
        if (currentFrames == null) return;

        frameCounter++;

        if (frameCounter >= animationSpeed) {
            currentFrame++;

            if (currentState == STATE_DESTROYED) {
                if (currentFrame >= currentFrames.length) {
                    setDying(true);
                    return;
                }
            } else {
                if (currentFrame >= currentFrames.length) {
                    currentFrame = 0;
                }
            }

            int bossWidth = ALIEN_WIDTH * 3;
            int bossHeight = ALIEN_HEIGHT * 3;
            var scaledImage = currentFrames[currentFrame].getScaledInstance(
                    bossWidth, bossHeight, java.awt.Image.SCALE_SMOOTH
            );
            setImage(scaledImage);
            frameCounter = 0;
        }
    }

    private BufferedImage[] getCurrentFrames() {
        switch (currentState) {
            case STATE_MOVE: return moveFrames;
            case STATE_ATTACK1: return attack1Frames;
            case STATE_ATTACK2: return attack2Frames;
            case STATE_ROCKET: return rocketFrames;
            case STATE_BOOST: return boostFrames;
            case STATE_SHIELD: return shieldFrames;
            case STATE_DESTROYED: return destroyedFrames;
            default: return moveFrames;
        }
    }

    private void updatePhaseLogic() {
        if (currentState == STATE_DESTROYED) return;

        stateTimer++;

        if (currentFrame == 2 && !hasAttackedInCurrentState) {
            switch (currentState) {
                case STATE_ATTACK1:
                    fireWideAttack();
                    hasAttackedInCurrentState = true;
                    break;
                case STATE_ATTACK2:
                    fireCloseAttack();
                    hasAttackedInCurrentState = true;
                    break;
                case STATE_ROCKET:
                    fireRocketAttack();
                    hasAttackedInCurrentState = true;
                    break;
            }
        }

        if (stateTimer >= stateDuration) {
            stateTimer = 0;
            currentFrame = 0;
            hasAttackedInCurrentState = false;
            transitionToNextState();
        }
    }

    private void transitionToNextState() {
        double healthPercent = (double) health / maxHealth;

        if (healthPercent > 0.75) {
            if (currentState == STATE_ATTACK1) {
                currentState = STATE_MOVE;
            } else {
                currentState = STATE_ATTACK1;
            }
        } else if (healthPercent > 0.50) {
            switch (phaseStep % 4) {
                case 0: currentState = STATE_ATTACK2; break;
                case 1: currentState = STATE_MOVE; break;
                case 2: currentState = STATE_ATTACK1; break;
                case 3: currentState = STATE_MOVE; break;
            }
            phaseStep++;
        } else {
            if (!isEnraged) {
                isEnraged = true;
            }

            switch (phaseStep % 4) {
                case 0: currentState = STATE_ATTACK1; break;
                case 1: currentState = STATE_ATTACK2; break;
                case 2: currentState = STATE_ROCKET; break;
                case 3: currentState = STATE_BOOST; break;
            }
            phaseStep++;
        }
    }

    private void updateMovement() {
        moveCounter++;
        double healthPercent = (double) health / maxHealth;
        int bossWidth = ALIEN_WIDTH * 3;

        if (healthPercent > 0.75) {
            if (moveCounter % 3 == 0) {
                this.x += this.direction * 2;
            }
            this.y = baseY;

            if (this.x <= BORDER_LEFT + 10) {
                this.x = BORDER_LEFT + 10;
                this.direction = 1;
            }
            if (this.x >= BOARD_WIDTH - BORDER_RIGHT - bossWidth - 10) {
                this.x = BOARD_WIDTH - BORDER_RIGHT - bossWidth - 10;
                this.direction = -1;
            }

        } else if (healthPercent > 0.50) {
            int centerX = BOARD_WIDTH / 2;
            int centerY = baseY + 60;
            int radiusX = 180;
            int radiusY = 40;
            double angle = (moveCounter * Math.PI) / 90;
            this.x = centerX + (int)(Math.cos(angle) * radiusX) - bossWidth / 2;
            this.y = centerY + (int)(Math.sin(angle * 2) * radiusY);

        } else if (healthPercent > 0.25) {
            int attackCycle = moveCounter % 360;
            if (attackCycle < 180) {
                int targetX = BOARD_WIDTH / 2;
                int targetY = baseY + 150;
                this.x += (targetX - this.x) / 25;
                this.y += (targetY - this.y) / 15;
            } else {
                int centerX = BOARD_WIDTH / 2;
                int retreatY = baseY + 20;
                int spiralRadius = 100 + (attackCycle - 180);
                double spiralAngle = (attackCycle - 180) * Math.PI / 30;
                this.x = centerX + (int)(Math.cos(spiralAngle) * spiralRadius) - bossWidth / 2;
                this.y = retreatY + (int)(Math.sin(spiralAngle) * 30);
            }

        } else {
            if (moveCounter % 90 == 0) {
                this.x = 80 + (int)(Math.random() * (BOARD_WIDTH - 160 - bossWidth));
                this.y = 30 + (int)(Math.random() * 120);
            }
            if (moveCounter % 10 == 0) {
                this.x += (int)(Math.random() * 6) - 3;
                this.y += (int)(Math.random() * 4) - 2;
            }
        }

        if (this.x <= BORDER_LEFT) {
            this.x = BORDER_LEFT;
        }
        if (this.x >= BOARD_WIDTH - BORDER_RIGHT - bossWidth) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - bossWidth;
        }
        if (this.y <= 20) {
            this.y = 20;
        }
        if (this.y >= BOARD_HEIGHT / 2) {
            this.y = BOARD_HEIGHT / 2;
        }
    }

    private void fireWideAttack() {
        int bossWidth = ALIEN_WIDTH * 3;
        int centerX = this.x + bossWidth / 2;
        int cannonSpacing = bossWidth / 4;
        int laserY = this.y + ALIEN_HEIGHT * 3;
        int leftCannonX = centerX - cannonSpacing / 2 - 3;
        int rightCannonX = centerX + cannonSpacing / 2;

        BossLaserCharge1 leftLaser = new BossLaserCharge1(leftCannonX, laserY);
        leftLaser.setDestroyed(false);
        attack1Lasers.add(leftLaser);

        BossLaserCharge1 rightLaser = new BossLaserCharge1(rightCannonX, laserY);
        rightLaser.setDestroyed(false);
        attack1Lasers.add(rightLaser);
    }

    private void fireCloseAttack() {
        int bossWidth = ALIEN_WIDTH * 3;
        int centerX = this.x + bossWidth / 2;
        int laserY = this.y + ALIEN_HEIGHT * 3;

        BossLaserCharge2 leftLaser = new BossLaserCharge2(centerX - 2, laserY);
        leftLaser.setDestroyed(false);
        attack2Lasers.add(leftLaser);

        BossLaserCharge2 rightLaser = new BossLaserCharge2(centerX + 2, laserY);
        rightLaser.setDestroyed(false);
        attack2Lasers.add(rightLaser);
    }

    private void fireRocketAttack() {
        int bossWidth = ALIEN_WIDTH * 3;
        int centerX = this.x + bossWidth / 2;
        int cannonSpacing = bossWidth / 4;
        int rocketY = this.y + ALIEN_HEIGHT * 3;
        int leftCannonX = centerX - cannonSpacing / 2 - 3;
        int rightCannonX = centerX + cannonSpacing / 2;

        BossRocket leftRocket = new BossRocket(leftCannonX, rocketY);
        leftRocket.setDestroyed(false);
        rockets.add(leftRocket);

        BossRocket rightRocket = new BossRocket(rightCannonX, rocketY);
        rightRocket.setDestroyed(false);
        rockets.add(rightRocket);
    }

    private void updateProjectiles() {
        if (isVisible()) {
            attack1Lasers.removeIf(laser -> {
                if (!laser.isDestroyed()) {
                    laser.act();
                    return false;
                }
                return true;
            });

            attack2Lasers.removeIf(laser -> {
                if (!laser.isDestroyed()) {
                    laser.act();
                    return false;
                }
                return true;
            });

            rockets.removeIf(rocket -> {
                if (!rocket.isDestroyed()) {
                    rocket.act();
                    return false;
                }
                return true;
            });
        } else {
            for (BossLaserCharge1 laser : attack1Lasers) {
                if (!laser.isDestroyed()) {
                    laser.act();
                }
            }
            for (BossLaserCharge2 laser : attack2Lasers) {
                if (!laser.isDestroyed()) {
                    laser.act();
                }
            }
            for (BossRocket rocket : rockets) {
                if (!rocket.isDestroyed()) {
                    rocket.act();
                }
            }
        }
    }

    private void updateShieldLogic() {
        double healthPercent = (double) health / maxHealth;

        if (healthPercent <= 0.25) {
            shieldTimer++;

            if (shieldTimer >= shieldCooldown && currentState != STATE_SHIELD) {
                currentState = STATE_SHIELD;
                currentFrame = 0;
                stateTimer = 0;
                shieldTimer = 0;
                shieldLastUsed = phaseStep;
                isInvulnerable = true;
                hasAttackedInCurrentState = false;
            }
        }

        if (currentState != STATE_SHIELD && isInvulnerable) {
            isInvulnerable = false;
        }
    }

    public boolean takeDamage() {
        if (isInvulnerable) {
            return false;
        }

        health--;

        if (health <= 0) {
            health = 0;
            currentState = STATE_DESTROYED;
            currentFrame = 0;
            stateTimer = 0;
            setDying(true);
            return true;
        }
        return false;
    }

    @Override
    public void act() {
        act(0);
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isEnraged() {
        return isEnraged;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public List<BossLaserCharge1> getAttack1Lasers() {
        return attack1Lasers;
    }

    public List<BossLaserCharge2> getAttack2Lasers() {
        return attack2Lasers;
    }

    public List<BossRocket> getRockets() {
        return rockets;
    }

    public List<Alien2LaserProjectile> getLasers() {
        List<Alien2LaserProjectile> allLasers = new ArrayList<>();
        allLasers.addAll(attack1Lasers);
        allLasers.addAll(attack2Lasers);
        return allLasers;
    }

    @Override
    public List<Bomb> getBombs() {
        return new ArrayList<>();
    }
}