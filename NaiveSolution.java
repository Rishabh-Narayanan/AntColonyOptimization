
import java.math.BigInteger;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class NaiveSolution {
	GameState gs;
	BigInteger totalPaths;
	BigInteger checkedPaths;

	Execute onEachPath;

	public NaiveSolution(BigInteger totalPaths, Point[] pts, Execute onEachPath) {
		gs = new GameState(pts);
		checkedPaths = BigInteger.valueOf(0);
		this.totalPaths = totalPaths;
		this.onEachPath = onEachPath;
	}

	public void dfs() {
		int[] path = new int[gs.n];
		boolean[] visited = new boolean[gs.n];
		visited[0] = true;
		dfs(path, visited, 1);
	}

	private void dfs(int[] path, boolean[] visited, int depth) {
		if (visited.length != gs.n || path.length != gs.n || depth <= 0 || depth > gs.n)
			return;
		if (depth == gs.n) {
			gs.tryPath(path);
			checkedPaths = checkedPaths.add(BigInteger.ONE);
			onEachPath.apply(totalPaths, checkedPaths, gs.pts, gs.bestPath);
		} else {
			for (int i = 0; i < gs.n; i++) {
				if (visited[i])
					continue;
				visited[i] = true;
				path[depth - 1] = i;
				dfs(path, visited, depth + 1);
				visited[i] = false;
			}
		}
	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	public static void main(String[] args) {
		int n = 10;

		int seed = new Random().nextInt(Integer.MAX_VALUE);
		for (String s : args) {
			if (s.startsWith("-n=")) {
				try {
					n = Integer.parseInt(s.substring(3));
				} catch (Exception e) {
					System.out.println("Invalid argument for n. Defaulting to n=10");
				}
			}
			if (s.startsWith("-s=")) {
				try {
					seed = Integer.parseInt(s.substring(3));
				} catch (Exception e) {
					System.out.println("Invalid argument for s. Defaulting to random seed");
				}
			}
		}

		BigInteger totalPaths = BigInteger.valueOf(1);
		for (int i = 2; i < n; i++) {
			totalPaths = totalPaths.multiply(BigInteger.valueOf(i));
		}

		Point[] pts = new Point[n];
		Random r = new Random(seed);
		for (int i = 0; i < n; i++) {
			pts[i] = new Point(r.nextInt(1000), r.nextInt(1000));
		}

		NaiveStatsGUI sg = new NaiveStatsGUI(totalPaths);
		BestGUI bg = new BestGUI(pts);
		JFrame fr = new JFrame("Naive Solution");
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		main.add(sg);
		main.add(bg);

		NaiveSolution ns = new NaiveSolution(totalPaths, pts, (total, checked, points, best) -> {
			sg.set(total, checked);
			bg.set(points, best);
		});

		fr.add(main);
		fr.pack();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);

		sleep(500);

		sg.start();
		bg.start();

		ns.dfs();
		// sg.dispose();
		// bg.dispose();

		sleep(50);
		ns.onEachPath.apply(totalPaths, ns.checkedPaths, ns.gs.pts, ns.gs.bestPath);
	}
}