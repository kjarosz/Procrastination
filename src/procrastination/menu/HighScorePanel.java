package procrastination.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HighScorePanel extends JPanel {
    private class HighScore {
        String name;
        int score;
    }
    
    private HighScore mHighScores[];
    private JLabel mScores[];
    
    private BufferedImage mBackgroundImage;
    
    public HighScorePanel() {
        mHighScores = new HighScore[10];
        mScores = new JLabel[10];
        for(int i = 0; i < mHighScores.length; i++) {
            mHighScores[i] = new HighScore();
            mScores[i] = new JLabel();
        }
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 0));
        
        try {
            mBackgroundImage = ImageIO.read(new File("images" + File.separator + "highscores" + File.separator + "high score screen.png"));
        } catch(IOException ex) {
            System.out.println("Could not load high score background.");
            throw new RuntimeException("Could not load high score background.");
        }
        
        reloadHighScores();
    }
    
    public void reloadHighScores() {
        try (FileReader fReader = new FileReader("high_scores.txt");
                BufferedReader reader = new BufferedReader(fReader)) {
            for(HighScore score: mHighScores) {
                String line = reader.readLine();
                int breakpoint = line.indexOf(":");
                score.name = line.substring(0, breakpoint);
                score.score = Integer.parseInt(line.substring(breakpoint+1));
            }
        } catch(IOException ex) {
            System.out.println("High scores could not be read.");
            throw new RuntimeException("High scores could not read.");
        } catch(NumberFormatException ex) {
            System.out.println("One of the high scores contains an invalid number.");
            throw new RuntimeException("High scores sheet contains invalid entries: invalid number.");
        }
        
        for(int i = 0; i < mHighScores.length; i++) {
            mScores[i].setText(mHighScores[i].name);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        double scaleRatio = 1.0;
        if(getWidth() / mBackgroundImage.getWidth() < getHeight() / mBackgroundImage.getHeight()) {
            scaleRatio = (double)getWidth() / (double)mBackgroundImage.getWidth();
        } else {
            scaleRatio = (double)getHeight() / (double)mBackgroundImage.getHeight();
        }
        System.out.println(scaleRatio);
        g.drawImage(mBackgroundImage, 
                0, 
                0, 
                (int)(mBackgroundImage.getWidth() * scaleRatio), 
                (int)(mBackgroundImage.getHeight() * scaleRatio), null);
    }
}
