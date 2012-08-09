public class Link {
	public Feature f0;
	public Feature f1;
	public Vector2D dir;
	private double angle;

	public Link(Feature f0, Feature f1) {
		this.f0 = f0;
		this.f1 = f1;
		Vector2D diff = f0.position().sub(f1.position());
		dir = diff.normalize();
		angle = Math.acos(dir.x);
	}
	
	public double angle() {
		return angle;
	}
}
