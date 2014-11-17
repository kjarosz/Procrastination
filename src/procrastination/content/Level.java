package procrastination.content;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

import procrastination.GamePanel;
import procrastination.content.Powerup.powerups;
import procrastination.menu.CustomNumberImage;

public class Level {

    private Rectangle mLevelSize;

    private Player mPlayer;
    private LinkedList<Entity> mEntities;
    private LinkedList<Entity> mRemovedEntities;

    private BufferedImage mBackground;

    private long mEnemySpawnTime; // milliseconds
    private long mLastEnemySpawn; // milliseconds
    
    private int drawWidth, drawHeight;
    
    private int mapXOffset = 0;
    private int mapYOffset = 0;
    
    private Random rnd;
    
    private CustomNumberImage score;
    private double scoreVal = 0;
    
    public Level(int width, int height) {
        drawWidth = width;
        drawHeight = height;
        loadLevelBackground();
        mEntities = new LinkedList<>();
        mRemovedEntities = new LinkedList<>();
        
        mPlayer = new Player(mLevelSize.width, mLevelSize.height);
        mEntities.add(mPlayer);
        
        mEnemySpawnTime = 800;
        mLastEnemySpawn = System.currentTimeMillis() - mEnemySpawnTime;
        
        rnd = new Random();
        
        score = new CustomNumberImage(0);
    }
    
    private void loadLevelBackground() {
        try {
            mBackground = ImageIO.read(new File("images" + File.separator + "terrain.png"));
        } catch(IOException ex) {
            System.out.println("Level background failed to load.");
            mBackground = null;
        }
        if(mBackground != null){
            mLevelSize = new Rectangle(0, 0, mBackground.getWidth(), mBackground.getHeight());
            mapXOffset = (mBackground.getWidth() - drawWidth) / 2;
            mapYOffset = (mBackground.getHeight() - drawHeight) / 2;
        }else{
            mLevelSize = new Rectangle(0, 0, drawWidth, drawHeight);
        }
    }

    public Rectangle getLevelSize() {
        return mLevelSize;
    }
    
    public Point getOffset(){
        return new Point(mapXOffset, mapYOffset);
    }
    
    public Player getPlayer() {
        return mPlayer;
    }

    public void spawnBullet(Point2D.Double position, Point2D.Double direction) {
        Bullet bullet = new Bullet(position, direction);
        mEntities.add(bullet);
    }

    private void spawnNewEnemies() {
        while (System.currentTimeMillis() - mLastEnemySpawn > mEnemySpawnTime) {
            mLastEnemySpawn += mEnemySpawnTime;

            Point2D.Double newEnemyPosition = new Point2D.Double();
            int side = (int) (Math.random() * 4);
            switch (side) {
                case 0: // left side of the screen
                    newEnemyPosition.x = -32;
                    newEnemyPosition.y = Math.random() * mLevelSize.height;
                    break;
                case 1: // top side of the screen
                    newEnemyPosition.x = Math.random() * mLevelSize.width;
                    newEnemyPosition.y = -32;
                    break;
                case 2: // right side of the screen
                    newEnemyPosition.x = mLevelSize.width + 32;
                    newEnemyPosition.y = Math.random() * mLevelSize.height;
                    break;
                default: // bottom side of the screen
                    newEnemyPosition.x = Math.random() * mLevelSize.width;
                    newEnemyPosition.y = mLevelSize.height + 32;
                    break;
            }
            
            Enemy newEnemy = new Enemy(newEnemyPosition, powerups.values()[rnd.nextInt(powerups.values().length)]);
            mEntities.add(newEnemy);
        }
    }

    public void deleteEntity(Entity entity) {
        if(entity.getType() == Entity.objectTypes.ENEMY){
            incrementScore(10 * mPlayer.getScoreMultiplier());
        }
        mRemovedEntities.add(entity);
    }
    
    public void addEntity(Entity entity){
        mEntities.add(entity);
    }

    public void update(GamePanel game) {
        //Update Objects
        spawnNewEnemies();  
        for(int i = 0; i < mEntities.size(); i++){
            mEntities.get(i).update(this);
        }
        
        //Collisions
        for(int i = 0; i < mEntities.size(); i++){
            for(int j = i + 1; j < mEntities.size(); j++){
                if(mEntities.get(i).intersectsBBox(mEntities.get(j).getBBox())){
                    mEntities.get(i).collision(mEntities.get(j), this);
                    mEntities.get(j).collision(mEntities.get(i), this);
                }
            }
        }
        
        //Remove dead objects
        if (!mRemovedEntities.isEmpty()) {
            removeEntities();
        }
        
        if(mPlayer.isDead()) {
            game.gameOver((int)scoreVal);
        }
    }
    
    public void updateOffset(){
        mapXOffset = Math.min(Math.max((int)(mPlayer.getPosition().x - drawWidth / 2), 0), mLevelSize.width - drawWidth);
        mapYOffset = Math.min(Math.max((int)(mPlayer.getPosition().y - drawHeight / 2), 0), mLevelSize.height - drawHeight);
    }
    
    private void removeEntities() {
        for (Entity entity : mRemovedEntities) {
            mEntities.remove(entity);
        }
        mRemovedEntities.clear();
    }

    public void draw(Graphics g) {
        if(mBackground != null) {
            g.drawImage(mBackground, -mapXOffset, -mapYOffset, null);
        }
        
        for(Entity e : mEntities){
            e.draw(g, mapXOffset, mapYOffset);
        }
        
        Dimension scoreSize = score.getSize();
        score.draw(g, drawWidth - scoreSize.width - 10, 10);
        
        if(mPlayer.getScoreMultiplier() != 1){
            g.drawImage(Powerup.mSprites[5], drawWidth - scoreSize.width - 30 - (int)(Powerup.mSprites[5].getWidth() * 0.25), 10, (int)(Powerup.mSprites[5].getWidth() * 0.25), (int)(Powerup.mSprites[5].getHeight() * 0.25), null);
        }
    }
    
    public void incrementScore(double ammount){
        scoreVal += ammount;
        score.assembleNumberImage((int)scoreVal);
    }
}