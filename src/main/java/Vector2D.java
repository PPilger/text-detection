public class Vector2D {
	public double x;
	public double y;

	public Vector2D() {

	}

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D add(Vector2D vector) {
		return new Vector2D(x + vector.x, y + vector.y);
	}

	public Vector2D sub(Vector2D vector) {
		return new Vector2D(x - vector.x, y - vector.y);
	}

	public Vector2D mul(double scalar) {
		return new Vector2D(x * scalar, y * scalar);
	}

	public Vector2D div(double scalar) {
		return mul(1 / scalar);
	}

	public double dot(Vector2D vector) {
		return x * vector.x + y * vector.y;
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public double distance(Vector2D point) {
		double x = point.x - this.x;
		double y = point.y - this.y;
		return Math.sqrt(x * x + y * y);
	}

	public Vector2D normalize() {
		return div(length());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2D other = (Vector2D) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("[x=%.2f, y=%.2f]", x, y);
	}
}
