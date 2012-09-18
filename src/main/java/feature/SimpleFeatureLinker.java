package feature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * A SimpleFeatureLinker simply establishes all links allowed by the
 * LinkingRules.
 * 
 * @author PilgerstorferP
 * 
 */
public class SimpleFeatureLinker extends FeatureLinker {

	@Override
	public FeatureSet link(FeatureSet features,
			Map<Feature, List<Feature>> adjacencyList) {
		// find all connected components of the graph described by the adjacency
		// list
		FeatureSet result = new FeatureSet(features);
		{
			Set<Feature> marked = new HashSet<Feature>();
			Stack<Feature> stack = new Stack<Feature>();

			for (Feature start : features) {
				if (!marked.contains(start)) {
					List<Feature> component = new ArrayList<Feature>();

					// depth first search
					marked.add(start);
					stack.push(start);

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

					result.add(LinkedFeature.create(component));
				}
			}
		}

		return result;
	}
}
