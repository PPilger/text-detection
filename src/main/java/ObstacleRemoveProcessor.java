import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_core.*;


public class ObstacleRemoveProcessor extends SimpleImageProcessor {
	private int size;
	private int min;
	private int max;

	public ObstacleRemoveProcessor(int size, int min, int max) {
		this.size = size;
		this.min = min;
		this.max = max;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		CvMat kernel = CvMat.create(size, size, CV_32FC1);
		cvSetZero(kernel);
		cvAddS(kernel, cvScalarAll(1 / 255.0), kernel, null);
		cvFilter2D(img, temp, kernel, cvPoint(-1, -1));

		cvAnd(temp, img, temp, null);
		cvCmpS(temp, min, img, CV_CMP_GE);
		//cvCmpS(temp, max, temp, CV_CMP_LE);
		//cvAnd(temp, img, img, null);
	}

}
