package procrastination.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

public class CustomImageButton extends JButton {
   private BufferedImage mImage;
   private BufferedImage mRolloverImage;
   private Dimension mSize;
   private boolean mMouseOver;
      
   public CustomImageButton(String image, String rolloverImage, Dimension size) {
      setImage(image);
      setRolloverImage(rolloverImage);
      mSize = size;
      setOpaque(false);
      setBackground(new Color(255, 255, 255, 0));
      setBorder(new LineBorder(new Color(255, 255, 255, 0)));
      addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent e) {
            mMouseOver = true;
         }
         
         @Override
         public void mouseExited(MouseEvent e) {
            mMouseOver = false;
         }
      });
   }
   
   private void setImage(String image) {
      try {
         mImage = ImageIO.read(new File(image));
      } catch(IOException ex) {
         System.out.println("Could not load button image.");
         throw new RuntimeException("Image \"" + image + "\" could not be loaded.");
      }
   }
   
   private void setRolloverImage(String image) {
      try {
         mRolloverImage = ImageIO.read(new File(image));
      } catch(IOException ex) {
         System.out.println("Could not load button image.");
         throw new RuntimeException("Image \"" + image + "\" could not be loaded.");
      }
   }
   
   @Override
   public Dimension getMinimumSize() {
       return new Dimension(mSize);
   }
   
   @Override
   public Dimension getMaximumSize() {
       return new Dimension(mSize);
   }
   
   @Override
   public Dimension getPreferredSize() {
       return new Dimension(mSize);
   }
   
   @Override
   public void paintComponent(Graphics g) {
       Dimension size = this.getSize();
       g.setColor(getBackground());
       g.fillRect(0,  0, size.width, size.height);
       if(mMouseOver) {
          g.drawImage(mRolloverImage, 0, 0, size.width, size.height, null);
       } else {
          g.drawImage(mImage, 0, 0, size.width, size.height, null);
       }
   }
}
