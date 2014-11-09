package procrastination.content;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class Enemy {
   private final double TERMINAL_VELOCITY = 90; // pixels / second
   
   private Point2D.Double mPosition;
   private Point2D.Double mVelocity;
   
   private long mLastTime; // milliseconds
   
   public Enemy(Point2D.Double position) {
      mPosition = position;
      mVelocity = new Point2D.Double();
      
      mLastTime = System.currentTimeMillis();
   }
   
   private void setVelocity(Point2D.Double direction) {
      double length = Math.sqrt(direction.x*direction.x + direction.y*direction.y);
      mVelocity.x = (direction.x / length) * TERMINAL_VELOCITY;
      mVelocity.y = (direction.y / length) * TERMINAL_VELOCITY;
   }
   
   public void update(Player player) {
      Point2D.Double playerPosition = player.getPosition();
      Point2D.Double directionVector = new Point2D.Double();
      directionVector.x = playerPosition.x - mPosition.x;
      directionVector.y = playerPosition.y - mPosition.y;
      setVelocity(directionVector);
      
      move();
   }
   
   private void move() {
      long deltaTime = System.currentTimeMillis() - mLastTime;
      mLastTime += deltaTime;
      
      mPosition.x += mVelocity.x * deltaTime / 1000.0f;
      mPosition.y += mVelocity.y * deltaTime / 1000.0f;
   }
   
   public void draw(Graphics g) {
      Color oldColor = g.getColor();
      g.setColor(Color.BLUE);
      g.fillRect((int)mPosition.x - 16, (int)mPosition.y - 16, 32, 32);
      g.setColor(oldColor);
   }
}
