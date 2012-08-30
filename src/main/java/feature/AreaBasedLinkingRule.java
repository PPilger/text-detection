package feature;
import java.util.*;

public class AreaBasedLinkingRule extends LinkingRule {
	private double maxAreaGrowth;

	public AreaBasedLinkingRule(double maxAreaGrowth) {
		this.maxAreaGrowth = maxAreaGrowth;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
		double areaGrowth = lf.area()-f0.area()-f1.area();
		return areaGrowth < maxAreaGrowth;
	}
}
