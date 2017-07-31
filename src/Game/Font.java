package Game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Font {
	
	private SpriteSheet sheet;
	
	public Font(SpriteSheet sheet) {
		this.sheet = sheet;
	}
	
	private BufferedImage getLetter(char c) {
		int n = (int)c;
		if(n >= 65 && n <= 90 ) return sheet.getSprite(n - 65);
		if(n >= 48 && n <= 57) return sheet.getSprite(n - 8);
		if(n == 58) return sheet.getSprite(6, 3, 32);
		if(n == 46) return sheet.getSprite(5, 3, 32);
		if(n == 37) return sheet.getSprite(3, 4, 32);
		else return null;
	}
	
	public void write(String text, int x, int y, Graphics g) {
		int increment = 0;
		text = text.toUpperCase();
		for(int i = 0; i < text.length(); i++) {
			//if(text.charAt(i) == 32) increment -= 10;
			g.drawImage(this.getLetter(text.charAt(i)), x + increment, y, null);
			increment += 20;
		}
	}
	public void writeTextBox(String text, Graphics g) {
		if(text == "yes".toLowerCase() || text == "ok".toLowerCase()) this.write(text, 910, 150, g);
		else if(text == "no".toLowerCase()) this.write(text, 270, 150, g);
		else { 
			String[] arr = text.split(",");
			int i = 0;
			for(String s : arr) {
				this.write(s, 225 + 30, 15 + 30 + i * 40 , g);
				i++;
			}
			
		}
	}
}
