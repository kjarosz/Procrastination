package procrastination.content;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Level {
   private Dimension mLevelSize;
   
   private Player mPlayer;
   private LinkedList<Enemy> mEnemies;
   private LinkedList<Bullet> mBullets;
      
   
   private long mEnemySpawnTime; // milliseconds
   private long mLastEnemySpawn; // milliseconds
   
   
	public Level(int width, int height) {
	   mLevelSize = new Dimension(width, height);
		loadLevel();
      mPlayer = new Player(mLevelSize.width, mLevelSize.height);
      mEnemies = new LinkedList<>();
      mBullets = new LinkedList<>();
      mEnemySpawnTime = 5000;
      mLastEnemySpawn = System.currentTimeMillis() - mEnemySpawnTime;
	}

	private void loadLevel() {
		// Any obstacle types or level settings loaded in here
	}
	
	public Player getPlayer() {
	   return mPlayer;
	}
	
	public void spawnBullet(Point2D.Double position, Point2D.Double direction) {
	   Bullet bullet = new Bullet(position, direction);
	   mBullets.add(bullet);
	}
	
	public void update() {
	   mPlayer.update(this);
	   
	   spawnNewEnemies();
	   
	   for(Enemy enemy: mEnemies) {
	      enemy.update(this);
	   }
	   
	   for(Bullet bullet: mBullets) {
	      bullet.update(this);
	   }
	}
	
	private void spawnNewEnemies() {
	   while(System.currentTimeMillis() - mLastEnemySpawn > mEnemySpawnTime) {
	      mLastEnemySpawn += mEnemySpawnTime;
	      
	      Point2D.Double newEnemyPosition = new Point2D.Double();
	      int side = (int)(Math.random()*4);
	      switch(side) {
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
	
	public void draw(Graphics g) {
	   mPlayer.draw(g);
	   
	   for(Enemy enemy: mEnemies) {
	      enemy.draw(g);
	   }
	   
	   for(Bullet bullet: mBullets) {
	      bullet.draw(g);
	   }
	}
	
	
}
