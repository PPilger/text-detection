import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.*;

import com.googlecode.javacpp.PointerPointer;

public class LinkedFeature extends Feature {
	private static CvMemStorage mem = cvCreateMemStorage(0);

	public static LinkedFeature create(List<Feature> subFeatures) {
		int[] coords = new int[8 * subFeatures.size()];
		int i = 0;
		for (Feature f : subFeatures) {
			Box2D box = f.box();
			for (Vector2D corner : box.corners()) {
				coords[i] = (int) Math.round(corner.x);
				coords[i + 1] = (int) Math.round(corner.y);
				i+=2;
			}

			/*CvBox2D box = f.cvBox();
			float[] values = new float[8];
			cvBoxPoints(box, values);

			for (int j = 0; j < 8; j++) {
				coords[i * 8 + j] = Math.round(values[j]);
			}*/
		}

		CvSeq seq = cvCreateSeq(CV_SEQ_ELTYPE_POINT, sizeof(CvSeq.class),
				sizeof(CvPoint.class), mem);
		cvSeqPushMulti(seq, new PointerPointer(coords).get(),
				coords.length / 2, CV_FRONT);

		CvBox2D box = cvMinAreaRect2(seq, mem);
		return new LinkedFeature(box);
	}
	
	private LinkedFeature(CvBox2D box) {
		super(box);
	}
}
