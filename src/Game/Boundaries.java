package Game;

public class Boundaries {

	private Vector startPos;
	private int width;
	private int hight;
	
	public Boundaries(Vector pos, int width, int hight) {
		this.startPos = pos;
		this.width = width;
		this.hight = hight;
	}
	
	public boolean intersectsPoint(int x, int y) {
		boolean a = x < this.startPos.x + this.width && x > this.startPos.x;
		boolean b = y < this.startPos.y + this.hight && y > this.startPos.y;
		if(a && b) return true;
		return false;
	}
	
	public boolean intersectsBoundries(Boundaries bounds) {
		boolean isEntersecting = false;
		boolean a = bounds.getStartPos().x < this.startPos.x + this.width && bounds.getStartPos().x > this.startPos.x;
		boolean b = bounds.getStartPos().y < this.startPos.y + this.hight && bounds.getStartPos().y > this.startPos.y;
		if(a && b) { isEntersecting = true; }  
		a = this.startPos.x < bounds.getStartPos().x + bounds.getWidth() && this.startPos.x > bounds.getStartPos().x;
		b = this.startPos.y < bounds.getStartPos().y + bounds.getHight() && this.startPos.y > bounds.getStartPos().y;
		if(a && b) { isEntersecting = true; }	
		return isEntersecting;
	}
	
	public Vector getStartPos() { return this.startPos; }
	public int getWidth() { return this.width; }
	public int getHight() { return this.hight; }
}
