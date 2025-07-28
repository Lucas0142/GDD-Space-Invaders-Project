package gdd.sprite;

import static gdd.Global.*;
import java.io.File;
import javax.swing.ImageIcon;

public class Boss extends Enemy {

    private int health;
    private int maxHealth;
    private int phase = 1;
    private int moveCounter = 0;
    private int attackTimer = 0;
    private int direction = 1;
    private boolean isEnraged = false;

    public Boss(int x, int y) {
        super(x, y);
        initBoss();
    }

    private void initBoss() {
        this.maxHealth = BOSS_HEALTH;
        this.health = maxHealth;

        var ii = new ImageIcon(IMG_ENEMY);

        if (new File(IMG_ENEMY2).exists()) {
            ii = new ImageIcon(IMG_ENEMY2);
        }

        var scaledImage = ii.getImage().getScaledInstance(
                BOSS_WIDTH,
                BOSS_HEIGHT,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        super.act(0);
        moveCounter++;
        attackTimer++;

        if (health <= maxHealth / 2 && !isEnraged) {
            enterPhase2();
        }

        if (phase == 1) {
            if (moveCounter % 3 == 0) {
                this.x += this.direction * 2;
            }
        } else {
            if (moveCounter % 2 == 0) {
                this.x += this.direction * 3;
            }
        }

        if (this.x <= BORDER_LEFT) {
            this.x = BORDER_LEFT;
            this.direction = 1;
        }
        if (this.x >= BOARD_WIDTH - BORDER_RIGHT - BOSS_WIDTH) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - BOSS_WIDTH;
            this.direction = -1;
        }

        handleAttackPatterns();
    }

    private void enterPhase2() {
        isEnraged = true;
        phase = 2;
    }

    private void handleAttackPatterns() {
        if (phase == 1) {
            if (attackTimer >= 180) {
                createSingleBomb();
                attackTimer = 0;
            }
        } else {
            if (attackTimer >= 90) {
                createSpreadBombs();
                attackTimer = 0;
            }
        }
    }

    private void createSingleBomb() {
        int centerX = this.x + BOSS_WIDTH / 2;
        int bombY = this.y + BOSS_HEIGHT;

        Bomb newBomb = new Bomb(centerX, bombY);
        newBomb.setDestroyed(false);
        getBombs().add(newBomb);
    }

    private void createSpreadBombs() {
        int centerX = this.x + BOSS_WIDTH / 2;
        int bombY = this.y + BOSS_HEIGHT;

        getBombs().add(new SpreadBomb(centerX - 30, bombY, -1));
        getBombs().add(new SpreadBomb(centerX, bombY, 0));
        getBombs().add(new SpreadBomb(centerX + 30, bombY, 1));
    }

    @Override
    public void act() {
        act(0);
    }

    public boolean takeDamage() {
        health--;

        if (health <= 0) {
            setDying(true);
            return true;
        }
        return false;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getPhase() {
        return phase;
    }

    public boolean isEnraged() {
        return isEnraged;
    }

    private class SpreadBomb extends Bomb {
        private int spreadDirection;

        public SpreadBomb(int x, int y, int direction) {
            super(x, y);
            this.spreadDirection = direction;
            setDestroyed(false);
        }

        @Override
        public void act() {
            if (!isDestroyed()) {
                y += 2;
                x += spreadDirection;

                if (y > BOARD_HEIGHT || x < 0 || x > BOARD_WIDTH) {
                    setDestroyed(true);
                }
            }
        }
    }
}