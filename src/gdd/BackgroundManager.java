package gdd;

import static gdd.Global.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;

public class BackgroundManager {

    private Image scene1Background;
    private Image scene2Background;
    private double scrollOffset = 0.0;

    private static final double SCROLL_SPEED = 1.2;

    private int backgroundWidth;
    private int backgroundHeight;

    public BackgroundManager() {
        loadBackgroundImages();
    }

    private void loadBackgroundImages() {
        scene1Background = loadLongBackground("src/images/scene1_background_long.png");
        scene2Background = loadLongBackground("src/images/scene2_background_long.png");
    }

    private Image loadLongBackground(String path) {
        try {
            File imageFile = new File(path);
            if (imageFile.exists()) {
                var bufferedImage = ImageIO.read(imageFile);
                int originalWidth = bufferedImage.getWidth();
                int originalHeight = bufferedImage.getHeight();
                backgroundWidth = BOARD_WIDTH;
                backgroundHeight = (originalHeight * BOARD_WIDTH) / originalWidth;

                Image scaledImage = bufferedImage.getScaledInstance(backgroundWidth, backgroundHeight, Image.SCALE_SMOOTH);
                return scaledImage;
            } else {
                return createFallbackLongBackground(path.contains("scene2"));
            }
        } catch (Exception e) {
            return createFallbackLongBackground(path.contains("scene2"));
        }
    }

    private Image createFallbackLongBackground(boolean isScene2) {
        java.awt.image.BufferedImage fallback = new java.awt.image.BufferedImage(
                BOARD_WIDTH, BOARD_HEIGHT * 3, java.awt.image.BufferedImage.TYPE_INT_RGB
        );

        java.awt.Graphics2D g2d = fallback.createGraphics();

        if (isScene2) {
            g2d.setColor(new java.awt.Color(20, 10, 40));
        } else {
            g2d.setColor(new java.awt.Color(10, 10, 30));
        }
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT * 3);

        java.util.Random starRandom = new java.util.Random(42);
        g2d.setColor(java.awt.Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = starRandom.nextInt(BOARD_WIDTH);
            int y = starRandom.nextInt(BOARD_HEIGHT * 3);
            g2d.fillOval(x, y, 2, 2);
        }

        g2d.dispose();
        backgroundWidth = BOARD_WIDTH;
        backgroundHeight = BOARD_HEIGHT * 3;
        return fallback;
    }

    private boolean isScene1 = false;

    public void update() {
        scrollOffset -= SCROLL_SPEED;

        if (isScene1) {
            double maxScroll = (backgroundHeight - BOARD_HEIGHT) * 0.9;
            if (scrollOffset < 0) {
                scrollOffset = maxScroll;
            }
        } else {
            double maxScroll = backgroundHeight - BOARD_HEIGHT;
            if (scrollOffset < 0) {
                scrollOffset = maxScroll;
            }
        }
    }

    public void drawBackground(Graphics g, boolean isScene2) {
        Image background = isScene2 ? scene2Background : scene1Background;

        if (background == null) {
            if (isScene2) {
                g.setColor(new java.awt.Color(20, 10, 40));
            } else {
                g.setColor(new java.awt.Color(10, 10, 30));
            }
            g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
            return;
        }

        double maxScroll = backgroundHeight - BOARD_HEIGHT;
        int sourceY = (int) Math.max(0, Math.min(scrollOffset, maxScroll));

        g.drawImage(background,
                0, 0, BOARD_WIDTH, BOARD_HEIGHT,
                0, sourceY, backgroundWidth, sourceY + BOARD_HEIGHT,
                null);

        addMinimalMovingEffects(g, isScene2);
    }

    private void addMinimalMovingEffects(Graphics g, boolean isScene2) {
        java.util.Random particleRandom = new java.util.Random(123);

        for (int i = 0; i < 8; i++) {
            int baseX = particleRandom.nextInt(BOARD_WIDTH);
            double baseY = particleRandom.nextDouble() * BOARD_HEIGHT;

            double speed = 0.3 + particleRandom.nextDouble() * 0.7;
            double y = (baseY + (scrollOffset * speed * -0.3)) % BOARD_HEIGHT;
            if (y < 0) y += BOARD_HEIGHT;

            if (isScene2) {
                g.setColor(new java.awt.Color(150, 150, 255, 30));
            } else {
                g.setColor(new java.awt.Color(200, 200, 255, 25));
            }

            g.fillOval(baseX, (int)y, 1, 1);
        }
    }

    public void reset() {
        isScene1 = false;
        scrollOffset = backgroundHeight - BOARD_HEIGHT;
    }

    public void resetScene1() {
        isScene1 = true;
        double maxScroll = (backgroundHeight - BOARD_HEIGHT) * 0.9;
        scrollOffset = maxScroll;
    }

    public String getBackgroundInfo() {
        return "";
    }
}