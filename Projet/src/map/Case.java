package map;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

public abstract class Case extends Animation {
	private int id;
	private Point origine;
	private Point position;
	
	public Case(SpriteSheet spriteSheet, int idCase, int nbFrames, int dureeFrame, Point _origine, Point _position) {
		origine = _origine;
		position = _position;
		id = idCase;

		for (int i = 0; i < nbFrames; i++) {
            this.addFrame(spriteSheet.getSprite(i, idCase), dureeFrame);
        }
		this.setCurrentFrame((int)(Math.random() * nbFrames));
	}
	
	public int getId() {
		return id;
	}
	
	public Point getPosition() {
		return (Point) position.clone();
	}
	
	public void setPosition(Point _position) {
		position = _position;
	}
	
	public void move(int _x, int _y) {
		position.x += _x;
		position.y += _y;
	}
	
	@Override
	public void draw() {
		this.draw(origine.x + position.x, origine.y + position.y);
	}
}
