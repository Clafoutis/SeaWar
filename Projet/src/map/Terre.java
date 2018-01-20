package map;

import java.awt.Point;

import org.newdawn.slick.SpriteSheet;

public class Terre extends Case {
	public static final int ID = 1;
	private static final int NB_FRAMES_ANIMATION = 1;
	private static final int DUREE_FRAME = 100;
	
	public Terre(Point position) {
		super(ID, NB_FRAMES_ANIMATION, DUREE_FRAME, position);
	}
}
