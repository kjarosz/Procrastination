package procrastination.content;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.LinkedList;

public class Level {
   private Dimension mLevelSize;
   
   private Player mPlayer;
   private LinkedList<Enemy> mEnemies;
   
	public Level(int width, int height) {
	   mLevelSize = new Dimension(width, height);
		loadLevel();
      mPlayer = new Player(mLevelSize.width, mLevelSize.height);
      mEnemies = new LinkedList<>();
	}

	private void loadLevel() {
		// Any obstacle types or level settings loaded in here
	}
	
	public void update() {
	   mPlayer.update();
	   
	   spawnNewEnemies();
	   
	   for(Enemy enemy: mEnemies) {
	      enemy.update(mPlayer);
	   }
	}
	
	private void spawnNewEnemies() {
	   
	}
	
	public void draw(Graphics g) {
	   mPlayer.draw(g);
	   
	   for(Enemy enemy: mEnemies)
	      enemy.draw(g);
	}
}
