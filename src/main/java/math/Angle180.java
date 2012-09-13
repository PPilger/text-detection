package math;

/**
 * Represents an angle in the range of 0 (incl.) to 180 (excl.) degrees.
 * 
 * The class can be used to define the angles of lines, where -10° = 170°
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
	 * @param p first point of the line
	 * @param q second point of the line
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
	 * 
	 */
	private void fix() {
		while (rad >= Math.PI) {
			rad -= Math.PI;
		}
		while (rad < 0) {
			rad += Math.PI;
		}
	}

	public double getDegrees() {
		return Math.toDegrees(rad);
	}

	public double getRadians() {
		return rad;
	}

	public Angle180 difference(Angle180 other) {
		return new Angle180(difference(rad, other.rad));
	}

	public Angle180 difference(double rad) {
		return new Angle180(difference(this.rad, rad));
	}

	public static double difference(double rad0, double rad1) {
		double diff = Math.abs(rad0 - rad1);

		return diff <= Math.PI / 2 ? diff : Math.PI - diff;
	}

	public Angle180 avg(Angle180 other) {
		double a0 = rad;
		double a1 = other.rad;

		if (Math.abs(a0 - a1) > Math.PI / 2) {
			if (a0 < a1) {
				a1 = a1 - Math.PI;
			} else {
				a0 = a0 - Math.PI;
			}
		}
		return new Angle180((a0 + a1) / 2);
	}

	public double absRadians() {
		return rad > Math.PI / 2 ? Math.PI - rad : rad;
	}

	public void rotate(double rad) {
		this.rad += rad;
		fix();
	}

	public String toString() {
		return getDegrees() + "°";
	}
}
