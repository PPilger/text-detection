import java.io.File;
import java.util.List;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		britishIsles();
		//portolanAtlas();
	}

	public static void portolanAtlas() {
		Image image = new Image("samples" + File.separator
				+ "PORTOLAN_ATLAS_S.jpg");
		ImageDisplay display = new ImageDisplay("output", 1200, 800);

		Image lines = new Image(image.getGray());
		{

			lines.process(new ThresholdProcessor(165));
			lines.process(new InvertProcessor());
			lines.process(new BigObjectEraseProcessor(3));
			lines.process(new SmallObjectErasorProcessor(20));
			lines.process(new LineSegmentsProcessor(40, 50, 800));

			display.show(lines.getImg(), image.getGray());
		}
		
		{
			image.process(new ThresholdProcessor(165));
			image.process(new InvertProcessor());
			
			display.show(image.getImg());
			image.process(new CloseProcessor(3));
			image.process(new BigObjectEraseProcessor(11));
			display.show(image.getImg());
			image.process(new SmallObjectErasorProcessor(10));
			display.show(image.getImg());
			image.process(new ThicknessProcessor(1, 7));
			//img.process(new CloseProcessor(3));
			display.show(image.getImg());
			
			image.process(new EraseProcessor(lines.getImg()));
			
			display.show(image.getImg());

			// img.process(new RGRatioDisplayProcessor("1", 1200, 800, 0.5));
			// img.process(new RBRatioDisplayProcessor("1", 1200, 800, 0.5));
			// img.process(new GBRatioDisplayProcessor("1", 1200, 800, 0.5));

			// img.process(new RGBRatioEraseProcessor(0.9, 3, 1, 3, 1, 3, 2,
			// true));
			// img.process(new RGBRatioEraseProcessor(0.9, 3, 0.8, 3, 0.95, 3,
			// 2,
			// true));

			image.process(new FirstDerivateEraseProcessor(120, 3));
			image.process(new SecondDerivateEraseProcessor(130, 3));

			image.process(new CloseProcessor(3));
			image.process(new ThicknessProcessor(1, 7));

			image.process(new RemoveLinesProcessor(100));

			image.process(new DilateProcessor(3));
			image.process(new CloseProcessor(3));
		}

		FeatureDetector detector = new ContourBasedFeatureDetector(15, 1000000,
				1, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);

		image.setImageDisplay(display, display);
		image.findText(detector, linker);
		image.save("Portolan Atlas.jpg");
	}

	public static void britishIsles() {
		Image img = new Image("samples" + File.separator + "British Isles.png");

		img.process(new ThresholdProcessor(207));
		img.process(new InvertProcessor());
		img.process(new ColorEraseProcessor(0, 100, 0, 50, 0, 50, 10, false));
		img.process(new ThicknessProcessor(1, 5));
		img.process(new RemoveLinesProcessor(60));
		img.process(new DilateProcessor(3));
		img.process(new CloseProcessor(3));

		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000,
				100, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);

		//ImageDisplay display = new ImageDisplay("output", 1200, 800);
		//img.setImageDisplay(display, display);

		FeatureSet features = img.findText(detector, linker);
		
		features.save("Features.js");
		img.save("British Isles.png");
	}
}
