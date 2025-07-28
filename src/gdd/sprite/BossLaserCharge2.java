package gdd.sprite;

import static gdd.Global.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class BossLaserCharge2 extends Alien2LaserProjectile {

    private static final int BOSS_LASER_SPEED = 4;

    public BossLaserCharge2(int x, int y) {
        super(x, y);
        initBossLaser();
    }

    private void initBossLaser() {
        File chargeFile = new File("src/images/Corvette_Charge2.png");
        if (chargeFile.exists()) {
            try {
                BufferedImage chargeImage = ImageIO.read(chargeFile);
                int laserWidth = (int)(chargeImage.getWidth() * SCALE_FACTOR * 0.3);
                int laserHeight = (int)(chargeImage.getHeight() * SCALE_FACTOR * 0.5);
                var scaledImage = chargeImage.getScaledInstance(laserWidth, laserHeight, java.awt.Image.SCALE_SMOOTH);
                setImage(scaledImage);
            } catch (Exception e) {
                initFallbackBossLaser();
            }
        } else {
            initFallbackBossLaser();
        }
    }

    private void initFallbackBossLaser() {
        setImage(null);
    }

    @Override
    public void act() {
        if (!isDestroyed()) {
            y += BOSS_LASER_SPEED;
            if (y > BOARD_HEIGHT + 50) {
                setDestroyed(true);
            }
        }
    }

    @Override
    public int getLaserWidth() {
        if (getImage() != null) {
            return getImage().getWidth(null);
        }
        return 3;
    }

    @Override
    public int getLaserHeight() {
        if (getImage() != null) {
            return getImage().getHeight(null);
        }
        return 6;
    }
}