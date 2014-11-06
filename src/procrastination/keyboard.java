package procrastination;

import java.util.TreeMap;

public class keyboard {
    //Various return statuses
    public static final byte SUCCESS = 0x0;
    public static final byte KEY_DNE = 0x1;
    public static final byte KEY_ALREADY_REGISTERED = 0x2;
    public static final byte KEY_NOT_REGISTERED = 0x3;
    public static final byte KEY_IS_MAPPED = 0x4;
    public static final byte KEY_NOT_MAPPED = 0x5;
    
    //Special case that is triggered whenever any key is pressed, can be accessed like any other key
    public static final int VK_ANY = -1;
    
    //The different states a key can be in
    public static final byte CLEARED = 0x2;
    public static final byte PRESSED = 0x3;
    public static final byte RELEASED = 0x4;
    
    //Public access to the static keyboard
    public static keyboard instance;
    
    //Map keyCodes to key states
    private TreeMap<Integer, key_state> registered_keys;
    //Possible to map keyCodes to other keyCodes
    private TreeMap<Integer, Integer> maped_keys;
    
    /**
     * Initializes the keyboard and creates the TreeMaps.
     * Also creates the special 'anykey' key.
     */
    keyboard(){
        if(instance != null){
            System.err.println("The keyboard can only be initialized once");
        }else{
            instance = this;
        }
        registered_keys = new TreeMap<>();
        maped_keys = new TreeMap<>();
        registered_keys.put(-1, new key_state(-1));
    }
    
    /**
     * Register the system to keep track of a key
     * @param key_code The keycode of the key that is going to be registered
     * @return The status of the command
     */
    public byte register_key(int key_code){
        if(registered_keys.containsKey(key_code)){
            return KEY_ALREADY_REGISTERED;
        }
        if(maped_keys.containsKey(key_code)){
            return KEY_IS_MAPPED;
        }
        registered_keys.put(key_code, new key_state(key_code));
        return SUCCESS;
    }
    
    /**
     * Check if the key is registered
     * @param key_code The keyCode being checked
     * @return True if registered
     */
    public boolean is_key_registered(int key_code){
        return registered_keys.containsKey(key_code);
    }
    
    /**
     * Check if the key is mapped to another key
     * @param key_code The keyCode being checked
     * @return True if mapped
     */
    public boolean is_key_mapped(int key_code){
        return maped_keys.containsKey(key_code);
    }
    
    /**
     * Remove the registration for the key
     * @param key_code The keyCode being removed
     * @return Status of execution
     */
    public byte unregister_key(int key_code){
        if(!registered_keys.containsKey(key_code)){
            return KEY_NOT_REGISTERED;
        }
        registered_keys.remove(key_code);
        return SUCCESS;
    }
    
    /**
     * Remove the mapping for the key
     * @param key_code The keyCode being removed
     * @return Status of execution
     */
    public byte unmap_key(int key_code){
        if(!maped_keys.containsKey(key_code)){
            return KEY_NOT_MAPPED;
        }
        registered_keys.remove(maped_keys.get(key_code));
        maped_keys.remove(key_code);
        return SUCCESS;
    }
    
    /**
     * Maps one key to generate events as another
     * @param from_key Keycode of key that will be mapped
     * @param to_key Keycode of key that is simulated
     * @return the status of execution
     */
    public byte map_key(int from_key, int to_key){
        if(registered_keys.containsKey(to_key)){
            return KEY_ALREADY_REGISTERED;
        }
        if(maped_keys.containsKey(from_key)){
            return KEY_IS_MAPPED;
        }
        maped_keys.put(from_key, to_key);
        registered_keys.put(to_key, new key_state(to_key));
        return SUCCESS;
    }
    
    /**
     * Called by the keyListener whenever a key is pressed
     * @param key_code Key that was pressed
     * @return Status of execution
     */
    public byte key_press(int key_code){
        registered_keys.get(-1).press();
        registered_keys.get(-1).set_key_code(key_code);
        //POPULATE CORRECT KEY IF IT EXISTS
        if(maped_keys.containsKey(key_code)){
            key_code = maped_keys.get(key_code);
        }
        if(!registered_keys.containsKey(key_code)){
            return KEY_DNE;
        }
        registered_keys.get(key_code).press();
        return SUCCESS;
    }
    
    /**
     * Called by the keyListener whenever a key is released
     * @param key_code key that was released
     * @return the status of the execution
     */
    public byte key_release(int key_code){
        if(registered_keys.get(-1).get_key_code() == key_code){
            registered_keys.get(-1).release();
        }
        if(maped_keys.containsKey(key_code)){
            key_code = maped_keys.get(key_code);
        }
        if(!registered_keys.containsKey(key_code)){
            return KEY_DNE;
        }
        registered_keys.get(key_code).release();
        return SUCCESS;
    }
    
    /**
     * Clears the state of a key even if it is pressed
     * THis is useful for creating trigger type events
     * @param key_code key being cleared
     * @return the status of the execution
     */
    public byte clear_key(int key_code){
        if(maped_keys.containsKey(key_code)){
            key_code = maped_keys.get(key_code);
        }
        if(!registered_keys.containsKey(key_code)){
            return KEY_DNE;
        }
        registered_keys.get(key_code).clear();
        return SUCCESS;
    }
    
    /**
     * Check to see if the key is pressed
     * @param key_code key being checked
     * @return True if key is pressed
     */
    public boolean is_key_pressed(int key_code){
        if(maped_keys.containsKey(key_code)){
            key_code = maped_keys.get(key_code);
        }
        if(!registered_keys.containsKey(key_code)){
            return false;
        }
        return registered_keys.get(key_code).is_pressed();
    } 
    
    /**
     * Get the key that was pressed last
     * @return the keycode of the last pressed key
     */
    public int get_last_key_pressed(){
        return registered_keys.get(-1).get_key_code();
    }

    /**
     * Check to see if a key is cleared
     * @param key_code the key being checked
     * @return True of cleared
     */
    public boolean is_key_cleared(int key_code) {
        return registered_keys.get(key_code).is_cleared();
    }

    /**
     * Clears out both the mapped list and the registered list
     */
    public void clear() {
        registered_keys.clear();
        maped_keys.clear();
    }
    
    /**
     * Storage for the state of registered keys
     */
    public class key_state {
        private int key_code;
        private int state;

        public key_state(int _key_code) {
            key_code = _key_code;
            state = RELEASED;
        }
        public void press(){
            if(state == RELEASED){
                state = PRESSED;
            }
        }
        public void release(){
            state = RELEASED;
        }
        public void clear(){
            if(state == PRESSED){
                state = CLEARED;
            }
        }
        public boolean is_pressed(){
            return state == PRESSED;
        }
        public int get_key_code(){
            return key_code;
        }
        protected void set_key_code(int _key_code){
            key_code = _key_code;
        }

        private boolean is_cleared() {
            return state == CLEARED;
        }
    }
}