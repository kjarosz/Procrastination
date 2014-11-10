package procrastination.content;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

import procrastination.input.ControlFunction;
import procrastination.input.KeyManager;
import procrastination.input.KeyMapping;
import procrastination.input.MouseManager;

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
   
   private final double TERMINAL_VELOCITY = 100.0; // pixels per second
   private final Point2D.Double LEFT_VELOCITY = new Point2D.Double(-TERMINAL_VELOCITY, 0.0);
   private final Point2D.Double RIGHT_VELOCITY = new Point2D.Double(TERMINAL_VELOCITY, 0.0);
   private final Point2D.Double DOWN_VELOCITY = new Point2D.Double(0.0, TERMINAL_VELOCITY);
   private final Point2D.Double UP_VELOCITY = new Point2D.Double(0.0, -TERMINAL_VELOCITY);
   
   private BufferedImage mSprites[];
   private int mCurrentImage;
   
   private LinkedList<KeyMapping> mKeyMappings;
   
   private long mFiringCooldown = 500; // milliseconds
   private long mLastFiredBullet; // milliseconds
   
	private Point2D.Double mVelocity;
	
	public Player(int levelWidth, int levelHeight) {
	   setPosition(new Point2D.Double(levelWidth/2, levelHeight/2));
		mVelocity = new Point2D.Double(0.0, 0.0);
		
		loadSprites();
		
		constructKeyMappings();
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
	
	public void setDirection(Point2D.Double direction) {
		double length = Math.sqrt(direction.x*direction.x + direction.y*direction.y);
		mDirection.x = direction.x/length;
		mDirection.y = direction.y/length;
	}
	
	public Point2D.Double getPosition() {
		return mPosition;
	}
	
	public void update(Level level) {
	   processKeyInputs();
	   processMouse(level);
	   move(mVelocity);
	}
	
	private void processKeyInputs() {
      for(KeyMapping keyMapping: mKeyMappings) {
         if(KeyManager.isKeyPressed(keyMapping.keyCode) && !keyMapping.pressProcessed) {
            keyMapping.keyFunction.keyPressed();
            keyMapping.pressProcessed = true;
            keyMapping.releaseProcessed = false;
         } else if(!KeyManager.isKeyPressed(keyMapping.keyCode) && !keyMapping.releaseProcessed) {
            keyMapping.keyFunction.keyReleased();
            keyMapping.releaseProcessed = true;
            keyMapping.pressProcessed = false;
         }
      }
	}
	
	private void processMouse(Level level) {
	   Point mousePos = MouseManager.getMousePosition();
	   setDirection(new Point2D.Double(mousePos.x - mPosition.x, mousePos.y - mPosition.y));
	   
	   if(MouseManager.isButtonPressed(MouseEvent.BUTTON1)) {
	      if(System.currentTimeMillis() - mLastFiredBullet > mFiringCooldown) {
	         mLastFiredBullet = System.currentTimeMillis();
	         
	         level.spawnBullet(mPosition, mDirection);
	      }
	   }
	}
}
