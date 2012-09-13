package feature;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import math.Vector;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FeatureSet implements Iterable<Feature> {
	private double distance;
	private int rows;
	private int cols;
	private Collection<Feature> allFeatures;
	private Collection<Feature>[][] featureMap;

	@SuppressWarnings("unchecked")
	public FeatureSet(double distance, int width, int height) {
		this.distance = distance;
		this.rows = (int) Math.ceil(height / (double) distance);
		this.cols = (int) Math.ceil(width / (double) distance);

		this.allFeatures = new HashSet<Feature>();
		this.featureMap = (Collection<Feature>[][]) new Collection[this.rows][this.cols];

		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				featureMap[i][j] = new ArrayList<Feature>();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public FeatureSet(FeatureSet other) {
		this.distance = other.distance;
		this.rows = other.rows;
		this.cols = other.cols;

		this.allFeatures = new HashSet<Feature>();
		this.featureMap = (Collection<Feature>[][]) new Collection[this.rows][this.cols];

		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				featureMap[i][j] = new ArrayList<Feature>();
			}
		}
	}

	public double getDistance() {
		return distance;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public int size() {
		return allFeatures.size();
	}

	public Collection<Feature> getNeighbours(Feature feature) {
		Vector pos = feature.getCenter();

		int x = (int) (pos.x / distance);
		int y = (int) (pos.y / distance);

		return featureMap[y][x];
	}

	public void add(Feature feature) {
		allFeatures.add(feature);

		for (Collection<Feature> cell : getNeighbourCells(feature)) {
			cell.add(feature);
		}
	}

	public void add(Collection<? extends Feature> features) {
		for (Feature f : features) {
			add(f);
		}
	}

	public void add(FeatureSet features) {
		if (distance == features.distance && rows == features.rows
				&& cols == features.cols) {
			allFeatures.addAll(features.allFeatures);

			for (int i = 0; i <= rows; i++) {
				for (int j = 0; j <= cols; j++) {
					featureMap[i][j].addAll(features.featureMap[i][j]);
				}
			}
		} else {
			add(features.allFeatures);
		}
	}

	public void remove(Feature feature) {
		allFeatures.remove(feature);

		for (Collection<Feature> cell : getNeighbourCells(feature)) {
			cell.remove(feature);
		}
	}

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

	public int dontRemove(FeatureRule rule) {
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

	public void merge(FeatureSet other) {
		List<Feature> add = new ArrayList<Feature>();
		List<Feature> remove = new ArrayList<Feature>();

		for (Feature f0 : allFeatures) {
			for (Collection<Feature> cell : other.getCells(f0)) {
				for (Feature f1 : cell) {
					double area = f0.intersectionArea(f1);
					double area0 = f0.getArea();
					double area1 = f1.getArea();
					
					if (area / area0 > 0.5 || area / area1 > 0.5) {
						if(area0 < area1) {
							remove.add(f0);
							add.add(f1);
						}
					} else {
						add.add(f1);
					}
				}
			}
		}

		for(Feature f : remove) {
			remove(f);
		}
		
		add(add);
	}

	@Override
	public Iterator<Feature> iterator() {
		return new Iterator<Feature>() {
			Iterator<Feature> iter = allFeatures.iterator();
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

				for (Collection<Feature> cell : getNeighbourCells(current)) {
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

	private List<Collection<Feature>> getNeighbourCells(Feature feature) {
		List<Collection<Feature>> neighbourCells = new ArrayList<Collection<Feature>>();

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
				neighbourCells.add(featureMap[i][j]);
			}
		}

		return neighbourCells;
	}
	
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
				cells.add(featureMap[i][j]);
			}
		}

		return cells;
	}
}
