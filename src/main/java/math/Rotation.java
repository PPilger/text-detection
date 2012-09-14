package math;

/**
 * Defines an object that can rotate 2D points.
 * 
 * @author PilgerstorferP
 * 
 */
public class Rotation {
	// the rotation matrix
	private double c00;
	private double c01;
	private double c10;
	private double c11;

	/**
	 * @param angle
	 *            the rotation angle in radians
	 */
	public Rotation(double angle) {
		c00 = c11 = Math.cos(angle);
		c10 = Math.sin(angle);
		c01 = -c10;
	}

	/**
	 * Rotates point around the origin
	 * 
	 * @param point
	 * @return a new, rotated point
	 */
	public Vector rotate(Vector point) {
		double x = point.x * c00 + point.y * c01;
		double y = point.x * c10 + point.y * c11;

		return new Vector(x, y);
	}

	/**
	 * Rotates point around the point center
	 * 
	 * @param point
	 * @param center
	 * @return a new, rotated point
	 */
	public Vector rotate(Vector point, Vector center) {
		double xt = point.x - center.x;
		double yt = point.y - center.y;

		double x = xt * c00 + yt * c01 + center.x;
		double y = xt * c10 + yt * c11 + center.y;

		return new Vector(x, y);
	}

	/**
	 * Rotates every point in points around the point center
	 * 
	 * @param points
	 * @param center
	 * @return an array of new, rotated points
	 */
	public Vector[] rotate(Vector[] points, Vector center) {
		Vector[] result = new Vector[points.length];

		for (int i = 0; i < points.length; i++) {
			result[i] = rotate(points[i], center);
		}

		return result;
	}

	/**
	 * Rotates every point in points around the origin. The array points is used
	 * to store the result!
	 * 
	 * @param points
	 *            an array {x0, y0, x1, y1, ..., xn, yn}
	 */
	public void rotate(int[] points) {
		for (int i = 0; i < points.length; i += 2) {
			int px = points[i];
			int py = points[i + 1];

			double x = px * c00 + py * c01;
			double y = px * c10 + py * c11;

			points[i] = (int) Math.round(x);
			points[i + 1] = (int) Math.round(y);
		}
	}

	/**
	 * Rotates every point in points around the point center. The array points is used
	 * to store the result!
	 * 
	 * @param points
	 *            an array {x0, y0, x1, y1, ..., xn, yn}
	 * @param center
	 */
	public void rotate(int[] points, Vector center) {
		for (int i = 0; i < points.length; i += 2) {
			int px = points[i];
			int py = points[i + 1];

			double xt = px - center.x;
			double yt = py - center.y;
			double x = xt * c00 + yt * c01 + center.x;
			double y = xt * c10 + yt * c11 + center.y;

			points[i] = (int) Math.round(x);
			points[i + 1] = (int) Math.round(y);
		}
	}
}
