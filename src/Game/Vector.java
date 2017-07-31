package Game;

public class Vector {

	public float x;
	public float y;
	
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void add(Vector vec) {
		this.x += vec.x;
		this.y += vec.y;
	}
	public void subtract(Vector vec) {
		this.x -= vec.x;
		this.y -= vec.y;
	}
	public void multply(float n) {
		this.x *= n;
		this.y *= n;
	}
	public void setMagnitude(float mag) {
		this.normalize();
		this.multply(mag);
	}
	
	public float magnitude() {
		return (float) Math.sqrt(x*x + y*y);
	}
	public void normalize() {
		float mag = this.magnitude();
		this.x /= mag;
		this.y /= mag;
	}
	public float getAngle(Vector v) {
		double rad = Math.acos((this.x * v.x + this.y * v.y) / (this.magnitude() * v.magnitude()));
		return (float)Math.toDegrees(rad); 
	}
}