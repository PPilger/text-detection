public class DistanceBasedLinkingRule extends LinkingRule {
	private double maxDistance;

	public DistanceBasedLinkingRule(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		double distance = f0.distance(f1);
		return distance < maxDistance;
	}
}
