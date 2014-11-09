package procrastination.content;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import procrastination.input.ControlFunction;
import procrastination.input.KeyManager;
import procrastination.input.KeyMapping;
import procrastination.input.MouseManager;

public class Player {
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
   
   
   private long mLastTime; // milliseconds
   
   private long mFiringCooldown = 500; // milliseconds
   private long mLastFiredBullet; // milliseconds
   
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
	   try {
	      BufferedImage spriteSheet = ImageIO.read(new File("images" + File.separator + "characterwalkspritesheetv1.png"));
	      mSprites = new BufferedImage[SPRITES.length];
	      for(int i = 0; i < SPRITES.length; i++) {
	         mSprites[i] = spriteSheet.getSubimage(
	               SPRITES[i].x, 
	               SPRITES[i].y, 
	               SPRITES[i].width - SPRITES[i].x, 
	               SPRITES[i].height - SPRITES[i].y);
	      }
	      mCurrentImage = 0;
	   } catch (IOException ex) {
	      System.out.println("Could not read sprite sheet.");
	      throw new RuntimeException("Main character sprite sheet could not be loaded.");
	   }
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
	   
	   if(MouseManager.isButtonPressed(MouseEvent.BUTTON1)) {
	      if(System.currentTimeMillis() - mLastFiredBullet > mFiringCooldown) {
	         mFiringCooldown = System.currentTimeMillis();
	         
	         // Fire booleet
	      }
	   }
	}
	
	private void move() {
	   long deltaTime = System.currentTimeMillis() - mLastTime;
	   mLastTime += deltaTime;
	   
	   double deltaTimeSeconds = deltaTime / 1000.0;
	   
	   mPosition.x += mVelocity.x * deltaTimeSeconds;
	   mPosition.y += mVelocity.y * deltaTimeSeconds;
	}
	
	public void draw(Graphics g) {
	   BufferedImage image = mSprites[mCurrentImage];
	   
	   Graphics2D g2 = (Graphics2D)g;
	   AffineTransform oldTransform = g2.getTransform();
	   g2.translate(mPosition.x, mPosition.y);
	   if(mDirection.x < 0) {
         g2.rotate(Math.atan(mDirection.y / mDirection.x) - 3.14/2);
	   } else {
         g2.rotate(Math.atan(mDirection.y / mDirection.x) + 3.14/2);
	   }
	   g2.drawImage(image, 
	         -image.getWidth()/2, 
	         -image.getHeight()/2,
	         image.getWidth(),
	         image.getHeight(),
	         null);
	   g2.setTransform(oldTransform);
	}
}
