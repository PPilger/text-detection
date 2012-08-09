/**
 * Represents an angle in the range of 0 to 180 degrees
 * An angle x is treated the same as x + 180°
 * @author PilgerstorferP
 *
 */
public class Angle180 {
	private double rad;

	public static Angle180 fromDegrees(double deg) {
		return new Angle180(deg / 180 * Math.PI);
	}

	public static double radToDeg(double rad) {
		return rad * 180 / Math.PI;
	}

	public static double degToRad(double deg) {
		return deg * Math.PI / 180;
	}
	
	public Angle180(double rad) {
		this.rad = rad;
		fix();
	}
	
	public Angle180(Vector2D p, Vector2D q) {
		if(p.x == q.x) {
			this.rad = Math.PI / 2;
		} else {
			this.rad = Math.atan((q.y - p.y) / (q.x - p.x));
		}
		fix();
	}
	
	private void fix() {
		while (rad >= Math.PI) {
			rad -= Math.PI;
		}
		while (rad < 0) {
			rad += Math.PI;
		}
	}
	
	public double getDegrees() {
		return radToDeg(rad);
	}
	
	public double getRadians() {
		return rad;
	}
	
	public Angle180 difference(Angle180 other) {
		return new Angle180(rad - other.rad);
	}
	
	public Angle180 difference(double rad) {
		return new Angle180(this.rad - rad);
	}
	
	public void rotate(double rad) {
		this.rad += rad;
		fix();
	}
}
