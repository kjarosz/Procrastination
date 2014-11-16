package procrastination.menu;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CustomNumberImage {
    private static final int WIDTH_PER_DIGIT = 15;
    private static final int HEIGHT_PER_DIGIT = 30;
    
    private static BufferedImage mDigits[];
    
    private BufferedImage mNumber;
    
    public CustomNumberImage(int number) {
        if(mDigits == null) {
            loadDigits();
        }
        
        assembleNumberImage(number);
    }
    
    private void loadDigits() {
        mDigits = new BufferedImage[10];
        
        try {
            for(int i = 0; i < 10; i++) {
                mDigits[i] = ImageIO.read(new File(
                        "images" + File.separator 
                        + "highscores" + File.separator 
                        + "organic " + i + ".png"));
            }
        } catch(IOException ex) {
            System.out.println("Could not load digit image.");
            throw new RuntimeException("Could not load digit image.");
        }
    }
    
    private void assembleNumberImage(int number) {
        char numberString[] = Integer.toString(number).toCharArray();
        int imageWidth = numberString.length * WIDTH_PER_DIGIT;
        int imageHeight = HEIGHT_PER_DIGIT;
        
        mNumber = new BufferedImage(imageWidth*numberString.length, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = mNumber.getGraphics();
        for(int i = 0; i < numberString.length; i++) {
            int digit = Integer.parseInt(numberString[i]+"");
            g.drawImage(mDigits[digit], i*imageWidth, 0, imageWidth, imageHeight, null);
        }
        g.dispose();
    }
    
    public Dimension getSize() {
        return new Dimension(mNumber.getWidth(), mNumber.getHeight());
    }
    
    public void draw(Graphics g, int x, int y) {
        g.drawImage(mNumber, x, y, mNumber.getWidth(), mNumber.getHeight(), null);
    }
    
    public void draw(Graphics g, int x, int y, int width, int height) {
        g.drawImage(mNumber, x, y, width, height, null);
    }
}
