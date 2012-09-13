package feature;

import static application.TextDetection.count;
import static application.TextDetection.start;
import static application.TextDetection.stop;

import java.util.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FeatureLinker {
	private List<LinkingRule> linkingRules;

	public FeatureLinker() {
		this.linkingRules = new ArrayList<LinkingRule>();
	}

	public void addRule(LinkingRule linkingRule) {
		this.linkingRules.add(linkingRule);
	}

	public FeatureSet link(FeatureSet features, IplImage img) {
		Map<Feature, List<Feature>> adjacencyList = new HashMap<Feature, List<Feature>>();

		// initialize the linking rules
		for (LinkingRule linkingRule : linkingRules) {
			linkingRule.initialize(features, img);
		}

		// initialize the adjacency list
		for (Feature f : features) {
			adjacencyList.put(f, new ArrayList<Feature>());
		}

		// fill the adjacency list
		{
			Set<Feature> marked = new HashSet<Feature>();
			for (Feature f0 : features) {
				marked.add(f0);
				for (Feature f1 : features.getNeighbours(f0)) {
					if (!marked.contains(f1)) {
						if (link(f0, f1)) {
							adjacencyList.get(f0).add(f1);
							adjacencyList.get(f1).add(f0);
						}
					}
				}
			}
		}

		FeatureSet result = link(features, adjacencyList);

		return result;
	}

	public FeatureSet link(FeatureSet features,
			Map<Feature, List<Feature>> adjacencyList) {

		// find all connected components of the graph

		FeatureSet result = new FeatureSet(features);
		{
			Set<Feature> marked = new HashSet<Feature>();
			Stack<Feature> stack = new Stack<Feature>();

			for (Feature start : features) {
				if (!marked.contains(start)) {
					List<Feature> component = new ArrayList<Feature>();

					// breadth first search stack.push(start);
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

	private boolean link(Feature f0, Feature f1) {
		for (LinkingRule l : linkingRules) {
			if (!l.link(f0, f1)) {
				count(l.getClass().toString() + " invalid");
				return false;
			}
			count(l.getClass().toString() + " valid");
		}

		return true;
	}
}
