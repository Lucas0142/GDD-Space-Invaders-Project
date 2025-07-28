# Space Invaders Extended Edition

A modern, feature-rich extension of the classic Space Invaders game built in Java using Swing for the graphical interface.

## Member Names
Soe Min Min Latt (6611938),
Aung Khant Zaw (6611947)
## Description

This is an extended Java implementation of the classic arcade game Space Invaders, featuring two levels, animated sprites, boss fights, and power-up systems. Players control a spaceship and must fight against waves of descending alien invaders across two different levels ending in an epic boss battle.

The game extends the original Space Invaders concept with:
* **Two complete levels** with 5 minutes of gameplay each
* **Multiple enemy types** with unique movement patterns and attacks
* **Animated sprites** for all game entities
* **Power-up system** with speed boosts and multi-shot capabilities
* **Epic boss fight** with multiple phases and attack patterns
* **Progressive difficulty** from Entry Level to Hard Level
* **Comprehensive HUD** showing score, upgrades, lives, and time
* **Dynamic backgrounds** with scrolling space environments
* **Sound effects and background music** for immersive gameplay

## Game Flow

1. **Title Screen** - Choose your difficulty:
    - Press `SPACE` for Entry Level (Scene2)
    - Press `1` for Hard Level (Scene1) - Direct to boss fight

2. **Entry Level (Scene2)** - 5 minutes of progressive enemy waves
    - Face Bomber, Fighter, and Bombship aliens
    - Collect power-ups to enhance your ship
    - Complete the level to unlock Hard Level

3. **Hard Level (Scene1)** - 5 minutes + Epic Boss Fight
    - Encounter more challenging enemy patterns
    - Confront the multi-phase Corvette Boss
    - Achieve victory to complete the game

## Controls

* **Left Arrow**: Move spaceship left
* **Right Arrow**: Move spaceship right
* **Space**: Fire weapon
* **Space** (on completion screens): Progress to next level or restart game

## Game Rules

### Victory Conditions
* **Entry Level**: Survive 5 minutes of enemy waves (18,000 frames at 60 FPS)
* **Hard Level**: Survive enemy waves AND defeat the Corvette Boss
* **Overall Victory**: Complete both levels to win the game

### Survival Mechanics
* Start with 100 lives
* Lose a life when hit by enemy projectiles
* Avoid enemy bombs, lasers, and rockets
* Game over when all lives are lost

### Scoring System
* **Alien1 (Bomber)**: 40 points
* **Alien2 (Fighter)**: 50 points
* **Alien3 (Bombship)**: 30 points
* **Boss**: 100 points per hit
* **Power-Up Collection**: 5 points

## Enemy Types

### Animated Alien1 (Bomber)
* Wave movement pattern with dual laser attacks
* Animated movement and attack states

### Animated Alien2 (Fighter)
* Zigzag and spiral movement patterns
* Close-range double laser shots
* High speed

### Animated Alien3 (Bombship)
* Straight downward movement
* Drops animated bombs

### Corvette Boss
* **6 distinct states**: Move, Attack1, Attack2, Rocket, Boost, Shield
* **Multiple phases** based on health percentage
* **Complex attack patterns**: Wide lasers, close shots, homing rockets
* **200 health points** requiring sustained assault
* **Invulnerability periods** with shield activation

## Power-Up System

### Speed Boost (4 Levels)
* **Level 1**: Normal speed (default)
* **Level 2**: +2 speed boost
* **Level 3**: +4 speed boost
* **Level 4**: +6 speed boost (maximum)

### Multi-Shot (4 Levels)
* **Level 1**: Single shot (default)
* **Level 2**: Double shot (side-by-side)
* **Level 3**: Triple shot (spread pattern)
* **Level 4**: Quad shot (wide spread)

## Project Structure

```
src/
├── gdd/
│   ├── Main.java                    # Game entry point
│   ├── Game.java                    # Main game controller and scene management
│   ├── Global.java                  # Game constants and configuration
│   ├── AudioPlayer.java             # Background music system
│   ├── SoundEffect.java             # Sound effects system
│   ├── DataLoader.java              # CSV data loading utilities
│   ├── BackgroundManager.java       # Scrolling background system
│   ├── SpawnDetails.java            # Enemy spawn data structure
│   ├── scene/
│   │   ├── TitleScene.java          # Main menu implementation
│   │   ├── Scene1.java              # Hard level with boss fight
│   │   └── Scene2.java              # Entry level implementation
│   ├── sprite/
│   │   ├── Sprite.java              # Base sprite class with collision detection
│   │   ├── Player.java              # Basic player spaceship
│   │   ├── AnimatedPlayer.java      # Enhanced player with animations
│   │   ├── Enemy.java               # Base enemy class
│   │   ├── Alien1.java              # Basic alien implementation
│   │   ├── AnimatedAlien1.java      # Bomber alien with full animations
│   │   ├── Alien2.java              # Enhanced alien with zigzag movement
│   │   ├── AnimatedAlien2.java      # Fighter alien with complex patterns
│   │   ├── AnimatedAlien3.java      # Bombship alien with explosive attacks
│   │   ├── Boss.java                # Basic boss implementation
│   │   ├── AnimatedBoss.java        # Advanced multi-phase boss
│   │   ├── Shot.java                # Basic player projectile
│   │   ├── LaserShot.java           # Enhanced laser projectile
│   │   ├── Bomb.java                # Enemy projectile base
│   │   ├── Explosion.java           # Explosion effect system
│   │   ├── Alien1LaserProjectile.java   # Bomber weapon system
│   │   ├── Alien2LaserProjectile.java   # Fighter weapon system
│   │   ├── BossLaserCharge1.java        # Boss wide attack pattern
│   │   ├── BossLaserCharge2.java        # Boss close attack pattern
│   │   └── BossRocket.java              # Boss homing rocket system
│   └── powerup/
│       ├── PowerUp.java             # Base power-up class
│       ├── SpeedUp.java             # Speed enhancement system
│       └── MultiShot.java           # Multi-shot upgrade system
├── data/
│   ├── level1_spawns.csv            # Hard level enemy spawn patterns
│   └── level2_spawns.csv            # Entry level enemy spawn patterns
├── images/                          # Game sprites and animation frames
└── audio/                           # Sound effects and background music
```


