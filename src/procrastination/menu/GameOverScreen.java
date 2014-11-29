package procrastination.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import procrastination.Procrastination;

public class GameOverScreen extends JPanel {
    private String mNames[];
    private int mScores[];
    
    private int mNewHighScore;
    private int mNewScoreIndex;
    
    private JTextField mNameField;
    private JButton mSubmitButton;
    
    private BufferedImage mBackground;
    
    public GameOverScreen(Procrastination procrastination) {
        mNames = new String[10];
        mScores = new int[10];
        
        try {
            mBackground = ImageIO.read(new File("images\\high score background.png"));
        } catch(IOException ex) {
            System.out.println("Could not load game over background.");
            throw new RuntimeException("Could not load game over background.");
        }
        
        createWidgets(procrastination);
    }
    
    private void createWidgets(final Procrastination procrastination) {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        Font font = gameOverLabel.getFont();
        Font newFont = new Font(font.getName(), Font.PLAIN, 36);
        gameOverLabel.setFont(newFont);
        gameOverLabel.setForeground(Color.BLUE);
        add(gameOverLabel, BorderLayout.NORTH);
        
        JPanel scorePanel = new JPanel();
        scorePanel.setOpaque(false);
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        JLabel scoreLabel = new JLabel();
        scoreLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        mNameField = new JTextField(25);
        mNameField.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        
        scorePanel.add(Box.createVerticalGlue());
        scorePanel.add(scoreLabel);
        scorePanel.add(Box.createVerticalStrut(5));
        scorePanel.add(mNameField);
        scorePanel.add(Box.createVerticalGlue());
        
        add(scorePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procrastination.showTitle();
            }
        });
        mSubmitButton = new JButton("Submit");
        mSubmitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!nameIsValid()) {
                    JOptionPane.showMessageDialog(null, "Your name cannot contain \":\" and cannot be empty", 
                            "Invalid name", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                saveHighScore();
                procrastination.showHighScore();
            }
        });
        buttonPanel.add(backButton);
        buttonPanel.add(mSubmitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private boolean nameIsValid() {
        String name = mNameField.getText();
        return !name.isEmpty() && (name.indexOf(":") < 0);
    }
    
    private void saveHighScore() {
        insertHighScore();
        saveHighScores();
    }
    
    private void insertHighScore() {
        for(int i = mNames.length-1; i > mNewScoreIndex; i--) {
            mNames[i] = mNames[i-1];
            mScores[i] = mScores[i-1];
        }
        mNames[mNewScoreIndex] = mNameField.getText();
        mScores[mNewScoreIndex] = mNewHighScore;
    }
    
    private void saveHighScores() {
        try (FileWriter fWriter = new FileWriter("high_scores.txt");
                BufferedWriter writer = new BufferedWriter(fWriter)) {
            for(int i = 0; i < mNames.length; i++) {
                writer.write(mNames[i] + ":" + mScores[i]);
                writer.newLine();
            }
            writer.flush();
        } catch(IOException ex) {
            System.out.println("Could not write high score file.");
            throw new RuntimeException("Could not write high score file.");
        }
    }
    
    public void showScreen(int score) {
        loadHighScores();
        mNewHighScore = score;
        mNewScoreIndex = newHighScore(mNewHighScore);
        if(mNewScoreIndex >= 0) {
            mNameField.setVisible(true);
            mSubmitButton.setVisible(true);
        } else {
            mNameField.setVisible(false);
            mSubmitButton.setVisible(false);
        }
    }
    
    private void loadHighScores() {
        try (FileReader fReader = new FileReader("high_scores.txt");
                BufferedReader reader = new BufferedReader(fReader)) {
            for(int i = 0; i < mNames.length; i++) {
                String line = reader.readLine();
                int breakpoint = line.indexOf(":");
                mNames[i] = line.substring(0, breakpoint);
                mScores[i] = Integer.parseInt(line.substring(breakpoint+1));
            }
        } catch(IOException ex) {
            System.out.println("High scores could not be read.");
            throw new RuntimeException("High scores could not read.");
        } catch(NumberFormatException ex) {
            System.out.println("One of the high scores contains an invalid number.");
            throw new RuntimeException("High scores sheet contains invalid entries: invalid number.");
        }
    }
    
    private int newHighScore(int score) {
        for(int i = 0; i < mScores.length; i++) {
            if(mScores[i] < score)
                return i;
        }
        return -1;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(mBackground, 0, 0, getWidth(), getHeight(), null);
        
        super.paintComponent(g);
    }
}
