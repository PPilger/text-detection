package feature;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import math.Vector2D;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FeatureSet implements Iterable<Feature> {
	private double distance;
	private int height;
	private int width;
	private Collection<Feature> allFeatures;
	private Collection<Feature>[][] featureMap;

	@SuppressWarnings("unchecked")
	public FeatureSet(double distance, int width, int height) {
		this.distance = distance;
		this.height = (int) Math.ceil(height / (double) distance);
		this.width = (int) Math.ceil(width / (double) distance);

		this.allFeatures = new HashSet<Feature>();
		this.featureMap = (Collection<Feature>[][]) new Collection[this.height][this.width];

		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				featureMap[i][j] = new ArrayList<Feature>();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public FeatureSet(FeatureSet other) {
		this.distance = other.distance;
		this.height = other.height;
		this.width = other.width;

		this.allFeatures = new HashSet<Feature>();
		this.featureMap = (Collection<Feature>[][]) new Collection[this.height][this.width];

		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				featureMap[i][j] = new ArrayList<Feature>();
			}
		}
	}

	public double getDistance() {
		return distance;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int size() {
		return allFeatures.size();
	}

	public Collection<Feature> getNeighbours(Feature feature) {
		Vector2D pos = feature.position();

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
		allFeatures.addAll(features.allFeatures);

		for (int i = 0; i <= height; i++) {
			for (int j = 0; j <= width; j++) {
				featureMap[i][j].addAll(features.featureMap[i][j]);
			}
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

			if (!rule.isValid(f)) {
				iter.remove();
				count++;
			}
		}

		return count;
	}

	public FeatureSet split(FeatureRule rule) {
		FeatureSet result = new FeatureSet(distance, width, height);

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

	public Iterator<Feature> iterator(final Feature feature) {
		return new Iterator<Feature>() {
			Iterator<Feature> iter = getNeighbours(feature).iterator();
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
				FeatureSet.this.remove(current);
			}
		};
	}

	public void draw(IplImage img, CvScalar color) {
		for (Feature f : this) {
			f.draw(img, color);
		}
	}

	public void write(String filename) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));

			writer.write('[');

			Iterator<Feature> iter = iterator();
			if (iter.hasNext()) {
				writer.write(iter.next().toJSON());
				while (iter.hasNext()) {
					writer.write(',');
					writer.write(iter.next().toJSON());
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

		Vector2D min = feature.box().min();
		Vector2D max = feature.box().max();
		int xmin = (int) (min.x / distance) - 1;
		int ymin = (int) (min.y / distance) - 1;
		int xmax = (int) (max.x / distance) + 1;
		int ymax = (int) (max.y / distance) + 1;

		xmin = xmin >= 0 ? xmin : 0;
		ymin = ymin >= 0 ? ymin : 0;
		xmax = xmax < width ? xmax : (width - 1);
		ymax = ymax < height ? ymax : (height - 1);

		for (int i = ymin; i <= ymax; i++) {
			for (int j = xmin; j <= xmax; j++) {
				neighbourCells.add(featureMap[i][j]);
			}
		}

		return neighbourCells;
	}
}
