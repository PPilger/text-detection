import java.io.File;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_photo.*;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		//britishIsles();
		portolanAtlas();
	}
	

	public static void portolanAtlas() {
		Image image = new Image("samples" + File.separator
				+ "PORTOLAN_ATLAS_S.jpg");
		ImageDisplay display = new ImageDisplay("output1", 1200, 800);
		ImageDisplay display2 = new ImageDisplay("output2", 1200, 800);
		
		Image lines = new Image(image.getGray());
		{

			lines.process(new ThresholdProcessor(165));
			lines.process(new InvertProcessor());
			lines.process(new BigObjectEraseProcessor(3));
			lines.process(new SmallObjectErasorProcessor(20));
			lines.process(new LineSegmentsProcessor(40, 50, 800));


			/*lines.process(new DilateProcessor(3));
			display.show(image.getColor());
			cvInpaint(image.getColor(), lines.getImg(), image.getColor(), 20, CV_INPAINT_NS);
			display2.show(image.getColor());*/
			//display.show(lines.getImg(), image.getGray());
		}
		
		{
			image.process(new ThresholdProcessor(165));
			image.process(new InvertProcessor());
			
			//display.show(image.getImg());
			image.process(new CloseProcessor(3));
			image.process(new BigObjectEraseProcessor(11));
			//display.show(image.getImg());
			image.process(new SmallObjectErasorProcessor(10));
			//display.show(image.getImg());
			image.process(new ThicknessProcessor(1, 7));
			
			image.process(new EraseProcessor(lines.getImg()));

			// img.process(new RGRatioDisplayProcessor("1", 1200, 800, 0.5));
			// img.process(new RBRatioDisplayProcessor("1", 1200, 800, 0.5));
			// img.process(new GBRatioDisplayProcessor("1", 1200, 800, 0.5));

			// img.process(new RGBRatioEraseProcessor(0.9, 3, 1, 3, 1, 3, 2,
			// true));
			// img.process(new RGBRatioEraseProcessor(0.9, 3, 0.8, 3, 0.95, 3,
			// 2,
			// true));

			image.process(new FirstDerivateEraseProcessor(120, 9));
			image.process(new SecondDerivateEraseProcessor(130, 9));

			//image.process(new CloseProcessor(5));
			//display.show(image.getImg());
			
			/*Image bla = new Image(image.getImg());
			{
				bla.process(new InvertProcessor());
				for(int i = 0; i < 4; i++) {
					display2.show(bla.getImg());
					bla.process(new ObstacleRemoveProcessor(3, 7, 25));
				}
				bla.process(new InvertProcessor());

				bla.process(new OpenProcessor(3));
			}
			image.process(new MaskProcessor(bla.getImg()));

			for(int i = 0; i < 4; i++) {
				display2.show(image.getImg());
				image.process(new ObstacleRemoveProcessor(3, 3, 25));
			}*/
			
			image.process(new VarianceProcessor(11,50));
			//display2.show(image.getImg());
			image.process(new ObstacleRemoveProcessor(3, 3));
			//display2.show(image.getImg());

			image.process(new DilateProcessor(3));
			image.process(new CloseProcessor(3));

			display.show(image.getImg());
			
			//image.process(new DilateProcessor(3));
			//display.show(image.getColor());
			//cvInpaint(image.getColor(), image.getImg(), image.getColor(), 20, CV_INPAINT_TELEA);
			//display2.show(image.getColor());
		}

		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000,
				1, 5000);
		FeatureLinker linker = new DirectionBasedFeatureLinker(25, 10, 5);
		//linker = new AreaBasedFeatureLinker(300);

		//image.setImageDisplay(display, display);
		image.findText(detector, linker);
		image.save("Portolan Atlas.jpg");
	}

	public static void britishIsles() {
		Image image = new Image("samples" + File.separator + "British Isles.png");

		image.process(new ThresholdProcessor(207));
		image.process(new InvertProcessor());
		image.process(new ColorEraseProcessor(0, 100, 0, 50, 0, 50, 10, false));
		image.process(new ThicknessProcessor(1, 5));
		image.process(new RemoveLinesProcessor(60));
		image.process(new DilateProcessor(3));
		image.process(new CloseProcessor(3));

		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000,
				100, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1000);

		//ImageDisplay display = new ImageDisplay("output", 1200, 800);
		//img.setImageDisplay(display, display);

		FeatureSet features = image.findText(detector, linker);
		
		features.save("Features.js");
		image.save("British Isles.png");
	}
}
