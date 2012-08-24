import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public abstract class FeatureLinker {

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

				if (link(f0, f1)) {
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
	}

	public abstract boolean link(Feature f0, Feature f1);
}
