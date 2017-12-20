package map;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

public abstract class Case extends Animation {
	private int id;
	private Point position;
	
	public Case(int idCase, int nbFrames, int dureeFrame, Point _position) {
		position = _position;
		id = idCase;

		for (int i = 0; i < nbFrames; i++) {
            this.addFrame(Map.getInstance().getSpriteSheet().getSprite(i, idCase), dureeFrame);
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
		this.draw(Map.getInstance().getPosition().x + position.x, Map.getInstance().getPosition().y + position.y);
	}
}
