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
	
	public static void draw(int[] img, int width, int x0, int y0, int x1, int y1) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		int sx;
		int sy;

		sx = x0 < x1 ? 1 : -1;
		sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;

		img[y0 * width + x0] = 1;
		while (x0 != x1 || y0 != y1) {
			int e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
			img[y0 * width + x0] = 1;
		}
	}
}
