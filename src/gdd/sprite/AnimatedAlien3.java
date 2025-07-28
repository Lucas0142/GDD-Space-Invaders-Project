package gdd.sprite;

import static gdd.Global.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class AnimatedAlien3 extends Enemy {

    private BufferedImage spriteSheet;
    private BufferedImage[] frames;

    private int currentFrame = 0;
    private int frameCounter = 0;
    private int animationSpeed = 15;

    private int bombCooldown = 0;
    private List<BombshipBomb> alienBombs;

    public AnimatedAlien3(int x, int y) {
        super(x, y);
        this.alienBombs = new ArrayList<>();
        initAnimatedAlien3();
    }

    private void initAnimatedAlien3() {
        File spriteFile = new File("src/images/Bombship.png");

        if (spriteFile.exists()) {
            try {
                spriteSheet = ImageIO.read(spriteFile);
                extractFrames();

                if (frames != null && frames.length > 0) {
                    int alien3Width = ALIEN_WIDTH * 3;
                    int alien3Height = ALIEN_HEIGHT * 3;
                    var scaledImage = frames[0].getScaledInstance(
                            alien3Width, alien3Height,
                            java.awt.Image.SCALE_SMOOTH
                    );
                    setImage(scaledImage);
                }
            } catch (IOException e) {
                initFallbackAlien3();
            }
        } else {
            initFallbackAlien3();
        }
    }

    private void extractFrames() {
        if (spriteSheet == null) return;

        frames = new BufferedImage[3];
        int frameWidth = spriteSheet.getWidth() / 3;
        int frameHeight = spriteSheet.getHeight();

        for (int i = 0; i < 3; i++) {
            int x = i * frameWidth;
            if (x + frameWidth <= spriteSheet.getWidth()) {
                frames[i] = spriteSheet.getSubimage(x, 0, frameWidth, frameHeight);
            }
        }
    }

    private void initFallbackAlien3() {
        var ii = new javax.swing.ImageIcon(IMG_ENEMY);
        int alien3Width = ALIEN_WIDTH * 3;
        int alien3Height = ALIEN_HEIGHT * 3;
        var scaledImage = ii.getImage().getScaledInstance(
                alien3Width, alien3Height,
                java.awt.Image.SCALE_SMOOTH
        );
        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        super.act(0);
        updateAnimation();
        updateMovement();
        updateBombs();
    }

    private void updateAnimation() {
        if (frames == null || frames.length == 0) return;

        frameCounter++;

        if (frameCounter >= animationSpeed) {
            currentFrame = (currentFrame + 1) % frames.length;
            int alien3Width = ALIEN_WIDTH * 3;
            int alien3Height = ALIEN_HEIGHT * 3;
            var scaledImage = frames[currentFrame].getScaledInstance(
                    alien3Width, alien3Height,
                    java.awt.Image.SCALE_SMOOTH
            );
            setImage(scaledImage);
            frameCounter = 0;
        }

        if (bombCooldown > 0) {
            bombCooldown--;
        }
    }

    private void updateMovement() {
        this.y += 1;

        if (y > BOARD_HEIGHT) {
            die();
        }
    }

    private void updateBombs() {
        alienBombs.removeIf(bomb -> {
            if (!bomb.isDestroyed()) {
                bomb.act();
                return false;
            }
            return true;
        });
    }

    @Override
    public void tryCreateBomb() {
        if (bombCooldown <= 0 && isVisible()) {
            int alien3Width = ALIEN_WIDTH * 3;
            int alien3Height = ALIEN_HEIGHT * 3;
            int centerX = this.x + alien3Width / 2;
            int bombY = this.y + alien3Height;

            BombshipBomb newBomb = new BombshipBomb(centerX - 4, bombY);
            newBomb.setDestroyed(false);
            alienBombs.add(newBomb);
            bombCooldown = 120;
        }
    }

    @Override
    public List<Bomb> getBombs() {
        List<Bomb> allBombs = new ArrayList<>(super.getBombs());
        allBombs.addAll(alienBombs);
        return allBombs;
    }

    public List<BombshipBomb> getAlienBombs() {
        return alienBombs;
    }

    @Override
    public void act() {
        act(0);
    }

    @Override
    public void die() {
        super.die();
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setAnimationSpeed(int speed) {
        this.animationSpeed = Math.max(1, speed);
    }

    public static class BombshipBomb extends Bomb {
        private BufferedImage chargeSheet;
        private BufferedImage[] chargeFrames;
        private int currentFrame = 0;
        private int frameCounter = 0;
        private int animationSpeed = 8;

        public BombshipBomb(int x, int y) {
            super(x, y);
            loadBombAnimation();
        }

        private void loadBombAnimation() {
            File chargeFile = new File("src/images/Corvette_Charge3.png");
            if (chargeFile.exists()) {
                try {
                    chargeSheet = ImageIO.read(chargeFile);
                    extractChargeFrames();

                    if (chargeFrames != null && chargeFrames.length > 0) {
                        int bombWidth = (int)(chargeFrames[0].getWidth() * SCALE_FACTOR * 0.4);
                        int bombHeight = (int)(chargeFrames[0].getHeight() * SCALE_FACTOR * 0.4);
                        var scaledImage = chargeFrames[0].getScaledInstance(
                                bombWidth, bombHeight, java.awt.Image.SCALE_SMOOTH
                        );
                        setImage(scaledImage);
                    }
                } catch (Exception e) {
                    setImage(null);
                }
            } else {
                setImage(null);
            }
        }

        private void extractChargeFrames() {
            if (chargeSheet == null) return;

            chargeFrames = new BufferedImage[3];
            int frameWidth = chargeSheet.getWidth();
            int frameHeight = chargeSheet.getHeight() / 3;

            for (int i = 0; i < 3; i++) {
                int y = i * frameHeight;
                if (y + frameHeight <= chargeSheet.getHeight()) {
                    chargeFrames[i] = chargeSheet.getSubimage(0, y, frameWidth, frameHeight);
                }
            }
        }

        @Override
        public void act() {
            if (!isDestroyed()) {
                y += 5;
                updateBombAnimation();

                if (y > BOARD_HEIGHT) {
                    setDestroyed(true);
                }
            }
        }

        private void updateBombAnimation() {
            if (chargeFrames == null || chargeFrames.length == 0) return;

            frameCounter++;

            if (frameCounter >= animationSpeed) {
                currentFrame = (currentFrame + 1) % chargeFrames.length;

                int bombWidth = (int)(chargeFrames[0].getWidth() * SCALE_FACTOR * 0.4);
                int bombHeight = (int)(chargeFrames[0].getHeight() * SCALE_FACTOR * 0.4);
                var scaledImage = chargeFrames[currentFrame].getScaledInstance(
                        bombWidth, bombHeight, java.awt.Image.SCALE_SMOOTH
                );
                setImage(scaledImage);
                frameCounter = 0;
            }
        }
    }
}