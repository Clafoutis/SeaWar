package map;

import java.awt.Point;

import org.newdawn.slick.SpriteSheet;

public class Phare extends Case {
	private static final int ID = 2;
	private static final int NB_FRAMES_ANIMATION = 6;
	private static final int DUREE_FRAME = 250;
	
	public Phare(SpriteSheet spriteSheet, Point origine, Point position) {
		super(spriteSheet, ID, NB_FRAMES_ANIMATION, DUREE_FRAME, origine, position);
	}
}
