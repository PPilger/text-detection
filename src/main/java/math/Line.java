package math;

/**
 * Represents a line between two points.
 * 
 * @author PilgerstorferP
 * 
 */
public class Line {
	// the start and end points of the line
	private Vector p;
	private Vector q;

	// slope and offset of the line (compare y = k * x + d)
	private double k;
	private double d;

	/**
	 * Creates a new Line from p to q
	 * 
	 * @param p
	 * @param q
	 */
	public Line(Vector p, Vector q) {
		this.p = p;
		this.q = q;

		this.k = (q.y - p.y) / (q.x - p.x);
		this.d = p.y - k * p.x;
	}

	/**
	 * @param value
	 * @return true if value lies between the two points along the x-axis
	 */
	private boolean insideX(double value) {
		if (p.x < q.x) {
			return p.x < value && value < q.x;
		} else {
			return q.x < value && value < p.x;
		}
	}

	/**
	 * @param value
	 * @return true if value lies between the two points along the y-axis
	 */
	private boolean insideY(double value) {
		if (p.y < q.y) {
			return p.y < value && value < q.y;
		} else {
			return q.y < value && value < p.y;
		}
	}

	/**
	 * @param other
	 * @return true if the line intersects other, false otherwise
	 */
	public boolean intersects(Line other) {
		if (k == other.k
				|| (Double.isInfinite(k) && Double.isInfinite(other.k))) {
			return false;
		}

		if (Double.isInfinite(k) || Double.isInfinite(other.k)) {
			double y = other.k * p.x + other.d;

			return insideY(y) && other.insideY(y);
		} else if (Double.isInfinite(other.k)) {
			double y = k * other.p.x + d;

			return insideY(y) && other.insideY(y);
		} else {
			double x = (other.d - d) / (d - other.k);

			return insideX(x) && other.insideX(x);
		}
	}
}
