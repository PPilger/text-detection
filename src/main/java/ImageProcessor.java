import com.googlecode.javacv.cpp.opencv_core.IplImage;

public interface ImageProcessor {
	/**
	 * @param img
	 *            8u1c image
	 * @param colorImg
	 *            8u3c image
	 * @param temp
	 *            temporary image used by the processor (can be null if no
	 *            temporary image is used)
	 */
	public void process(IplImage img, IplImage colorImg, IplImage temp);
}
