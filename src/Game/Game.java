package Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import GameObject.Human;
import GameObject.Ship;

public class Game  extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 1280;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static String NAME;
	public static GameState gameState;
	public static SpriteSheet humans = new SpriteSheet("/SpriteSheets/Humans.png");
	public static SpriteSheet machines = new SpriteSheet("/SpriteSheets/Machines.png");
	public static Font font = new Font(new SpriteSheet("/Font.png"));
	public static BufferedImage[] gameBackgrounds = { loadImage("/Backgrounds/Background.png"),
			loadImage("/Backgrounds/jumpBackground.png"),
			loadImage("/Backgrounds/jumpBackground2.png")
			
	};

	
	public static BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	
	private JFrame frame;
	private Thread gameThread;
	private boolean running;
	private Input input;
	private BufferedImage texyBox = Game.loadImage("/textBox.png");
	private BufferedImage transparentLayer = Game.loadImage("/transparnt.png");
	private boolean won = false;

	
	//game members
	public static boolean gameOver = false;
	public static int systemType;
	
	private int timer;
	private Ship ship;
	private Human[] crew;
	private boolean textBox = false;
	private Boundaries textBoxYes = new Boundaries(new Vector(910, 150), 110, 32);
	private Boundaries textBoxNo = new Boundaries(new Vector(270, 150), 80, 32);
	private int jumpTimer = 0;
	private String arrivalMessage;
	private int humanSelected = -1;
	private Boundaries topButton;
	private Boundaries bottomButton;
	
	public Game(String name) {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		frame = new JFrame();
		frame.setResizable(false);
		NAME = name;
		frame.setName(NAME);
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.requestFocus();
		input = new Input();
		addKeyListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		this.topButton = new Boundaries(new Vector(550, 200), 200, 32);
		this.bottomButton = new Boundaries(new Vector(570, 300), 100, 32);
		gameState = GameState.GAME;
		inisialize();
	}
	
	private void inisialize() {
		timer = 0;
		this.crew = new Human[5];
		Random r = new Random(System.currentTimeMillis());
		crew[0] = new Human(new Vector(1100, 320), 5);
		crew[1] = new Human(new Vector(800, 320), r.nextInt(4));
		crew[2] = new Human(new Vector(747, 200), r.nextInt(4));
		crew[3] = new Human(new Vector(760, 470), r.nextInt(4));
		crew[4] = new Human(new Vector(940, 460), r.nextInt(4));
		this.ship = new Ship(new Vector(0, 0), loadImage("/ship.png"), this.crew);
	}

	public synchronized void start() {
		this.gameThread = new Thread(this, "Game");
		running = true;
		gameThread.start();
	}
	public synchronized void stop() {
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		long lastUpdateTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		int frames = 0, updates = 0;
		int desiredUPS = 60;
		double ns = 1000000000.0/desiredUPS;
		double delta = 0.0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastUpdateTime) / ns;
			lastUpdateTime = System.nanoTime();
			while(delta >= 1){
				update();
				updates++;
				delta--;
			}
			render();
			frames++;
			if(System.currentTimeMillis() - timer >= 1000){
				timer += 1000;
				frame.setTitle(NAME + " | " + "UPS : " + updates + " | FPS: " + frames);
				updates = 0;
				frames = 0;
			}
		}
	}
	
	private void update() {
		if(gameState == GameState.END) {
			if(timer >= 60 * 60 * 20) {
				updateEnd(true);
				this.won = true;
			}
			else updateEnd(false);
		}
		if(input.Keys[KeyEvent.VK_ESCAPE]) {
			gameState = GameState.PAUSED;
			
		}
		if(Game.gameOver == true) gameState = GameState.END;
		else if(gameState == GameState.PAUSED) updatePaused();
		else {
			this.humanSelected = -1;
			if(!textBox) {
				if(ship.hasJumped) this.jumpTimer--;
				if(jumpTimer == 1) {
					this.arrivalMessage = getArrivalMessage();
					this.textBox = true;
					this.ship.energy -= 40;
					this.ship.fuelMissions = 2;
					if(systemType == 7) {
						Random r = new Random(System.currentTimeMillis());
						int n = r.nextInt(11);
						while(!ship.machines[n].isBuilt) { n = r.nextInt(11); }
						ship.machines[n].isBroken = true;
						while(!ship.machines[n].isBuilt && ship.machines[n].isBroken) { n = r.nextInt(11); }
						ship.machines[n].isBroken = true;
						n = r.nextInt(1000);
						if(n % 2 == 0) {
							n = r.nextInt(11);
							while(!ship.machines[n].isBuilt) { n = r.nextInt(11); }
							ship.machines[n].isBroken = true;
						}
					}
					if(systemType == 6) {
						Random r = new Random(System.currentTimeMillis());
						int n = r.nextInt(1000);
						if(n % 2 == 0) {
							n = r.nextInt(11);
							while(!ship.machines[n].isBuilt) { n = r.nextInt(11); }
							ship.machines[n].isBroken = true;
						}
					}
				}
				ship.update(5, false, timer);
				if(ship.isJumping == true) textBox = true;;
				for(int i = 0; i < this.crew.length; i++) {
					crew[i].update(this.timer, this.input, this.ship, this.crew);
					if(crew[i].isSelected) this.humanSelected = i;
					if(crew[i].wasSelected) {
						for(int j = 0; j < this.crew.length; j++) {
							if(j != i && crew[j].isSelected) crew[j].isSelected = false;
						}
					}
				}
				timer++;
			}
			else {
				if(ship.isJumping) {
					if(input.mouseB == 1 && this.textBoxYes.intersectsPoint(input.mouseX, input.mouseY)) {
						ship.isJumping = false;
						ship.hasJumped = true;
						this.textBox = false;
						input.mouseB = -1;
						this.jumpTimer = 60 * 5;
					}
					if(input.mouseB == 1 && this.textBoxNo.intersectsPoint(input.mouseX, input.mouseY)) {
						this.textBox = false;
						ship.isJumping = false;
						input.mouseB = -1;
					}
				}
				else if(input.mouseB == 1 && this.textBoxYes.intersectsPoint(input.mouseX, input.mouseY)) { 
					this.textBox = false;
					input.mouseB = -1;
				}
			}
		}
	}

	
	private String getArrivalMessage() {
		String message1 = "we have arrived at a system, which seems rather rich with fuel";
		String message2 = "this new system, seems to have a lot of fuel";
		String message3= "we have arrived at a system, which seems to have, small amounts of fuel";
		String message4 = "we cannot detect, any fuel in this system";
		String message5 = "our ship has collided with a meteor, but no damege was done";
		String message6 = "our ship has collided with a meteor, and we sustained some damege";
		String message7 = "our ship has collided with a meteor, that leaked some fuel, so we gathered it";
		String message = null;
		Random r = new Random(System.currentTimeMillis());
		int rand = r.nextInt(1000);

		if(rand > 600) {
			message = message3; 
			systemType = 3;
		}
		else if(rand > 300) {
			message = message4;
			systemType = 4;
		}
		else if(rand > 100) {
			if(rand % 2 == 0) {
				message = message1;
				systemType = 1;
			}
			else {
				message = message2;
				systemType = 2;
			}
		}
		else if(rand > 50) {
			message = message5;
			systemType = 5;
		}
		else {
			if(rand % 2 == 0) {
				message = message6;
				systemType = 6;
				this.ship.addFuel(35);
			}
			else {
				message = message7;
				systemType = 7;
			}
		}
		return message;
	}


	private void updatePaused() {
		if(this.input.mouseB == 1) {
			if(this.topButton.intersectsPoint(this.input.mouseX, this.input.mouseY)) {
				this.inisialize();
				gameState = GameState.GAME;
			}
			if(this.bottomButton.intersectsPoint(this.input.mouseX, this.input.mouseY)){
				System.exit(0);
			}
		}
	}
	
	private void updateEnd(boolean won) {
		if(this.input.mouseB == 1) {
			if(this.topButton.intersectsPoint(this.input.mouseX, this.input.mouseY)) {
				this.inisialize();
				gameState = GameState.GAME;
			}
			if(this.bottomButton.intersectsPoint(this.input.mouseX, this.input.mouseY)){
				System.exit(0);
			}
		}
	}
	

	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null); 
		
		
		
		if(this.jumpTimer > 1)  g.drawImage(Game.gameBackgrounds[1], 0, 0, null);
		else g.drawImage(Game.gameBackgrounds[0], 0, 0, null);
		ship.render(g);
		for(Human h : crew) {
			h.render(g);
		}
		
		//g.drawRect(726 - 20, 405 - 40, 40, 40);
		//System.out.println(v.getAngle(new Vector(1, 0)));
		
		if(this.textBox) {
			if (this.ship.isJumping) {
				g.drawImage(this.texyBox, 225, 15, null);
				font.writeTextBox("are you sure you want to jump ?", g);
				font.writeTextBox("yes", g);
				font.writeTextBox("no", g);
			}
			if(this.jumpTimer == 1) {
				g.drawImage(this.texyBox, 225, 15, null);
				font.writeTextBox(this.arrivalMessage, g);
				font.writeTextBox("ok", g);
			}
		}
		
		font.write("Energy: " + this.ship.energy + "%", 0, 0, g);
		font.write("Oxygen: " + this.ship.oxygen + "%", 0, 50, g);
		font.write("Water: " + this.ship.water + "%", 0, 100, g);
		font.write("Food: " + this.ship.food + "%", 0, 150, g);
		if(this.humanSelected >= 0) { 
			font.write("Task: " + this.crew[humanSelected].currentTask, 0, 600, g);
			font.write("Stamina: " + this.crew[humanSelected].stamina + "%", 0, 650, g);
 
		}
		else {
			font.write("Fuel: " + this.ship.fuel, 0, 600, g);
			font.write("Refiend: " + this.ship.refiendFuel, 0, 650, g);
		}
		if(gameState == GameState.PAUSED) renderPaused(g);
		if(gameState == GameState.END) renderEnd(g);
		
		g.dispose();
		bs.show();
	}
	
	
	private void renderPaused(Graphics g) {
		g.drawImage(this.transparentLayer, 0, 0, null);
		font.write("Restart", 550, 200, g);
		font.write("Exit", 570, 300, g);
	}
	
	private void renderEnd(Graphics g) {
		g.drawImage(gameBackgrounds[0], 0, 0, null);
		if(won) {
			font.write("congratulations you have beat the game", 250, 100, g);
		}
		else {
			font.write("no mattar better luck next time", 280, 100, g);
		}
		
		font.write("Restart", 550, 200, g);
		font.write("Exit", 570, 300, g);
	}

	public static BufferedImage loadImage(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(Class.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	

	public static void main(String[] args) {
		Game game = new Game("Running Out Of Power");
		game.start();
	}
	
}
