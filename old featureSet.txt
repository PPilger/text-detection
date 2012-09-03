package feature;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class OldFeatureSet extends ArrayList<Feature> {
	public OldFeatureSet() {
	}
	
	public OldFeatureSet(Collection<? extends Feature> features) {
		super(features);
	}
	
	public int remove(FeatureRule rule) {
		int count = 0;
		
		Iterator<Feature> iter = iterator();
		while(iter.hasNext()) {
			Feature f = iter.next();
			
			if(!rule.isValid(f)) {
				iter.remove();
				count++;
			}
		}
		
		return count;
	}
	
	public OldFeatureSet split(FeatureRule rule) {
		OldFeatureSet result = new OldFeatureSet();
		
		Iterator<Feature> iter = iterator();
		while(iter.hasNext()) {
			Feature f = iter.next();
			
			if(!rule.isValid(f)) {
				iter.remove();
				result.add(f);
			}
		}
		
		return result;
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
