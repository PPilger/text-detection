package feature;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.Formatter;
import java.util.Locale;

import math.Angle180;
import math.Box2D;
import math.Vector2D;

public abstract class Feature {
	private Vector2D position;
	private double width;
	private double height;
	private Angle180 angle;
	private boolean text;
	private Box2D box;

	// private CvBox2D cvBox;

	public Feature(CvBox2D box) {
		this.box = new Box2D(box);
		init(box.center().x(), box.center().y(), box.size().width(), box.size()
				.height(), Angle180.degToRad(box.angle()));
	}

	public Feature(double x, double y, double width, double height) {
		this.box = new Box2D(x, y, width, height, 0);
		init(x, y, width, height, 0);
	}

	public Feature(double x, double y, double width, double height, double angle) {
		this.box = new Box2D(x, y, width, height, angle);
		init(x, y, width, height, angle);
	}

	private void init(double x, double y, double width, double height,
			double angle) {
		this.position = new Vector2D(x, y);
		this.width = width;
		this.height = height;
		this.angle = new Angle180(angle);

		if (width < height) {
			this.width = height;
			this.height = width;
			this.angle.rotate(Math.PI / 2);
		}

		if (Math.min(width / (double) height, height / (double) width) < 0.5) {
			text = true;
		} else {
			text = false;
		}
	}

	public Vector2D position() {
		return position;
	}

	public Box2D box() {
		return box;
	}

	public double height() {
		return height;
	}

	public double width() {
		return width;
	}

	public double size() {
		return Math.max(width, height);
	}

	public double area() {
		return width * height;
	}

	public boolean isText() {
		return text;
	}

	public Angle180 angle() {
		return angle;
	}

	public CvPoint cvPosition() {
		return cvPoint((int) Math.round(position.x),
				(int) Math.round(position.y));
	}

	public double distance(Feature other) {
		return box.distance(other.box);
	}
	
	public double overlappingArea(Feature other) {
		return 0;
	}

	public int[] getCorners() {
		int[] corners = new int[box.corners.length * 2];
		
		int i = 0;
		for (Vector2D corner : box.corners) {
			corners[i] = (int) Math.round(corner.x);
			corners[i + 1] = (int) Math.round(corner.y);
			i+=2;
		}
		
		return corners;
	}

	public void draw(CvArr img, CvScalar color) {
		CvPoint pos = cvPosition();
		cvDrawCircle(img, pos, 1, color, 2, 0, 0);
		box.draw(img, color);
	}

	public abstract void fill(CvArr img, CvScalar color);

	public String toJSON() {
		String corner0 = box.corners[0].toJSON();
		String corner1 = box.corners[1].toJSON();
		String corner2 = box.corners[2].toJSON();
		String corner3 = box.corners[3].toJSON();

		return String.format(Locale.US,
				"{\"angle\": %.2f, \"corners\": [%s, %s, %s, %s]}",
				angle.getDegrees(), corner0, corner1, corner2, corner3);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(height);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		temp = Double.doubleToLongBits(width);
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
		Feature other = (Feature) obj;
		if (Double.doubleToLongBits(height) != Double
				.doubleToLongBits(other.height))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (Double.doubleToLongBits(width) != Double
				.doubleToLongBits(other.width))
			return false;
		return true;
	}

	public String toString() {
		return position + ", " + width + "/" + height + ", "
				+ angle.getDegrees() + "°";
	}
}
