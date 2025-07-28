package gdd.sprite;

import static gdd.Global.*;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class LaserShot extends Shot {

    public LaserShot() {
        super();
    }

    public LaserShot(int x, int y) {
        super();
        int playerWidth = 32 * SCALE_FACTOR;
        int laserWidth = 16 * SCALE_FACTOR;
        int centeredX = x + (playerWidth / 2) - (laserWidth / 2) + 1;
        setX(centeredX);
        setY(y - 1);
        initLaserImage();
    }

    private void initLaserImage() {
        File laserFile = new File("src/images/laser_green.png");
        if (laserFile.exists()) {
            try {
                var laserImage = ImageIO.read(laserFile);
                int laserWidth = 16 * SCALE_FACTOR;
                int laserHeight = 32 * SCALE_FACTOR;
                var scaledImage = laserImage.getScaledInstance(
                        laserWidth, laserHeight,
                        java.awt.Image.SCALE_SMOOTH
                );
                setImage(scaledImage);
            } catch (Exception e) {
                initFallbackLaser();
            }
        } else {
            initFallbackLaser();
        }
    }

    private void initFallbackLaser() {
        var ii = new ImageIcon(IMG_SHOT);
        var scaledImage = ii.getImage().getScaledInstance(
                ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH
        );
        setImage(scaledImage);
    }

    @Override
    public void act() {
        int currentY = getY();
        currentY -= 20;
        setY(currentY);

        if (currentY < 0) {
            die();
        }
    }

    public int getCollisionX() {
        return getX() + (8 * SCALE_FACTOR);
    }
}