package GameObject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Game.Boundaries;
import Game.Vector;

public class GameObject {
		
	protected Vector position;
	protected BufferedImage currentSprites;
	
	public GameObject(Vector pos) {
		this.position = pos;
	}
	public GameObject(Vector pos, BufferedImage sprite) {
		this.position = pos;
		this.currentSprites = sprite;
	}
	
	public void render(Graphics g) {
		g.drawImage(this.currentSprites, (int)this.position.x, (int)this.position.y, null);
	}
	
	public float getX() { return this.position.x; }
	public float getY() { return this.position.y; }
}
