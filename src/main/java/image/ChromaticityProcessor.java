package image;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.cvAnd;
import static com.googlecode.javacv.cpp.opencv_core.cvConvert;
import static com.googlecode.javacv.cpp.opencv_core.cvDiv;
import validator.DInterval;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Sets all pixels with an invalid chromaticity to zero (black). The
 * chromaticity is defined by color value divided by the brightness.
 * 
 * @author PilgerstorferP
 * 
 */
public class ChromaticityProcessor implements ImageProcessor {
	private DInterval[] range;

	/**
	 * @param red
	 *            defines the valid range for red color chromaticity
	 * @param green
	 *            defines the valid range for green color chromaticity
	 * @param blue
	 *            defines the valid range for blue color chromaticity
	 */
	public ChromaticityProcessor(DInterval red, DInterval green, DInterval blue) {
		this.range = new DInterval[] { red, green, blue };
	}

	@Override
	public void process(Image image) {
		IplImage img = image.getImg();
		IplImage temp = image.getTemp();

		// store the color channels in an array to make them iterable
		IplImage[] rgb = new IplImage[] { image.getRed(), image.getGreen(),
				image.getBlue() };

		// store the brightness as 32F image
		IplImage brightness = IplImage.create(img.cvSize(), IPL_DEPTH_32F, 1);
		cvConvert(image.getGray(), brightness);

		IplImage channel = IplImage.create(img.cvSize(), IPL_DEPTH_32F, 1);

		for (int i = 0; i < 3; i++) {
			cvConvert(rgb[i], channel);

			// calculate chromaticity for the current color channel
			cvDiv(channel, brightness, channel, 1);

			range[i].validate(channel, temp);

			cvAnd(img, temp, img, null);
		}
	}
}
