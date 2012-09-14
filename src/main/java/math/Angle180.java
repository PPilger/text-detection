package math;

/**
 * Represents an angle in the range of [0, 180) degrees.
 * 
 * The class can be used to define the angles of lines for example, where
 * following equation is valid: angle = 180 + angle
 * 
 * @author PilgerstorferP
 * 
 */
public class Angle180 {
	private double rad; // the angle value in radians

	/**
	 * Initializes the object with the specified angle in radians.
	 * 
	 * rad doesn't need to be in the range [0, PI), it can have any value.
	 * 
	 * @param rad
	 *            angle in radians
	 */
	public Angle180(double rad) {
		this.rad = rad;
		fix();
	}

	/**
	 * Initializes the object with the angle of the line that connects the
	 * points p and q.
	 * 
	 * @param p
	 *            first point of the line
	 * @param q
	 *            second point of the line
	 */
	public Angle180(Vector p, Vector q) {
		if (p.x == q.x) {
			this.rad = Math.PI / 2;
		} else {
			this.rad = Math.atan((q.y - p.y) / (q.x - p.x));
		}
		fix();
	}

	/**
	 * Adjusts the radiant-value so that it is in the range [0, PI)
	 */
	private void fix() {
		while (rad >= Math.PI) {
			rad -= Math.PI;
		}
		while (rad < 0) {
			rad += Math.PI;
		}
	}

	/**
	 * @return the angle in degrees
	 */
	public double getDegrees() {
		return Math.toDegrees(rad);
	}

	/**
	 * @return the angle in radians
	 */
	public double getRadians() {
		return rad;
	}

	/**
	 * Calculates the difference between this angle and the angle other As there
	 * are always two possible solutions, this function returns the smaller
	 * difference
	 * 
	 * Examples: 10° and 20° results in 10° 10° and 150° results in 40°
	 * 
	 * @param other
	 * @return the difference between this angle and the angle other
	 */
	public Angle180 difference(Angle180 other) {
		return new Angle180(difference(rad, other.rad));
	}

	/**
	 * Calculates the difference between the angles rad0 and rad1 As there are
	 * always two possible solutions, this function returns the smaller
	 * difference
	 * 
	 * Examples: 10° and 20° results in 10° 10° and 150° results in 40°
	 * 
	 * @param rad0
	 *            angle in radians in the range [0, PI)
	 * @param rad1
	 *            angle in radians in the range [0, PI)
	 * @return the difference between the angles rad0 and rad1
	 */
	private static double difference(double rad0, double rad1) {
		double diff = Math.abs(rad0 - rad1);

		return diff <= Math.PI / 2 ? diff : Math.PI - diff;
	}

	/**
	 * Rotates the angle by rad randians (adds rad to the angle)
	 * 
	 * @param rad
	 *            angle in radians
	 */
	public void rotate(double rad) {
		this.rad += rad;
		fix();
	}

	public String toString() {
		return getDegrees() + "°";
	}
}
