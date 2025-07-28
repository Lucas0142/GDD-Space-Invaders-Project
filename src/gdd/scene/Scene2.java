package gdd.scene;

import gdd.AudioPlayer;
import gdd.BackgroundManager;
import gdd.DataLoader;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.AnimatedAlien1;
import gdd.sprite.AnimatedAlien2;
import gdd.sprite.AnimatedAlien3;
import gdd.sprite.AnimatedPlayer;
import gdd.sprite.Bomb;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Alien2LaserProjectile;
import gdd.sprite.Alien1LaserProjectile;
import gdd.sprite.LaserShot;
import gdd.sprite.Shot;
import gdd.SoundEffect;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene2 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private AnimatedPlayer player;
    private BackgroundManager backgroundManager;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;
    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;
    private int score = 0;

    private boolean inGame = true;
    private boolean levelComplete = false;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private HashMap<Integer, SpawnDetails> spawnMap;
    private AudioPlayer audioPlayer;

    public Scene2(Game game) {
        this.game = game;
        this.backgroundManager = new BackgroundManager();
        loadGameData();
    }

    private void loadGameData() {
        spawnMap = DataLoader.loadSpawns("src/data/level2_spawns.csv");
        DataLoader.printSpawnStats(spawnMap);
    }

    private void initAudio() {
        try {
            String filePath = AUDIO_SCENE2;
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Scene2 audio not found, using Scene1 audio");
            try {
                audioPlayer = new AudioPlayer(AUDIO_SCENE1);
                audioPlayer.play();
            } catch (Exception e2) {
                System.err.println("Error initializing audio player: " + e2.getMessage());
            }
        }
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();

        player = new AnimatedPlayer();
        score = 0;
        levelComplete = false;

        backgroundManager.reset();
    }

    private void drawBackground(Graphics g) {
        backgroundManager.drawBackground(g, true);
        drawScene2OverlayEffects(g);
    }

    private void drawScene2OverlayEffects(Graphics g) {
        Random effectRandom = new Random(frame / 20);

        for (int i = 0; i < 15; i++) {
            int x = effectRandom.nextInt(BOARD_WIDTH);
            int y = effectRandom.nextInt(BOARD_HEIGHT);

            if (effectRandom.nextFloat() > 0.8f) {
                Color[] scene2Colors = {Color.CYAN, Color.MAGENTA, new Color(128, 0, 255), Color.WHITE};
                g.setColor(scene2Colors[effectRandom.nextInt(scene2Colors.length)]);

                int size = effectRandom.nextInt(3) + 1;
                float alpha = 0.4f + effectRandom.nextFloat() * 0.6f;
                g.setColor(new Color(g.getColor().getRed() / 255f, g.getColor().getGreen() / 255f, g.getColor().getBlue() / 255f, alpha));
                g.fillOval(x, y, size, size);
            }
        }
    }

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
            if (enemy.isDying()) {
                enemy.die();
            }
        }
    }

    private void drawPowerUps(Graphics g) {
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }
            if (p.isDying()) {
                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {
        for (Enemy enemy : enemies) {
            if (!(enemy instanceof AnimatedAlien1) && !(enemy instanceof AnimatedAlien2) && !(enemy instanceof AnimatedAlien3)) {
                List<Bomb> bombs = enemy.getBombs();
                for (Bomb bomb : bombs) {
                    if (!bomb.isDestroyed()) {
                        if (bomb.getImage() != null) {
                            g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
                        } else {
                            g.setColor(Color.RED);
                            g.fillRect(bomb.getX(), bomb.getY(), 6, 6);
                            g.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy instanceof AnimatedAlien1) {
                AnimatedAlien1 alien1 = (AnimatedAlien1) enemy;
                List<Alien1LaserProjectile> lasers = alien1.getLasers();
                for (Alien1LaserProjectile laser : lasers) {
                    if (!laser.isDestroyed()) {
                        if (laser.getImage() != null) {
                            g.drawImage(laser.getImage(), laser.getX(), laser.getY(), this);
                        } else {
                            g.setColor(Color.RED);
                            g.fillRect(laser.getX(), laser.getY(), 2, 6);
                            g.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy instanceof AnimatedAlien2) {
                AnimatedAlien2 alien2 = (AnimatedAlien2) enemy;
                List<Alien2LaserProjectile> lasers = alien2.getLasers();
                for (Alien2LaserProjectile laser : lasers) {
                    if (!laser.isDestroyed()) {
                        if (laser.getImage() != null) {
                            g.drawImage(laser.getImage(), laser.getX(), laser.getY(), this);
                        } else {
                            g.setColor(Color.CYAN);
                            g.fillRect(laser.getX(), laser.getY(), 2, 6);
                            g.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy instanceof AnimatedAlien3) {
                AnimatedAlien3 alien3 = (AnimatedAlien3) enemy;
                List<AnimatedAlien3.BombshipBomb> bombs = alien3.getAlienBombs();
                for (AnimatedAlien3.BombshipBomb bomb : bombs) {
                    if (!bomb.isDestroyed()) {
                        if (bomb.getImage() != null) {
                            g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
                        } else {
                            g.setColor(Color.ORANGE);
                            g.fillRect(bomb.getX(), bomb.getY(), 8, 8);
                            g.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }
        explosions.removeAll(toRemove);
    }

    private void drawHUD(Graphics g) {
        int hudY = BOARD_HEIGHT - 30;

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Score: " + score, 20, hudY);
        g.drawString("Speed: " + player.getSpeedLevel() + "/" + MAX_SPEED_LEVEL, 180, hudY);
        g.drawString("Shot: " + player.getShotLevel() + "/" + MAX_SHOT_LEVEL, 280, hudY);
        g.drawString("Lives: " + player.getLives(), 370, hudY);

        int totalSeconds = frame / 60;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);
        g.drawString(timeText, 450, hudY);

        g.drawString("Frame: " + frame, 570, hudY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        if (inGame) {
            drawBackground(g);
            drawExplosions(g);
            drawPowerUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
            drawHUD(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);

        if (levelComplete) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("Press SPACE to advance to next level", (BOARD_WIDTH - fontMetrics.stringWidth("Press SPACE to advance to next level")) / 2,
                    BOARD_WIDTH / 2 + 30);
        }
    }

    private void update() {
        backgroundManager.update();

        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            switch (sd.type) {
                case "Alien1":
                    AnimatedAlien1 animatedAlien1 = new AnimatedAlien1(sd.x, sd.y);
                    enemies.add(animatedAlien1);
                    break;
                case "Alien2":
                    AnimatedAlien2 animatedAlien2 = new AnimatedAlien2(sd.x, sd.y);
                    enemies.add(animatedAlien2);
                    break;
                case "Alien3":
                    AnimatedAlien3 bombship = new AnimatedAlien3(sd.x, sd.y);
                    enemies.add(bombship);
                    break;
                case "PowerUp-SpeedUp":
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-MultiShot":
                    PowerUp multiShot = new MultiShot(sd.x, sd.y);
                    powerups.add(multiShot);
                    break;
            }
        }

        if (frame >= 18000) {
            levelComplete = true;
            inGame = false;
            timer.stop();
            message = "Level Complete!";
        }

        player.act();

        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                    score += SCORE_POWERUP;
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
                enemy.tryCreateBomb();
            } else {
                if (enemy instanceof AnimatedAlien3) {
                    AnimatedAlien3 alien3 = (AnimatedAlien3) enemy;
                    alien3.act(direction);
                }
            }
        }

        updateAlienLasers();
        handleBombPlayerCollisions();
        updateShots();
    }

    private void handleBombPlayerCollisions() {
        for (Enemy enemy : enemies) {
            if (enemy instanceof AnimatedAlien3) {
                AnimatedAlien3 alien3 = (AnimatedAlien3) enemy;
                List<AnimatedAlien3.BombshipBomb> bombs = alien3.getAlienBombs();
                for (AnimatedAlien3.BombshipBomb bomb : bombs) {
                    if (!bomb.isDestroyed() && player.isVisible()) {
                        int bombX = bomb.getX();
                        int bombY = bomb.getY();
                        int bombWidth = 8;
                        int bombHeight = 8;

                        int playerX = player.getX();
                        int playerY = player.getY();
                        int visualWidth = 32 * SCALE_FACTOR;
                        int visualHeight = 24 * SCALE_FACTOR;
                        int collisionWidth = (int) (visualWidth * 0.6);
                        int collisionHeight = (int) (visualHeight * 0.4);
                        int collisionX = playerX + (visualWidth - collisionWidth) / 2;
                        int collisionY = playerY + (visualHeight - collisionHeight) / 2;

                        if (bombX + bombWidth >= collisionX && bombX <= (collisionX + collisionWidth)
                                && bombY + bombHeight >= collisionY && bombY <= (collisionY + collisionHeight)) {

                            explosions.add(new Explosion(bomb.getX(), bomb.getY()));
                            boolean playerDied = player.takeDamage();
                            bomb.setDestroyed(true);

                            if (playerDied) {
                                inGame = false;
                                message = "Level Failed - Try Again!";
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateAlienLasers() {
        for (Enemy enemy : enemies) {
            if (enemy instanceof AnimatedAlien1) {
                AnimatedAlien1 alien1 = (AnimatedAlien1) enemy;
                List<Alien1LaserProjectile> lasers = alien1.getLasers();

                if (!enemy.isVisible()) {
                    for (Alien1LaserProjectile laser : lasers) {
                        if (!laser.isDestroyed()) {
                            laser.act();
                        }
                    }
                }

                for (Alien1LaserProjectile laser : lasers) {
                    if (!laser.isDestroyed()) {
                        handleAlien1LaserPlayerCollision(laser);
                    }
                }
            } else if (enemy instanceof AnimatedAlien2) {
                AnimatedAlien2 alien2 = (AnimatedAlien2) enemy;
                List<Alien2LaserProjectile> lasers = alien2.getLasers();

                if (!enemy.isVisible()) {
                    for (Alien2LaserProjectile laser : lasers) {
                        if (!laser.isDestroyed()) {
                            laser.act();
                        }
                    }
                }

                for (Alien2LaserProjectile laser : lasers) {
                    if (!laser.isDestroyed()) {
                        handleLaserPlayerCollision(laser);
                    }
                }
            }
        }
    }

    private void updateShots() {
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    if (handleShotEnemyCollision(shot, enemy, shotX, shotY)) {
                        shotsToRemove.add(shot);
                        break;
                    }
                }

                int y = shot.getY();
                y -= 20;

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);
    }

    private void handleAlien1LaserPlayerCollision(Alien1LaserProjectile laser) {
        if (laser.isDestroyed() || !player.isVisible()) return;

        int laserCenterX = laser.getX() + laser.getLaserWidth() / 2;
        int laserCenterY = laser.getY() + laser.getLaserHeight() / 2;

        int playerX = player.getX();
        int playerY = player.getY();
        int visualWidth = 32 * SCALE_FACTOR;
        int visualHeight = 24 * SCALE_FACTOR;
        int collisionWidth = (int) (visualWidth * 0.75);
        int collisionHeight = (int) (visualHeight * 0.5);
        int collisionX = playerX + (visualWidth - collisionWidth) / 2;

        if (laserCenterX >= collisionX && laserCenterX <= (collisionX + collisionWidth)
                && laserCenterY >= playerY && laserCenterY <= (playerY + collisionHeight)) {

            explosions.add(new Explosion(laser.getX(), laser.getY()));
            boolean playerDied = player.takeDamage();
            laser.setDestroyed(true);

            if (playerDied) {
                inGame = false;
                message = "Level Failed - Try Again!";
            }
        }
    }

    private void handleLaserPlayerCollision(Alien2LaserProjectile laser) {
        if (laser.isDestroyed() || !player.isVisible()) return;

        int laserCenterX = laser.getX() + laser.getLaserWidth() / 2;
        int laserCenterY = laser.getY() + laser.getLaserHeight() / 2;

        int playerX = player.getX();
        int playerY = player.getY();
        int visualWidth = 32 * SCALE_FACTOR;
        int visualHeight = 24 * SCALE_FACTOR;
        int collisionWidth = (int) (visualWidth * 0.75);
        int collisionHeight = (int) (visualHeight * 0.5);
        int collisionX = playerX + (visualWidth - collisionWidth) / 2;

        if (laserCenterX >= collisionX && laserCenterX <= (collisionX + collisionWidth)
                && laserCenterY >= playerY && laserCenterY <= (playerY + collisionHeight)) {

            explosions.add(new Explosion(laser.getX(), laser.getY()));
            boolean playerDied = player.takeDamage();
            laser.setDestroyed(true);

            if (playerDied) {
                inGame = false;
                message = "Level Failed - Try Again!";
            }
        }
    }

    private boolean handleShotEnemyCollision(Shot shot, Enemy enemy, int shotX, int shotY) {
        int enemyX = enemy.getX();
        int enemyY = enemy.getY();

        int collisionX = shotX;
        if (shot instanceof LaserShot) {
            collisionX = ((LaserShot) shot).getCollisionX();
        }

        int enemyWidth = ALIEN_WIDTH;
        int enemyHeight = ALIEN_HEIGHT;

        if (enemy instanceof AnimatedAlien1 || enemy instanceof AnimatedAlien2 || enemy instanceof AnimatedAlien3) {
            enemyWidth = ALIEN_WIDTH * 3;
            enemyHeight = ALIEN_HEIGHT * 3;
        }

        int collisionWidth = (int) (enemyWidth * 0.8);
        int collisionHeight = (int) (enemyHeight * 0.7);
        int collisionEnemyX = enemyX + (enemyWidth - collisionWidth) / 2;
        int collisionEnemyY = enemyY + (enemyHeight - collisionHeight) / 2;

        if (enemy.isVisible() && shot.isVisible()
                && collisionX >= collisionEnemyX && collisionX <= (collisionEnemyX + collisionWidth)
                && shotY >= collisionEnemyY && shotY <= (collisionEnemyY + collisionHeight)) {

            var ii = new ImageIcon(IMG_EXPLOSION);
            enemy.setImage(ii.getImage());
            enemy.setDying(true);
            explosions.add(new Explosion(enemyX, enemyY));
            deaths++;

            if (enemy instanceof Alien1 || enemy instanceof AnimatedAlien1) {
                score += SCORE_ALIEN1;
            } else if (enemy instanceof AnimatedAlien2) {
                score += SCORE_ALIEN2;
            } else if (enemy instanceof AnimatedAlien3) {
                score += SCORE_ALIEN3;
            }

            shot.die();
            return true;
        }
        return false;
    }

    private void createMultipleShots(int playerX, int playerY) {
        int shotLevel = player.getShotLevel();
        int baseOffset = 15;

        switch (shotLevel) {
            case 1:
                shots.add(new LaserShot(playerX, playerY));
                break;
            case 2:
                shots.add(new LaserShot(playerX - baseOffset, playerY));
                shots.add(new LaserShot(playerX + baseOffset, playerY));
                break;
            case 3:
                shots.add(new LaserShot(playerX - baseOffset, playerY));
                shots.add(new LaserShot(playerX, playerY));
                shots.add(new LaserShot(playerX + baseOffset, playerY));
                break;
            case 4:
                shots.add(new LaserShot(playerX - baseOffset * 2, playerY));
                shots.add(new LaserShot(playerX - baseOffset, playerY));
                shots.add(new LaserShot(playerX + baseOffset, playerY));
                shots.add(new LaserShot(playerX + baseOffset * 2, playerY));
                break;
        }
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                int maxShots = 4 * player.getShotLevel();
                if (shots.size() < maxShots) {
                    createMultipleShots(x, y);
                    SoundEffect.playSound(AUDIO_SHOOT);
                }
            }

            if (key == KeyEvent.VK_SPACE && !inGame && levelComplete) {
                game.progressToScene1();
            }
        }
    }
}