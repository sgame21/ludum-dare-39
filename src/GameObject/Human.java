package GameObject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import Game.Vector;
import Game.Boundaries;
import Game.Game;
import Game.Input;

public class Human extends GameObject {

	public static enum Task { SLEEP, MINE, MAINTENANCE, BUILD, FREE, WORK }
	//when changing between move states set the animation timer to the timer.
	public static enum MoveState { REST_EAST, REST_WEST, REST_NORTH, REST_SOUTH, WALK_EAST, WALK_WEST, WALK_NORTH, WALK_SOUTH }
	public static int spriteSize = 32;
	
	public Task currentTask;
	public int type;
	public boolean wasSelected = false;
	public boolean isSelected;
	public int stamina;

	
	private int lastUpdateTimer = 0;
	private int animationTimer = 0;
	private int taskTimer = 0;
	
	private BufferedImage[] sprites = new BufferedImage[14];
	private MoveState moveState;
	private Boundaries bounds;
	private Vector destenation;
	private Vector desiredDest;
	private float speed;
	private Vector velocity;
	private boolean changeMovment;
	private boolean reachDest;
	private Boundaries destBound;
	private boolean isMining;
	private boolean isSleeping;
	private Machine taskMachine;
	private Boundaries sleppElevetor;
	
	public Human(Vector pos, int type) {
		super(pos);
		currentTask = Task.FREE;
		for(int i = 0; i < 14; i++) {
			sprites[i] = Game.humans.getSprite(i, type, Human.spriteSize);
		}
		chooseStartState();
		this.isSelected = false;
		this.setBounds();
		this.speed = 1.5f;
		this.velocity = new Vector(0, 0);
		this.changeMovment = false;
		this.reachDest = true;
		this.stamina = 100;
		this.type = type;
		this.isMining = false;
		this.isSleeping = false;
		this.stamina = 100;
		this.sleppElevetor = new Boundaries(new Vector(803 - 20, 128 - 20), 50, 50);
	}
	
	private void setBounds() {
		this.bounds = new Boundaries(new Vector(this.position.x - 8, this.position.y - 16), 17, 32);
	}
	
	private void chooseStartState() {
		Random r = new Random(System.currentTimeMillis());
		int n = r.nextInt(100);
		if(n < 26)  {
			this.moveState = MoveState.REST_EAST; 
			this.currentSprites = sprites[0];
		}
		else if(n < 51) {
			this.moveState = MoveState.REST_WEST;
			this.currentSprites = sprites[3];
		}
		else if(n < 76) {
			this.moveState = MoveState.REST_NORTH;
			this.currentSprites = sprites[10];
		}
		else if(n < 101) {
			this.moveState = MoveState.REST_SOUTH;
			this.currentSprites = sprites[6];
		}
		else {
			this.moveState = MoveState.REST_EAST;
			this.currentSprites = sprites[0];
		}
	}
	
	public void render (Graphics g) {
		if(!this.isMining && !this.isSleeping) g.drawImage(this.currentSprites, (int)this.position.x - 16, (int)this.position.y - 16, null);
		if(this.isSelected == true) {
			//display the human overlay
		}
	}

	public void update(int timer, Input input, Ship ship, Human[] humans) {
		//update boundaries for wall collision.
		this.wasSelected = false;
		this.setBounds();
		if(input.mouseB == 1 && this.isSelected == false) {
			if(this.bounds.intersectsPoint(input.mouseX, input.mouseY)) {
				this.isSelected = true;
				this.wasSelected = true;
				input.mouseB = -1;
			}
		}
		else if(this.isSelected && input.mouseB == 1) {
			boolean didSelect = false;
			for(int i = 0; i < humans.length; i++) {
				if(humans[i].bounds.intersectsPoint(input.mouseX, input.mouseY)) didSelect = true;
			}
			if(!didSelect) {
				this.desiredDest = new Vector(input.mouseX, input.mouseY);
				this.reachDest = true;
				this.changeMovment = true;
			}
		}
		else if(this.isSelected && input.mouseB == 3) this.isSelected = false;
		updateAI(ship);
		updateMovment(timer);
		updateMoveState(timer);
		if(this.currentTask == Task.FREE && this.stamina <= 0) {
			this.currentTask = Task.SLEEP;
			this.taskTimer = 60 * 10;
			if(stamina < 0) this.taskTimer += 60 * Math.abs(this.stamina);
			this.isSleeping = true;
		}
	}

	private void updateAI(Ship ship) {
		if(this.currentTask == Task.FREE && this.desiredDest != null) this.freeAI(ship);
		if(this.currentTask == Task.MINE) this.mineAI(ship);
		if(this.currentTask == Task.BUILD) this.buildAI(ship);
		if(this.currentTask == Task.MAINTENANCE) this.maintenanceAI(ship);
		if(this.currentTask == Task.WORK) this.workAI();
		if(this.currentTask == Task.SLEEP) this.sleepAI();
	}
	
	private void sleepAI() {
		if(this.taskTimer > 0) {
			this.taskTimer--;
		}
		else {
			this.currentTask = Task.FREE;
			this.isSleeping = false;
		}
	}

	private void workAI() {
		if(this.taskTimer > 0) {
			this.taskTimer--;
		}
		else {
			this.currentTask = Task.FREE;
			this.taskMachine.isWorked = false;
			this.taskMachine = null;
		}
	}

	private void maintenanceAI(Ship ship) {
		if(this.taskTimer > 0) {
			this.taskTimer--;
		}
		else {
			this.currentTask = Task.FREE;
			this.taskMachine.isBroken = false;
			this.taskMachine = null;
			ship.energy -= 10;
		}
	}

	private void buildAI(Ship ship) {
		if(this.taskTimer > 0) {
			this.taskTimer--;
		}
		else {
			this.currentTask = Task.FREE;
			this.taskMachine.isBuilt = true;
			this.taskMachine = null;
			ship.energy -= 10;
		}
	}

	private void mineAI(Ship ship) {
		if(this.reachDest && !this.isMining) {
			int room = Ship.getRoom(this.position);
			if(room == 0) {
				this.destenation = new Vector(440, 420);
				this.desiredDest = this.destenation;
				this.destBound = new Boundaries(new Vector(this.destenation.x - 20, this.destenation.y - 20), 40, 40);
				this.reachDest = false;
			}
			if(room == 6 && this.isMining == false) {
				this.destenation = new Vector(440, 380);
				this.desiredDest = new Vector(640, 300);
				this.destBound = new Boundaries(new Vector(this.destenation.x - 20, this.destenation.y - 20), 40, 40);
				this.currentTask = Task.FREE;
				this.reachDest = false;
				ship.fuelReturn();
			}
		}
		else if(this.destBound != null) {
			if(this.destBound.intersectsPoint((int)this.position.x, (int)this.position.y)) { 
				this.desiredDest = null;
				this.destenation = null;
				this.destBound = null;
				this.reachDest = true;
				this.changeMovment = true;
				this.isMining = true;
				this.taskTimer = 60 * 30;
			}
		}
		else { 
			if(this.taskTimer > 0) this.taskTimer--;
			else {
				this.isMining = false;
			}
		}
	}
	
	private void freeAI(Ship ship) {
		if(this.reachDest) {
			int rPos = Ship.getRoom(this.position);
			int rDest = Ship.getRoom(this.desiredDest);
			if(rDest == 6) {
				this.desiredDest = new Vector(440, 380);
				rDest = 0;
			}
			if(rPos == rDest) {
				this.destenation = this.desiredDest;
				this.changeMovment = true;
				this.reachDest = false;
				this.destBound = new Boundaries(new Vector(this.destenation.x - 20, this.destenation.y - 20), 40, 40);
			}
			else {
				if(rPos == 0) {
					float d = new Vector(Ship.doors[0].x - this.desiredDest.x, Ship.doors[0].y - this.desiredDest.y).magnitude();
					int min = 0;
					for(int i = 1; i < Ship.doors.length; i++) {
						float d2 = new Vector(Ship.doors[i].x - this.desiredDest.x, Ship.doors[i].y - this.desiredDest.y).magnitude();
						if(d > d2) {
							min = i;
							d = d2;
						}
					}
					this.destenation = new Vector(Ship.doors[min].x, Ship.doors[min].y);
					this.changeMovment = true;
					this.reachDest = false;
					this.destBound = new Boundaries(new Vector(this.destenation.x - 20, this.destenation.y - 20), 40, 40);
				} else {
					float d = new Vector(Ship.doors[0].x - this.position.x, Ship.doors[0].y - this.position.y).magnitude();
					int min = 0;
					for(int i = 1; i < Ship.doors.length; i++) {
						float d2 = new Vector(Ship.doors[i].x - this.position.x, Ship.doors[i].y - this.position.y).magnitude();
						if(d > d2) {
							min = i;
							d = d2;
						}
					}
					this.destenation = new Vector(Ship.doors[min].x, Ship.doors[min].y);
					this.changeMovment = true;
					this.reachDest = false;
					this.destBound = new Boundaries(new Vector(this.destenation.x - 20, this.destenation.y - 20), 40, 40);
				}
			}
		}
		if(destBound != null) {
			if(this.destBound.intersectsPoint((int)this.position.x, (int)this.position.y)) {
				this.reachDest = true;
				if(this.destBound.intersectsPoint((int)this.desiredDest.x, (int)this.desiredDest.y)) {
					this.desiredDest = null;
					this.destenation = null;
					this.changeMovment = true;
					if(destBound.intersectsPoint(440, 380) && ship.fuelMissions > 0) this.currentTask = Task.MINE;
					else {
						for(int i = 0; i < ship.machines.length; i++) {
							if(ship.machines[i].getBounds().intersectsPoint((int)this.position.x, (int)this.position.y)) {
								this.taskMachine = ship.machines[i];
								if(!this.taskMachine.isBuilt) {
									this.currentTask = Task.BUILD;
									this.taskTimer = 60 * 5;
								}
								else if(this.taskMachine.isBroken) {
									this.currentTask = Task.MAINTENANCE;
									this.taskTimer = 60 * 5;
								}
								else if(!this.taskMachine.isWorked) {
									this.currentTask = Task.WORK;
									this.taskTimer = 60 * 30;
									this.taskMachine.isWorked = true;
								}
							}
						}
						if(this.sleppElevetor.intersectsPoint((int)this.position.x, (int)this.position.y)) {
							this.currentTask = Task.SLEEP;
							this.taskTimer = 60 * 10;
							if(stamina < 0) this.taskTimer += 60 * Math.abs(this.stamina);
							this.isSleeping = true;
						}
					}
				}
			}
		}	
	}

	private void updateMovment(int timer) {
		if(this.reachDest && this.changeMovment) {
			this.velocity = new Vector(0, 0);
			if(this.moveState == MoveState.WALK_EAST) this.moveState = MoveState.REST_EAST;
			if(this.moveState == MoveState.WALK_WEST) this.moveState = MoveState.REST_WEST;
			if(this.moveState == MoveState.WALK_NORTH) this.moveState = MoveState.REST_NORTH;
			if(this.moveState == MoveState.WALK_SOUTH) this.moveState = MoveState.REST_SOUTH;
		}
		if(this.destenation != null && this.changeMovment) {
			this.velocity = new Vector(this.destenation.x - this.position.x, this.destenation.y - this.position.y);
			this.velocity.setMagnitude(this.speed);
			float angle = velocity.getAngle(new Vector(1, 0));
			if(angle > 45 && angle < 135 && this.destenation.y < this.position.y) {
				this.moveState = MoveState.WALK_NORTH;
				this.currentSprites = this.sprites[12];
				this.animationTimer = timer;
			}
			if(angle > 135) {
				this.moveState = MoveState.WALK_WEST;
				this.currentSprites = this.sprites[5];
				this.animationTimer = timer;
			}
			if(angle > 45 && angle < 135 && this.destenation.y > this.position.y) {
				this.moveState = MoveState.WALK_SOUTH;
				this.currentSprites = this.sprites[9];
				this.animationTimer = timer;
			}
			if(angle < 45) {
				this.moveState = MoveState.WALK_EAST;
				this.currentSprites = this.sprites[2];
				this.animationTimer = timer;
			}
			this.changeMovment = false;
		}
		if(this.position.x < 130 || this.position.x > 1220 || this.position.y < 80 || this.position.y > 595 ||
				this.position.x < 770 && this.position.x > 628 && (this.position.y < 113 || this.position.y > 590)) {
			this.desiredDest = null; 
			this.destenation = null;
			this.reachDest = true;
			this.changeMovment = true;
		}
		
		this.position.x += this.velocity.x;
		this.position.y += this.velocity.y;
	}

	private void updateMoveState(int timer) {
		switch (moveState) {
		case REST_EAST:
			if(timer - animationTimer == 30) {
				this.currentSprites = sprites[0];
			}
			else if(timer - animationTimer == 60)  {
				this.currentSprites = sprites[1];
				animationTimer = timer;
			}
			break;
			
		case REST_NORTH:
			if(timer - animationTimer == 30) {
				this.currentSprites = sprites[10];
			}
			else if(timer - animationTimer == 60)  {
				this.currentSprites = sprites[11];
				animationTimer = timer;
			}
			break;
			
		case REST_SOUTH:
			if(timer - animationTimer == 30) {
				this.currentSprites = sprites[6];
			}
			else if(timer - animationTimer == 60)  {
				this.currentSprites = sprites[7];
				animationTimer = timer;
			}
			break;
			
		case REST_WEST:
			if(timer - animationTimer == 30) {
				this.currentSprites = sprites[3];
			}
			else if(timer - animationTimer == 60)  {
				this.currentSprites = sprites[4];
				animationTimer = timer;
			}
			break;
			
		case WALK_EAST:
			if(timer - animationTimer == 15) {
				this.currentSprites = sprites[0];
			}
			else if(timer - animationTimer == 30)  {
				this.currentSprites = sprites[2];
				animationTimer = timer;
			}
			break;
			
		case WALK_NORTH:
			if(timer - animationTimer == 10) {
				this.currentSprites = sprites[10];
			}
			else if(timer - animationTimer == 20)  {
				this.currentSprites = sprites[12];
			}
			if(timer - animationTimer == 30) {
				this.currentSprites = sprites[10];
			}
			else if(timer - animationTimer == 40)  {
				this.currentSprites = sprites[13];
				animationTimer = timer;
			}
			break;
			
		case WALK_SOUTH:
			if(timer - animationTimer == 10) {
				this.currentSprites = sprites[6];
			}
			else if(timer - animationTimer == 20)  {
				this.currentSprites = sprites[8];
			}
			if(timer - animationTimer == 30) {
				this.currentSprites = sprites[6];
			}
			else if(timer - animationTimer == 40)  {
				this.currentSprites = sprites[9];
				animationTimer = timer;
			}
			break;
			
			case WALK_WEST:
				if(timer - animationTimer == 15) {
					this.currentSprites = sprites[3];
				}
				else if(timer - animationTimer == 30)  {
					this.currentSprites = sprites[5];
					animationTimer = timer;
				}
				break;
		default:
			break;
		}
	}
	
	public Boundaries getBounds() { return this.bounds; }
}
