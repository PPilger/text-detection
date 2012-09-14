package feature;

import java.util.*;

import application.TextDetection;

import validator.DInterval;
import validator.DValidator;

import math.Rotation;
import math.Vector;

public class BestDirectionFeatureLinker extends FeatureLinker {
	private double[] angles;
	private DValidator centerVariance;

	public BestDirectionFeatureLinker(DValidator centerVariance,
			int numDirections) {
		this.centerVariance = centerVariance;
		this.angles = new double[numDirections];

		double step = Math.PI / numDirections;

		for (int i = 0; i < numDirections; i++) {
			angles[i] = i * step;
		}
	}

	public BestDirectionFeatureLinker(DValidator centerVariance,
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

			// List<Set<Feature>> linkedFeatures = new
			// ArrayList<Set<Feature>>();
			List<LinkedFeature> linkedFeatures = new ArrayList<LinkedFeature>();
			for (Feature start : features) {
				double bestVal = -1;
				Set<Feature> best = null;

				for (double angle : angles) {

					Map<Feature, Vector> positions = anglePositions.get(angle);

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
					best = new HashSet<Feature>();
					best.add(start);
				}
				linkedFeatures.add(LinkedFeature.create(best));
			}

			Collections.sort(linkedFeatures);

			Map<LinkedFeature, Integer> smaller = new HashMap<LinkedFeature, Integer>();

			int x = 0, y = 0, z = 0;
			for (int i = 0; i < linkedFeatures.size() - 1; i++) {
				LinkedFeature a = linkedFeatures.get(i);

				for (int j = i + 1; j < linkedFeatures.size(); j++) {
					LinkedFeature b = linkedFeatures.get(j);

					boolean removed = false;
					Iterator<Feature> iter = b.iterator();
					// for (Feature f : b) {
					while (iter.hasNext()) {
						Feature f = iter.next();
						if (a.contains(f)) {
							iter.remove();
							removed = true;
						}
					}

					if (removed) {
						if (!b.getSubFeatures().isEmpty()) {
							linkedFeatures.set(j,
									LinkedFeature.create(b.getSubFeatures()));
						}
					}
				}
			}

			for (LinkedFeature linkedFeature : linkedFeatures) {
				if (linkedFeature != null) {
					result.add(linkedFeature);
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
