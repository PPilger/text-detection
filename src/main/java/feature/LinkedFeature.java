package feature;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.*;

import math.Box2D;
import math.Vector2D;

import com.googlecode.javacpp.PointerPointer;

public class LinkedFeature extends Feature {
	private static CvMemStorage mem = cvCreateMemStorage(0);
	private List<Feature> subFeatures;

	public static LinkedFeature create(List<Feature> subFeatures) {
		int[] coords = new int[8 * subFeatures.size()];
		int i = 0;
		for (Feature f : subFeatures) {
			Box2D box = f.box();
			for (Vector2D corner : box.corners) {
				coords[i] = (int) Math.round(corner.x);
				coords[i + 1] = (int) Math.round(corner.y);
				i+=2;
			}
		}

		CvSeq seq = cvCreateSeq(CV_SEQ_ELTYPE_POINT, sizeof(CvSeq.class),
				sizeof(CvPoint.class), mem);
		cvSeqPushMulti(seq, new PointerPointer(coords).get(),
				coords.length / 2, CV_FRONT);

		CvBox2D box = cvMinAreaRect2(seq, mem);
		return new LinkedFeature(subFeatures, box);
	}
	
	private LinkedFeature(List<Feature> subFeatures, CvBox2D box) {
		super(box);
		this.subFeatures = subFeatures;
	}
	
	public void fill(CvArr img, CvScalar color) {
		for(Feature f : subFeatures) {
			f.fill(img, color);
		}
	}
}
