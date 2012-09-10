package feature;

import math.Angle180;
import math.Vector;

public class Link {
	public Feature f0;
	public Feature f1;
	public Vector dir;
	private Angle180 angle;

	public Link(Feature f0, Feature f1) {
		this.f0 = f0;
		this.f1 = f1;
		this.angle = new Angle180(f0.getCenter(), f1.getCenter());
	}
	
	public Angle180 angle() {
		return angle;
	}
}
