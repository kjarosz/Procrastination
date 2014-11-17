/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package procrastination.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author jradatz
 */
public class Powerup extends Entity {
    public static enum powerups {
        SPEED, SLOW, SCOREMULTIPLIER, REGULARGUN, RAPIDFIRE, SCATTERSHOT, EXPLODINGBULLETS
    };
    
    private static final Rectangle SPRITES[] = {
        new Rectangle(0, 0, 140, 147),
        new Rectangle(140, 0, 280, 147),
        new Rectangle(280, 0, 420, 147),
        new Rectangle(420, 0, 560, 147),
        new Rectangle(560, 0, 700, 147),
        new Rectangle(700, 0, 840, 147),
        new Rectangle(840, 0, 980, 147)
    };
    public static BufferedImage mSprites[];
    
    private powerups powerupType;

    private final long destroyAfter = 5000;
    private long startTime;
    
    Powerup(powerups newType, Point2D.Double position) {
        if(mSprites == null){
            mSprites = loadSprites(new File("images" + File.separator + "powerupsSpriteSheet.png"), SPRITES);
        }
        powerupType = newType;
        switch(newType){
            case SPEED:
                setCurrentImage(mSprites[4]);
                break;
            case SLOW:
                setCurrentImage(mSprites[3]);
                break;
            case SCOREMULTIPLIER:
                setCurrentImage(mSprites[5]);
                break;
            case REGULARGUN:
                setCurrentImage(mSprites[6]);
                break;
            case RAPIDFIRE:
                setCurrentImage(mSprites[0]);
                break;
            case SCATTERSHOT:
                setCurrentImage(mSprites[1]);
                break;
            case EXPLODINGBULLETS:
                setCurrentImage(mSprites[6]);
                powerupType = powerups.REGULARGUN;
                break;
        }
        setPosition(position);
        setBBox(56, 58);
        setDirection(new Point2D.Double(0, -1));
        setType(Entity.objectTypes.POWERUP);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void update(Level level) {
        if(startTime + destroyAfter < System.currentTimeMillis()){
            level.deleteEntity(this);
        }
    }
    
    @Override
    public void draw(Graphics g, int xOffset, int yOffset) {
        draw(g, 0.4, xOffset, yOffset);
        //drawBBox(g, Color.WHITE, xOffset, yOffset);
    }
    
    @Override
    public void collision(Entity other, Level level) {
        if (other.getType() == Entity.objectTypes.PLAYER) {
            level.deleteEntity(this);
        }
    }

    public powerups getPowerupType() {
        return powerupType;
    }
}
