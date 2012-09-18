package feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import math.Rotation;
import math.Vector;
import validator.DInterval;
import validator.DMaximum;

/**
 * Automatically links features in the best direction. The best direction is the
 * direction in which the most features can be linked together.
 * 
 * For each feature the best linking direction is determined and a LinkedFeature
 * is created. If several LinkedFeatures overlap, the one with the best rating
 * is kept and the others are adjusted so that they don't overlap.
 * 
 * @author PilgerstorferP
 * 
 */
public class BestDirectionFeatureLinker extends FeatureLinker {
	private double[] angles;
	private DMaximum centerVariance;

	/**
	 * Creates a new BestDirectionFeatureLinker that scans through
	 * <code>numDirections</code> angles in the interval [0,180) (degrees).
	 * 
	 * @param centerVariance
	 *            defines how much the centers can vary along a certain
	 *            direction
	 * @param numDirections
	 */
	public BestDirectionFeatureLinker(DMaximum centerVariance, int numDirections) {
		this.centerVariance = centerVariance;
		this.angles = new double[numDirections];

		double step = Math.PI / numDirections;

		for (int i = 0; i < numDirections; i++) {
			angles[i] = i * step;
		}
	}

	/**
	 * Creates a new BestDirectionFeatureLinker that scans through
	 * <code>numDirections</code> angles in the specified interval.
	 * 
	 * @param centerVariance
	 *            defines how much the centers can vary along a certain
	 *            direction
	 * @param numDirections
	 * @param range
	 */
	public BestDirectionFeatureLinker(DMaximum centerVariance,
			int numDirections, DInterval range) {
		this.centerVariance = centerVariance;
		this.angles = new double[numDirections];

		if (numDirections == 1) {
			this.angles[0] = 0;
		} else {
			double min = range.getMin();
			double max = range.getMax();
			double step = (max - min) / (numDirections - 1);

			for (int i = 0; i < numDirections; i++) {
				angles[i] = i * step + min;
			}
		}
	}

	@Override
	public FeatureSet link(FeatureSet features,
			Map<Feature, List<Feature>> adjacencyList) {
		FeatureSet result = new FeatureSet(features);

		// calculate the rotated centers of all features
		// key1: angle, key2: feature, value: rotated center
		Map<Double, Map<Feature, Vector>> rotatedCenters = new HashMap<Double, Map<Feature, Vector>>();
		for (double angle : angles) {
			Rotation rotation = new Rotation(angle);

			Map<Feature, Vector> positions = new HashMap<Feature, Vector>();

			for (Feature feature : features) {
				Vector pos = rotation.rotate(feature.getCenter());
				positions.put(feature, pos);
			}

			rotatedCenters.put(angle, positions);
		}

		// create a LinkedFeature for each feature
		List<LinkedFeature> linkedFeatures = new ArrayList<LinkedFeature>();
		for (Feature start : features) {
			double bestVal = -1;
			Set<Feature> best = null;

			// try to link in all possible directions and store the best result
			for (double angle : angles) {
				// link features in this direction beginning with start
				Set<Feature> component = connectedComponent(adjacencyList,
						rotatedCenters.get(angle), start);

				double val = component.size();
				if (bestVal < val) {
					bestVal = val;
					best = component;
				}
			}

			if (best == null) {
				best = new HashSet<Feature>();
				best.add(start);
			}
			linkedFeatures.add(LinkedFeature.create(best));
		}

		// sort all LinkedFeatures by their rating
		Collections.sort(linkedFeatures);

		// if two LinktedFeatures overlap, adjust the one with the lower ranking
		for (int i = 0; i < linkedFeatures.size() - 1; i++) {
			if (linkedFeatures.get(i) == null) {
				continue;
			}
			Set<Feature> a = linkedFeatures.get(i).getSubFeatures();

			for (int j = i + 1; j < linkedFeatures.size(); j++) {
				if (linkedFeatures.get(j) == null) {
					continue;
				}
				Set<Feature> b = linkedFeatures.get(j).getSubFeatures();

				boolean removed = false;

				Iterator<Feature> iter = b.iterator();
				while (iter.hasNext()) {
					Feature f = iter.next();
					if (a.contains(f)) {
						// remove the overlapping feature in the lower ranked
						// LinkedFeature
						iter.remove();
						removed = true;
					}
				}

				// if one or more features were removed, the LinkedFeature has
				// to be recreated to maintain consistency
				if (removed) {
					if (b.isEmpty()) {
						linkedFeatures.set(j, null);
					} else {
						linkedFeatures.set(j, LinkedFeature.create(b));
					}
				}
			}
		}

		// get result FeatureSet by adding all non empty features
		for (LinkedFeature linkedFeature : linkedFeatures) {
			if (linkedFeature != null) {
				result.add(linkedFeature);
			}
		}

		return result;
	}

	/**
	 * Determines the connected component of the specified start feature,
	 * considering the defined centerVariance parameter.
	 * 
	 * @param adjacencyList
	 *            defines the graph
	 * @param centers
	 *            centers of each feature (may differ from Feature.getCenter())
	 * @param start
	 * @return the connected component including the start feature
	 */
	private Set<Feature> connectedComponent(
			Map<Feature, List<Feature>> adjacencyList,
			Map<Feature, Vector> centers, Feature start) {
		Set<Feature> component = new HashSet<Feature>();

		// minimum and maximum y-value of the connected centers
		double upperY;
		double lowerY;
		{
			Vector pos = centers.get(start);
			upperY = pos.y;
			lowerY = pos.y;
		}

		// todo is stored as a list so it can be sorted
		List<Feature> todo = new ArrayList<Feature>();
		Set<Feature> marked = new HashSet<Feature>();

		todo.add(start);
		marked.add(start);

		// search connected features in the graph
		while (!todo.isEmpty()) {
			Feature feature = todo.remove(todo.size() - 1);

			// update the center bounds
			{
				Vector pos = centers.get(feature);
				if (upperY < pos.y) {
					upperY = pos.y;
				} else if (pos.y < lowerY) {
					lowerY = pos.y;
				}
			}

			if (!centerVariance.isValid(upperY - lowerY)) {
				// as todo is sorted by the distance to the center of the
				// connected
				// component, the loop can be ended after the first invalid
				// component
				break;
			}

			component.add(feature);

			// add all possible neighbors to the todo list
			for (Feature neighbor : adjacencyList.get(feature)) {
				if (!marked.contains(neighbor)) {
					marked.add(neighbor);

					Vector pos = centers.get(feature);
					double u = upperY;
					double l = lowerY;
					if (upperY < pos.y) {
						u = pos.y;
					} else if (pos.y < lowerY) {
						l = pos.y;
					}

					if (centerVariance.isValid(u - l)) {
						todo.add(neighbor);
					}
				}
			}

			// sort the todo list by the connected component center to feature
			// center difference
			Comparator<Feature> comp = new CenterVarianceComparator(centers,
					(upperY + lowerY) / 2);
			Collections.sort(todo, comp);
		}

		return component;
	}

	/**
	 * Compares the distances of the specified center and the feature-centers.
	 * 
	 * Sorts in descending order.
	 * 
	 * @author PilgerstorferP
	 * 
	 */
	private class CenterVarianceComparator implements Comparator<Feature> {
		private double center = 0;
		private Map<Feature, Vector> featureCenters;

		public CenterVarianceComparator(Map<Feature, Vector> featureCenters,
				double center) {
			this.featureCenters = featureCenters;
			this.center = center;
		}

		@Override
		public int compare(Feature o1, Feature o2) {
			double center1 = featureCenters.get(o1).y;
			double center2 = featureCenters.get(o2).y;

			double variance1 = Math.abs(center1 - center);
			double variance2 = Math.abs(center2 - center);

			if (variance1 < variance2) {
				return 1;
			} else if (variance1 > variance2) {
				return -1;
			} else {
				return 0;
			}
		}

	}
}
