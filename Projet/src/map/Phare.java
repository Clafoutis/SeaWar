package map;

import java.awt.Point;

import org.newdawn.slick.SpriteSheet;

public class Phare extends Case {
	private static final int ID = 2;
	private static final int NB_FRAMES_ANIMATION = 6;
	private static final int DUREE_FRAME = 250;
	
	public Phare(Point position) {
		super(ID, NB_FRAMES_ANIMATION, DUREE_FRAME, position);
	}
}
