package math;

import java.util.Arrays;

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

	public Vector2D rotate(Vector2D point, Vector2D center) {
		double xt = point.x - center.x;
		double yt = point.y - center.y;
		
		double x = xt * c00 + yt * c01 + center.x;
		double y = xt * c10 + yt * c11 + center.y;

		return new Vector2D(x, y);
	}

	public Vector2D[] rotate(Vector2D[] points, Vector2D center) {
		Vector2D[] result = new Vector2D[points.length];
		
		for(int i = 0; i < points.length; i++) {
			result[i] = rotate(points[i], center);
		}
		
		return result;
	}

	public void rotate(int[] points, Vector2D center) {
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
