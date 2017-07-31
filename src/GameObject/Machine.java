package GameObject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import Game.Boundaries;
import Game.Game;
import Game.Vector;

public class Machine extends GameObject {

	public static enum MachineType { ENERGY, OXYGEN, WATER, FOOD, REFINERY, ENGINE };
	
	
	public MachineType machineType; 
	public boolean isBuilt;
	public boolean isBroken;
	public boolean isWorked;
	
	private Boundaries bounds;
	
	
	public Machine(Vector pos, MachineType type, boolean show) {
		super(pos);
		this.machineType = type;
		this.chooseSprite();
		this.isBuilt = show;
		this.isBroken = false;
		this.isWorked = false;
		if(this.machineType == MachineType.ENGINE) this.bounds = new Boundaries(new Vector(this.position.x - 10, this.position.y), 42, 64);
		else this.bounds = new Boundaries(new Vector(this.position.x - 10, this.position.y - 10), 50, 50);
	}
	
	
	private void chooseSprite() {
		switch(machineType) {
		case ENERGY:
			this.currentSprites = Game.machines.getSprite(1, 0, 32);
			break;
		case ENGINE:
			this.currentSprites = Game.machines.getSprite(0, 0, 32, 64);
			break;
		case FOOD:
			this.currentSprites = Game.machines.getSprite(3, 0, 32);
			break;
		case OXYGEN:
			this.currentSprites = Game.machines.getSprite(2, 0, 32);
			break;
		case REFINERY:
			this.currentSprites = Game.machines.getSprite(4, 0, 32);
			break;
		case WATER:
			this.currentSprites = Game.machines.getSprite(5, 0, 32);
			break;
		default:
			break;
		}
	}
	public Boundaries getBounds() { return this.bounds; }
	public Vector getPos() { return this.position; }
}
