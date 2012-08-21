import com.googlecode.javacv.cpp.opencv_core.IplImage;

public abstract class SingleImageProcessor implements ImageProcessor {
	public abstract void process(IplImage img, IplImage temp);

	public void process(ImageCollection images) {
		process(images.getProcessed(), images.getTemp());
	}
}
