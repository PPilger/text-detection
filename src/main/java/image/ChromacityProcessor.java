package image;

import validator.DInterval;

import com.googlecode.javacv.cpp.opencv_core.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

public class ChromacityProcessor implements ImageProcessor {
	private DInterval[] range;

	public ChromacityProcessor(DInterval red, DInterval green, DInterval blue) {
		this.range = new DInterval[] { red, green, blue };
	}

	@Override
	public void process(Image image) {
		IplImage img = image.getImg();
		IplImage temp = image.getTemp();

		IplImage br = image.getGray();
		IplImage[] rgb = new IplImage[] { image.getRed(), image.getGreen(),
				image.getBlue() };

		IplImage tempBr = IplImage.create(br.cvSize(), IPL_DEPTH_32F, 1);
		cvConvert(br, tempBr);

		IplImage tempColor = IplImage.create(br.cvSize(), IPL_DEPTH_32F, 1);

		for (int i = 0; i < 3; i++) {
			IplImage ch = rgb[i];
			cvConvert(ch, tempColor);
			cvDiv(tempColor, br, tempColor, 1);
			
			//cvScale(tempColor, tempColor, 128, 0);
			//cvSaveImage("chromacity"+i+".jpg", tempColor);
			
			range[i].validate(tempColor, temp);
			
			cvAnd(img, temp, img, null);
		}
	}
}
