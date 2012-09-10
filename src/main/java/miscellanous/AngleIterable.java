package miscellanous;

import java.util.Iterator;

public class AngleIterable implements Iterable<Double> {
	private int numAngles;
	private double min;
	private double step;
	
	public AngleIterable(int numAngles) {
		this.numAngles = numAngles;
		this.min = 0;
		this.step = Math.PI / numAngles;
	}
	
	public AngleIterable(int numAngles, Interval<Double> range) {
		this.numAngles = numAngles;
		this.min = range.getMin();
		this.step = range.getMax() - min;
		if(numAngles > 1) {
			this.step /= numAngles - 1;
		}
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
				double value = min + i * step;
				i++;
				return value;
			}

			@Override
			public void remove() {
			}
		};
	}
	
}
