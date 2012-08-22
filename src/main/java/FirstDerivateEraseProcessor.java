
import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;


public class FirstDerivateEraseProcessor implements ImageProcessor {
	private int threshold;
	private int border;
	
	public FirstDerivateEraseProcessor(int threshold, int border) {
		this.threshold = threshold;
		this.border = border;
	}

	@Override
	public void process(Image image) {
		IplImage gray = image.getGray();
		IplImage processed = image.getImg();
		IplImage temp = image.getTemp();
		IplImage hTemp = cvCloneImage(temp);
		IplImage vTemp = cvCloneImage(temp);

		cvSobel(gray, hTemp, 1, 0, 3);
		cvSobel(gray, vTemp, 0, 1, 3);
		
		cvMax(hTemp, vTemp, temp);
		
		cvThreshold(temp, temp, threshold, 255, CV_THRESH_BINARY);
		
		new DilateProcessor(border).process(temp, null);
		
		cvAnd(processed, temp, processed, null);
	}
	
}
