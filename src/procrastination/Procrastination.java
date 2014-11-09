package procrastination;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import procrastination.input.KeyManager;
import procrastination.input.MouseManager;

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
        new KeyManager(gamePanel);
        new MouseManager(gamePanel);
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
}