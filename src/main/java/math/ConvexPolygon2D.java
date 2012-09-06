package math;

import java.util.*;

import feature.Feature;

import miscellanous.Interval;

public class ConvexPolygon2D {
	private int[] corners;

	public ConvexPolygon2D(int[] corners) {
		this.corners = corners;
	}

	public int[] getCorners() {
		return corners;
	}

	public static double intersection(Feature f0, Feature f1) {
		Rotation2D rotation = new Rotation2D(-f0.angle().getRadians());
		int[] corners0 = f0.getCorners();
		int[] corners1 = f1.getCorners();
		corners0 = Arrays.copyOf(corners0, corners0.length);
		corners1 = Arrays.copyOf(corners1, corners1.length);

		// the rectangles are transformed so that the first one is axis aligned
		// with one point at (0/0)
		{
			rotation.rotate(corners0);
			rotation.rotate(corners1);

			int xmin = corners0[0];
			int ymin = corners0[1];

			for (int i = 2; i < corners0.length; i += 2) {
				xmin = xmin < corners0[i] ? xmin : corners0[i];
				ymin = ymin < corners0[i + 1] ? ymin : corners0[i + 1];
			}

			for (int i = 2; i < corners1.length; i += 2) {
				corners1[i] -= xmin;
				corners1[i + 1] -= ymin;
			}
		}

		// renaming the two arrays
		int[] srcCorners = Arrays.copyOf(corners1, corners1.length);
		int srcLength = corners1.length;
		int[] dstCorners = Arrays.copyOf(corners0, corners0.length);
		int dstLength = 0;

		EdgeClipper[] clipperArr = new EdgeClipper[4];
		clipperArr[0] = new Top((int) Math.round(f0.height()));
		clipperArr[1] = new Left();
		clipperArr[2] = new Right((int) Math.round(f0.width()));
		clipperArr[3] = new Bottom();

		for (EdgeClipper clipper : clipperArr) {
			int x0 = srcCorners[srcLength - 2];
			int y0 = srcCorners[srcLength - 1];

			for (int i = 0; i < srcLength; i += 2) {
				int x1 = srcCorners[i];
				int y1 = srcCorners[i + 1];

				if (clipper.inside(x0, y0)) {
					// add corner
					dstCorners[dstLength] = x0;
					dstCorners[dstLength + 1] = y0;
					dstLength += 2;
				}

				if (clipper.inside(x0, y0) ^ clipper.inside(x1, y1)) {
					// add intersection point
					int[] ipoint = clipper.intersection(x0, y0, x1, y1);
					dstCorners[dstLength] = ipoint[0];
					dstCorners[dstLength + 1] = ipoint[1];
					dstLength += 2;
				}

				x0 = x1;
				y0 = y1;
			}

			// swap src and dst for the next iteration
			int[] temp0 = srcCorners;
			srcCorners = dstCorners;
			dstCorners = temp0;

			srcLength = dstLength;
			dstLength = 0;
		}

		// calculate area
		int area = 0;

		int x0 = srcCorners[srcLength - 2];
		int y0 = srcCorners[srcLength - 1];

		for (int i = 0; i < srcLength; i += 2) {
			int x1 = srcCorners[i];
			int y1 = srcCorners[i + 1];

			area += (x0 - x1) * (y0 + y1);

			x0 = x1;
			y0 = y1;
		}

		return area;
	}

	static interface EdgeClipper {
		public boolean inside(int x, int y);

		public int[] intersection(int x0, int y0, int x1, int y1);
	}

	static class Left implements EdgeClipper {
		@Override
		public boolean inside(int x, int y) {
			return x >= 0;
		}

		@Override
		public int[] intersection(int x0, int y0, int x1, int y1) {
			int[] p = new int[2];

			p[0] = 0;
			p[1] = (int) Math.round(y0 - x0 * (y1 - y0) / (double) (x1 - x0));
			// division by 0 is not possible if one of the points is inside and
			// the other one outside

			return p;
		}
	}

	static class Bottom implements EdgeClipper {
		@Override
		public boolean inside(int x, int y) {
			return y >= 0;
		}

		@Override
		public int[] intersection(int x0, int y0, int x1, int y1) {
			int[] p = new int[2];

			p[0] = (int) Math.round(x0 - y0 * (x1 - x0) / (double) (y1 - y0));
			p[1] = 0;

			return p;
		}
	}

	static class Right implements EdgeClipper {
		private int width;

		public Right(int width) {
			this.width = width;
		}

		@Override
		public boolean inside(int x, int y) {
			return x <= width;
		}

		@Override
		public int[] intersection(int x0, int y0, int x1, int y1) {
			int[] p = new int[2];

			p[0] = width;
			p[1] = (int) Math.round(y0 + (width - x0) * (y1 - y0)
					/ (double) (x1 - x0));

			return p;
		}
	}

	static class Top implements EdgeClipper {
		private int height;

		public Top(int height) {
			this.height = height;
		}

		@Override
		public boolean inside(int x, int y) {
			return y <= height;
		}

		@Override
		public int[] intersection(int x0, int y0, int x1, int y1) {
			int[] p = new int[2];

			p[0] = (int) Math.round(x0 + (height - y0) * (x1 - x0)
					/ (double) (y1 - y0));
			p[1] = height;

			return p;
		}
	}

	static class Point {
		public int x;
		public int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	static class Line {
		private int xs, ys;
		private int xd, yd;

		private double k;
		private double d;

		public Line(int xs, int ys, int xd, int yd) {
			this.xs = xs;
			this.ys = ys;
			this.xd = xd;
			this.yd = yd;

			this.k = (yd - ys) / (double) (xd - xs);
			this.d = ys - k * xs;
		}

		private static boolean inside(double value, double bound0, double bound1) {
			return Math.min(bound0, bound1) < value
					&& value < Math.max(bound0, bound1);
		}

		public int y(int x) {
			return (int) Math.round(k * x + d);
		}

		public double y(double x) {
			return k * x + d;
		}

		public static int[] intersect(Line pq0, Line pq1) {
			if (pq0.k == pq1.k
					|| (Double.isInfinite(pq0.k) && Double.isInfinite(pq1.k))) {
				return null;
			}

			double x, y;
			boolean inside;
			if (Double.isInfinite(pq0.k)) {
				x = pq0.xs;
				y = pq1.y(x);

				inside = inside(y, pq0.ys, pq0.yd) && inside(y, pq1.ys, pq1.yd);

			} else if (Double.isInfinite(pq1.k)) {
				x = pq1.xs;
				y = pq0.y(x);

				inside = inside(y, pq0.ys, pq0.yd) && inside(y, pq1.ys, pq1.yd);
			} else {
				x = (pq1.d - pq0.d) / (pq0.k - pq1.k);
				y = pq0.y(x);

				inside = inside(x, pq0.xs, pq0.xd) && inside(x, pq1.xs, pq1.xd);
			}

			if (!inside) {
				return null;
			}

			int[] intersection = { (int) Math.round(x), (int) Math.round(y) };
			return intersection;
		}
	}

	static class LineComparator implements Comparator<Line> {
		private int x;

		public LineComparator(int x) {
			this.x = x;
		}

		@Override
		public int compare(Line o1, Line o2) {
			int y1 = o1.y(x);
			int y2 = o2.y(x);
			if (y1 == y2) {
				return o1.yd - o2.yd;
			}
			return y1 - y2;
		}
	}
}
