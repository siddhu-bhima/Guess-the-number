import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Random;

public class GuessTheNumberGame extends JFrame {
    private JTextField guessField;
    private JButton guessButton, resetButton, hintButton;
    private JLabel feedbackLabel, attemptsLabel, hintLabel, timerLabel, highScoreLabel;
    private JComboBox<String> difficultyCombo;
    private JTextArea guessHistory;
    private int targetNumber, maxRange, attempts, hintsRemaining, highScore;
    private long startTime;
    private Timer timer;

    public GuessTheNumberGame() {
        // Frame setup
        setTitle("Guess the Number");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Initialize variables
        highScore = Integer.MAX_VALUE; // Start with a high value to be beaten

        // Main panel with GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Components
        JLabel instructionLabel = new JLabel("Guess a number between 1 and 100:", SwingConstants.CENTER); // Default text
        guessField = new JTextField(10);
        guessButton = new JButton("Guess");
        resetButton = new JButton("Reset Game");
        hintButton = new JButton("Hint");
        feedbackLabel = new JLabel("Enter your guess above!", SwingConstants.CENTER);
        attemptsLabel = new JLabel("Attempts: 0", SwingConstants.CENTER);
        hintLabel = new JLabel("Hints remaining: 3", SwingConstants.CENTER);
        timerLabel = new JLabel("Time: 0.0s", SwingConstants.CENTER);
        highScoreLabel = new JLabel("Best: N/A", SwingConstants.CENTER);
        guessHistory = new JTextArea(5, 20);
        guessHistory.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(guessHistory);

        // Difficulty selection
        String[] difficulties = {"Easy (1-50)", "Medium (1-100)", "Hard (1-1000)"};
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setSelectedIndex(1); // Default to Medium

        // Set button sizes
        guessButton.setPreferredSize(new Dimension(100, 30));
        resetButton.setPreferredSize(new Dimension(100, 30));
        hintButton.setPreferredSize(new Dimension(100, 30));

        // Styling
        guessButton.setBackground(new Color(50, 205, 50)); // Lime green
        guessButton.setForeground(Color.WHITE);
        resetButton.setBackground(new Color(255, 69, 0)); // Orange red
        resetButton.setForeground(Color.WHITE);
        hintButton.setBackground(new Color(255, 215, 0)); // Gold
        hintButton.setForeground(Color.BLACK);

        // Add components to main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(instructionLabel, gbc);

        gbc.gridy = 1;
        mainPanel.add(difficultyCombo, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(guessField, gbc);

        gbc.gridx = 1;
        mainPanel.add(guessButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(feedbackLabel, gbc);

        gbc.gridy = 4;
        mainPanel.add(attemptsLabel, gbc);

        gbc.gridy = 5;
        mainPanel.add(hintLabel, gbc);

        gbc.gridy = 6;
        mainPanel.add(timerLabel, gbc);

        gbc.gridy = 7;
        mainPanel.add(highScoreLabel, gbc);

        gbc.gridy = 8;
        mainPanel.add(hintButton, gbc);

        gbc.gridy = 9;
        mainPanel.add(resetButton, gbc);

        gbc.gridy = 10;
        mainPanel.add(historyScroll, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Event listeners
        guessButton.addActionListener(e -> checkGuess());
        resetButton.addActionListener(e -> setDifficultyAndReset());
        hintButton.addActionListener(e -> giveHint());
        guessField.addActionListener(e -> checkGuess());
        difficultyCombo.addActionListener(e -> setDifficultyAndReset());

        // Timer setup
        timer = new Timer(100, e -> updateTimer());
        timer.start();

        // Initial game setup after components are created
        setDifficultyAndReset();
    }

    private void setDifficultyAndReset() {
        String selected = (String) difficultyCombo.getSelectedItem();
        if (selected.equals("Easy (1-50)")) maxRange = 50;
        else if (selected.equals("Medium (1-100)")) maxRange = 100;
        else maxRange = 1000;
        JLabel instructionLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent(0); // Update instruction
        instructionLabel.setText("Guess a number between 1 and " + maxRange + ":");
        resetGame();
    }

    private void checkGuess() {
        if (attempts == 0) startTime = System.currentTimeMillis(); // Start timer on first guess
        try {
            int guess = Integer.parseInt(guessField.getText().trim());
            attempts++;
            attemptsLabel.setText("Attempts: " + attempts);

            if (guess < 1 || guess > maxRange) {
                feedbackLabel.setText("Please enter a number between 1 and " + maxRange + "!");
                guessHistory.append(guess + " - Out of range\n");
            } else if (guess < targetNumber) {
                feedbackLabel.setText("Too low! Try a higher number.");
                guessHistory.append(guess + " - Too low\n");
            } else if (guess > targetNumber) {
                feedbackLabel.setText("Too high! Try a lower number.");
                guessHistory.append(guess + " - Too high\n");
            } else {
                feedbackLabel.setText("Congratulations! You guessed it in " + attempts + " attempts!");
                guessHistory.append(guess + " - Correct!\n");
                guessButton.setEnabled(false);
                guessField.setEnabled(false);
                hintButton.setEnabled(false);
                timer.stop();
                if (attempts < highScore) {
                    highScore = attempts;
                    highScoreLabel.setText("Best: " + highScore + " attempts");
                }
            }
            guessField.setText("");
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Please enter a valid number!");
            guessHistory.append(guessField.getText() + " - Invalid\n");
        }
    }

    private void giveHint() {
        if (hintsRemaining > 0) {
            hintsRemaining--;
            hintLabel.setText("Hints remaining: " + hintsRemaining);
            int range = maxRange / 4;
            int lower = Math.max(1, targetNumber - range);
            int upper = Math.min(maxRange, targetNumber + range);
            feedbackLabel.setText("Hint: The number is between " + lower + " and " + upper + ".");
            guessHistory.append("Hint: " + lower + " to " + upper + "\n");
        } else {
            feedbackLabel.setText("No hints left!");
            hintButton.setEnabled(false);
        }
    }

    private void resetGame() {
        Random rand = new Random();
        targetNumber = rand.nextInt(maxRange) + 1;
        attempts = 0;
        hintsRemaining = 3;
        attemptsLabel.setText("Attempts: 0");
        hintLabel.setText("Hints remaining: " + hintsRemaining);
        timerLabel.setText("Time: 0.0s");
        feedbackLabel.setText("Enter your guess above!");
        guessField.setText("");
        guessHistory.setText("");
        guessButton.setEnabled(true);
        guessField.setEnabled(true);
        hintButton.setEnabled(true);
        if (!timer.isRunning()) timer.start();
        startTime = System.currentTimeMillis();
    }

    private void updateTimer() {
        if (attempts > 0) { // Only update if game has started
            long elapsed = System.currentTimeMillis() - startTime;
            double seconds = elapsed / 1000.0;
            DecimalFormat df = new DecimalFormat("#.#");
            timerLabel.setText("Time: " + df.format(seconds) + "s");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuessTheNumberGame().setVisible(true));
    }
}
