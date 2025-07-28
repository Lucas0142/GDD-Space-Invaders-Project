package gdd.sprite;

import static gdd.Global.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Alien2LaserProjectile extends Sprite {

    private boolean destroyed = true;
    private static final int LASER_SPEED = 5;

    public Alien2LaserProjectile(int x, int y) {
        initLaser(x, y);
    }

    private void initLaser(int x, int y) {
        setDestroyed(true);
        this.x = x;
        this.y = y;

        File chargeFile = new File("src/images/Fighter_Charge2.png");
        if (chargeFile.exists()) {
            try {
                BufferedImage chargeImage = ImageIO.read(chargeFile);
                int laserWidth = (int)(chargeImage.getWidth() * SCALE_FACTOR * 0.2);
                int laserHeight = (int)(chargeImage.getHeight() * SCALE_FACTOR * 0.4);
                var scaledImage = chargeImage.getScaledInstance(laserWidth, laserHeight, java.awt.Image.SCALE_SMOOTH);
                setImage(scaledImage);
            } catch (Exception e) {
                initFallbackLaser();
            }
        } else {
            initFallbackLaser();
        }
    }

    private void initFallbackLaser() {
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
            y += LASER_SPEED;
            if (y > BOARD_HEIGHT + 50) {
                setDestroyed(true);
            }
        }
    }

    public int getCenterX() {
        if (getImage() != null) {
            return x + getImage().getWidth(null) / 2;
        }
        return x + 3;
    }

    public int getCenterY() {
        if (getImage() != null) {
            return y + getImage().getHeight(null) / 2;
        }
        return y + 3;
    }

    public int getLaserWidth() {
        if (getImage() != null) {
            return getImage().getWidth(null);
        }
        return 2;
    }

    public int getLaserHeight() {
        if (getImage() != null) {
            return getImage().getHeight(null);
        }
        return 6;
    }
}