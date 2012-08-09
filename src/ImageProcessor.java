import com.googlecode.javacv.cpp.opencv_core.IplImage;


public interface ImageProcessor {
	/**
	 * Processes an 8u1c image (8 bit, 1 channel)
	 * @param img
	 */
	public void process(IplImage img);
}
