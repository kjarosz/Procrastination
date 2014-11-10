package procrastination.content;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class Level {

    private Rectangle mLevelSize;

    private Player mPlayer;
    private LinkedList<Enemy> mEnemies;
    private LinkedList<Bullet> mBullets;

    private LinkedList<Entity> mRemovedEntities;
    
    private BufferedImage mBackground;

    private long mEnemySpawnTime; // milliseconds
    private long mLastEnemySpawn; // milliseconds
    
    public Level(int width, int height) {
        mLevelSize = new Rectangle(0, 0, width, height);
        mPlayer = new Player(mLevelSize.width, mLevelSize.height);
        mEnemies = new LinkedList<>();
        mBullets = new LinkedList<>();
        mRemovedEntities = new LinkedList<>();
        mEnemySpawnTime = 5000;
        mLastEnemySpawn = System.currentTimeMillis() - mEnemySpawnTime;
        
        loadLevelBackground();
    }
    
    private void loadLevelBackground() {
        try {
            mBackground = ImageIO.read(new File("images" + File.separator + "terrain.jpg"));
        } catch(IOException ex) {
            System.out.println("Level background failed to load.");
            mBackground = null;
        }
    }

    public Rectangle getLevelSize() {
        return mLevelSize;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void spawnBullet(Point2D.Double position, Point2D.Double direction) {
        Bullet bullet = new Bullet(position, direction);
        mBullets.add(bullet);
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

            Enemy newEnemy = new Enemy(newEnemyPosition);
            mEnemies.add(newEnemy);
        }
    }

    public void deleteEntity(Entity entity) {
        mRemovedEntities.add(entity);
    }

    public void update() {
        //Update Objects
        mPlayer.update(this);

        spawnNewEnemies();
        for (Enemy enemy : mEnemies) {
            enemy.update(this);
        }
        for (Bullet bullet : mBullets) {
            bullet.update(this);
        }
        //Collisions
        for(Enemy enemy : mEnemies){
            //Check for collisions with bullets
            for(Bullet bullet : mBullets){
                if(bullet.intersectsBBox(enemy.getBBox())){
                    enemy.collision(bullet.getType(), this);
                    bullet.collision(enemy.getType(), this);
                }
            }
            //Check for collisions with player
            if(mPlayer.intersectsBBox(enemy.getBBox())){
                enemy.collision(mPlayer.getType(), this);
                mPlayer.collision(enemy.getType(), this);
            }
        }
        //Remove dead objects
        if (!mRemovedEntities.isEmpty()) {
            removeEntities();
        }
    }

    private void removeEntities() {
        for (Entity entity : mRemovedEntities) {
            if (entity instanceof Bullet) {
                mBullets.remove(entity);
            } else if (entity instanceof Enemy) {
                mEnemies.remove(entity);
            }
        }
        mRemovedEntities.clear();
    }

    public void draw(Graphics g) {
        if(mBackground != null) {
            g.drawImage(mBackground, 0, 0, mLevelSize.width, mLevelSize.height, null);
        }
        
        mPlayer.draw(g);

        for (Enemy enemy : mEnemies) {
            enemy.draw(g);
        }

        for (Bullet bullet : mBullets) {
            bullet.draw(g);
        }
    }

}
