package map;

import java.awt.Point;

import org.newdawn.slick.SpriteSheet;

public class Ocean extends Case {
	private static final int ID = 0;
	private static final int NB_FRAMES_ANIMATION = 1;
	private static final int DUREE_FRAME = 100;
	
	public Ocean(Point position) {
		super(0, NB_FRAMES_ANIMATION, DUREE_FRAME, position);
	}
}
