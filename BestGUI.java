
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class BestGUI extends JPanel {
	Point[] pts;
	int[] best;
	long startTime;

	Timer t;

	public BestGUI(Point[] pts) {
		this.pts = pts;
		this.best = new int[pts.length];

		this.startTime = 0;
		startTime = System.currentTimeMillis();

		int b = 15;
		setBorder(new EmptyBorder(b, b, b, b)); // padding
		setPreferredSize(new Dimension(400, 400));
	}

	public void start() {
		t = new Timer(1000 / 30, (e) -> {
			repaint();
		});
		t.setRepeats(false);
		t.start();
	}

	public void dispose() {
		t.stop();
	}

	public void set(Point[] pts, int[] best) {
		if (t.isRunning())
			return;
		SwingUtilities.invokeLater(() -> {
			t.restart();
			this.pts = pts.clone();
			this.best = best.clone();
			repaint();
		});
	}


	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		double sW = (double) this.getWidth() / GameState.MAX_POINT_COORDINATE;
		double sH = (double) this.getHeight() / GameState.MAX_POINT_COORDINATE;
		final int r = 6;
		
		// draw lines
		g2.setColor(Color.BLACK);
		for (int i = 0; i < best.length; i++) {
			g2.drawLine((int) (pts[best[i]].x * sW), (int) (pts[best[i]].y * sH),
					(int) (pts[best[(i + 1) % best.length]].x * sW), (int) (pts[best[(i + 1) % best.length]].y * sH));
		}

		// draw points
		g2.setColor(Color.RED);
		for (Point p : pts) {
			g2.fillArc((int) (p.x * sW) - r/2, (int) (p.y * sH) - r/2, r, r, 0, 360);
		}
	}
}