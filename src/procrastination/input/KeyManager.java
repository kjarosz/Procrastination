package procrastination.input;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyManager {
   private static boolean mKeyStates[] = new boolean[KeyEvent.KEY_LAST];

   public KeyManager(JComponent window) {
      InputMap keyMappings = window.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
      ActionMap actionMappings = window.getActionMap();
      
      generateKeyMappings(keyMappings);
      generateActionMappings(actionMappings);
   }
   
   private void generateKeyMappings(InputMap keyMappings) {
      for(int i = 0; i < KeyEvent.KEY_LAST; i++) {
         keyMappings.put(KeyStroke.getKeyStroke(i, 0, false), i + " pressed");
         keyMappings.put(KeyStroke.getKeyStroke(i, 0, true), i + " released");
      }
   }
   
   private void generateActionMappings(ActionMap actionMappings) {
      for(int i = 0; i < KeyEvent.KEY_LAST; i++) {
         actionMappings.put(i + " pressed", generatePressAction(i));
         actionMappings.put(i + " released", generateReleaseAction(i));
      }
   }
   
   private Action generatePressAction(final int keycode) {
      return new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            pressKey(keycode);
         }
      };
   }
   
   private Action generateReleaseAction(final int keycode) {
      return new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            releaseKey(keycode);
         }
      };
   }
   
   public synchronized static void clearKeys() {
      for(int i = 0; i < mKeyStates.length; i++)
         mKeyStates[i] = false;
   }

   public synchronized static boolean isKeyPressed(int key) {
      return mKeyStates[key];
   }

   public synchronized static void pressKey(int key) {
      mKeyStates[key] = true;
   }

   public synchronized static void releaseKey(int key) {
      mKeyStates[key] = false;
   }
}
