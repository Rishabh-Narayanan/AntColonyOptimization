
/*
References:
- https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms
- https://www.researchgate.net/publication/220835272_On_Optimal_Parameters_for_Ant_Colony_Optimization_Algorithms
*/
import java.math.BigInteger;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AcoSolution {

	private int numAnts;
	private double[][] pheremoneStrength;
	private double[][] pheremoneUpdateBuffer;

	GameState gs;
	BigInteger totalPaths;
	BigInteger checkedPaths;

	Random r;

	Execute onEachPath;

	double evaporationRate = 0.2;
	double pheremonePower = 0.6; // (0, 1)
	double desirabilityPower = 7; // (0, 15)
	double q0 = 1; // The probability that the ant will not choose the existing best path

	public AcoSolution(int seed, BigInteger totalPaths, int numAnts, Point[] pts, Execute onEachPath) {
		r = new Random(seed); // change seed

		gs = new GameState(pts);
		checkedPaths = BigInteger.valueOf(0);
		this.totalPaths = totalPaths;
		this.onEachPath = onEachPath;

		this.numAnts = numAnts;
		this.pheremoneStrength = new double[pts.length][pts.length];
		this.pheremoneUpdateBuffer = new double[pts.length][pts.length];
		for (int i = 0; i < gs.n; i++) {
			for (int j = 0; j < gs.n; j++) {
				pheremoneStrength[i][j] = 1;
				pheremoneUpdateBuffer[i][j] = 0;
			}
		}
	}

	double[] decisionProbability;
	int[] path;
	boolean[] visited;

	public void simulateAnt(int depth) {
		if (depth == gs.n)
			return;
		int previousAntPoint = path[depth - 1];

		double sum = 0;
		int iMax = 0;
		for (int i = 0; i < visited.length; i++) {
			decisionProbability[i] = 0; // reset
			if (!visited[i]) {
				double desirability = 1 / gs.weights[previousAntPoint][i];
				decisionProbability[i] = Math.pow(desirability, desirabilityPower)
						* Math.pow(pheremoneStrength[previousAntPoint][i], pheremonePower);
				if (decisionProbability[i] > decisionProbability[iMax])
					iMax = i;
			}
			sum += decisionProbability[i];
		}

		boolean chooseBest = r.nextDouble() > q0;
		double threshold = r.nextDouble() * sum;
		int i;

		if (chooseBest) {
			i = iMax;
		} else {
			sum = 0;
			for (i = 0; i < visited.length - 1; i++) {
				sum += decisionProbability[i];
				if (sum >= threshold)
					break;
			}
		}

		visited[i] = true;
		path[depth] = i;
		simulateAnt(depth + 1);
	}

	public void train(int numIterations) {
		decisionProbability = new double[gs.n];
		path = new int[gs.n];
		visited = new boolean[gs.n];

		for (int i = 0; i < numIterations; i++) {
			for (int j = 0; j < numAnts; j++) {
				// reset
				for (int k = 0; k < gs.n; k++)
					visited[k] = false;
				path[0] = 0; // shouldn't ever change anyways
				visited[0] = true; // shouldn't ever change anyways

				simulateAnt(1);
				resetUpdateBuffer();

				double cost = gs.tryPath(path);
				for (int k = 0; k < path.length - 1; k++) {
					pheremoneUpdateBuffer[path[k]][path[(k + 1) % gs.n]] += 1 / cost;
				}
				checkedPaths = checkedPaths.add(BigInteger.ONE);
				onEachPath.apply(totalPaths, checkedPaths, gs.pts, gs.bestPath);
			}
			updatePheremonesFromBuffer();
		}
	}

	private void resetUpdateBuffer() {
		for (int i = 0; i < gs.n; i++) {
			for (int j = 0; j < gs.n; j++) {
				pheremoneUpdateBuffer[i][j] = 0;
			}
		}
	}

	private void updatePheremonesFromBuffer() {
		for (int i = 0; i < gs.n; i++) {
			for (int j = 0; j < gs.n; j++) {
				pheremoneStrength[i][j] *= (1 - evaporationRate);
				pheremoneStrength[i][j] += pheremoneUpdateBuffer[i][j];
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
		int numAnts = 10;
		int numIterations = 10;
		int seed = new Random().nextInt(Integer.MAX_VALUE);

		for (String s : args) {
			if (s.startsWith("-n=")) {
				try {
					n = Integer.parseInt(s.substring(3));
				} catch (Exception e) {
					System.out.println("Invalid argument for n. Defaulting to n=10");
				}
			}
			if (s.startsWith("-a=")) {
				try {
					numAnts = Integer.parseInt(s.substring(3));
				} catch (Exception e) {
					System.out.println("Invalid argument for a. Defaulting to a=10");
				}
			}
			if (s.startsWith("-i=")) {
				try {
					numIterations = Integer.parseInt(s.substring(3));
				} catch (Exception e) {
					System.out.println("Invalid argument for i. Defaulting to i=10");
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

		AcoStatsGUI sg = new AcoStatsGUI(totalPaths);
		BestGUI bg = new BestGUI(pts);
		JFrame fr = new JFrame("ACO Solution");
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		main.add(sg);
		main.add(bg);

		AcoSolution ns = new AcoSolution(seed, totalPaths, numAnts, pts, (total, checked, points, best) -> {
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

		ns.train(numIterations);

		sleep(50);
		ns.onEachPath.apply(totalPaths, ns.checkedPaths, ns.gs.pts, ns.gs.bestPath);
	}
}