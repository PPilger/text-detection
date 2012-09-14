package math;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoxPoints;

import java.util.Arrays;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;

/**
 * Represents a 2D box
 * @author PilgerstorferP
 *
 */
public class Box {
	private Vector[] corners; // the corners of the box

	// the corners of the axis aligned bounding box
	private Vector min;
	private Vector max;

	// another representation of the box
	private Vector center;
	private double width; // the longer side of the box
	private double height; // the shorter side of the box
	private Angle180 angle; // the angle of the longer side of the box to the
							// x-axis

	/**
	 * Creates a new box with the specified properties
	 * 
	 * @param center
	 * @param width
	 *            width >= 0
	 * @param height
	 *            height >= 0
	 * @param angle
	 *            the angle of the "width"-side of the box
	 */
	public Box(Vector center, double width, double height, Angle180 angle) {
		this(new CvBox2D(cvPoint2D32f(center.x, center.y), cvSize2D32f(width,
				height), (float) angle.getDegrees()));
	}

	/**
	 * Creates a new box from the specified CvBox2D object
	 * 
	 * @param box
	 */
	public Box(CvBox2D box) {
		// calculate corners of the box
		{
			corners = new Vector[4];
			float[] coords = new float[8];
			cvBoxPoints(box, coords);

			corners[0] = new Vector(coords[0], coords[1]);
			corners[1] = new Vector(coords[2], coords[3]);
			corners[2] = new Vector(coords[4], coords[5]);
			corners[3] = new Vector(coords[6], coords[7]);
		}

		// calculate min and max vectors
		{
			double xmin = corners[0].x;
			double ymin = corners[0].y;
			double xmax = 0;
			double ymax = 0;

			for (Vector corner : corners) {
				if (corner.x < xmin) {
					xmin = corner.x;
				} else if (xmax < corner.x) {
					xmax = corner.x;
				}

				if (corner.y < ymin) {
					ymin = corner.y;
				} else if (ymax < corner.y) {
					ymax = corner.y;
				}
			}

			min = new Vector(xmin, ymin);
			max = new Vector(xmax, ymax);
		}

		center = new Vector(box.center().x(), box.center().y());
		width = box.size().width();
		height = box.size().height();
		angle = new Angle180(Math.toRadians(box.angle()));

		if (width < height) {
			// swap width and height
			double temp = width;
			width = height;
			height = temp;
			angle.rotate(Math.PI / 2);
		}
	}

	/**
	 * Returns a new box that is created by adding the specified border to the
	 * box (border is added to width and to height)
	 * 
	 * @param border
	 *            border >= 0
	 * @return the new box
	 */
	public Box makeBorder(double border) {
		return new Box(center, width + border, height + border, angle);
	}

	public Vector[] getCorners() {
		return corners;
	}

	/**
	 * @return vector with the minimum x and y value
	 */
	public Vector getMin() {
		return min;
	}

	/**
	 * @return vector with the maximum x and y value
	 */
	public Vector getMax() {
		return max;
	}

	public Vector getCenter() {
		return center;
	}

	/**
	 * @return the longer side of the box
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return the shorter side of the box
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @return the angle to the longer side of the box
	 */
	public Angle180 getAngle() {
		return angle;
	}

	public double getArea() {
		return width * height;
	}

	/**
	 * Draws the box onto the image img
	 * 
	 * @param img
	 *            the image to draw onto
	 * @param color
	 *            the color of the box
	 */
	public void draw(CvArr img, CvScalar color) {
		CvPoint pp0 = cvPoint((int) corners[0].x, (int) corners[0].y);
		CvPoint pp1 = cvPoint((int) corners[1].x, (int) corners[1].y);
		CvPoint pp2 = cvPoint((int) corners[2].x, (int) corners[2].y);
		CvPoint pp3 = cvPoint((int) corners[3].x, (int) corners[3].y);

		cvDrawLine(img, pp0, pp1, color, 1, 0, 0);
		cvDrawLine(img, pp1, pp2, color, 1, 0, 0);
		cvDrawLine(img, pp2, pp3, color, 1, 0, 0);
		cvDrawLine(img, pp3, pp0, color, 1, 0, 0);
	}

	@Override
	public String toString() {
		return center + ", " + width + "/" + height + ", " + angle.getDegrees()
				+ "°";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(corners);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Box other = (Box) obj;
		if (!Arrays.equals(corners, other.corners))
			return false;
		return true;
	}

	/**
	 * @param other
	 *            box to be tested for intersection
	 * @return true if the box intersects with other, false otherwise
	 */
	public boolean intersects(Box other) {
		// intersect all possible pairs of lines
		int ip = 3;
		for (int i = 0; i < 4; ip = i, i++) {
			Line line0 = new Line(this.corners[ip], this.corners[i]);

			int jp = 3;
			for (int j = 0; j < 4; jp = j, j++) {
				Line line1 = new Line(other.corners[jp], other.corners[j]);

				if (line0.intersects(line1)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @param other
	 * @return distance between this box and the box other
	 */
	public double distance(Box other) {
		// distance is 0 if the two boxes intersect
		if (this.intersects(other)) {
			return 0;
		}

		double distance = Double.POSITIVE_INFINITY;

		Vector[][] corners = new Vector[][] { this.corners, other.corners };

		// calculate the distance of the two boxes by calculating the distance
		// of each point to each line of the other box

		// iterate through the two boxes
		for (int i = 0; i < 2; i++) {
			int o = 1 - i;

			double[] edges = getEdges(corners[o]);

			// iterate through the corners of one box
			for (int iCorner = 0; iCorner < 4; iCorner++) {
				double[] distances = getDistances(corners[i][iCorner],
						corners[o]);

				int count = 0;

				// iterate through the corners of the other box
				int iCorner0 = 3;
				for (int iCorner1 = 0; iCorner1 < 4; iCorner0 = iCorner1, iCorner1++) {
					// //////////////////////////////////////////////////////////
					// Formula to calculate the distance between a point and an
					// edge
					// given: points P, Q, R
					// result: distance from P to the edge QR
					//
					// point X ... if you put a line normal to QR through P,
					// X is the intersection-point of this line and QR
					//
					// formula: x = sqrt(a² - ((c² + a² - b²) / (2c))²)
					//
					// variables:
					// a ... |PQ|
					// b ... |PR|
					// c ... |QR|
					// aa, bb, cc ... squares of a, b and c
					// c0 ... |XQ|
					// x ... |PX|
					// //////////////////////////////////////////////////////////

					double a = distances[iCorner0];
					double b = distances[iCorner1];
					double c = edges[iCorner0];
					double aa = a * a, bb = b * b, cc = c * c;

					distance = Math.min(distance, a);
					distance = Math.min(distance, b);

					double c0 = cc + aa - bb;
					if (c0 < 0) {
						// the value x does not exist, X is not on the edge
						continue;
					}
					c0 /= 2 * c;
					if (c0 > c) {
						// the value x does not exist, X is not on the edge
						continue;
					}

					double x = Math.sqrt(aa - c0 * c0);
					distance = Math.min(distance, x);

					count++;
				}

				// if 4 normals (for each edge) through the point exist,
				// the point is inside the box
				if (count == 4) {
					return 0;
				}
			}
		}

		return distance;
	}

	/**
	 * @param corners
	 *            the 4 corners that define the box
	 * @return the lengths of the edges of a box. The index of an edge is the
	 *         same as the index of the starting corner.
	 */
	private static double[] getEdges(Vector[] corners) {
		double[] edges = new double[4];
		edges[0] = corners[0].distance(corners[1]);
		edges[1] = corners[1].distance(corners[2]);
		edges[2] = corners[2].distance(corners[3]);
		edges[3] = corners[3].distance(corners[0]);

		return edges;
	}

	/**
	 * @param point
	 * @param others
	 *            an array of 4 points
	 * @return the distance between the single point and each of the other
	 *         points
	 */
	private static double[] getDistances(Vector point, Vector[] others) {
		double[] distances = new double[4];
		distances[0] = point.distance(others[0]);
		distances[1] = point.distance(others[1]);
		distances[2] = point.distance(others[2]);
		distances[3] = point.distance(others[3]);

		return distances;
	}

	/**
	 * @param other
	 * @return the intersection area of this box and the box other
	 */
	public double intersectionArea(Box other) {
		// the rectangles are transformed so that the first one is axis aligned
		// with the min point at (0/0) and the max point at (width/height) (this
		// one is not stored in a local variable)
		int[] otherCorners;
		{
			int[] corners = Vector.asIntArray(this.corners);
			otherCorners = Vector.asIntArray(other.corners);

			Rotation rotation = new Rotation(-angle.getRadians());
			rotation.rotate(corners);
			rotation.rotate(otherCorners);

			// determine the vector by which the corners need to be translated
			int xmin = corners[0];
			int ymin = corners[1];

			for (int i = 2; i < corners.length; i += 2) {
				xmin = xmin < corners[i] ? xmin : corners[i];
				ymin = ymin < corners[i + 1] ? ymin : corners[i + 1];
			}

			// translate the rotated corners of other
			for (int i = 0; i < otherCorners.length; i += 2) {
				otherCorners[i] -= xmin;
				otherCorners[i + 1] -= ymin;
			}
		}

		// calculate the intersecting polygon
		int[] polygon;
		int numPoints; // number of points of the polygon
		{
			// src array is initialized with the corners of the other box
			// src and dst array can contain twice as many points as corners to
			// handle the worst case of intersection
			int[] srcCorners = Arrays.copyOf(otherCorners,
					otherCorners.length * 2);
			int srcLength = otherCorners.length;

			int[] dstCorners = new int[otherCorners.length * 2];
			int dstLength = 0;

			// calculate the intersecting polygon by clipping the rectangle for
			// each edge
			EdgeClipper[] clipperArr = { new Top(), new Left(), new Right(),
					new Bottom() };

			for (EdgeClipper clipper : clipperArr) {
				// previous point
				int x0 = srcCorners[srcLength - 2];
				int y0 = srcCorners[srcLength - 1];

				for (int i = 0; i < srcLength; i += 2) {
					// current point
					int x1 = srcCorners[i];
					int y1 = srcCorners[i + 1];

					if (clipper.inside(x0, y0)) {
						// add corner
						dstCorners[dstLength] = x0;
						dstCorners[dstLength + 1] = y0;
						dstLength += 2;
					}

					if (clipper.inside(x0, y0) ^ clipper.inside(x1, y1)) {
						// transition inside -> outside or outside -> inside
						// add intersection point
						int[] ipoint = clipper.intersection(x0, y0, x1, y1);
						dstCorners[dstLength] = ipoint[0];
						dstCorners[dstLength + 1] = ipoint[1];
						dstLength += 2;
					}

					x0 = x1;
					y0 = y1;
				}

				// if less than 3 points are present after clipping, the
				// intersection-area is 0
				if (dstLength < 3 * 2) {
					return 0;
				}

				// prepare src and dst for the next iteration
				int[] temp0 = srcCorners;
				srcCorners = dstCorners;
				dstCorners = temp0;

				srcLength = dstLength;
				dstLength = 0;
			}

			polygon = srcCorners;
			numPoints = srcLength;
		}

		// calculate area
		int area;
		{
			area = 0;

			int x0 = polygon[numPoints - 2];
			int y0 = polygon[numPoints - 1];

			for (int i = 0; i < numPoints; i += 2) {
				int x1 = polygon[i];
				int y1 = polygon[i + 1];

				area += (x0 - x1) * (y0 + y1);

				x0 = x1;
				y0 = y1;
			}

			area /= 2;
		}

		return area;
	}

	private interface EdgeClipper {
		/**
		 * @param x
		 * @param y
		 * @return true if the point (x/y) is inside, false otherwise
		 */
		public boolean inside(int x, int y);

		/**
		 * Calculates the intersection point of a the line PQ with the clipping
		 * edge, where P = (x0/y0) and Q = (x1/y1)
		 * 
		 * @param x0
		 * @param y0
		 * @param x1
		 * @param y1
		 * @return the intersection point {x, y}
		 */
		public int[] intersection(int x0, int y0, int x1, int y1);
	}

	private class Left implements EdgeClipper {
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

	private class Bottom implements EdgeClipper {
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

	private class Right implements EdgeClipper {
		private int width = (int) Math.round(Box.this.width);

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

	private class Top implements EdgeClipper {
		private int height = (int) Math.round(Box.this.height);

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
}
