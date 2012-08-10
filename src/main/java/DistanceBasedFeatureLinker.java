import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;

import java.util.*;

import com.googlecode.javacv.cpp.opencv_core.*;

public class DistanceBasedFeatureLinker implements FeatureLinker {
	private int numScans;
	private double maxVariance;
	private double maxDistance;

	public DistanceBasedFeatureLinker(int numScans, double maxVariance,
			double maxDistance) {
		this.numScans = numScans;
		this.maxVariance = maxVariance;
		this.maxDistance = maxDistance;
	}

	private boolean approxEqual(double value0, double value1, double tolerance, double offset) {
		return value0 < value1 * tolerance + offset && value1 < value0 * tolerance + offset;
	}

	@Override
	public List<LinkedFeature> linkFeatures(List<Feature> features, IplImage img) {
		List<LinkedFeature> linkedFeatures = new ArrayList<LinkedFeature>();

		// scan through every angle
		for (int i = 0; i < numScans; i++) {
			// double angle = Math.PI * i / numScans;
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

					// Vector2D dir =
					// f0.position().sub(f1.position()).normalize();

					/*
					 * double dAngle = Math.abs(Math.acos(dir.x) - angle); if
					 * (dAngle > maxVariance && Math.PI - dAngle > maxVariance)
					 * { continue; }
					 */
					
					LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
					if(lf.area()-f0.area()-f1.area() < 1000) {//0.5*(f0.area()+f1.area())) {
						//cvDrawLine(img, f0.cvPosition(), f1.cvPosition(), CvScalar.BLACK, 2,
						//		0, 0);

						adjacencyList.get(f0).add(f1);
						adjacencyList.get(f1).add(f0);
						continue;
					}

					double dPos = f0.distance(f1);
					if (dPos > maxDistance) {
						continue;
					}

					Angle180 angle = new Angle180(f0.position(), f1.position());

					double diff0 = angle.difference(f0.angle()).absRadians();
					double diff1 = angle.difference(f1.angle()).absRadians();

					double size0 = angle.difference(f0.angle()).absRadians() < Angle180
							.degToRad(45) ? f0.height() : f0.width();
					double size1 = angle.difference(f1.angle()).absRadians() < Angle180
							.degToRad(45) ? f1.height() : f1.width();

					CvScalar color = CvScalar.BLACK;

					// 45
					int a = 10;
					if (diff0 > Angle180.degToRad(a)
							&& diff0 < Angle180.degToRad(90 - a)
							|| diff1 > Angle180.degToRad(a)
							&& diff1 < Angle180.degToRad(90 - a)) {
						color = CvScalar.BLACK;
						continue;
					} else if (approxEqual(size0, size1, 1.3, 3)) {
						color = CvScalar.BLUE;
						// continue;
					} else {
						continue;
					}

					/*cvDrawLine(img, f0.cvPosition(), f1.cvPosition(), color, 2,
							0, 0);
					// cvShowImage("", img);
					// cvWaitKey();

					adjacencyList.get(f0).add(f1);
					adjacencyList.get(f1).add(f0);*/
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

					linkedFeatures.add(LinkedFeature.create(component));
				}
			}
		}

		return linkedFeatures;
	}
	/*
	 * private void depthSearch(Feature feature, Map<Feature, List<Feature>>
	 * adjacencyList, Set<Feature> marked, List<Feature> result) {
	 * marked.add(feature); result.add(feature);
	 * 
	 * for(Feature neighbour : adjacencyList.get(feature)) {
	 * if(!marked.contains(feature)) { depthSearch(neighbour, adjacencyList,
	 * marked, result); } } }
	 */

	/*
	 * public List<List<Feature>> linkFeaturesOld(List<Feature> features) {
	 * Collections.sort(features); LinkedList<Feature> active = new
	 * LinkedList<Feature>(); List<Link> links = new ArrayList<Link>();
	 * 
	 * for (int i = 0; i < features.size(); i++) { Feature f = features.get(i);
	 * Feature rem = null;
	 * 
	 * for (Feature o : active) { if(f.height() > o.height() *
	 * maxSizeToSizeRatio) { rem = o; break; } if (f.distance(o) <
	 * Math.max(f.height(), o.height()) * maxDistanceToSizeRatio) {
	 * links.add(new Link(f, o)); } }
	 * 
	 * if(rem != null) { while(rem != active.getLast()) { active.pollLast(); }
	 * active.pollLast(); }
	 * 
	 * active.offerFirst(f); }
	 * 
	 * List<List<Link>> candidates = new ArrayList<List<Link>>();
	 * 
	 * //Scan through all degrees double i = 20*Math.PI/180; //for(int i = -20;
	 * i < 1; i++) { double dx = Math.cos(i); double dy = Math.sin(i);
	 * 
	 * LinkedList<Link> tempLinks = new LinkedList<Link>(); for(Link l : links)
	 * { if(Math.abs(l.dir[0]*dx+l.dir[1]*dy) > 0.97) { tempLinks.add(l); } }
	 * 
	 * List<Link> taken = new ArrayList<Link>(); Set<Feature> nodes = new
	 * HashSet<Feature>();
	 * 
	 * while(!tempLinks.isEmpty()) { Link l = tempLinks.poll(); taken.clear();
	 * taken.add(l); nodes.clear(); nodes.add(l.f0); nodes.add(l.f1);
	 * 
	 * Iterator<Link> iter = tempLinks.iterator(); while(iter.hasNext()) { l =
	 * iter.next(); if(nodes.contains(l.f0) || nodes.contains(l.f1) ) {
	 * taken.add(l); nodes.add(l.f0); nodes.add(l.f1); iter.remove(); iter =
	 * tempLinks.iterator(); } } if(taken.size()>3){ candidates.add(new
	 * ArrayList<Link>(taken)); } }
	 * 
	 * //}
	 * 
	 * return null; }
	 */

}
