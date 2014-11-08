package procrastination.input;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Mouse {
    //The different types of events that can occur
    public static final int LEFT_BUTTON = MouseEvent.BUTTON1;
    public static final int MIDDLE_BUTTON = MouseEvent.BUTTON2;
    public static final int RIGHT_BUTTON = MouseEvent.BUTTON3;
    public static final int MOUSE_WHEEL = MouseEvent.MOUSE_WHEEL;
    
    //Public access for the mouse
    public static Mouse sMouse;
    
    //Stores the state of the three buttons
    private boolean left_pressed = false;
    private boolean middle_pressed = false;
    private boolean right_pressed = false;
    
    //The points where the mouse was last pressed at
    private Point left_start = new Point(0, 0);
    private Point right_start = new Point(0, 0);
    private Point middle_start = new Point(0, 0);
    
    private Point mouse_position = new Point(0, 0);
    
    //Stores any mouse events that happen for processing
    private ArrayList<mouse_event> events;
    
    private Mouse(){
        events = new ArrayList<>();
    }
    
    public static Mouse getMouse() {
        if(sMouse == null)
            sMouse = new Mouse();
        
        return sMouse;
    }
    
    /**
     * Called by the mouseListener whenever there is a mouse press
     * @param button the button that was pressed
     */
    public void mouse_press(MouseEvent event){
         int button = event.getButton();
         mouse_position = event.getPoint();
         if(button == LEFT_BUTTON){
             left_pressed = true;
             left_start = new Point(mouse_position.x, mouse_position.y);
         }else if(button == MIDDLE_BUTTON){
             middle_pressed = true;
             middle_start = new Point(mouse_position.x, mouse_position.y);
         }else if(button == RIGHT_BUTTON){
             right_pressed = true;
             right_start = new Point(mouse_position.x, mouse_position.y);
         }
         events.add(new mouse_event(button, mouse_position.x, mouse_position.y, true));
    }
    
    /**
     * Called by the mouseListener whenever there is a mouse released
     * @param button the button that was released
     */
    public void mouse_release(MouseEvent event){
         int button = event.getButton();
         if(button == LEFT_BUTTON){
             left_pressed = false;
         }else if(button == MIDDLE_BUTTON){
             middle_pressed = false;
         }else if(button == RIGHT_BUTTON){
             right_pressed = false;
         }
         mouse_position = event.getPoint();
         events.add(new mouse_event(button, mouse_position.x, mouse_position.y, false));
    }
    
    /**
     * Called by the mouseMotionListener whenever mouse changes position
     * @param position position relative to the source panel
     */
    public void mouse_move(Point position) {
       mouse_position = position;
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