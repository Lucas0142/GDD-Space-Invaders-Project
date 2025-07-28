package gdd.sprite;

import static gdd.Global.*;
import java.io.File;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {

    private int frameCounter = 0;
    private int zigzagDirection = 1;

    public Alien2(int x, int y) {
        super(x, y);

        var ii = new ImageIcon(IMG_ENEMY2);

        if (!new File(IMG_ENEMY2).exists()) {
            ii = new ImageIcon(IMG_ENEMY);
        }

        var scaledImage = ii.getImage().getScaledInstance(
                ALIEN_WIDTH,
                ALIEN_HEIGHT,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        super.act(0);
        this.y += 1;

        frameCounter++;
        if (frameCounter >= 30) {
            zigzagDirection *= -1;
            frameCounter = 0;
        }

        this.x += zigzagDirection * 1;

        if (this.x <= BORDER_LEFT) {
            this.x = BORDER_LEFT;
            zigzagDirection = 1;
        }
        if (this.x >= BOARD_WIDTH - BORDER_RIGHT - ALIEN_WIDTH) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - ALIEN_WIDTH;
            zigzagDirection = -1;
        }

        if (y > BOARD_HEIGHT) {
            die();
        }
    }

    @Override
    public void act() {
        act(0);
    }
}