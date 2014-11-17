package procrastination;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import procrastination.input.KeyManager;
import procrastination.input.MouseManager;
import procrastination.menu.GameOverScreen;
import procrastination.menu.HighScoreScreen;
import procrastination.menu.TitleScreen;

public class Procrastination extends JFrame  {
    private final String TITLE_SCREEN = "Title";
    private final String GAME_SCREEN = "Game";
    private final String HIGH_SCORE_SCREEN = "High Score";
    private final String GAME_OVER_SCREEN = "Game Over";
    
    GamePanel mGamePanel;
    
    private GameOverScreen mGameOverScreen;
    
    //The width and height of the draw region in pixels
    private int wPort = 1280;
    private int hPort = 720;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Procrastination();
            }
        });
    }
        
    Procrastination(){
        setupWindow();
        
        addPanels();
        
        setVisible(true);
    }
    
    private void addPanels() {
        getContentPane().setLayout(new CardLayout());
        addTitleScreen();
        addGamePanel();
        addHighScorePanel();
        addGameOverPanel();
    }
    
    private void addTitleScreen() {
        TitleScreen screen = new TitleScreen(this);
        getContentPane().add(screen, TITLE_SCREEN);
    }
    
    private void addGamePanel() {
        //Create and add the GamePanel
        mGamePanel = new GamePanel(wPort, hPort, this);
        addListeners(mGamePanel);
        getContentPane().add(mGamePanel, GAME_SCREEN);
    }
    
    private void addHighScorePanel() {
        HighScoreScreen screen = new HighScoreScreen(this);
        getContentPane().add(screen, HIGH_SCORE_SCREEN);
    }
    
    private void addGameOverPanel() {
        GameOverScreen screen = new GameOverScreen(this);
        getContentPane().add(screen, GAME_OVER_SCREEN);
    }
    
    private void setupWindow() {
        //Changes some JFrsame settings for full screen
        getContentPane().setBackground(Color.black);
        setFocusTraversalKeysEnabled(false);
        setUndecorated(true);
        setResizable(false);
        //Make the window not go away on close so that we can do graceful closing
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        //Gets the GraphicsDevice to set the window as a full screen window
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        
        device.setFullScreenWindow(this);
    }
    
    private void addListeners(GamePanel gamePanel) {
        addWindowListener(gamePanel);
        new KeyManager(gamePanel);
        new MouseManager(gamePanel);
    }
    
    private void addWindowListener(final GamePanel gamePanel) {
       //Create a window listener that we can add to the JFrame to generate an event when the window
       //is sent a close event from windows (like when the user presses the close button
       addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e) {
               gamePanel.window_closing();
           }
       });
    }
    
    public void showTitle() {
        showPanel(TITLE_SCREEN);
    }
    
    public void startGame() {
        showPanel(GAME_SCREEN);
        mGamePanel.startGame();
    }
    
    public void showHighScore() {
        showPanel(HIGH_SCORE_SCREEN);
    }
    
    public void showGameOver(int score) {
        mGameOverScreen.showScreen(score);
        showPanel(GAME_OVER_SCREEN);        
    }
    
    private void showPanel(String panel) {
        Container contentPanel = getContentPane();
        CardLayout layout = (CardLayout)contentPanel.getLayout();
        layout.show(contentPanel, panel);
    }
    
    public void quit() {
        System.exit(0);
    }
}