package map;

import java.awt.Point;

import org.newdawn.slick.SpriteSheet;

/**
 * Case sp�cifique repr�sentant la Terre (cf. classe Case pour plus d'informations)
 */
public class Terre extends Case {
	public static final int ID = 1;
	private static final int NB_FRAMES_ANIMATION = 1;
	private static final int DUREE_FRAME = 100;
	
	/**
	 * Cr��e une case Terre.
	 * @param position La position de la case (dans la fen�tre) relative � l'origine de la map.
	 */
	public Terre(Point position) {
		super(ID, NB_FRAMES_ANIMATION, DUREE_FRAME, position);
	}
}
