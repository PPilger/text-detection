package math;

import java.util.*;

import miscellanous.Interval;

public class ConvexPolygon2D {
	private int[] corners;

	public ConvexPolygon2D(int[] corners) {
		this.corners = corners;
	}

	public int[] getCorners() {
		return corners;
	}

	public static double intersection(int[]... polygons) {
		PriorityQueue<Event> queue = new PriorityQueue<Event>();

		for (int p = 0; p < 2; p++) {
			int[] polygon = polygons[p];

			int x0 = polygon[polygon.length - 2];
			int y0 = polygon[polygon.length - 1];

			for (int i = 0; i < polygon.length; i += 2) {
				int x1 = polygon[i];
				int y1 = polygon[i + 1];

				Line line;
				if (x0 < x1) {
					line = new Line(x0, x0, x1, y1, p);
				} else {
					line = new Line(x1, x1, x0, y0, p);
				}

				queue.offer(new LineStarts(line));
				queue.offer(new LineEnds(line));

				x0 = x1;
				y0 = y1;
			}
		}

		boolean intersecting = false;
		int[] count = new int[2];
		Set<Line> lines = new TreeSet<Line>();
		while (!queue.isEmpty()) {
			Event event = queue.poll();

			Line line;
			if ((line = event.ends()) != null) {
				lines.remove(line);
				count[line.polygon]--;
				
				if (count[line.polygon] == 0) {
					break;
				}
			}
			if ((line = event.starts()) != null) {
				lines.add(line);
				count[line.polygon]++;

				if (count[1 - line.polygon] != 0) {

				}
			}
		}

		return 0;
	}

	static abstract class Event implements Comparable<Event> {
		private int x;

		public Event() {
		}

		public Event(int x) {
			this.x = x;
		}

		void init(int x) {
			this.x = x;
		}

		public Line starts() {
			return null;
		}

		public Line ends() {
			return null;
		}

		public abstract int order();

		@Override
		public int compareTo(Event o) {
			if (x == o.x) {
				return order() - o.order();
			} else {
				return x - o.x;
			}
		}
	}

	static class LineStarts extends Event {
		private Line line;

		public LineStarts(Line line) {
			super(line.xs);

			this.line = line;
		}

		@Override
		public Line starts() {
			return line;
		}

		@Override
		public int order() {
			return 0;
		}
	}

	static class Intersection extends Event {
		private Line l0, l1;

		public Intersection(Line l0, Line l1) {
			int[] intersection = Line.intersect(l0, l1);

			init(intersection[0]);
		}

		@Override
		public int order() {
			return 1;
		}

	}

	static class LineEnds extends Event {
		private Line line;

		public LineEnds(Line line) {
			super(line.xd);

			this.line = line;
		}

		@Override
		public Line ends() {
			return line;
		}

		@Override
		public int order() {
			return 2;
		}
	}
	
	static class Line implements Comparable<Line> {
		private int xs, ys;
		private int xd, yd;
		private int polygon;

		private double k;
		private double d;

		public Line(int xs, int ys, int xd, int yd, int polygon) {
			this.xs = xs;
			this.ys = ys;
			this.xd = xd;
			this.yd = yd;
			this.polygon = polygon;

			this.k = (yd - ys) / (double) (xd - xs);
			this.d = ys - k * xs;
		}

		private static boolean inside(double value, double bound0, double bound1) {
			return Math.min(bound0, bound1) < value
					&& value < Math.max(bound0, bound1);
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
				y = pq1.k * x + pq1.d;

				inside = inside(y, pq0.ys, pq0.yd) && inside(y, pq1.ys, pq1.yd);
			} else if (Double.isInfinite(pq1.k)) {
				x = pq1.xs;
				y = pq0.k * x + pq0.d;

				inside = inside(y, pq0.ys, pq0.yd) && inside(y, pq1.ys, pq1.yd);
			} else {
				x = (pq1.d - pq0.d) / (pq0.k - pq1.k);
				y = pq0.k * x + pq0.d;

				inside = inside(x, pq0.xs, pq0.xd) && inside(x, pq1.xs, pq1.xd);
			}

			if (inside) {
				int[] intersection = { (int) Math.round(x), (int) Math.round(y) };
				return intersection;
			} else {
				return null;
			}
		}

		@Override
		public int compareTo(Line o) {
			return ys - o.ys;
		}
	}
}
