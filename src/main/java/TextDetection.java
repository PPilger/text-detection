import java.io.File;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Image img = new Image("samples" + File.separator + 
				//"PORTOLAN_ATLAS.jpg");
				"British Isles.png");//TestbildM.jpg");//
		
		img.addProcessor(new ThresholdProcessor(200)); // 170 TB, 200 BI, 150 PA
		//img.addProcessor(new ColorThresholdProcessor(img.iplImage(), 180, 200, 210));
		//img.addProcessor(new ColorRangeProcessor(img.iplImage(), 0, 50, 0, 50, 0, 200));
		img.addProcessor(new InvertProcessor());
		img.addProcessor(new ColorEraseProcessor(img.iplImage(), 0, 50, 0, 50, 0, 100, 10));
		//img.addProcessor(new CloseProcessor(3));
		img.addProcessor(new ThicknessProcessor(5));
		img.addProcessor(new DilateProcessor(3));
		img.addProcessor(new CloseProcessor(3));

		//10/90
		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000, 100, 5000);
		FeatureLinker linker = new DistanceBasedFeatureLinker(1, 0.05, 10);
		
		img.findCharacters(detector, linker);
		img.save("TestbildOut.jpg");
	}
}
