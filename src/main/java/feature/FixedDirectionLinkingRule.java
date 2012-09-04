package feature;

import math.Box2D;
import math.Rotation2D;
import math.Validator;
import math.Vector2D;

public class FixedDirectionLinkingRule extends LinkingRule {
	// parameters
	private Rotation2D matrix;
	private Validator<Integer> width;
	private Validator<Integer> height;

	public FixedDirectionLinkingRule(double angle,
			Validator<Integer> width, Validator<Integer> height) {
		this.matrix = new Rotation2D(angle);
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		Vector2D min0;
		Vector2D max0;
		Vector2D min1;
		Vector2D max1;
		
		{
			Vector2D center = f0.position().center(f1.position());
			
			Box2D box = f0.box();
			Vector2D[] bounds = Vector2D.bounds(matrix.rotate(box.corners, center));
			min0 = bounds[0];
			max0 = bounds[1];
			
			box = f1.box();
			bounds = Vector2D.bounds(matrix.rotate(box.corners, center));
			min1 = bounds[0];
			max1 = bounds[1];
		}
		
		int w;
		if(max0.x <= min1.x || max1.x <= min0.x) {
			w = (int) Math.round(Math.max(min0.x, min1.x) - Math.min(max0.x, max1.x));
		} else {
			w = 0;
		}
		
		if(!width.isValid(w)) {
			return false;
		}

		int h;
		if(max0.y <= min1.y || max1.y <= min0.y) {
			h = 0;
		} else {
			h = (int) Math.round(Math.min(max0.y, max1.y) - Math.max(min0.y, min1.y)); 
		}

		if (!height.isValid(h)) {
			return false;
		}

		return true;
	}
}
