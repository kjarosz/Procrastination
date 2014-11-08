package procrastination;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import procrastination.input.Keyboard;
import procrastination.input.Mouse;

public class Procrastination extends JFrame implements Runnable{
	private GamePanel content;
    
    //The width and height of the draw region in pixels
    private int hPort = 600;
    private int wPort = 800;
    
    public static void main(String[] args) {
        new Procrastination();
    }
        
    Procrastination(){
        SwingUtilities.invokeLater(this);
    }

    @Override
    public void run() {
        //Changes some JFrsame settings for full screen
        getContentPane().setBackground(Color.black);
        setFocusTraversalKeysEnabled(false);
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Gets the GraphicsDevice to set the window as a full screen window
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        
        device.setFullScreenWindow(this);
        
        //Create and add the GamePanel
        content = new GamePanel(wPort, hPort, this);
        
        //Make the window not go away on close so that we can do gracefull closing
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addListeners(content);
        
        //Add the GamePanel to the window and show it
        add(content);
        setVisible(true);
        //Start the game loop
        content.startGame();
    }
    
    private void addListeners(GamePanel gamePanel) {
        addWindowListener();
        addKeyListener();
        addMouseListener(gamePanel);
        addMouseMotionListener(gamePanel);
        addMouseWheelListener(gamePanel);
    }
    
    private void addWindowListener() {
       //Create a window listener that we can add to the JFrame to generate an event when the window
       //is sent a close event from windows (like when the user presses the close button
       addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e) {
               content.window_closing();
           }
       });
    }
    
    private void addKeyListener() {
       //Allow for keyboard input to be passed from the JFrame to the keyboard class
        addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
               Keyboard.getKeyboard().key_press(e.getKeyCode());
               e.consume();
           }

           @Override
           public void keyReleased(KeyEvent e) {
               Keyboard.getKeyboard().key_release(e.getKeyCode());
               e.consume();
           }

           @Override
           public void keyTyped(KeyEvent e) {
               e.consume();
           }
       });
    }
    
    private void addMouseListener(GamePanel gamePanel) {
       	//Allow for mouse input to be passed from the JFrame to the mouse class
        gamePanel.addMouseListener(new MouseListener() {
	      @Override
	      public void mouseClicked(MouseEvent me) {
	          me.consume();
	      }
	
	      @Override
	      public void mousePressed(MouseEvent me) {
	          Mouse.getMouse().mouse_press(me);
	          me.consume();
	      }
	
	      @Override
	      public void mouseReleased(MouseEvent me) {
	          Mouse.getMouse().mouse_release(me);
	          me.consume();
	      }
	
	      @Override
	      public void mouseEntered(MouseEvent me) {
	          me.consume();
	      }
	
	      @Override
	      public void mouseExited(MouseEvent me) {
	          Mouse.getMouse().clear_buttons();
	          me.consume();
	      }
        });
    }
    
    private void addMouseMotionListener(GamePanel gamePanel) {
       gamePanel.addMouseMotionListener(new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent arg0) {
            Mouse.getMouse().mouse_move(arg0.getPoint());
         }

         @Override
         public void mouseMoved(MouseEvent arg0) {
            // TODO Auto-generated method stub
            Mouse.getMouse().mouse_move(arg0.getPoint());
         }
          
       });
    }
    
    private void addMouseWheelListener(GamePanel gamePanel) {
        gamePanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                Mouse.getMouse().wheel_moved(mwe.getPreciseWheelRotation());
                mwe.consume();
            }
        });
    }
}