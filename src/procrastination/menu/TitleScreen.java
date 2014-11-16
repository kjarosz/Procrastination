package procrastination.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import procrastination.Procrastination;

public class TitleScreen extends JPanel {
    private static final String IMAGE_DIRECTORY = "images" + File.separator + "buttons";
    private static final String START_BUTTON_IMAGE = "start.png";
    private static final String START_BUTTON_ROLLOVER = "start scrollover.png";
    private static final String HIGH_SCORE_BUTTON_IMAGE = "high score.png";
    private static final String HIGH_SCORE_BUTTON_ROLLOVER = "high score scrollover.png";
    private static final String QUIT_BUTTON_IMAGE = "quit.png";
    private static final String QUIT_BUTTON_ROLLOVER = "quit scrollover.png";
    
    private static final Dimension START_BUTTON_SIZE = new Dimension(200, 80);
    private static final Dimension HIGH_SCORE_BUTTON_SIZE = new Dimension(320, 80);
    private static final Dimension QUIT_BUTTON_SIZE = new Dimension(200, 80);
    
    private BufferedImage mBackground;
    
    public TitleScreen(Procrastination procrastination) {
        setOpaque(false);
        
        createWidgets(procrastination);
        
        try {
            mBackground = ImageIO.read(new File("images" + File.separator + "menu background.png"));
        } catch(IOException ex) {
            System.out.println("Could not load menu background.");
            throw new RuntimeException("Could not load menu background.");
        }
    }
    
    private void createWidgets(final Procrastination procrastination) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new LineBorder(Color.red));
        
        add(Box.createVerticalGlue());
        
        add(makeButton(IMAGE_DIRECTORY + File.separator + START_BUTTON_IMAGE,
                       IMAGE_DIRECTORY + File.separator + START_BUTTON_ROLLOVER,
                       START_BUTTON_SIZE,
                       new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               procrastination.startGame();
           }
        }));
        
        add(Box.createVerticalStrut(10));
        
        add(makeButton(IMAGE_DIRECTORY + File.separator + HIGH_SCORE_BUTTON_IMAGE,
                       IMAGE_DIRECTORY + File.separator + HIGH_SCORE_BUTTON_ROLLOVER,
                       HIGH_SCORE_BUTTON_SIZE,
                       new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procrastination.viewHighScore();
            }
        }));
        
        add(Box.createVerticalStrut(10));
        
        add(makeButton(IMAGE_DIRECTORY + File.separator + QUIT_BUTTON_IMAGE,
                       IMAGE_DIRECTORY + File.separator + QUIT_BUTTON_ROLLOVER,
                       QUIT_BUTTON_SIZE,
                       new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procrastination.quit();
            }
        }));
        
        add(Box.createVerticalGlue());
    }
    
    private JButton makeButton(String image, String rolloverImage, Dimension size, ActionListener listener) {
       CustomImageButton button = new CustomImageButton(image, rolloverImage, size);
       button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
       button.setAlignmentY(JComponent.CENTER_ALIGNMENT);
       button.addActionListener(listener);
       return button;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(mBackground, 0, 0, getWidth(), getHeight(), null);
        
        super.paintComponent(g);
    }
}
