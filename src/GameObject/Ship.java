package GameObject;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import Game.Boundaries;
import Game.Game;
import Game.Vector;

public class Ship extends GameObject {

	public static Vector[] doors = { new Vector(1047, 374), new Vector(955, 265), new Vector(728, 265), new Vector(728, 405), new Vector(955, 405) };
	public BufferedImage[] broken = { Game.machines.getSprite(0, 2, 32), Game.machines.getSprite(1, 2, 32) };
	
	public int oxygen;
	public int water;
	public int food;
	public int energy;
	public boolean isJumping;
	public boolean hasJumped;
	public int jumpTimer;
	public int miningNum;
	public int fuelMissions = 2;
	public int fuel;
	public int refiendFuel;



	
	public Machine[] machines;
	
	private int lastUpdateTimer = 0;
	private Boundaries table;
	private Human[] humans;
	
		
	public static int getRoom(Vector pos) {
		if(pos.y < 265 && pos.x < 850) return 1;
		if(pos.y > 400 && pos.x < 465) return 6;
		if(pos.y < 265 && pos.x > 860) return 2;
		if(pos.y > 405 && pos.x < 850) return 3;
		if(pos.y > 405 && pos.x > 860) return 4;
		if(pos.x > 1047) return 5;
		else return 0;
	}
	
	public Ship(Vector pos, BufferedImage sprite, Human[] humans) {
		super(pos, sprite);
		this.oxygen = 100;
		this.water = 100;
		this.food = 100;
		this.energy = 100;
		this.machines = new Machine[12];
		loadMachines();
		this.table = new Boundaries(new Vector(1110, 301), 64, 64);
		this.humans = humans;
		this.fuel = 0;
	}
	
	private void loadMachines() {
		this.machines[0] = new Machine(new Vector(1008,112), Machine.MachineType.ENGINE, true);
		this.machines[1] = new Machine(new Vector(1008,495), Machine.MachineType.ENGINE, true);
		this.machines[2] = new Machine(new Vector(256,272), Machine.MachineType.OXYGEN, true);
		this.machines[3] = new Machine(new Vector(256,368), Machine.MachineType.OXYGEN, true);
		this.machines[4] = new Machine(new Vector(816,560), Machine.MachineType.FOOD, true);
		this.machines[5] = new Machine(new Vector(704,517), Machine.MachineType.WATER, true);
		this.machines[6] = new Machine(new Vector(864,560), Machine.MachineType.ENERGY, true);
		this.machines[7] = new Machine(new Vector(864,80), Machine.MachineType.REFINERY, true);
		this.machines[8] = new Machine(new Vector(816,224), Machine.MachineType.FOOD, false);
		this.machines[9] = new Machine(new Vector(640,224), Machine.MachineType.WATER, false);
		this.machines[10] = new Machine(new Vector(864,416), Machine.MachineType.REFINERY, false);
		this.machines[11] = new Machine(new Vector(864,224), Machine.MachineType.ENERGY, false);

	}

	public void update(int peopleNum, boolean isDameged, int timer) {
		if(timer - this.lastUpdateTimer >= 60 * 30) { // every 30 seconds
			updateMachines();
			energy -= 10;
			oxygen -= 4;
			water -= 8;
			food -= 6;
			if(food >= 0 && water >= 0 && oxygen >= 0) this.lastUpdateTimer = timer;
		}
		if(oxygen <= 0) Game.gameOver = true;
		if(water <= 0 && timer - this.lastUpdateTimer >= 60 * 60 * 1) Game.gameOver = true;
		if(food <= 0 && timer - this.lastUpdateTimer >= 60 * 60 * 2) Game.gameOver = true;
		updateJump();
	}
	
	private void updateMachines() {
		for(Machine m : machines) {
			if(!m.isBroken && m.isBuilt) {
				if(m.machineType == Machine.MachineType.ENERGY) {
					if(m.isWorked && this.refiendFuel > 10) {
						this.energy += 6;
						this.refiendFuel -= 5;
					}
					else this.energy +=4;
				}
				if(m.machineType == Machine.MachineType.OXYGEN) {
					if(m.isWorked) {
						this.oxygen += 3;
						this.energy -= 1;
					}
					else this.oxygen += 1;
				}
				if(m.machineType == Machine.MachineType.FOOD) {
					if(m.isWorked) {
						this.food += 4;
						this.energy -= 1;
					}
					else this.food += 2;
				}
				if(m.machineType == Machine.MachineType.WATER) {
					if(m.isWorked) {
						this.water += 5;
						this.energy -= 1;
					}
					else this.water += 4;
				}
				if(m.machineType == Machine.MachineType.REFINERY) {
					if(this.fuel > 10) {
						if(m.isWorked) {
							this.refiendFuel += 10;
							this.energy -= 4;
							this.fuel -= 10;
						}
						else {
							this.refiendFuel += 5;
							this.energy -= 1;
							this.fuel -= 10;
						}
					}
				}
			}	
		}
	}

	public void fuelReturn() {
		if(fuelMissions > 0) {
			Random r = new Random(System.currentTimeMillis());
			switch(Game.systemType) {
			case 1:
				fuel += r.nextInt(25) + 75;
				break;
			case 2:
				fuel += r.nextInt(25) + 75;
				break;
			case 3:
				fuel += r.nextInt(10) + 35;
				break;
			case 4:
				fuel += 0;
				break;
			case 5:
				fuel += 0;
				break;
			case 6:
				fuel += r.nextInt(0) + 20;
				break;
			case 7:
				fuel += r.nextInt(0) + 15;
				break;
			}
			this.fuelMissions--;
		}
	}
	
	private void updateJump() {
		if(this.jumpTimer > 0) jumpTimer--;
		else {
			for(int i = 0; i < humans.length; i++) {
				if(this.humans[i].type == 5 && this.humans[i].getBounds().intersectsBoundries(this.table)) {
					this.isJumping = true;
					this.jumpTimer = 60 * 5;
				}
			}
		}
		
	}

	public void render(Graphics g) {
		super.render(g);
		for(int i = 0; i < this.machines.length; i++) {
			if(this.machines[i].isBuilt) this.machines[i].render(g);
			if(this.machines[i].isBroken) {
				g.drawImage(broken[0], (int)this.machines[i].getPos().x, (int)this.machines[i].getPos().y, null);
			}
		}
	}

	public void addFuel(int fuel) { this.fuel += fuel; }
}
