package procrastination;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Procrastination extends JFrame implements Runnable{
	
    private static JFrame owner;
    private static WindowListener wl;
    private static KeyListener kl;
    private static GamePanel content;
    
    //The width and height of the draw region in pixels
    private int hPort = 600;
    private int wPort = 800;
    
    public static void main(String[] args) {
        owner = new Procrastination();
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
        //Create a window listener that we can add to the JFrame to generate an event when the window
        //is sent a close event from windows (like when the user presses the close button
        wl = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                content.window_closing();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        addWindowListener(wl);
        
        //Allow for keyboard input to be passed from the JFrame to the GamePanel
        kl = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    content.key_pressed_event(e.getKeyCode());
                    e.consume();
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    content.key_released_event(e.getKeyCode());
                    e.consume();
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    e.consume();
                }
            };
            addKeyListener(kl);
        
        //Add the GamePanel to the window and show it
        add(content);
        setVisible(true);
        //Start the game loop
        content.startGame();
    }
}