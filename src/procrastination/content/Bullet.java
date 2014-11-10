package procrastination.content;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;

public class Bullet extends Entity {
   private static final Rectangle SPRITES[] = {
         new Rectangle(0, 0, 120, 180),
         new Rectangle(177, 2, 297, 188)
   };
   
   private static final double TERMINAL_VELOCITY = 250; // pixels / second
   
   private BufferedImage mSprites[];
   private int mCurrentImage;
   private long mLastImageSwitch; // milliseconds
   private long mFrameTime; // milliseconds
   
   public Bullet(Point2D.Double position, Point2D.Double direction) {
      setPosition(position);
      setDirection(direction);
      setType(objectTypes.BULLET);
      setBBox(30, 45);
      
      mSprites = loadSprites(new File("images" + File.separator + "shot animation.png"), SPRITES);
      mCurrentImage = 0;
      mLastImageSwitch = System.currentTimeMillis();
      mFrameTime = 100;
      setCurrentImage(mSprites[mCurrentImage]);
   }
   
   public void update(Level level) {
      move(TERMINAL_VELOCITY);
      
      if(!level.getLevelSize().contains(mPosition)) {
         level.deleteEntity(this);
      }
      
      animate();
   }
   
   private void animate() {
       long deltaTime = System.currentTimeMillis() - mLastImageSwitch;
       if(deltaTime > mFrameTime) {
           mLastImageSwitch += deltaTime;
           mCurrentImage++;
           if(mCurrentImage >= mSprites.length) {
               mCurrentImage = 0;
           }
           setCurrentImage(mSprites[mCurrentImage]);
       }
       
   }
   
   public void draw(Graphics g, int xOffset, int yOffset) {
      draw(g, 0.25, xOffset, yOffset);
      drawBBox(g, Color.MAGENTA, xOffset, yOffset);
   }

    @Override
    public void collision(objectTypes other, Level level) {
        switch (other){
            case ENEMY:
                level.deleteEntity(this);
                break;
        }
    }
}
