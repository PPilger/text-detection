package math;

public class Line {
	private Vector p;
	private Vector q;

	private double k;
	private double d;

	public Line(Vector v0, Vector v1) {
		this.p = v0;
		this.q = v1;

		this.k = (q.y - p.y) / (q.x - p.x);
		this.d = p.y - k * p.x;
	}

	private boolean insideX(double value) {
		if (p.x < q.x) {
			return p.x < value && value < q.x;
		} else {
			return q.x < value && value < p.x;
		}
	}

	private boolean insideY(double value) {
		if (p.y < q.y) {
			return p.y < value && value < q.y;
		} else {
			return q.y < value && value < p.y;
		}
	}

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
