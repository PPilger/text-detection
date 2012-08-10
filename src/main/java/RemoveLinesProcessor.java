import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class RemoveLinesProcessor implements ImageProcessor {
	private int threshold;

	public RemoveLinesProcessor(int threshold) {
		this.threshold = threshold;
	}

	@Override
	public void process(IplImage img, IplImage colorImg) {
		CvMemStorage mem = CvMemStorage.create();
		CvSeq lines = cvHoughLines2(img, mem, CV_HOUGH_STANDARD, 0.5,
				Angle180.degToRad(0.5), threshold, 0, 0);

		for (int i = 0; i < lines.total(); i++) {
			CvPoint2D32f polar = new CvPoint2D32f(cvGetSeqElem(lines, i));

			int px = (int) (Math.cos(polar.y()) * polar.x());
			int py = (int) (Math.sin(polar.y()) * polar.x());

			double lineLength = img.width() + img.height();
			double angle = polar.y() + Math.PI / 2;
			cvDrawLine(
					img,
					cvPoint(px - (int) (Math.cos(angle) * lineLength), py
							- (int) (Math.sin(angle) * lineLength)),
					cvPoint(px + (int) (Math.cos(angle) * lineLength), py
							+ (int) (Math.sin(angle) * lineLength)),
					CvScalar.BLACK, 2, 0, 0);
		}
	}

}
