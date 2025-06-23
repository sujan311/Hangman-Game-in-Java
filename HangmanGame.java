import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

/**
 * Enhanced Hangman game with:
 *  - visual hangman drawing
 *  - list of already‑guessed letters
 *  - duplicate‑guess protection
 *  - case‑insensitive input
 *  - restart button
 */
public class HangmanGame extends JFrame implements ActionListener {
    private static final int MAX_GUESSES = 6;

    private final String[] words = {"hangman","java", "swing", "programming", "openai"};
    private String wordToGuess;
    private int guessesLeft;
    private StringBuilder hiddenWord;
    private final Set<Character> guessedLetters = new HashSet<>();

    // UI components
    private final JLabel hiddenWordLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel guessesLeftLabel = new JLabel();
    private final JLabel guessedLettersLabel = new JLabel();
    private final JTextField guessTextField = new JTextField(2);
    private final JButton guessButton = new JButton("Guess");
    private final JButton restartButton = new JButton("Restart");
    private final HangmanPanel hangmanPanel = new HangmanPanel();

    public HangmanGame() {
        setTitle("Hangman Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Styling
        hiddenWordLabel.setFont(new Font("Monospaced", Font.BOLD, 28));

        // Top: hidden word
        add(hiddenWordLabel, BorderLayout.NORTH);

        // Center: drawing panel
        add(hangmanPanel, BorderLayout.CENTER);

        // Controls & info
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Enter a letter:"));
        controlPanel.add(guessTextField);
        controlPanel.add(guessButton);
        controlPanel.add(restartButton);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(guessesLeftLabel);
        infoPanel.add(guessedLettersLabel);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(infoPanel, BorderLayout.NORTH);
        southPanel.add(controlPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Listeners
        guessButton.addActionListener(this);
        restartButton.addActionListener(e -> initializeGame());
        guessTextField.addActionListener(this);

        initializeGame();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Reset the game state and update all labels/panels.
     */
    private void initializeGame() {
        wordToGuess = words[(int) (Math.random() * words.length)].toLowerCase();
        hiddenWord = new StringBuilder("_".repeat(wordToGuess.length()));
        guessesLeft = MAX_GUESSES;
        guessedLetters.clear();
        updateLabels();
        hangmanPanel.repaint();
        guessTextField.setEnabled(true);
        guessButton.setEnabled(true);
        guessTextField.setText("");
        guessTextField.requestFocus();
    }

    private void updateLabels() {
        hiddenWordLabel.setText(formatHiddenWord());
        guessesLeftLabel.setText("Guesses Left: " + guessesLeft);
        guessedLettersLabel.setText("Guessed Letters: " + guessedLetters);
    }

    private String formatHiddenWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hiddenWord.length(); i++) {
            sb.append(hiddenWord.charAt(i)).append(' ');
        }
        return sb.toString();
    }

    private void processGuess(char guess) {
        guess = Character.toLowerCase(guess);
        if (!Character.isLetter(guess)) return;

        if (guessedLetters.contains(guess)) {
            JOptionPane.showMessageDialog(this, "You already guessed '" + guess + "'.", "Duplicate Guess", JOptionPane.WARNING_MESSAGE);
            return;
        }

        guessedLetters.add(guess);
        boolean found = false;
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                hiddenWord.setCharAt(i, guess);
                found = true;
            }
        }

        if (!found) {
            guessesLeft--;
        }

        updateLabels();
        hangmanPanel.repaint();

        if (hiddenWord.toString().equals(wordToGuess)) {
            endGame("Congratulations! You guessed the word.");
        } else if (guessesLeft == 0) {
            endGame("Sorry, you lost! The word was '" + wordToGuess + "'.");
        }
    }

    private void endGame(String message) {
        guessTextField.setEnabled(false);
        guessButton.setEnabled(false);
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = guessTextField.getText().trim();
        if (!text.isEmpty()) {
            processGuess(text.charAt(0));
            guessTextField.setText("");
        }
    }

    /**
     * Inner panel to draw the gallows and hangman figure.
     */
    private class HangmanPanel extends JPanel {
        HangmanPanel() {
            setPreferredSize(new Dimension(300, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));

            // Gallows
            g2.drawLine(50, 250, 250, 250); // base
            g2.drawLine(100, 250, 100, 50); // pole
            g2.drawLine(100, 50, 200, 50);  // top
            g2.drawLine(200, 50, 200, 80);  // rope

            int incorrect = MAX_GUESSES - guessesLeft;
            // Draw body parts based on incorrect guesses
            if (incorrect > 0) g2.drawOval(175, 80, 50, 50);           // head
            if (incorrect > 1) g2.drawLine(200, 130, 200, 190);        // body
            if (incorrect > 2) g2.drawLine(200, 150, 170, 170);        // left arm
            if (incorrect > 3) g2.drawLine(200, 150, 230, 170);        // right arm
            if (incorrect > 4) g2.drawLine(200, 190, 170, 230);        // left leg
            if (incorrect > 5) g2.drawLine(200, 190, 230, 230);        // right leg
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HangmanGame::new);
    }
}
