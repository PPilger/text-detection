import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class RBRatioDisplayProcessor extends ImageDisplayProcessor {
	double scale;

	public RBRatioDisplayProcessor(String title) {
		super(title);
		
		this.scale = 1;
	}

	public RBRatioDisplayProcessor(String title, int width, int height, double scale) {
		super(title, width, height);
		
		this.scale = scale;
	}

	@Override
	public void process(IplImage img, IplImage colorImg, IplImage temp) {

		IplImage rImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage gImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage bImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);

		cvSplit(colorImg, rImg, gImg, bImg, null);

		IplImage rbImg = IplImage
				.create(cvSize(img.width(), img.height()), IPL_DEPTH_32F, 1);

		cvDiv(rImg, bImg, rbImg, 1);

		cvConvertScale(rbImg, rbImg, scale, 0);

		super.process(rbImg, null, null);
	}
	
}
