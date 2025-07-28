package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import javax.swing.JFrame;

public class Game extends JFrame  {

    TitleScene titleScene;
    Scene1 scene1;
    Scene2 scene2;

    public Game() {
        titleScene = new TitleScene(this);
        scene1 = new Scene1(this);
        scene2 = new Scene2(this);
        initUI();
        loadTitle();
    }

    private void initUI() {
        setTitle("Space Invaders - Extended Edition");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void loadTitle() {
        getContentPane().removeAll();

        try {
            if (scene1 != null) {
                scene1.stop();
            }
        } catch (Exception e) {
        }

        try {
            if (scene2 != null) {
                scene2.stop();
            }
        } catch (Exception e) {
        }

        titleScene = new TitleScene(this);
        add(titleScene);

        titleScene.start();

        revalidate();
        repaint();
        titleScene.requestFocusInWindow();

        javax.swing.SwingUtilities.invokeLater(() -> {
            titleScene.requestFocusInWindow();
        });

        System.out.println("Title Scene Loaded");
    }

    public void loadScene1() {
        getContentPane().removeAll();
        add(scene1);

        try {
            titleScene.stop();
        } catch (Exception e) {
        }

        try {
            if (scene2 != null) {
                scene2.stop();
            }
        } catch (Exception e) {
        }

        scene1.start();
        revalidate();
        repaint();

        System.out.println("Scene 1 Loaded - Hard Level");
    }

    public void loadScene2() {
        getContentPane().removeAll();
        add(scene2);

        try {
            titleScene.stop();
        } catch (Exception e) {
        }

        try {
            if (scene1 != null) {
                scene1.stop();
            }
        } catch (Exception e) {
        }

        scene2.start();
        revalidate();
        repaint();

        System.out.println("Scene 2 Loaded - Entry Level");
    }

    public void progressToScene1() {
        System.out.println("Progressing from Entry Level to Hard Level!");
        loadScene1();
    }

    public void restartGame() {
        System.out.println("Game completed! Returning to title screen");
        loadTitle();
    }
}