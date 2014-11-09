package procrastination.content;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Enemy {
   private static final Rectangle SPRITES[] = {
         new Rectangle(28, 34, 191, 362),
         new Rectangle(213, 12, 377, 362)
   };
   
   private final double TERMINAL_VELOCITY = 90; // pixels / second
   
   private Point2D.Double mPosition;
   private Point2D.Double mDirection;
   
   private BufferedImage mSprites[];
   private int mCurrentImage;
   
   private long mLastTime; // milliseconds
   
   public Enemy(Point2D.Double position) {
      mPosition = position;
      mDirection = new Point2D.Double();
      
      mLastTime = System.currentTimeMillis();
      
      loadSprites();
   }
   
   private void loadSprites() {
      try {
         BufferedImage spriteSheet = ImageIO.read(new File("images" + File.separator + "enemy-walk-sheet.png"));
         mSprites = new BufferedImage[SPRITES.length];
         for(int i = 0; i < SPRITES.length; i++) {
            mSprites[i] = spriteSheet.getSubimage(
                  SPRITES[i].x, 
                  SPRITES[i].y, 
                  SPRITES[i].width - SPRITES[i].x, 
                  SPRITES[i].height - SPRITES[i].y);
         }
      } catch(IOException ex) {
         
      }
   }
   
   private void setDirection(Point2D.Double playerPosition) {
      mDirection.x = playerPosition.x - mPosition.x;
      mDirection.y = playerPosition.y - mPosition.y;
      double dirLength = Math.sqrt(mDirection.x*mDirection.x + mDirection.y*mDirection.y);
      mDirection.x /= dirLength;
      mDirection.y /= dirLength;
   }
   
   public void update(Player player) {
      setDirection(player.getPosition());
      
      move();
   }
   
   private void move() {
      long deltaTime = System.currentTimeMillis() - mLastTime;
      mLastTime += deltaTime;
      
      mPosition.x += mDirection.x * TERMINAL_VELOCITY * deltaTime / 1000.0f;
      mPosition.y += mDirection.y * TERMINAL_VELOCITY * deltaTime / 1000.0f;
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
      g2.scale(0.5, 0.5);
      g2.drawImage(image, 
            image.getWidth()/2, 
            image.getHeight()/2,
            -image.getWidth(),
            -image.getHeight(),
            null);
      g2.setTransform(oldTransform);
   }
}
