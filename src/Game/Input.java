package Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Input implements KeyListener, MouseListener, MouseMotionListener {

	public boolean[] Keys = new boolean[65536];
	public boolean up = false, down = false, left = false, right = false, nokey = true;
	public int mouseX = -1, mouseY = -1, mouseB = -1;
	
	public Input(){	}
	
	public void update(){
		up = Keys[KeyEvent.VK_W];
		down = Keys[KeyEvent.VK_S];
		left = Keys[KeyEvent.VK_A];
		right = Keys[KeyEvent.VK_D];
	}
	
	public void keyPressed(KeyEvent e) {
		Keys[e.getKeyCode()] = true;
		nokey = false;		
	}

	public void keyReleased(KeyEvent e) {
		Keys[e.getKeyCode()] = false;
		nokey = true;
	}

	public void mousePressed(MouseEvent e) {	
		this.mouseB = e.getButton();
	}

	public void mouseReleased(MouseEvent e) {	
		this.mouseB = -1;
	}

	public void mouseDragged(MouseEvent e) {		
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

	public void mouseMoved(MouseEvent e) {		
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

