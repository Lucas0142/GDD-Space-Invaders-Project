package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Explosion extends Sprite {

    public Explosion(int x, int y) {
        initExplosion(x, y);
    }

    private void initExplosion(int x, int y) {
        this.x = x;
        this.y = y;

        var ii = new ImageIcon(IMG_EXPLOSION);

        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act() {
        // Explosions don't move, they just fade out
        // The visibleCountDown() is handled in Scene1
    }
}