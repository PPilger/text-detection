import static com.googlecode.javacv.cpp.opencv_core.*;

public class ImageDisplayProcessor extends ImageDisplay implements ImageProcessor {
	private String imageName;
	private double scale;
	
	public ImageDisplayProcessor(String imageName) {
		this(imageName, 1);
	}
	
	public ImageDisplayProcessor(String imageName, double scale) {
		super(imageName);
		
		this.imageName = imageName;
	}

	public ImageDisplayProcessor(String imageName, int width, int height) {
		this(imageName, width, height, 1);
	}

	public ImageDisplayProcessor(String imageName, int width, int height, double scale) {
		super(imageName, width, height);
		
		this.imageName = imageName;
		this.scale = scale;
	}

	public ImageDisplayProcessor(String imageName, String title, int width, int height) {
		this(imageName, title, width, height, 1);
	}

	public ImageDisplayProcessor(String imageName, String title, int width, int height, double scale) {
		super(title, width, height);
		
		this.imageName = imageName;
		this.scale = scale;
	}

	@Override
	public void process(ImageCollection images) {
		IplImage img = cvCloneImage(images.get(imageName));
		cvConvertScale(img, img, scale, 0);
		super.show(img);
	}
}
