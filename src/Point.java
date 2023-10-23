package src;

public class Point {
	public final int x, y;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public double dist(Point o) {
		return Math.sqrt((o.x - x) * (o.x - x) + (o.y - y) * (o.y - y));
	}
}