package procrastination.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class HighScorePanel extends JPanel {
    private final static Point HIGH_SCORE_OFFSET = new Point(150, 163);
    private final static int HIGH_SCORE_HEIGHT = 85;
    private final static int HIGH_SCORE_GAP = 15;
    private final static int FONT_OFFSET = 35;
    
    private String mNames[];
    private CustomNumberImage mScores[];
    
    private BufferedImage mBackgroundImage;
    
    public HighScorePanel() {
        mNames = new String[10];
        mScores = new CustomNumberImage[10];
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
            for(int i = 0; i < mNames.length; i++) {
                String line = reader.readLine();
                int breakpoint = line.indexOf(":");
                mNames[i] = line.substring(0, breakpoint);
                mScores[i] = new CustomNumberImage(
                        Integer.parseInt(
                                line.substring(breakpoint+1)));
            }
        } catch(IOException ex) {
            System.out.println("High scores could not be read.");
            throw new RuntimeException("High scores could not read.");
        } catch(NumberFormatException ex) {
            System.out.println("One of the high scores contains an invalid number.");
            throw new RuntimeException("High scores sheet contains invalid entries: invalid number.");
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

        g.drawImage(mBackgroundImage, 
                0, 
                0, 
                (int)(mBackgroundImage.getWidth() * scaleRatio), 
                (int)(mBackgroundImage.getHeight() * scaleRatio), null);
        
        drawHighScores(g, scaleRatio);
    }
    
    private void drawHighScores(Graphics g, double scaleRatio) {
        Font font = g.getFont();
        Font biggerFont = new Font(font.getName(), Font.PLAIN, (int)(50*scaleRatio));
        g.setFont(biggerFont);
        
        for(int i = 0; i < mNames.length; i++) {
            g.setColor(Color.ORANGE);
            g.drawString(mNames[i], 
                    (int)(HIGH_SCORE_OFFSET.x*scaleRatio), 
                    (int)((HIGH_SCORE_OFFSET.y
                        + i*(HIGH_SCORE_GAP + HIGH_SCORE_HEIGHT)
                        + FONT_OFFSET)
                        * scaleRatio));
            
            Dimension numberSize = mScores[i].getSize();
            Rectangle boundaries = g.getClipBounds();
                        
            mScores[i].draw(g, 
                    (int)((HIGH_SCORE_OFFSET.x + boundaries.width - numberSize.width)*scaleRatio), 
                    (int)((HIGH_SCORE_OFFSET.y
                            + i*(HIGH_SCORE_GAP + HIGH_SCORE_HEIGHT))
                            * scaleRatio),
                    (int)(numberSize.width*scaleRatio),
                    (int)(HIGH_SCORE_HEIGHT*scaleRatio));
        }
    }
}
