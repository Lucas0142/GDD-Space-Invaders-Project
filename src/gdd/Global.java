package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites

    public static final int BOARD_WIDTH = 716; // Doubled from 358
    public static final int BOARD_HEIGHT = 700; // Doubled from 350
    public static final int BORDER_RIGHT = 60; // Doubled from 30
    public static final int BORDER_LEFT = 10; // Doubled from 5

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 10; // Doubled from 5

    public static final int ALIEN_HEIGHT = 24; // Doubled from 12
    public static final int ALIEN_WIDTH = 24; // Doubled from 12
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 24;
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 30; // Doubled from 15
    public static final int PLAYER_HEIGHT = 20; // Doubled from 10

    // Images
    public static final String IMG_ENEMY = "src/images/alien.png";
    public static final String IMG_ENEMY2 = "src/images/alien2.png"; // NEW: Different sprite for Alien2
    public static final String IMG_PLAYER = "src/images/player.png";
    public static final String IMG_SHOT = "src/images/shot.png";
    public static final String IMG_BOMB = "src/images/bomb.png"; // NEW: Enemy bomb sprite
    public static final String IMG_EXPLOSION = "src/images/explosion.png";
    public static final String IMG_TITLE = "src/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "src/images/powerup-s.png";
    public static final String IMG_POWERUP_MULTISHOT = "src/images/powerup-m.png"; // NEW: Multi-shot powerup

    // Audio files
    public static final String AUDIO_TITLE = "src/audio/title.wav";
    public static final String AUDIO_SCENE1 = "src/audio/scene1.wav";
    public static final String AUDIO_SCENE2 = "src/audio/scene2.wav";
    public static final String AUDIO_SHOOT = "src/audio/shoot.wav";
    public static final String AUDIO_EXPLOSION = "src/audio/explosion.wav"; // NEW: Explosion sound
    public static final String AUDIO_POWERUP = "src/audio/powerup.wav"; // NEW: PowerUp collection sound

    // Score values
    public static final int SCORE_ALIEN1 = 40;
    public static final int SCORE_ALIEN2 = 50;
    public static final int SCORE_ALIEN3 = 30;
    public static final int SCORE_BOSS = 100;
    public static final int SCORE_POWERUP = 5;

    // PowerUp levels
    public static final int MAX_SPEED_LEVEL = 4;
    public static final int MAX_SHOT_LEVEL = 4; // NEW: Maximum shot level for MultiShot

    // Boss Stats
    public static final int BOSS_WIDTH = 72;
    public static final int BOSS_HEIGHT = 72;
    public static final int BOSS_HEALTH = 200;

    // Game Timing
    public static final int GAME_FPS = 60; // Target frames per second
    public static final int FRAME_DELAY_MS = 1000 / GAME_FPS; // Milliseconds per frame
    public static final int SCENE_DURATION_FRAMES = 18000; // 5 minutes at 60 FPS
    public static final double SCENE_DURATION_SECONDS = SCENE_DURATION_FRAMES / (double) GAME_FPS;
}