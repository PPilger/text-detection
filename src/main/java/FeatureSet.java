import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FeatureSet implements Iterable<Feature> {
	private List<Feature> features;

	public FeatureSet() {
		features = new ArrayList<Feature>();
	}

	public void add(Feature f) {
		features.add(f);
	}

	public int size() {
		return features.size();
	}

	public void draw(IplImage img, CvScalar color) {
		for (Feature f : features) {
			f.draw(img, color);
		}
	}

	public void save(String filename) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));

			writer.write('[');

			Iterator<Feature> iter = features.iterator();
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

	@Override
	public Iterator<Feature> iterator() {
		return features.iterator();
	}
}
