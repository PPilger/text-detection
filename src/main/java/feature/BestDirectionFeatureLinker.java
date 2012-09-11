package feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import validator.DoubleValidator;

import math.Rotation;
import math.Vector;

public class BestDirectionFeatureLinker extends FeatureLinker {
	private double[] angles;
	private DoubleValidator centerVariance;
	
	public BestDirectionFeatureLinker(int numDirections, DoubleValidator centerVariance) {
		this.angles = new double[numDirections];
		double step = Math.PI / numDirections;
		for(int i = 0; i < numDirections; i++) {
			angles[i] = i * step;
		}
		this.centerVariance = centerVariance;
	}
	
	public BestDirectionFeatureLinker(DoubleValidator centerVariance, double[] angles) {
		this.angles = angles;
		this.centerVariance = centerVariance;
	}

	@Override
	public FeatureSet link(FeatureSet features,
			Map<Feature, List<Feature>> adjacencyList) {
		FeatureSet result = new FeatureSet(features);
		{

			Map<Double, Map<Feature, Vector>> anglePositions = new HashMap<Double, Map<Feature, Vector>>();

			for (double angle : angles) {
				Rotation rotation = new Rotation(angle);

				Map<Feature, Vector> positions = new HashMap<Feature, Vector>();

				for (Feature feature : features) {
					Vector pos = rotation.rotate(feature.getCenter());
					positions.put(feature, pos);
				}

				anglePositions.put(angle, positions);
			}

			List<Set<Feature>> linkedFeatures = new ArrayList<Set<Feature>>();
			for (Feature start : features) {
				double bestVal = -1;
				Set<Feature> best = null;

				for (double angle : angles) {

					Map<Feature, Vector> positions = anglePositions
							.get(angle);

					Set<Feature> component = new HashSet<Feature>();
					List<Feature> todo = new ArrayList<Feature>();

					double upperY;
					double lowerY;
					{
						Vector pos = positions.get(start);
						upperY = pos.y;
						lowerY = pos.y;
					}

					// breadth first search
					Set<Feature> marked = new HashSet<Feature>();
					todo.add(start);
					marked.add(start);

					while (!todo.isEmpty()) {
						Feature feature = todo.remove(todo.size() - 1);

						{
							Vector pos = positions.get(feature);
							if (upperY < pos.y) {
								upperY = pos.y;
							} else if (pos.y < lowerY) {
								lowerY = pos.y;
							}
						}

						if (!centerVariance.isValid(upperY - lowerY)) {
							break;
						}
						component.add(feature);

						for (Feature neighbour : adjacencyList.get(feature)) {
							if (!marked.contains(neighbour)) {
								marked.add(neighbour);

								Vector pos = positions.get(feature);
								double u = upperY;
								double l = lowerY;
								if (upperY < pos.y) {
									u = pos.y;
								} else if (pos.y < lowerY) {
									l = pos.y;
								}

								if (centerVariance.isValid(u - l)) {
									todo.add(neighbour);
								}
							}
						}

						Comparator<Feature> comp = new RotatedPositionComparator(
								positions, (upperY + lowerY) / 2);
						Collections.sort(todo, comp);
					}

					double val = component.size();
					if (bestVal < val) {
						bestVal = val;
						best = component;
					}
				}

				if (best == null) {
					Set<Feature> temp = new HashSet<Feature>();
					temp.add(start);
					linkedFeatures.add(temp);
				} else {
					linkedFeatures.add(best);
				}
			}

			Map<Set<Feature>, Double> ranking = new HashMap<Set<Feature>, Double>();
			for (Set<Feature> lf : linkedFeatures) {
				LinkedFeature temp = LinkedFeature.create(lf);
				double area = 0;
				for (Feature f : lf) {
					area += f.getArea();
				}
				double rank = lf.size() * area / temp.getArea();

				ranking.put(lf, rank);
			}

			Collections.sort(linkedFeatures, new RankedSetComparator(ranking));

			for (int i = 0; i < linkedFeatures.size() - 1; i++) {
				Set<Feature> a = linkedFeatures.get(i);
				if (a == null) {
					continue;
				}

				for (int j = i + 1; j < linkedFeatures.size(); j++) {
					Set<Feature> b = linkedFeatures.get(j);
					if (b == null) {
						continue;
					}

					for (Feature f : b) {
						if (a.contains(f)) {
							linkedFeatures.set(j, null);
						}
					}
				}
			}

			for (Set<Feature> linkedFeature : linkedFeatures) {
				if (linkedFeature != null) {
					result.add(LinkedFeature.create(linkedFeature));
				}
			}
		}
		return result;
	}

	private class RotatedPositionComparator implements Comparator<Feature> {
		private double yOffset = 0;
		private Map<Feature, Vector> positions;

		public RotatedPositionComparator(Map<Feature, Vector> positions,
				double yOffset) {
			this.positions = positions;
			this.yOffset = yOffset;
		}

		@Override
		public int compare(Feature o1, Feature o2) {
			double v1 = positions.get(o1).y;
			double v2 = positions.get(o2).y;

			v1 = Math.abs(v1 - yOffset);
			v2 = Math.abs(v2 - yOffset);

			if (v1 < v2) {
				return 1;
			} else if (v1 > v2) {
				return -1;
			} else {
				return 0;
			}
		}

	}
	
	private class RankedSetComparator implements Comparator<Set<Feature>> {
		private Map<Set<Feature>, Double> ranking;
		
		public RankedSetComparator(Map<Set<Feature>, Double> ranking) {
			this.ranking = ranking;
		}

		@Override
		public int compare(Set<Feature> o1, Set<Feature> o2) {
			double v1 = ranking.get(o1);
			double v2 = ranking.get(o2);

			if (v1 < v2) {
				return 1;
			} else if (v1 > v2) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
