import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

public class AreaBasedFeatureLinker extends FeatureLinker {
	private double maxAreaGrowth;

	public AreaBasedFeatureLinker(double maxAreaGrowth) {
		this.maxAreaGrowth = maxAreaGrowth;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
		double areaGrowth = lf.area()-f0.area()-f1.area();
		return areaGrowth < maxAreaGrowth;
	}

	/*@Override
	public FeatureSet linkFeatures(List<Feature> features, IplImage img) {
		FeatureSet featureSet = new FeatureSet();
			Map<Feature, List<Feature>> adjacencyList = new HashMap<Feature, List<Feature>>();

		// initialize the adjacency list
		for (Feature f : features) {
			adjacencyList.put(f, new ArrayList<Feature>());
		}

		// fill the adjacency list
		for (int j = 0; j < features.size(); j++) {
			for (int k = j + 1; k < features.size(); k++) {
				Feature f0 = features.get(j);
				Feature f1 = features.get(k);
				
				LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
				if(lf.area()-f0.area()-f1.area() < maxAreaGrowth) {
					//cvDrawLine(img, f0.cvPosition(), f1.cvPosition(), CvScalar.BLACK, 2,
					//		0, 0);

					adjacencyList.get(f0).add(f1);
					adjacencyList.get(f1).add(f0);
				}
			}
		}

		// find all connected components of the graph
		Set<Feature> marked = new HashSet<Feature>();
		for (Feature start : features) {
			if (!marked.contains(start)) {
				List<Feature> component = new ArrayList<Feature>();

				// breadth first search
				Stack<Feature> stack = new Stack<Feature>();
				stack.push(start);
				marked.add(start);

				while (!stack.isEmpty()) {
					Feature feature = stack.pop();
					component.add(feature);

					for (Feature neighbour : adjacencyList.get(feature)) {
						if (!marked.contains(neighbour)) {
							marked.add(neighbour);
							stack.push(neighbour);
						}
					}
				}

				featureSet.add(LinkedFeature.create(component));
			}
		}

		return featureSet;
	}*/
}
