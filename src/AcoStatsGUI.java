package src;


import java.math.BigInteger;
import java.text.DecimalFormat;

import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class AcoStatsGUI extends JPanel {
	long startTime;
	BigInteger expectedTime;

	JLabel timeElapsedLabel;
	JLabel totalLabel;
	JLabel finishedLabel;

	DecimalFormat formatter;

	Timer t;

	public AcoStatsGUI(BigInteger total) {
		formatter = new DecimalFormat("0.0000E0");

		this.startTime = 0;
		startTime = System.currentTimeMillis();

		timeElapsedLabel = new JLabel("");
		totalLabel = new JLabel("");
		finishedLabel = new JLabel("");

		Font f = new Font("Verdana", Font.PLAIN, 16);
		totalLabel.setFont(f);
		finishedLabel.setFont(f);
		timeElapsedLabel.setFont(f);

		int b = 15;
		setBorder(new EmptyBorder(b, b, b, b)); // padding
		add(totalLabel);
		add(finishedLabel);
		add(timeElapsedLabel);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		totalLabel.setText("Total paths: " + formatter.format(total) + " paths");
		finishedLabel.setText("Completed paths: 0 paths");
		timeElapsedLabel.setText("Time elapsed: 0 seconds");
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
			timeElapsedLabel.setText("Time elapsed: " + formatter.format(elapsed) + " ms");
			totalLabel.setText("Total paths: " + formatter.format(total) + " paths");
			finishedLabel.setText("Completed paths: " + formatter.format(checked) + " paths");
		});
	}
}