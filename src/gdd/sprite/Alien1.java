package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien1 extends Enemy {

    public Alien1(int x, int y) {
        super(x, y);
        initAlien1();
    }

    private void initAlien1() {
        var ii = new ImageIcon(IMG_ENEMY);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act(int direction) {
        super.act(0);
        this.y += 1;

        if (y > BOARD_HEIGHT) {
            die();
        }
    }

    @Override
    public void act() {
        act(0);
    }
}