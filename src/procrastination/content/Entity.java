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

public abstract class Entity {
   protected Point2D.Double mPosition;
   protected Point2D.Double mDirection;
   
   protected long mLastTime; // milliseconds
   
   private BufferedImage mImage;
   
   public Entity() {
      mPosition = new Point2D.Double();
      mDirection = new Point2D.Double();
      
      mLastTime = System.currentTimeMillis();
   }
   
   protected BufferedImage[] loadSprites(File spriteSheetFile, Rectangle spriteMappings[]) {
      try {
         BufferedImage spriteSheet = ImageIO.read(spriteSheetFile);
         if(spriteSheet == null)
            throw new RuntimeException("\"" + spriteSheetFile.getPath() + "\" sprite sheet could not be loaded.");
         
         BufferedImage sprites[] = new BufferedImage[spriteMappings.length];
         for(int i = 0; i < spriteMappings.length; i++) {
            sprites[i] = spriteSheet.getSubimage(
                  spriteMappings[i].x, 
                  spriteMappings[i].y, 
                  spriteMappings[i].width - spriteMappings[i].x, 
                  spriteMappings[i].height - spriteMappings[i].y);
         }
         return sprites;
      } catch (IOException ex) {
         System.out.println("Could not read sprite sheet.");
         throw new RuntimeException("\"" + spriteSheetFile.getPath() + "\" sprite sheet could not be loaded.");
      } catch (Exception ex) {
         System.out.println(ex.getMessage());
         throw new RuntimeException("\"" + spriteSheetFile.getPath() + "\" sprite sheet could not be loaded.");
      }
   }
   
   public void setPosition(Point2D.Double position) {
      mPosition.x = position.x;
      mPosition.y = position.y;
   }
   
   public Point2D.Double getPosition() {
      return mPosition;
   }
   
   public void setDirection(Point2D.Double direction) {
      double length = Math.sqrt(direction.x*direction.x + direction.y*direction.y);
      mDirection.x = direction.x/length;
      mDirection.y = direction.y/length;
   }
   
   public Point2D.Double getDirection() {
      return mDirection;
   }
   
   protected void setCurrentImage(BufferedImage image) {
      mImage = image;
   }
   
   public abstract void update(Level level);
   
   protected void move(double velocity) {
      long deltaTime = System.currentTimeMillis() - mLastTime;
      mLastTime += deltaTime;
      
      mPosition.x += mDirection.x * velocity * deltaTime / 1000.0f;
      mPosition.y += mDirection.y * velocity * deltaTime / 1000.0f;
   }
   
   protected void move(Point2D.Double velocity) {
      long deltaTime = System.currentTimeMillis() - mLastTime;
      mLastTime += deltaTime;
      
      mPosition.x += velocity.x * deltaTime / 1000.0f;
      mPosition.y += velocity.y * deltaTime / 1000.0f;
   }
   
   public void draw(Graphics g) {
      draw(g, 1.0);
   }
   
   protected void draw(Graphics g, double scale) {
      Graphics2D g2 = (Graphics2D)g;
      AffineTransform oldTransform = g2.getTransform();
      g2.translate(mPosition.x, mPosition.y);
      if(mDirection.x < 0) {
         g2.rotate(Math.atan(mDirection.y / mDirection.x) - 3.14/2);
      } else {
         g2.rotate(Math.atan(mDirection.y / mDirection.x) + 3.14/2);
      }
      g2.scale(scale, scale);
      g2.drawImage(mImage, 
            -mImage.getWidth()/2, 
            -mImage.getHeight()/2,
            mImage.getWidth(),
            mImage.getHeight(),
            null);
      g2.setTransform(oldTransform);
   }
}
