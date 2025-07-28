package gdd.sprite;

import static gdd.Global.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Bomb extends Sprite {

    private boolean destroyed;
    private static final int BOMB_SPEED = 1;

    public Bomb(int x, int y) {
        initBomb(x, y);
    }

    private void initBomb(int x, int y) {
        setDestroyed(true);
        this.x = x;
        this.y = y;

        File bombFile = new File("src/images/bomb.png");
        if (bombFile.exists()) {
            try {
                BufferedImage bombImage = ImageIO.read(bombFile);
                int bombSize = (int)(bombImage.getWidth() * SCALE_FACTOR * 0.5);
                var scaledImage = bombImage.getScaledInstance(bombSize, bombSize, java.awt.Image.SCALE_SMOOTH);
                setImage(scaledImage);
            } catch (Exception e) {
                initFallbackBomb();
            }
        } else {
            initFallbackBomb();
        }
    }

    private void initFallbackBomb() {
        setImage(null);
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void act() {
        if (!destroyed) {
            y += BOMB_SPEED;

            if (y > BOARD_HEIGHT) {
                setDestroyed(true);
            }
        }
    }
}