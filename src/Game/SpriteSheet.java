package Game;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {

	public BufferedImage sheet;
	
	public SpriteSheet(String path) {
		try {
			sheet = ImageIO.read(Class.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public BufferedImage getSprite(int x, int y, int spriteSize) {
		BufferedImage ref = sheet.getSubimage(x * spriteSize, y * spriteSize, spriteSize, spriteSize);
		return ref;
	}
	public BufferedImage getSprite(int x, int y, int spriteSizeX, int spriteSizeY) {
		BufferedImage ref = sheet.getSubimage(x * spriteSizeX, y * spriteSizeY, spriteSizeX, spriteSizeY);
		return ref;
	}
	
	public BufferedImage getSprite(int n) {
	    int x = n%8;
		int y = n/8;
		BufferedImage ref = this.getSprite(x, y, 32, 32);
		return ref;
	}	
}
