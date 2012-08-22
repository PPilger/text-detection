import static com.googlecode.javacv.cpp.opencv_core.cvNot;


import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class InvertProcessor extends SimpleImageProcessor {

	@Override
	public void process(IplImage img, IplImage temp) {
		cvNot(img, img);
	}
}
