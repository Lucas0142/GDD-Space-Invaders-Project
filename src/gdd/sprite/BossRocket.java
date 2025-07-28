package gdd.sprite;

import static gdd.Global.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class BossRocket extends Sprite {

    private boolean destroyed = true;
    private static final int ROCKET_SPEED = 2;

    private BufferedImage chargeSheet;
    private BufferedImage[] chargeFrames;
    private int currentFrame = 0;
    private int frameCounter = 0;
    private int animationSpeed = 10;

    public BossRocket(int x, int y) {
        initRocket(x, y);
    }

    private void initRocket(int x, int y) {
        setDestroyed(true);
        this.x = x;
        this.y = y;
        loadRocketAnimation();
    }

    private void loadRocketAnimation() {
        File chargeFile = new File("src/images/Corvette_Charge3.png");
        if (chargeFile.exists()) {
            try {
                chargeSheet = ImageIO.read(chargeFile);
                extractChargeFrames();

                if (chargeFrames != null && chargeFrames.length > 0) {
                    int rocketWidth = (int)(chargeFrames[0].getWidth() * SCALE_FACTOR * 0.6);
                    int rocketHeight = (int)(chargeFrames[0].getHeight() * SCALE_FACTOR * 0.6);
                    var scaledImage = chargeFrames[0].getScaledInstance(
                            rocketWidth, rocketHeight, java.awt.Image.SCALE_SMOOTH
                    );
                    setImage(scaledImage);
                }
            } catch (Exception e) {
                initFallbackRocket();
            }
        } else {
            initFallbackRocket();
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

    private void initFallbackRocket() {
        setImage(null);
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
        this.visible = !destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void act() {
        if (!destroyed) {
            y += ROCKET_SPEED;
            updateAnimation();

            if (y > BOARD_HEIGHT + 50) {
                setDestroyed(true);
            }
        }
    }

    private void updateAnimation() {
        if (chargeFrames == null || chargeFrames.length == 0) return;

        frameCounter++;

        if (frameCounter >= animationSpeed) {
            currentFrame = (currentFrame + 1) % chargeFrames.length;

            int rocketWidth = (int)(chargeFrames[0].getWidth() * SCALE_FACTOR * 0.6);
            int rocketHeight = (int)(chargeFrames[0].getHeight() * SCALE_FACTOR * 0.6);
            var scaledImage = chargeFrames[currentFrame].getScaledInstance(
                    rocketWidth, rocketHeight, java.awt.Image.SCALE_SMOOTH
            );
            setImage(scaledImage);
            frameCounter = 0;
        }
    }

    public int getRocketWidth() {
        if (getImage() != null) {
            return getImage().getWidth(null);
        }
        return 8;
    }

    public int getRocketHeight() {
        if (getImage() != null) {
            return getImage().getHeight(null);
        }
        return 12;
    }

    public int getCenterX() {
        return x + getRocketWidth() / 2;
    }

    public int getCenterY() {
        return y + getRocketHeight() / 2;
    }
}