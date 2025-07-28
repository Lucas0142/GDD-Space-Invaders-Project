package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
        initMultiShot();
    }

    private void initMultiShot() {
        ImageIcon ii = new ImageIcon(IMG_POWERUP_MULTISHOT);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth(),
                ii.getIconHeight(),
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act() {
        super.act();
    }

    @Override
    public void upgrade(Player player) {
        int currentLevel = player.getShotLevel();
        if (currentLevel < MAX_SHOT_LEVEL) {
            player.setShotLevel(currentLevel + 1);
        }

        this.die();
    }
}