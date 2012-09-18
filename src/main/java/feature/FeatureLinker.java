package feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * A FeatureLinker is able to link features to a LinkedFeature. LinkingRules
 * define which features get linked and which don't.
 * 
 * @author PilgerstorferP
 * 
 */
public abstract class FeatureLinker {
	private List<LinkingRule> linkingRules;

	public FeatureLinker() {
		this.linkingRules = new ArrayList<LinkingRule>();
	}

	public void addRule(LinkingRule linkingRule) {
		this.linkingRules.add(linkingRule);
	}

	/**
	 * Starts the linking algorithm.
	 * 
	 * @param features
	 *            the input features
	 * @param img
	 *            may be used by a linking rule for initialization
	 * @return a new FeatureSet containing the linked features
	 */
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

		return link(features, adjacencyList);
	}

	public abstract FeatureSet link(FeatureSet features,
			Map<Feature, List<Feature>> adjacencyList);

	private boolean link(Feature f0, Feature f1) {
		for (LinkingRule l : linkingRules) {
			if (!l.link(f0, f1)) {
				return false;
			}
		}

		return true;
	}
}
