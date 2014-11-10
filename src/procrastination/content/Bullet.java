package procrastination.content;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;

public class Bullet extends Entity {
   private static final Rectangle SPRITES[] = {
         new Rectangle(0, 0, 120, 180)
   };
   
   private static final double TERMINAL_VELOCITY = 150; // pixels / second
   
   private BufferedImage mSprites[];
   private int mCurrentImage;
   
   public Bullet(Point2D.Double position, Point2D.Double direction) {
      setPosition(position);
      setDirection(direction);
      
      setBBox(30, 45);
      
      mSprites = loadSprites(new File("images" + File.separator + "shot animation.png"), SPRITES);
      mCurrentImage = 0;
      setCurrentImage(mSprites[mCurrentImage]);
   }
   
   public void update(Level level) {
      move(TERMINAL_VELOCITY);
      
      if(!level.getLevelSize().contains(mPosition)) {
         level.deleteEntity(this);
      }
   }
   
   public void draw(Graphics g) {
      draw(g, 0.25);
      drawBBox(g, Color.MAGENTA);
   }
}
