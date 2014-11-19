package procrastination.menu;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CustomNumberImage {
    private static final int WIDTH_PER_DIGIT = 50;
    private static final int HEIGHT_PER_DIGIT = 30;
    
    private static BufferedImage mDigits[];
    
    private BufferedImage mNumber;
    
    private int value;
    
    public CustomNumberImage(int number) {
        if(mDigits == null) {
            loadDigits();
        }
        value = number;
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
    
    public void assembleNumberImage(int number) {
        value = number;
        char numberString[] = Integer.toString(number).toCharArray();
        int imageWidth = numberString.length * WIDTH_PER_DIGIT;
        int imageHeight = HEIGHT_PER_DIGIT;
        
        mNumber = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = mNumber.getGraphics();
        for(int i = 0; i < numberString.length; i++) {
            int digit = Integer.parseInt(numberString[i]+"");
            g.drawImage(mDigits[digit], i*WIDTH_PER_DIGIT, 0, WIDTH_PER_DIGIT, imageHeight, null);
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
    
    public int getValue(){
        return value;
    }
}