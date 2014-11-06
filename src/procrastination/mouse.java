package procrastination;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class mouse {
    //The different types of events that can occour
    public static final int LEFT_BUTTON = MouseEvent.BUTTON1;
    public static final int MIDDLE_BUTTON = MouseEvent.BUTTON2;
    public static final int RIGHT_BUTTON = MouseEvent.BUTTON3;
    public static final int MOUSE_WHEEL = MouseEvent.MOUSE_WHEEL;
    
    //Public access for the mouse
    public static mouse instance;
    
    //Stores the state of the three buttons
    private boolean left_pressed = false;
    private boolean middle_pressed = false;
    private boolean right_pressed = false;
    
    //The points where the mouse was last pressed at
    private Point left_start = new Point(0, 0);
    private Point right_start = new Point(0, 0);
    private Point middle_start = new Point(0, 0);
    
    //Stores any mouse events that happen for processing
    private ArrayList<mouse_event> events;
    
    //Access for calculating real world coordinates of the mouse to game screen coordinates
    private GamePanel gamePanel;
    private Rectangle drawRectangle;
    
    mouse(GamePanel p){
        if(instance != null){
            System.err.println("The mouse can only be initialized once");
        }else{
            instance = this;
        }
        gamePanel = p;
        events = new ArrayList<>();
    }
    
    /**
     * Called by the mouseListener whenever there is a mouse press
     * @param button the button that was pressed
     */
    public void mouse_press(int button){
        if(mouse_in_window()){
            if(button == LEFT_BUTTON){
                left_pressed = true;
                left_start = new Point(get_mouse_x(), get_mouse_y());
            }else if(button == MIDDLE_BUTTON){
                middle_pressed = true;
                middle_start = new Point(get_mouse_x(), get_mouse_y());
            }else if(button == RIGHT_BUTTON){
                right_pressed = true;
                right_start = new Point(get_mouse_x(), get_mouse_y());
            }
            events.add(new mouse_event(button, get_mouse_x(), get_mouse_y(), true));
        }
    }
    
    /**
     * Called by the mouseListener whenever there is a mouse released
     * @param button the button that was released
     */
    public void mouse_release(int button){
        if(mouse_in_window()){
            if(button == LEFT_BUTTON){
                left_pressed = false;
            }else if(button == MIDDLE_BUTTON){
                middle_pressed = false;
            }else if(button == RIGHT_BUTTON){
                right_pressed = false;
            }
            events.add(new mouse_event(button, get_mouse_x(), get_mouse_y(), false));
        }
    }
    
    /**
     * Called by the mouseWheelListener whenever the mouse wheel is rotated
     * @param rotation the amount that the wheel is rotated
     */
    public void wheel_moved(double rotation) {
        if(Math.round(rotation) != 0){
            events.add(new mouse_event(-(int)Math.rint(rotation)));
        }
    }
    
    /**
     * Clears the state of all the buttons
     */
    public void clear_buttons(){
        left_pressed = false;
        middle_pressed = false;
        right_pressed = false;
    }
    
    /**
     *  Gets the next event in the mouse event queue and removes it from the queue
     * @return the next event if there is one, null if not
     */
    public mouse_event next_mouse_event(){
        if(events.size() > 0){
            return events.remove(0);
        }
        return null;
    }
    
    /**
     * The mouse event queue
     * @return the size of the mouse event queue
     */
    public int get_queue_size(){
        return events.size();
    }
    
    /**
     * Clears the mouse event queue
     */
    public void clear_queue(){
        events.clear();
    }
    
    /**
     * Gets the mouse x position scaled to game screen coordinates
     * @return mouse x
     */
    public int get_mouse_x() {
        Point mouse_loc = gamePanel.getMousePosition();
        if(drawRectangle == null){
            drawRectangle = gamePanel.getDrawRectangle();
        }
        if(in_rectangle(drawRectangle.x, drawRectangle.y, drawRectangle.x + drawRectangle.width, drawRectangle.y + drawRectangle.height, mouse_loc.x, mouse_loc.y)){
            return (int)((((double)mouse_loc.x - (double)drawRectangle.x) / (double)drawRectangle.width) * (double)gamePanel.gameWindowSize.width);
        }else{
            return -1;
        }
    }
    
    /**
     * Gets the mouse y position scaled to game screen coordinates
     * @return mouse y
     */
    public int get_mouse_y() {
        Point mouse_loc = gamePanel.getMousePosition();
        if(drawRectangle == null){
            drawRectangle = gamePanel.getDrawRectangle();
        }
        if(in_rectangle(drawRectangle.x, drawRectangle.y, drawRectangle.x + drawRectangle.width, drawRectangle.y + drawRectangle.height, mouse_loc.x, mouse_loc.y)){
            return (int)((((double)mouse_loc.y - (double)drawRectangle.y) / (double)drawRectangle.height) * (double)gamePanel.gameWindowSize.width);
        }else{
            return -1;
        }
    }
    
    /**
     * Checks to see if the mouse is over the game window
     * @return True if the mouse is within the game window
     */
    public boolean mouse_in_window(){
        return get_mouse_x() != -1 && get_mouse_y() != -1;
    }
    
    /**
     * Checks to see if the mouse is in the rectangle formed by (x1, y1) and (x2, y2)
     * @param x1 the x coordinate of the first point
     * @param y1 the y coordinate of the first point
     * @param x2 the x coordinate of the second point
     * @param y2 the y coordinate of the second point
     * @return True if the mouse is within those four points
     */
    public boolean mouse_in_rectangle(int x1, int y1, int x2, int y2) {
        return in_rectangle(x1, y1, x2, y2, get_mouse_x(), get_mouse_y());
    }
    
    /**
     * Checks to see if (m1, y1) is in the rectangle formed by (x1, y1) and (x2, y2)
     * @param x1 the x coordinate of the first point
     * @param y1 the y coordinate of the first point
     * @param x2 the x coordinate of the second point
     * @param y2 the y coordinate of the second point
     * @param mx the x coordinate of the point that is being checked
     * @param my the y coordinate of the point that is being checked
     * @return true if the point is within the rectangle
     */
    private boolean in_rectangle(int x1, int y1, int x2, int y2, int mx, int my){
        return mx > x1 && mx < x2 && my > y1 && my < y2;
    }
    
    /**
     * Checks to see if the specified button is pressed
     * @param button the button being checked
     * @return true if the button is pressed
     */
    public boolean is_button_pressed(int button){
        switch (button){
            case LEFT_BUTTON:
                return left_pressed;
            case MIDDLE_BUTTON:
                return middle_pressed;
            case RIGHT_BUTTON:
                return right_pressed;
            default:
                return false;
        }
    }
    
    /**
     * Get the last press location for a specified button
     * @param button the button being checked
     * @return the last press location
     */
    public Point get_last_press_location(int button){
        switch (button){
            case LEFT_BUTTON:
                return left_start;
            case RIGHT_BUTTON:
                return right_start;
            case MIDDLE_BUTTON:
                return middle_start;
        }
        return new Point(-1, -1);
    }
    
    /**
     * Clears the buttons and the mouse queue
     */
    public void clear() {
        clear_queue();
        clear_buttons();
    }
    
    /**
     * Class for storing the various mouse events
     */
    public class mouse_event{
        private final int x;
        private final int y;
        private final int bttn;
        private final boolean pressed;
        private final int rotation;
        
        mouse_event(int button, int _x, int _y, boolean action){
            x = _x;
            y = _y;
            bttn = button;
            pressed = action;
            rotation = 0;
        }
        
        mouse_event(int _rotation){
            x = 0;
            y = 0;
            bttn = MOUSE_WHEEL;
            pressed = false;
            rotation = _rotation;
        }
        
        public int get_x(){
            return x;
        }
        
        public int get_y(){
            return y;
        }
        
        public int get_rotation(){
            return rotation;
        }
        
        public int get_button(){
            return bttn;
        }
        
        public boolean is_pressed(){
            return pressed;
        }
        
        @Override
        public String toString(){
            return "procrastination.mouse.mouse_event[x=" + x + ", y=" + y + ", bttn=" + bttn + ", pressed=" + pressed + ", rotation=" + rotation + "]";
        }
    }
}