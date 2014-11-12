package procrastination.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import procrastination.Procrastination;

public class TitleScreen extends JPanel {
    public TitleScreen(Procrastination procrastination) {
        setOpaque(false);
        
        createWidgets(procrastination);
    }
    
    private void createWidgets(final Procrastination procrastination) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               procrastination.startGame();
           }
        });
        add(startButton);
    }
}
