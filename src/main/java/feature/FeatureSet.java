package feature;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import validator.DValidator;

import math.Vector;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Represents a collection of Feature objects. The features are stored in a
 * single Collection to be able to iterate through all features.
 * 
 * Additionally the features are stored in a grid. Each cell in the grid has the
 * size <code>distance * distance</code> where <code>distance</code> is the
 * maximum distance of two features. Each feature is stored in all of the cells,
 * where a neighbor (a feature not more than <code>distance</code> away) can be.
 * 
 * Each feature is stored quite often that way, but neighbors can be accessed
 * very fast.
 * 
 * @author PilgerstorferP
 * 
 */
public class FeatureSet implements Iterable<Feature> {
	private double distance;
	private int rows; // number of rows in the grid
	private int cols; // number of columns in the grid
	private Set<Feature> features;
	private Collection<Feature>[][] grid;

	/**
	 * @param distance
	 *            maximum distance between features
	 * @param width
	 *            of the area where features are located in
	 * @param height
	 *            of the area where features are located in
	 */
	@SuppressWarnings("unchecked")
	public FeatureSet(double distance, int width, int height) {
		this.distance = distance;
		this.rows = (int) Math.ceil(height / (double) distance);
		this.cols = (int) Math.ceil(width / (double) distance);

		this.features = new HashSet<Feature>();
		this.grid = (Collection<Feature>[][]) new Collection[this.rows][this.cols];

		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				grid[i][j] = new ArrayList<Feature>();
			}
		}
	}

	/**
	 * Creates a new, empty FeatureSet with distance and grid-size copied from
	 * <code>other</code>
	 * 
	 * @param other
	 */
	@SuppressWarnings("unchecked")
	public FeatureSet(FeatureSet other) {
		this.distance = other.distance;
		this.rows = other.rows;
		this.cols = other.cols;

		this.features = new HashSet<Feature>();
		this.grid = (Collection<Feature>[][]) new Collection[this.rows][this.cols];

		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				grid[i][j] = new ArrayList<Feature>();
			}
		}
	}

	public int size() {
		return features.size();
	}

	/**
	 * Returns a collection of all neighbors. A neighbor is a feature with the
	 * distance (to <code>feature</code>) lower than the maximum distance. There
	 * may also be returned some false neighbors due to the grid approximation
	 * of the distance.
	 * 
	 * @param feature
	 * @return collection of all neighbors
	 */
	public Collection<Feature> getNeighbours(Feature feature) {
		Vector pos = feature.getCenter();

		int x = (int) (pos.x / distance);
		int y = (int) (pos.y / distance);

		return grid[y][x];
	}

	public void add(Feature feature) {
		features.add(feature);

		for (Collection<Feature> cell : getNeighborCells(feature)) {
			cell.add(feature);
		}
	}

	public void add(Collection<? extends Feature> features) {
		for (Feature f : features) {
			add(f);
		}
	}

	public void add(FeatureSet featureSet) {
		if (distance == featureSet.distance && rows == featureSet.rows
				&& cols == featureSet.cols) {
			features.addAll(featureSet.features);

			for (int i = 0; i <= rows; i++) {
				for (int j = 0; j <= cols; j++) {
					grid[i][j].addAll(featureSet.grid[i][j]);
				}
			}
		} else {
			add(featureSet.features);
		}
	}

	public void remove(Feature feature) {
		features.remove(feature);

		for (Collection<Feature> cell : getNeighborCells(feature)) {
			cell.remove(feature);
		}
	}

	/**
	 * Removes features that are valid according to the specified FeatureRule.
	 * 
	 * @param rule
	 * @return the amount of features removed
	 */
	public int remove(FeatureRule rule) {
		int count = 0;

		Iterator<Feature> iter = iterator();
		while (iter.hasNext()) {
			Feature f = iter.next();

			if (rule.isValid(f)) {
				iter.remove();
				count++;
			}
		}

		return count;
	}

	/**
	 * Keeps features that are valid according to the specified FeatureRule. All
	 * other features are removed.
	 * 
	 * @param rule
	 * @return the amount of features removed
	 */
	public int keep(FeatureRule rule) {
		int count = 0;

		Iterator<Feature> iter = iterator();
		while (iter.hasNext()) {
			Feature f = iter.next();

			if (!rule.isValid(f)) {
				iter.remove();
				count++;
			}
		}

		return count;
	}

	/**
	 * Splits the FeatureSet in two parts. All valid features will be kept in
	 * the featureSet. All other features are moved to the new FeatureSet.
	 * 
	 * @param rule
	 * @return a new FeatureSet containing all invalid features
	 */
	public FeatureSet split(FeatureRule rule) {
		FeatureSet result = new FeatureSet(distance, cols, rows);

		Iterator<Feature> iter = iterator();
		while (iter.hasNext()) {
			Feature f = iter.next();

			if (!rule.isValid(f)) {
				iter.remove();
				result.add(f);
			}
		}

		return result;
	}

	/**
	 * Merges two FeatureSets. If two features intersect, the smaller one is
	 * removed if the area ratio (intersecting area to the area of the smaller
	 * feature) is invalid.
	 * 
	 * @param other
	 * @param areaRatio
	 */
	public void merge(FeatureSet other, DValidator areaRatio) {
		List<Feature> remove0 = new ArrayList<Feature>();
		Set<Feature> remove1 = new HashSet<Feature>();

		for (Feature f0 : features) {
			// find intersecting features in the other feature set
			for (Collection<Feature> cell : other.getCells(f0)) {
				for (Feature f1 : cell) {
					double area = f0.intersectionArea(f1);
					double area0 = f0.getArea();
					double area1 = f1.getArea();

					if (area0 < area1 && !areaRatio.isValid(area / area0)) {
						// feature 0 is smaller and is invalid
						remove0.add(f0);
					} else if (!areaRatio.isValid(area / area1)) {
						// feature 1 is smaller and is invalid
						remove1.add(f1);
					}
				}
			}
		}

		// remove all invalid features from this feature set
		for (Feature f : remove0) {
			remove(f);
		}

		// add all valid features from other
		for (Feature f : other.features) {
			if (!remove1.contains(f)) {
				add(f);
			}
		}
	}

	@Override
	public Iterator<Feature> iterator() {
		return new Iterator<Feature>() {
			Iterator<Feature> iter = features.iterator();
			Feature current;

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public Feature next() {
				current = iter.next();
				return current;
			}

			@Override
			public void remove() {
				iter.remove();

				for (Collection<Feature> cell : getNeighborCells(current)) {
					cell.remove(current);
				}
			}
		};
	}

	public void draw(IplImage img, CvScalar color) {
		for (Feature f : this) {
			f.draw(img, color);
		}
	}

	public void fill(IplImage img, CvScalar color) {
		for (Feature f : this) {
			f.fill(img, color);
		}
	}

	public void write(IplImage source, String folder, int border) {
		for (Feature f : this) {
			f.write(source, folder, border);
		}
	}

	public void writeJSON(String filename) {
		writeJSON(filename, 10);
	}

	/**
	 * Writes all features in a file. The format is as follows:
	 * 
	 * [feature0, feature1, ..., featuren]
	 * 
	 * @param filename
	 * @param border
	 */
	public void writeJSON(String filename, int border) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));

			writer.write('[');

			Iterator<Feature> iter = iterator();
			if (iter.hasNext()) {
				writer.write(iter.next().toJSON(border));
				while (iter.hasNext()) {
					writer.write(',');
					writer.write(iter.next().toJSON(border));
				}
			}

			writer.write(']');

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param feature
	 * @return a list of all cells where the features may have neighbors in
	 */
	private List<Collection<Feature>> getNeighborCells(Feature feature) {
		List<Collection<Feature>> neighborCells = new ArrayList<Collection<Feature>>();

		Vector min = feature.getMin();
		Vector max = feature.getMax();
		int xmin = (int) (min.x / distance) - 1;
		int ymin = (int) (min.y / distance) - 1;
		int xmax = (int) (max.x / distance) + 1;
		int ymax = (int) (max.y / distance) + 1;

		xmin = xmin >= 0 ? xmin : 0;
		ymin = ymin >= 0 ? ymin : 0;
		xmax = xmax < cols ? xmax : (cols - 1);
		ymax = ymax < rows ? ymax : (rows - 1);

		for (int i = ymin; i <= ymax; i++) {
			for (int j = xmin; j <= xmax; j++) {
				neighborCells.add(grid[i][j]);
			}
		}

		return neighborCells;
	}

	/**
	 * @param feature
	 * @return a list of all cells that are intersected by the feature
	 */
	private List<Collection<Feature>> getCells(Feature feature) {
		List<Collection<Feature>> cells = new ArrayList<Collection<Feature>>();

		Vector min = feature.getMin();
		Vector max = feature.getMax();
		int xmin = (int) (min.x / distance);
		int ymin = (int) (min.y / distance);
		int xmax = (int) (max.x / distance);
		int ymax = (int) (max.y / distance);

		xmin = xmin >= 0 ? xmin : 0;
		ymin = ymin >= 0 ? ymin : 0;
		xmax = xmax < cols ? xmax : (cols - 1);
		ymax = ymax < rows ? ymax : (rows - 1);

		for (int i = ymin; i <= ymax; i++) {
			for (int j = xmin; j <= xmax; j++) {
				cells.add(grid[i][j]);
			}
		}

		return cells;
	}
}
