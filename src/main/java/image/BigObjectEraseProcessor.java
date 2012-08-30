package image;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.*;


public class BigObjectEraseProcessor extends SimpleImageProcessor {
	private int size;

	public BigObjectEraseProcessor(int size) {
		this.size = size;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvSetZero(temp);
		cvSet(temp, CvScalar.WHITE, img);
		new OpenProcessor(size).process(temp, null);
		cvSet(img, CvScalar.BLACK, temp);
	}

}
