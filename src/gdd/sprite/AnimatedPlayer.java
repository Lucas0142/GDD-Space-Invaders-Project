package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AnimatedPlayer extends Player {

    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private int frameCounter = 0;
    private int animationSpeed = 10;

    private static final int FRAMES_PER_ROW = 4;
    private static final int TOTAL_FRAMES = 8;
    private int spriteWidth;
    private int spriteHeight;

    public AnimatedPlayer() {
        super();
        initAnimatedPlayer();
    }

    private void initAnimatedPlayer() {
        File imageFile = new File("src/images/player_spritesheet.png");
        if (imageFile.exists()) {
            try {
                spriteSheet = ImageIO.read(imageFile);
                spriteWidth = spriteSheet.getWidth() / FRAMES_PER_ROW;
                spriteHeight = spriteSheet.getHeight() / 2;
                extractFrames();

                if (frames != null && frames.length > 0) {
                    int playerWidth = 32 * SCALE_FACTOR;
                    int playerHeight = 24 * SCALE_FACTOR;

                    try {
                        var scaledImage = frames[0].getScaledInstance(
                                playerWidth, playerHeight,
                                java.awt.Image.SCALE_FAST
                        );
                        setImage(scaledImage);
                    } catch (Exception e) {
                        initFallbackPlayer();
                    }
                }
            } catch (IOException e) {
                initFallbackPlayer();
            }
        } else {
            initFallbackPlayer();
        }
    }

    private void extractFrames() {
        try {
            frames = new BufferedImage[TOTAL_FRAMES];

            for (int i = 0; i < TOTAL_FRAMES; i++) {
                int row = i / FRAMES_PER_ROW;
                int col = i % FRAMES_PER_ROW;
                int x = col * spriteWidth;
                int y = row * spriteHeight;

                if (x + spriteWidth <= spriteSheet.getWidth() && y + spriteHeight <= spriteSheet.getHeight()) {
                    frames[i] = spriteSheet.getSubimage(x, y, spriteWidth, spriteHeight);
                } else {
                    frames[i] = frames[0];
                }
            }
        } catch (Exception e) {
            frames = null;
            initFallbackPlayer();
        }
    }

    private void initFallbackPlayer() {
        try {
            var ii = new javax.swing.ImageIcon(IMG_PLAYER);
            int playerWidth = 32 * SCALE_FACTOR;
            int playerHeight = 24 * SCALE_FACTOR;
            var scaledImage = ii.getImage().getScaledInstance(
                    playerWidth, playerHeight,
                    java.awt.Image.SCALE_FAST
            );
            setImage(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading player image: " + e.getMessage());
        }
    }

    @Override
    public void act() {
        super.act();
        updateAnimation();
    }

    private void updateAnimation() {
        if (frames != null && frames.length > 0) {
            frameCounter++;

            if (frameCounter >= animationSpeed) {
                try {
                    currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
                    int playerWidth = 32 * SCALE_FACTOR;
                    int playerHeight = 24 * SCALE_FACTOR;
                    var scaledImage = frames[currentFrame].getScaledInstance(
                            playerWidth, playerHeight,
                            java.awt.Image.SCALE_FAST
                    );
                    setImage(scaledImage);
                    frameCounter = 0;
                } catch (Exception e) {
                    frameCounter = 0;
                }
            }
        }
    }

    public int getVisualX() {
        return getX();
    }

    public void setAnimationSpeed(int speed) {
        this.animationSpeed = Math.max(1, speed);
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}