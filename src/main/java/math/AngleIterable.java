package math;

import java.util.Iterator;

public class AngleIterable implements Iterable<Double> {
	private int numAngles;
	
	public AngleIterable(int numAngles) {
		this.numAngles = numAngles;
	}

	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < numAngles;
			}

			@Override
			public Double next() {
				double value = i * Math.PI / (double) numAngles;
				i++;
				return value;
			}

			@Override
			public void remove() {
			}
		};
	}
	
}
