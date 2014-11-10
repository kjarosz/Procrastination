package procrastination.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class Enemy extends Entity {
   private static final Rectangle SPRITES[] = {
         new Rectangle(28, 34, 191, 362),
         new Rectangle(213, 12, 377, 362)
   };
   
   private final double TERMINAL_VELOCITY = 90; // pixels / second
   
   private BufferedImage mSprites[];
   private int mCurrentImage;
   
   public Enemy(Point2D.Double position) {
      setPosition(position);
      setBBox(81, 162);
      setType(objectTypes.ENEMY);
      mLastTime = System.currentTimeMillis();
      
      loadSprites();
      setCurrentImage(mSprites[mCurrentImage]);
   }
   
   private void loadSprites() {
      mSprites = loadSprites(new File("images" + File.separator + "enemy-walk-sheet.png"), SPRITES);
      mCurrentImage = 0;
   }
   
   public void update(Level level) {
      Point2D.Double playerPos = level.getPlayer().getPosition();
      Point2D.Double direction = new Point2D.Double();
      direction.x = playerPos.x - mPosition.x;
      direction.y = playerPos.y - mPosition.y;
      setDirection(direction);
      
      move(TERMINAL_VELOCITY);
   }
   
   public void draw(Graphics g) {
      draw(g, -0.5);
      drawBBox(g, Color.MAGENTA);
   }

    @Override
    public void collision(objectTypes other, Level level) {
        switch(other){
            case BULLET:
                level.deleteEntity(this);
                break;
            case PLAYER:
                level.deleteEntity(this);
                break;
        }
    }
}
