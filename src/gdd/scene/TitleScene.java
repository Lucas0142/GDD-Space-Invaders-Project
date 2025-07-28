package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScene extends JPanel {

    private int frame = 0;
    private Image image;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private Game game;

    public TitleScene(Game game) {
        this.game = game;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        initTitle();
        initAudio();

        javax.swing.SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }

            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initTitle() {
        var ii = new ImageIcon(IMG_TITLE);
        image = ii.getImage();
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/title.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error with playing sound.");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.drawImage(image, 0, -80, d.width, d.height, this);

        if (frame % 60 < 30) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.white);
        }

        g.setFont(g.getFont().deriveFont(32f));
        String mainText = "Space Invaders Extended";
        int stringWidth = g.getFontMetrics().stringWidth(mainText);
        int x = (d.width - stringWidth) / 2;
        g.drawString(mainText, x, 500);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(18f));

        String scene2Text = "Press SPACE for Entry Level";
        int scene2Width = g.getFontMetrics().stringWidth(scene2Text);
        int scene2X = (d.width - scene2Width) / 2;
        g.drawString(scene2Text, scene2X, 550);

        String scene1Text = "Press 1 for Hard Level (Boss Fight)";
        int scene1Width = g.getFontMetrics().stringWidth(scene1Text);
        int scene1X = (d.width - scene1Width) / 2;
        g.drawString(scene1Text, scene1X, 580);

        g.setColor(Color.gray);
        g.setFont(g.getFont().deriveFont(10f));
        g.drawString("Game by Chayapol - Extended Edition", 10, 650);
        g.drawString("Entry Level → Hard Level → Victory!", 10, 665);

        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        frame++;
    }

    private void doGameCycle() {
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
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                game.loadScene2();
            } else if (key == KeyEvent.VK_1) {
                game.loadScene1();
            }
        }
    }
}