package feature;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.CV_FRONT;
import static com.googlecode.javacv.cpp.opencv_core.CV_SEQ_ELTYPE_POINT;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateSeq;
import static com.googlecode.javacv.cpp.opencv_core.cvSeqPushMulti;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMinAreaRect2;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvBox2D;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;

/**
 * A LinkedFeature is a feature containing several sub-features that are linked
 * together.
 * 
 * @author PilgerstorferP
 * 
 */
public class LinkedFeature extends Feature {
	private static CvMemStorage mem = cvCreateMemStorage(0);

	private Set<Feature> subFeatures;

	// stores the rating of the feature, so it does not have to be recalculated
	// if it is needed more often.
	private double rating = -1;

	public static LinkedFeature create(Collection<Feature> subFeatures) {
		return create(new HashSet<Feature>(subFeatures));
	}

	public static LinkedFeature create(Set<Feature> subFeatures) {
		// create an array of all convex hull points of all sub-features
		// format: {x0, y0, x1, y1, ..., xn, yn}
		int[] coords;
		{
			int size = 0;
			for (Feature f : subFeatures) {
				size += f.getConvexHull().length;
			}
			coords = new int[size];

			int i = 0;
			for (Feature f : subFeatures) {
				int[] corners = f.getConvexHull();

				// append corners of feature f
				for (int j = 0; j < corners.length; j++) {
					coords[i + j] = corners[j];
				}

				i += corners.length;
			}
		}

		// calculate the minimum area rectangle containing all of the points
		CvBox2D box;
		{
			CvSeq seq = cvCreateSeq(CV_SEQ_ELTYPE_POINT, sizeof(CvSeq.class),
					sizeof(CvPoint.class), mem);
			cvSeqPushMulti(seq, new PointerPointer(coords).get(),
					coords.length / 2, CV_FRONT);

			cvClearMemStorage(mem);
			box = cvMinAreaRect2(seq, mem);
		}

		return new LinkedFeature(subFeatures, box);
	}

	private LinkedFeature(Set<Feature> subFeatures, CvBox2D box) {
		super(box);
		this.subFeatures = subFeatures;
	}

	/**
	 * Draws all sub-features in black and a colored rectangle around it.
	 */
	public void draw(CvArr img, CvScalar color) {
		for (Feature f : subFeatures) {
			f.draw(img, CvScalar.BLACK);
		}
		super.draw(img, color);
	}

	/**
	 * Fills all sub-features with the specified color.
	 */
	public void fill(CvArr img, CvScalar color) {
		for (Feature f : subFeatures) {
			f.fill(img, color);
		}
	}

	/**
	 * If the returned set of sub-features is changed, there will be no changes
	 * in this LinkedFeature (it gets inconsistent).
	 * 
	 * @return
	 */
	public Set<Feature> getSubFeatures() {
		return subFeatures;
	}

	@Override
	public double getRating() {
		if (rating == -1) {
			double area = 0;
			for (Feature f : subFeatures) {
				area += f.getArea();
			}
			rating = subFeatures.size() * area / getArea();
		}

		return rating;
	}
}
