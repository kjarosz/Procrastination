/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package procrastination;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import procrastination.input.Keyboard;
import procrastination.input.Mouse;
import procrastination.input.Mouse.mouse_event;

/**
 *
 * @author jradatz
 */
public class GamePanel extends JPanel implements Runnable{
    private JFrame container;
    
    public Dimension gameWindowSize;    //Pixel size of the buffer
    private Dimension screenSize;       //Size of the actual screen
    private Rectangle drawScale;        //Calculated scale and position of the on screen viewport
    
    //Buffer Components
    private Image dbImage;  //Buffer where everything is drawn before being flipped to the panel
    private Graphics dbg;   //Stores the buffers graphics for easy transport
    
    //FPS Variables
    private final double DESIRED_FPS = 30;
    private final double DESIRED_FRAMERATE = (1 / DESIRED_FPS) * 1000;
    private final double MAX_BEFORE_SYNC = 2 * DESIRED_FRAMERATE;
    private final int MAX_FRAMESKIP = 5;
    private int updateSecondStart; //The start if this second for updates
    private int updatesPerSecond; //The number of updates for this second
    private int updateCycles; //The last updates per second that was calculated
    private int drawsPerSecond; //The number of draws for this second
    private int drawCycles; //The last calculated draws per second
    
    private Thread gameThread;
    private boolean running = false;
    
    /**
     * Takes the game window size as well as the containing JPanel
     * @param wPort The width of the draw buffer
     * @param hPort The height of the draw buffer
     * @param owner The JFrame that this panel is contained within
     */
    public GamePanel(int wPort, int hPort, JFrame owner){
        gameWindowSize = new Dimension(wPort, hPort);
        container = owner;
        setBackground(Color.BLACK);
    }
    
    /**
     * This is called at the start of the game loop.
     * Can be used to initialize things
     */
    private void gameStart(){
        Keyboard.getKeyboard().register_key(KeyEvent.VK_ESCAPE);
    }
    
    /**
     * This is where all of the update logic of the game will take place
     */
    private void gameUpdate(){
        //Typical loop to iterate over all mouse input since last frame
        while(Mouse.getMouse().get_queue_size() > 0){
            mouse_event me = Mouse.getMouse().next_mouse_event();
            
            System.out.println(me.toString());
        }
        //Example of keyboard input
        if(Keyboard.getKeyboard().is_key_pressed(KeyEvent.VK_ESCAPE)){
            endGame();
        }
    }
    /**
     * Called by the game loop. This is where all of our draw code will go
     * @param g The graphics object that will be used for drawing
     */
    private void gameDraw(Graphics g){
        g.setColor(Color.white);
        g.drawString("UpdatesPerSecond: " + updatesPerSecond + "; DrawsPerSecond: " + drawsPerSecond, 10, 10);
        g.setColor(Color.red);
        g.drawRect(0, 0, gameWindowSize.width - 1, gameWindowSize.height - 1);
    }
    
    /**
     * Not completely necessary to unload resources in java because its managed
     * However, this function is always called at the end of the game loop
     */
    public void gameEnd(){
        
    }
    
    /**
     * Called whenever the user closes the game window through explorer rather than our exit features
     */
    public void window_closing(){
        running = false;
    }
    
    /**
     * The main logic that controls when things can run and sleeps while waiting for the next frame
     */
    public void run(){
        //Starts FPS counter
        System.out.println("System Starting");
        updateSecondStart = (int) (System.nanoTime() / 1000000);
        updateCycles = 0;
        updatesPerSecond = 0;
        drawCycles = 0;
        drawsPerSecond = 0;
        
        gameStart();
        
        //next_update - the next time the loop should cycle
        double next_update = (double) System.nanoTime() / 1000000;
        int skipped_frames = 0;

        while (running) {
            double time = (double) System.nanoTime() / 1000000;
            //Update the updatesPerSecond counters
            if ((time - updateSecondStart) >= 1000) {
                updatesPerSecond = updateCycles;
                updateCycles = 0;
                drawsPerSecond = drawCycles;
                drawCycles = 0;
                updateSecondStart = (int)time;
            }
            //If the game loop is significantly behind it stops trying to catch
            //up and resyncs itself to current time
            if ((time - next_update) > MAX_BEFORE_SYNC) {
                next_update = time;
            }
            //If it is time to run an update cycle
            if (time >= next_update) {
                next_update += DESIRED_FRAMERATE;
                updateCycles++;
                gameUpdate();
                //If there is time to update or there has been a
                //large number of frame skips it renders the screen
                if (time < next_update || skipped_frames >= MAX_FRAMESKIP) {
                    gameRender();
                    paintScreen();
                    skipped_frames = 0;
                    drawCycles++;
                } else {
                    skipped_frames++;
                }
            }else {
                //Tells the thread to sleep when its bored
                int sleep = (int) (next_update - time);
                if (sleep > 0) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException ex) {
                        //Ignore
                    }
                }
            }
        }
        //Clean up stuffs
        gameEnd();
        container.dispose();
        System.out.println("System Exiting");
    }
    
    /**
     * Gives access to the scale of the draw rectangle for converting mouse coordinates
     * @return the draw scale
     */
    public Rectangle getDrawRectangle(){
        return drawScale;
    }
    /**
     * Calculates the scale from the gameScreen size to the screen size as well as centers
     * @param d The dimension of the screen (usually straight from JFrame method getSize())
     */
    private void setWindowSize(Dimension d){
        screenSize = d;
        drawScale = new Rectangle();
        if(screenSize.width / gameWindowSize.width < screenSize.height / gameWindowSize.height){
            //wider than tall compared to screen
            drawScale.width = screenSize.width;
            drawScale.x = 0;
            drawScale.height = (int)(((double)screenSize.width / (double)gameWindowSize.width) * (double)gameWindowSize.height);
            drawScale.y = (screenSize.height - drawScale.height) / 2;
        }else{
            //taller than wide compared to screen
            drawScale.width = (int)(((double)screenSize.height / (double)gameWindowSize.height) * (double)gameWindowSize.width);
            drawScale.x = (screenSize.width - drawScale.width) / 2;
            drawScale.height = screenSize.height;
            drawScale.y = 0;
        }
        setSize(screenSize);
    }
    
    /**
     * Creates a new thread of the game loop logic
     */
    public void startGame(){
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    /**
     * Used for rendering all of the game to the buffer
     */
    private void gameRender(){
        //Creates Double Buffer with appropriate error checking
        if (dbImage == null) {
            try{
                dbImage = createImage(gameWindowSize.width, gameWindowSize.height);
            }catch(NullPointerException ex){
                System.err.println(ex);
            }
            if (dbImage == null) {
                System.out.println("Failed to Init DB");
                return;
            }
            dbg = dbImage.getGraphics();
        }
        //Flush Double Buffer
        dbg.setColor(Color.BLACK);
        dbg.fillRect(0, 0, gameWindowSize.width, gameWindowSize.height);
        //Draw Game Elements
        dbg.setColor(Color.BLACK);
        //HEY DRAW HERE
        gameDraw(dbg);
    }
    
    /**
     * Flips the buffer to the panel
     */
    private void paintScreen(){
        Graphics g;
        try {
            g = this.getGraphics();
            if (g != null && dbImage != null) {
                if(screenSize == null || screenSize.width == 0 || screenSize.height == 0){
                    setWindowSize(container.getSize());
                }
                g.drawImage(dbImage, drawScale.x, drawScale.y, drawScale.width, drawScale.height, null);
            }else{
                System.out.println("Error getting graphics");
            }
            Toolkit.getDefaultToolkit().sync();//Necesary for certain operating systems
            g.dispose();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    /**
     * This function will end the game
     */
    public void endGame(){
        running = false;
    }
}