
public class GameState {
	int n;
	Point[] pts;
	double[][] weights;
	static final int MAX_POINT_COORDINATE = 1000;

	int[] bestPath; // indices of 'pts' for the current best path
	double bestPathCost;

	public GameState(Point[] pts) {
		this.n = pts.length;

		this.pts = pts;
		bestPath = new int[n];
		weights = new double[n][n];
		
		for (int i = 0; i < n; i++) {
			bestPath[i] = i; // initial best path [1, 2, 3 ... n];
		}

		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++) {
				double dist = pts[j].dist(pts[i]);
				weights[i][j] = dist;
				weights[j][i] = dist;
			}
		}

		// calculate best cost of initial path [1, 2, 3 ... n];
		bestPathCost = weights[n - 1][0];
		for (int i = 0; i < n - 1; i++ ) bestPathCost += weights[i][i + 1];
	}


	private boolean valid(int[] path) {
		if (path.length != n) return false;

		boolean[] visitedAll = new boolean[n];
		for (int i : path) {
			if (i < 0 || i >= n) return false;
			visitedAll[i] = true;
		}
		for (boolean b : visitedAll) {
			if (!b) return false;
		}
		return true;
	}
	
	public double tryPath(int[] path) {
		if (!valid(path)) return Double.POSITIVE_INFINITY;
		double cost = weights[path[n - 1]][path[0]];
		for (int i = 0; i < n - 1; i++) {
			cost += weights[path[i]][path[i + 1]];
		}
		
		if (cost < bestPathCost) {
			// new best path
			bestPathCost = cost;
			bestPath = path.clone();
		}
		return cost;
	}
}
