package feature;

import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.Locale;

import math.Box;
import math.Vector;

public abstract class Feature extends Box {
	
	public Feature(CvBox2D box) {
		super(box);
	}

	public Feature(double xcenter, double ycenter, double width, double height, double angle) {
		super(xcenter, ycenter, width, height, angle);
	}

	public int[] getICorners() {
		return Vector.asIntArray(getCorners());
	}

	public void draw(CvArr img, CvScalar color) {
		cvDrawCircle(img, getCvCenter(), 1, color, 2, 0, 0);
		super.draw(img, color);
	}

	public abstract void fill(CvArr img, CvScalar color);

	public String toJSON() {
		Vector[] corners = getCorners();
		String corner0 = corners[0].toJSON();
		String corner1 = corners[1].toJSON();
		String corner2 = corners[2].toJSON();
		String corner3 = corners[3].toJSON();

		return String.format(Locale.US,
				"{\"angle\": %.2f, \"corners\": [%s, %s, %s, %s]}",
				getAngle().getDegrees(), corner0, corner1, corner2, corner3);
	}
}
