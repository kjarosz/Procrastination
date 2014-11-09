package procrastination.input;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

public class MouseManager {
   private static Point mMousePosition = new Point(0,0);
   
   private static boolean mLeftButtonPressed = false;
   private static boolean mRightButtonPressed = false;
   
   public MouseManager(JComponent component) {
      component.addMouseListener(constructMouseListener());
      component.addMouseMotionListener(constructMouseMotionListener());
   }
   
   private MouseListener constructMouseListener() {
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
   
   private MouseMotionListener constructMouseMotionListener() {
      return new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent arg0) {
            setMousePosition(arg0.getPoint());
            arg0.consume();
         }

         @Override
         public void mouseMoved(MouseEvent arg0) {
            setMousePosition(arg0.getPoint());
            arg0.consume();
         }
         
      };
   }
   
   private void setMousePosition(Point position) {
      mMousePosition.x = position.x;
      mMousePosition.y = position.y;
   }
   
   public static Point getMousePosition() {
      return mMousePosition;
   }
   
   public static boolean isButtonPressed(int button) {
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
