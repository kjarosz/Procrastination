package procrastination.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import procrastination.Procrastination;

public class HighScoreScreen extends JPanel {
    private HighScorePanel mHighScorePanel;
    
    private BufferedImage mBackground;
    
    public HighScoreScreen(Procrastination procrastination) {
        setOpaque(false);
        createWidgets(procrastination);
        
        try {
            mBackground = ImageIO.read(new File("images" + File.separator + "high score background.png"));
        } catch(IOException ex) {
            System.out.println("Could not load high score background.");
            throw new RuntimeException("Could not load high score background.");
        }
    }
    
    private void createWidgets(final Procrastination procrastination) {
        setLayout(new BorderLayout());
        
        mHighScorePanel = new HighScorePanel();
        mHighScorePanel.setBorder(new LineBorder(Color.blue));
        add(mHighScorePanel, BorderLayout.CENTER);
        
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setBorder(new LineBorder(Color.red));
        backButtonPanel.setOpaque(false);
        CustomImageButton button = new CustomImageButton(
                "images" + File.separator + "buttons" + File.separator + "back.png",
                "images" + File.separator + "buttons" + File.separator + "back scrollover.png",
                new Dimension(100, 40));
        button.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procrastination.showTitle();
            }
        });
        backButtonPanel.add(button);
        add(backButtonPanel, BorderLayout.SOUTH);
    }
    
    public void reloadHighScores() {
       mHighScorePanel.reloadHighScores();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(mBackground, 0, 0, getWidth(), getHeight(), null);
        
        super.paintComponent(g);
    }
}