import java.io.File;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Image img = new Image("samples" + File.separator + "PORTOLAN_ATLAS_S.jpg");
		img.addProcessor(new ColorToGrayProcessor());
		img.addProcessor(new ThresholdProcessor(165)); // 170 TB, 200 BI, 150 PA
		img.addProcessor(new InvertProcessor());
		
		//img.addProcessor(new RGRatioDisplayProcessor("1", 1200, 800, 0.5));
		//img.addProcessor(new RBRatioDisplayProcessor("1", 1200, 800, 0.5));
		//img.addProcessor(new GBRatioDisplayProcessor("1", 1200, 800, 0.5));
		
		//img.addProcessor(new RGBRatioEraseProcessor(0.9, 3, 1, 3, 1, 3, 2, true));
		//img.addProcessor(new ImageDisplayProcessor("1", 1200, 800));
		//img.addProcessor(new RGBRatioEraseProcessor(0.9, 3, 0.8, 3, 0.95, 3, 2, true));
		//img.addProcessor(new ImageDisplayProcessor("3", 1200, 800));

		int i = 10;
		/*int dil = 1;
		img.addProcessor(new RGBRatioEraseProcessor(0, 3, 0, 3, 0.95, 1, dil, true));
		img.addProcessor(new ImageDisplayProcessor(""+i++, 1200, 800));
		img.addProcessor(new RGBRatioEraseProcessor(0, 3, 0, 3, 0.95, 1.1, dil, true));
		img.addProcessor(new ImageDisplayProcessor(""+i++, 1200, 800));
		img.addProcessor(new RGBRatioEraseProcessor(0, 3, 0, 3, 0.95, 1.2, dil, true));
		img.addProcessor(new ImageDisplayProcessor(""+i++, 1200, 800));
		img.addProcessor(new RGBRatioEraseProcessor(0, 3, 0, 3, 0.95, 1.3, dil, true));
		img.addProcessor(new ImageDisplayProcessor(""+i++, 1200, 800));
		img.addProcessor(new RGBRatioEraseProcessor(0, 3, 0, 3, 0.95, 1.4, dil, true));
		img.addProcessor(new ImageDisplayProcessor(""+i++, 1200, 800));
		img.addProcessor(new RGBRatioEraseProcessor(0, 3, 0, 3, 0.95, 3, dil, true));
		img.addProcessor(new ImageDisplayProcessor("a"+i++, 1200, 800));
		*/
		
		
		new ImageDisplay("", 1200,800).show(img.iplImage());
		img.addProcessor(new ImageDisplayProcessor("a"+i++, 1200, 800));
		img.addProcessor(new ThicknessProcessor(1, 7));
		img.addProcessor(new ImageDisplayProcessor("a"+i++, 1200, 800));
		//img.addProcessor(new RemoveLinesProcessor(60));
		img.addProcessor(new DilateProcessor(3));
		img.addProcessor(new CloseProcessor(3));

		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000, 100, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);
		
				//"PORTOLAN_ATLAS.jpg");
				//TestbildM.jpg");//

		/*Image img = new Image("samples" + File.separator + "British Isles.png");
		img.addProcessor(new ColorToGrayProcessor());
		img.addProcessor(new ThresholdProcessor(207)); // 170 TB, 200 BI, 150 PA
		img.addProcessor(new InvertProcessor());
		img.addProcessor(new ColorEraseProcessor(0, 50, 0, 50, 0, 100, 10, false));
		img.addProcessor(new ThicknessProcessor(1, 5));
		img.addProcessor(new RemoveLinesProcessor(60));
		img.addProcessor(new DilateProcessor(3));
		img.addProcessor(new CloseProcessor(3));
		
		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000, 100, 5000);
		FeatureLinker linker = new AreaBasedFeatureLinker(1, 800);*/
		
		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		img.setImageDisplay(display, display, display);
		img.findText(detector, linker);
		img.save("TestbildOut.jpg");
	}
}
