package feature;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.Collection;

import com.googlecode.javacpp.PointerPointer;

public class LinkedFeature extends Feature {
	private static CvMemStorage mem = cvCreateMemStorage(0);
	private Collection<Feature> subFeatures;

	public static LinkedFeature create(Collection<Feature> subFeatures) {
		int size = 0;
		for (Feature f : subFeatures) {
			size += f.getICorners().length;
		}

		int[] coords = new int[size];

		int i = 0;
		for (Feature f : subFeatures) {
			int[] corners = f.getICorners();

			for (int j = 0; j < corners.length; j++) {
				coords[i + j] = corners[j];
			}

			i += corners.length;
		}

		CvSeq seq = cvCreateSeq(CV_SEQ_ELTYPE_POINT, sizeof(CvSeq.class),
				sizeof(CvPoint.class), mem);
		cvSeqPushMulti(seq, new PointerPointer(coords).get(),
				coords.length / 2, CV_FRONT);

		CvBox2D box = cvMinAreaRect2(seq, mem);
		return new LinkedFeature(subFeatures, box);
	}

	private LinkedFeature(Collection<Feature> subFeatures, CvBox2D box) {
		super(box);
		this.subFeatures = subFeatures;
	}

	public void draw(CvArr img, CvScalar color) {
		for (Feature f : subFeatures) {
			f.draw(img, CvScalar.BLACK);
		}
		super.draw(img, color);
	}

	public void fill(CvArr img, CvScalar color) {
		for (Feature f : subFeatures) {
			f.fill(img, color);
		}
	}
}
