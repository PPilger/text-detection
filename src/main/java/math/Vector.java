package math;

import java.util.Locale;

/**
 * Defines a 2D vector.
 * 
 * @author PilgerstorferP
 * 
 */
public class Vector {
	// Cartesian coordinates
	public double x;
	public double y;

	/**
	 * Transforms the points to the form {x0, y0, x1, y1, ..., xn, yn}
	 * 
	 * @param points
	 * @return
	 */
	public static int[] asIntArray(Vector[] points) {
		int[] array = new int[points.length * 2];

		int i = 0;
		for (Vector point : points) {
			array[i] = (int) Math.round(point.x);
			array[i + 1] = (int) Math.round(point.y);
			i += 2;
		}

		return array;
	}

	/**
	 * Calculates the bounds (min, max) of the points.
	 * 
	 * @param points
	 * @return an array of two points {min, max}
	 */
	public static Vector[] bounds(Vector[] points) {
		double xmin = points[0].x;
		double xmax = points[0].x;
		double ymin = points[0].y;
		double ymax = points[0].y;

		for (int i = 1; i < points.length; i++) {
			Vector p = points[i];
			if (p.x < xmin) {
				xmin = p.x;
			} else if (p.x > xmax) {
				xmax = p.x;
			}

			if (p.y < ymin) {
				ymin = p.y;
			} else if (p.y > ymax) {
				ymax = p.y;
			}
		}

		Vector[] bounds = { new Vector(xmin, ymin), new Vector(xmax, ymax) };
		return bounds;
	}

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param other
	 * @return the point in the middle of this point and other
	 */
	public Vector center(Vector other) {
		return new Vector((x + other.x) / 2, (y + other.y) / 2);
	}

	/**
	 * @param other
	 * @return the distance between this point and other
	 */
	public double distance(Vector other) {
		double x = other.x - this.x;
		double y = other.y - this.y;
		return Math.sqrt(x * x + y * y);
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "{\"x\": %.2f, \"y\": %.2f}", x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
}
