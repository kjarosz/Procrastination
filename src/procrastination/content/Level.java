package procrastination.content;

import java.awt.Dimension;
import java.awt.Graphics;

public class Level {
   private Dimension mLevelSize;
   
   private Player mPlayer;
   
	public Level(int width, int height) {
	   mLevelSize = new Dimension(width, height);
		loadLevel();
      mPlayer = new Player(mLevelSize.width, mLevelSize.height);
	}

	private void loadLevel() {
		// Any obstacle types or level settings loaded in here
	}
	
	public void update() {
	   mPlayer.update();
	}
	
	public void draw(Graphics g) {
	   mPlayer.draw(g);
	}
}
