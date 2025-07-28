/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import gdd.sprite.Sprite;

abstract public class PowerUp extends Sprite {

    PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void act() {
        // Default behavior for power-ups - move down
        y += 2;

        // Remove if off screen
        if (y > BOARD_HEIGHT) {
            die();
        }
    }

    abstract public void upgrade(Player player);
}