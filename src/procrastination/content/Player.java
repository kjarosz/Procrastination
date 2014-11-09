package procrastination.content;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import procrastination.input.ControlFunction;
import procrastination.input.KeyManager;
import procrastination.input.KeyMapping;
import procrastination.input.MouseManager;

public class Player {
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
   
   private LinkedList<KeyMapping> mKeyMappings;
   
   private long mLastTime; // milliseconds
   
	private Point2D.Double mPosition;
	private Point2D.Double mVelocity;
	private Point2D.Double mDirection;
	
	public Player(int levelWidth, int levelHeight) {
		mPosition = new Point2D.Double(levelWidth/2, levelHeight/2);
		mVelocity = new Point2D.Double(0.0, 0.0);
		mDirection = new Point2D.Double(0.0, 0.0);
		
		mLastTime = System.currentTimeMillis();
		
		loadSpriteSheet();
		constructKeyMappings();
	}
	
	private void loadSpriteSheet() {
	   
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
	
	public void update() {
	   processKeyInputs();
	   processMouse();
	   move();
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
	
	private void processMouse() {
	   Point mousePos = MouseManager.getMousePosition();
	   setDirection(new Point2D.Double(mousePos.x - mPosition.x, mousePos.y - mPosition.y));
	}
	
	private void move() {
	   long deltaTime = System.currentTimeMillis() - mLastTime;
	   mLastTime += deltaTime;
	   
	   double deltaTimeSeconds = deltaTime / 1000.0;
	   
	   mPosition.x += mVelocity.x * deltaTimeSeconds;
	   mPosition.y += mVelocity.y * deltaTimeSeconds;
	}
	
	public void draw(Graphics g) {
	   g.fillRect((int)mPosition.x - 16, (int)mPosition.y - 16, 32, 32);
	   g.drawLine((int)mPosition.x, (int)mPosition.y, 
	         (int)(mPosition.x + mDirection.x*64), (int)(mPosition.y + mDirection.y*64));
	}
}
