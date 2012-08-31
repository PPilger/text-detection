package feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static application.TextDetection.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FeatureLinker {
	private List<LinkingRule> linkingRules;

	public FeatureLinker() {
		this.linkingRules = new ArrayList<LinkingRule>();
	}

	public void addRule(LinkingRule... linkingRules) {
		for (LinkingRule l : linkingRules) {
			this.linkingRules.add(l);
		}
	}

	public FeatureSet link(FeatureSet features, IplImage img) {
		FeatureSet featureSet = new FeatureSet();
		Map<Feature, List<Feature>> adjacencyList = new HashMap<Feature, List<Feature>>();

		// initialize the linking rules
		for(LinkingRule linkingRule : linkingRules) {
			linkingRule.initialize(features, img);
		}
		
		// initialize the adjacency list
		for (Feature f : features) {
			adjacencyList.put(f, new ArrayList<Feature>());
		}

		// fill the adjacency list
		start();
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
		stop("adjacency list");

		// find all connected components of the graph
		Set<Feature> marked = new HashSet<Feature>();
		Stack<Feature> stack = new Stack<Feature>();
		
		start();
		for (Feature start : features) {
			if (!marked.contains(start)) {
				List<Feature> component = new ArrayList<Feature>();

				// breadth first search
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
		stop("connecting");

		return featureSet;
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
