package feature;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class FeatureSet extends ArrayList<Feature> {
	public FeatureSet() {
	}
	
	public FeatureSet(Collection<? extends Feature> features) {
		super(features);
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
}
