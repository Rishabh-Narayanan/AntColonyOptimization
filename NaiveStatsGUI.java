

import java.math.BigInteger;
import java.text.DecimalFormat;

import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class NaiveStatsGUI extends JPanel {
	long startTime;
	BigInteger expectedTime;

	JLabel totalLabel;
	JLabel finishedLabel;
	JLabel timeElapsedLabel;
	JLabel expectedTimeLabel;

	DecimalFormat formatter;

	Timer t;

	public NaiveStatsGUI(BigInteger total) {
		formatter = new DecimalFormat("0.0000E0");

		this.startTime = 0;
		startTime = System.currentTimeMillis();

		totalLabel = new JLabel("");
		finishedLabel = new JLabel("");
		timeElapsedLabel = new JLabel("");
		expectedTimeLabel = new JLabel("");
		Font f = new Font("Verdana", Font.PLAIN, 16);
		totalLabel.setFont(f);
		finishedLabel.setFont(f);
		timeElapsedLabel.setFont(f);
		expectedTimeLabel.setFont(f);

		int b = 15;
		setBorder(new EmptyBorder(b, b, b, b)); // padding
		add(totalLabel);
		add(finishedLabel);
		add(timeElapsedLabel);
		add(expectedTimeLabel);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		totalLabel.setText("Total paths: " + formatter.format(total) + " paths");
		finishedLabel.setText("Completed paths: 0 paths");
		timeElapsedLabel.setText("Time elapsed: 0 seconds");
		expectedTimeLabel.setText("Expected time: inf seconds");

	}

	public void start() {
		startTime = System.currentTimeMillis();
		t = new Timer(1000 / 30, (e) -> {
			repaint();
		});
		t.setRepeats(false);
		t.start();
	}

	public void dispose() {
		t.stop();
	}

	public void set(BigInteger total, BigInteger checked) {
		if (t.isRunning())
			return;

		SwingUtilities.invokeLater(() -> {
			t.restart();
			long elapsed = (System.currentTimeMillis() - startTime);

			BigInteger expected = null;
			if (!checked.equals(BigInteger.valueOf(0))) {
				expected = total.multiply(BigInteger.valueOf(elapsed)).divide(checked);
			}

			totalLabel.setText("Total paths: " + formatter.format(total) + " paths");
			finishedLabel.setText("Completed paths: " + formatter.format(checked) + " paths");
			timeElapsedLabel.setText("Time elapsed: " + formatter.format(elapsed) + " ms");
			expectedTimeLabel
					.setText("Expected time: " + (expected == null ? "inf" : formatter.format(expected)) + " ms");
		});
	}
}