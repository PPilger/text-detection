package feature;

import math.Angle180;
import math.Vector2D;

public class Link {
	public Feature f0;
	public Feature f1;
	public Vector2D dir;
	private Angle180 angle;

	public Link(Feature f0, Feature f1) {
		this.f0 = f0;
		this.f1 = f1;
		this.angle = new Angle180(f0.position(), f1.position());
	}
	
	public Angle180 angle() {
		return angle;
	}
}
