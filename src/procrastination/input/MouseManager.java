package procrastination.input;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import procrastination.GamePanel;

public class MouseManager {
   private static Point mMousePosition = new Point(0,0);
   
   private static boolean mLeftButtonPressed = false;
   private static boolean mRightButtonPressed = false;
   
   public MouseManager(GamePanel component) {
      component.addMouseListener(constructMouseListener(component));
      component.addMouseMotionListener(constructMouseMotionListener(component));
   }
   
   private MouseListener constructMouseListener(GamePanel component) {
      return new MouseListener() {
         @Override
         public void mouseClicked(MouseEvent arg0) {}

         @Override
         public void mouseEntered(MouseEvent arg0) {}

         @Override
         public void mouseExited(MouseEvent arg0) { }

         @Override
         public void mousePressed(MouseEvent arg0) {
            switch(arg0.getButton()) {
            case MouseEvent.BUTTON1:
               mLeftButtonPressed = true;
               break;
            case MouseEvent.BUTTON3:
               mRightButtonPressed = true;
               break;
            }
         }

         @Override
         public void mouseReleased(MouseEvent arg0) {
            switch(arg0.getButton()) {
            case MouseEvent.BUTTON1:
               mLeftButtonPressed = false;
               break;
            case MouseEvent.BUTTON3:
               mRightButtonPressed = false;
               break;
            }
         }
      };
   }
   
   private MouseMotionListener constructMouseMotionListener(final GamePanel component) {
      return new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent arg0) { 
            setMousePosition(scalePoint(arg0.getPoint(), component));
            arg0.consume();
         }

         @Override
         public void mouseMoved(MouseEvent arg0) {
            setMousePosition(scalePoint(arg0.getPoint(), component));
            arg0.consume();
         }
         
      };
   }
   
   private Point scalePoint(Point mousePos, GamePanel gamePanel) {
      Rectangle drawRect = gamePanel.getDrawRectangle();
      if(drawRect == null) {
         return mousePos;
      }
      
      Point scaledLocation = new Point();
      scaledLocation.x = (int)((mousePos.x - drawRect.x)/ (double)drawRect.width * (double)gamePanel.gameWindowSize.width);
      scaledLocation.y = (int)((mousePos.y - drawRect.y) / (double)drawRect.height * (double)gamePanel.gameWindowSize.height);
      return scaledLocation;
   }
   
   private synchronized void setMousePosition(Point position) {
      mMousePosition.x = position.x;
      mMousePosition.y = position.y;
   }
   
   public synchronized static Point getMousePosition() {
      return mMousePosition;
   }
   
   public synchronized static boolean isButtonPressed(int button) {
      switch(button) {
      case MouseEvent.BUTTON1:
         return mLeftButtonPressed;
      case MouseEvent.BUTTON3:
         return mRightButtonPressed;
      default:
         return false;
      }
   }
}
