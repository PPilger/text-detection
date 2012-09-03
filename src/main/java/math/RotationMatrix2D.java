package math;

public class RotationMatrix2D {
	private double c00;
	private double c01;
	private double c10;
	private double c11;
	
	public RotationMatrix2D(double angle) {
		c00 = c11 = Math.cos(angle);
		c10 = Math.sin(angle);
		c01 = -c10;
	}
	
	public Vector2D rotate(Vector2D point) {
		double x = point.x * c00 + point.y * c01;
		double y = point.x * c10 + point.y * c11;
		return new Vector2D(x, y);
	}
}
