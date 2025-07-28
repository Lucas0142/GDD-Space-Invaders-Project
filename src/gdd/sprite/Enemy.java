package gdd.sprite;

import static gdd.Global.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Sprite {

    private List<Bomb> bombs;
    private int bombCooldown = 0;

    public Enemy(int x, int y) {
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {
        this.x = x;
        this.y = y;
        bombs = new ArrayList<>();

        try {
            BufferedImage enemyImg = ImageIO.read(new File(IMG_ENEMY));
            var scaledImage = enemyImg.getScaledInstance(enemyImg.getWidth() * SCALE_FACTOR,
                    enemyImg.getHeight() * SCALE_FACTOR,
                    java.awt.Image.SCALE_SMOOTH);
            setImage(scaledImage);
        } catch (Exception e) {
            initFallbackEnemy();
        }
    }

    private void initFallbackEnemy() {
        var ii = new ImageIcon(IMG_ENEMY);
        var scaledImage = ii.getImage().getScaledInstance(
                ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act(int direction) {
        this.x += direction;

        if (bombCooldown > 0) {
            bombCooldown--;
        }
    }

    @Override
    public void act() {
        act(1);
    }

    public void tryCreateBomb() {
        if (bombCooldown <= 0 && isVisible()) {
            Bomb newBomb = new Bomb(x + ALIEN_WIDTH / 2, y + ALIEN_HEIGHT);
            newBomb.setDestroyed(false);
            bombs.add(newBomb);
            bombCooldown = 60;
        }
    }

    public List<Bomb> getBombs() {
        bombs.removeIf(bomb -> {
            if (!bomb.isDestroyed()) {
                bomb.act();
                return false;
            }
            return true;
        });

        return bombs;
    }

    @Override
    public void die() {
        super.die();
    }
}