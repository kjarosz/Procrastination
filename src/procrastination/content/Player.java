package procrastination.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

import procrastination.content.Powerup.powerups;
import procrastination.input.ControlFunction;
import procrastination.input.KeyManager;
import procrastination.input.KeyMapping;
import procrastination.input.MouseManager;
import procrastination.menu.CustomNumberImage;

public class Player extends Entity {

    private static final Rectangle SPRITES[] = {
        new Rectangle(17, 14, 107, 112),
        new Rectangle(115, 15, 205, 121),
        new Rectangle(213, 16, 303, 123)
    };
    
    private final int LEFT_KEY = KeyEvent.VK_A;
    private final int RIGHT_KEY = KeyEvent.VK_D;
    private final int UP_KEY = KeyEvent.VK_W;
    private final int DOWN_KEY = KeyEvent.VK_S;
    
    private final double TERMINAL_VELOCITY = 200.0; // pixels per second
    private final Point2D.Double LEFT_VELOCITY = new Point2D.Double(-TERMINAL_VELOCITY, 0.0);
    private final Point2D.Double RIGHT_VELOCITY = new Point2D.Double(TERMINAL_VELOCITY, 0.0);
    private final Point2D.Double DOWN_VELOCITY = new Point2D.Double(0.0, TERMINAL_VELOCITY);
    private final Point2D.Double UP_VELOCITY = new Point2D.Double(0.0, -TERMINAL_VELOCITY);
    
    private Powerup.powerups currentWeapon = Powerup.powerups.REGULARGUN;
    private final long mMovementDuration = 5000;    
    private double lastSpeedModifierTime = 0;
    private double movementMultiplier = 1;
    
    private final long mScoreDuration = 2500;
    private double lastScoreModifierTime = 0;
    private double scoreMultiplier = 1;
    
    private BufferedImage mSprites[];
    private int mCurrentImage;

    private LinkedList<KeyMapping> mKeyMappings;

    private long mStandardFiringCooldown = 500; // milliseconds
    private long mFastFiringCooldown = 250;
    private long mFiringCooldown = mStandardFiringCooldown;
    
    private long mLastFiredBullet; // milliseconds

    private Point2D.Double mVelocity;
    
    private int mapPadding = 55;
    
    //Bullet Correction
    private final Point2D.Double bulletPosition = new Point2D.Double(55, 22);
    private double bulletAngle;
    private double bulletDistance;
    private double bulletSpread = 45;

    //private CustomNumberImage healthDisplay;
    private double healthPoints = 50f;
    
    public Player(int levelWidth, int levelHeight) {
        setPosition(new Point2D.Double(levelWidth / 2, levelHeight / 2));
        setBBox(90, 98);
        setType(objectTypes.PLAYER);
        mVelocity = new Point2D.Double(0.0, 0.0);

        loadSprites();

        constructKeyMappings();
        
        bulletAngle = Math.atan2(bulletPosition.y, bulletPosition.x);
        bulletDistance = Math.sqrt(Math.pow(bulletPosition.x, 2) + Math.pow(bulletPosition.y, 2));
        bulletSpread = Math.toRadians(bulletSpread);
        
        //healthDisplay = new CustomNumberImage((int)healthPoints);
    }

    private void loadSprites() {
        mSprites = loadSprites(new File("images" + File.separator + "characterwalkspritesheetv1.png"), SPRITES);
        mCurrentImage = 0;
        setCurrentImage(mSprites[mCurrentImage]);
    }

    private void constructKeyMappings() {
        mKeyMappings = new LinkedList<>();
        constructKeyMapping(LEFT_KEY, constructMoveFunction(LEFT_VELOCITY));
        constructKeyMapping(RIGHT_KEY, constructMoveFunction(RIGHT_VELOCITY));
        constructKeyMapping(UP_KEY, constructMoveFunction(UP_VELOCITY));
        constructKeyMapping(DOWN_KEY, constructMoveFunction(DOWN_VELOCITY));
    }

    private void constructKeyMapping(int key, ControlFunction function) {
        KeyMapping keyMapping = new KeyMapping();
        keyMapping.keyCode = key;
        keyMapping.keyFunction = function;
        keyMapping.pressProcessed = false;
        keyMapping.releaseProcessed = true;

        mKeyMappings.add(keyMapping);
    }

    private ControlFunction constructMoveFunction(final Point2D.Double velocity) {
        return new ControlFunction() {
            @Override
            public void keyPressed() {
                addVelocity(velocity);
            }

            @Override
            public void keyReleased() {
                subtractVelocity(velocity);
            }
        };
    }

    private void addVelocity(Point2D.Double vel) {
        mVelocity.x += vel.x;
        mVelocity.y += vel.y;
    }

    private void subtractVelocity(Point2D.Double vel) {
        mVelocity.x -= vel.x;
        mVelocity.y -= vel.y;
    }
    
    public boolean isDead() {
        return healthPoints <= 0;
    }

    @Override
    public void update(Level level) {
        processKeyInputs();
        Rectangle levelSize = level.getLevelSize();
        processMouse(level);
        
        Point2D.Double newVel = new Point2D.Double(mVelocity.x * movementMultiplier, mVelocity.y * movementMultiplier);
        move(newVel);
        
        level.updateOffset();
        
        if(mPosition.x - mapPadding < 0){
            mPosition.x = mapPadding;
        }else if(mPosition.x > levelSize.width - mapPadding){
            mPosition.x = levelSize.width - mapPadding;
        }
        
        if(mPosition.y - mapPadding < 0){
            mPosition.y = mapPadding;
        }else if(mPosition.y > levelSize.height - mapPadding){
            mPosition.y = levelSize.height - mapPadding;
        }

        if(lastSpeedModifierTime < System.currentTimeMillis() - mMovementDuration){
            movementMultiplier = 1;
        }
        if(lastScoreModifierTime < System.currentTimeMillis() - mScoreDuration){
            scoreMultiplier = 1;
        }
    }
    
    public int getMapPadding(){
        return mapPadding;
    }
    
    private void processKeyInputs() {
        for (KeyMapping keyMapping : mKeyMappings) {
            if (KeyManager.isKeyPressed(keyMapping.keyCode) && !keyMapping.pressProcessed) {
                keyMapping.keyFunction.keyPressed();
                keyMapping.pressProcessed = true;
                keyMapping.releaseProcessed = false;
            } else if (!KeyManager.isKeyPressed(keyMapping.keyCode) && !keyMapping.releaseProcessed) {
                keyMapping.keyFunction.keyReleased();
                keyMapping.releaseProcessed = true;
                keyMapping.pressProcessed = false;
            }
        }
    }

    private void processMouse(Level level) {
        Point mousePos = MouseManager.getMousePosition();
        Point mapOffset = level.getOffset();
        setDirection(new Point2D.Double(mousePos.x - mPosition.x + mapOffset.x, mousePos.y - mPosition.y + mapOffset.y));

        if (MouseManager.isButtonPressed(MouseEvent.BUTTON1)) {
            if (System.currentTimeMillis() - mLastFiredBullet >= mFiringCooldown) {
                mLastFiredBullet = System.currentTimeMillis();
                
                Point2D.Double bulletPos = new Point2D.Double();
                double angle = Math.atan2(mDirection.y, mDirection.x);
                
                bulletPos.x = (int) (mPosition.x + bulletDistance * Math.cos(angle + bulletAngle));
                bulletPos.y = (int) (mPosition.y + bulletDistance * Math.sin(angle + bulletAngle));
                
                if(currentWeapon == powerups.REGULARGUN){
                    level.addEntity(new Bullet(bulletPos, mDirection));
                }else if(currentWeapon == powerups.SCATTERSHOT){
                    level.addEntity(new Bullet(bulletPos, mDirection));
                    //Create Angle 1
                    Point2D.Double bulletDirection = new Point2D.Double();
                    bulletDirection.x = Math.cos(angle - bulletSpread / 2);
                    bulletDirection.y = Math.sin(angle - bulletSpread / 2);
                    level.addEntity(new Bullet(bulletPos, bulletDirection));
                    //Create Angle 2
                    bulletDirection.x = Math.cos(angle + bulletSpread / 2);
                    bulletDirection.y = Math.sin(angle + bulletSpread / 2);
                    level.addEntity(new Bullet(bulletPos, bulletDirection));
                }else if(currentWeapon == powerups.EXPLODINGBULLETS){
                    level.addEntity(new ExplodingBullet(bulletPos, mDirection));
                }
            }
        }
    }
    
    @Override
    public void draw(Graphics g, int xOffset, int yOffset){
        draw(g, 1.0, xOffset, yOffset);
        //g.drawImage(mSprites[0], 10, 10, (int)(mSprites[0].getWidth() * 0.5), (int)(mSprites[0].getHeight() * 0.5), null);
        //healthDisplay.draw(g, 20 + (int)(mSprites[0].getWidth() * 0.5), 20);
        //g.setColor(Color.WHITE);
        //g.drawString("ScoreMultiplier: " + scoreMultiplier + "; MovementMultiplier: " + movementMultiplier, 10, 10);
        //drawBBox(g, Color.MAGENTA, xOffset, yOffset);
        drawHealthBar(g);
    }
    
    private void drawHealthBar(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(10, 10, 200, 20);
        g.setColor(Color.GREEN);
        if(healthPoints < 0)
            g.fillRect(10, 10, 0, 20);
        else
            g.fillRect(10, 10, (int)healthPoints * 4, 20);
    }

    @Override
    public void collision(Entity other, Level level) {
        switch(other.getType()){
            case ENEMY:
                //Ignore for now
                healthPoints -= 5;
                //healthDisplay.assembleNumberImage((int)healthPoints);
                break;
            case POWERUP:
                switch(((Powerup)other).getPowerupType()){
                    case SPEED:
                        if(movementMultiplier < 1){
                            movementMultiplier = 1;
                        }else{
                            movementMultiplier = 1.5;
                            lastSpeedModifierTime = System.currentTimeMillis();
                        }
                        break;
                    case SLOW:
                        movementMultiplier = 0.5;
                        lastSpeedModifierTime = System.currentTimeMillis();
                        break;
                    case SCOREMULTIPLIER:
                        lastScoreModifierTime = System.currentTimeMillis();
                        scoreMultiplier = 2;
                        break;
                    case REGULARGUN:
                        currentWeapon = powerups.REGULARGUN;
                        mFiringCooldown = mStandardFiringCooldown;
                        break;
                    case RAPIDFIRE:
                        currentWeapon = powerups.REGULARGUN;
                        mFiringCooldown = mFastFiringCooldown;
                        break;
                    case SCATTERSHOT:
                        currentWeapon = powerups.SCATTERSHOT;
                        mFiringCooldown = mStandardFiringCooldown;
                        break;
                    case EXPLODINGBULLETS:
                        currentWeapon = powerups.EXPLODINGBULLETS;
                        mFiringCooldown = mStandardFiringCooldown;
                        break;
                }
                break;
        }
    }
    
    public double getHealthPoints(){
        return healthPoints;
    }
    
    public double getScoreMultiplier(){
        return scoreMultiplier;
    }
}
