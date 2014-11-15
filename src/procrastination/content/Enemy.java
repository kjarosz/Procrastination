package procrastination.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import procrastination.content.Powerup.powerups;

public class Enemy extends Entity {
   private static final Rectangle SPRITES[] = {
         new Rectangle(28, 34, 191, 362),
         new Rectangle(213, 12, 377, 362),
         new Rectangle(421, 16, 568, 362),
         new Rectangle(607, 36, 750, 363),
         new Rectangle(796, 34, 960, 362)
   };
   
   private static BufferedImage mSprites[];
   
   private final double TERMINAL_VELOCITY = 90; // pixels / second
   
   private int mCurrentImage;
   private long mLastImageSwitch; // milliseconds
   private long mFrameTime; // milliseconds
   
   private powerups newSpawn;
   
   public Enemy(Point2D.Double position, powerups spawn) {
      setPosition(position);
      setBBox(81, 162);
      setType(objectTypes.ENEMY);
      mLastTime = System.currentTimeMillis();
      
      loadSprites();
      setCurrentImage(mSprites[mCurrentImage]);
      mLastImageSwitch = System.currentTimeMillis();
      mFrameTime = 100;
      
      newSpawn = spawn;
   }
   
   private void loadSprites() {
       if(mSprites == null){
           mSprites = loadSprites(new File("images" + File.separator + "enemy-walk-sheet.png"), SPRITES);
       }
      mCurrentImage = 0;
   }
   
   @Override
   public void update(Level level) {
      Point2D.Double playerPos = level.getPlayer().getPosition();
      Point2D.Double direction = new Point2D.Double();
      direction.x = playerPos.x - mPosition.x;
      direction.y = playerPos.y - mPosition.y;
      setDirection(direction);
      
      move(TERMINAL_VELOCITY);
      
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
   
   @Override
   public void draw(Graphics g, int xOffset, int yOffset) {
      draw(g, -0.5, xOffset, yOffset);
      //drawBBox(g, Color.MAGENTA, xOffset, yOffset);
   }

    @Override
    public void collision(Entity other, Level level) {
        switch(other.getType()){
            case BULLET:
                level.deleteEntity(this);
                if(newSpawn != null){
                    level.addEntity(new Powerup(newSpawn, mPosition));
                }
                break;
            case PLAYER:
                level.deleteEntity(this);
                break;
            case BULLET_EXPLOSION:
                level.deleteEntity(this);
                if(newSpawn != null){
                    level.addEntity(new Powerup(newSpawn, mPosition));
                }
                break;
        }
    }
}
