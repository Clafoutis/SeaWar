package map;

import java.awt.Point;

import org.newdawn.slick.SpriteSheet;

/**
 * Case sp�cifique repr�sentant l'Oc�an (cf. classe Case pour plus d'informations)
 */
public class Ocean extends Case {
	public static final int ID = 0;
	private static final int NB_FRAMES_ANIMATION = 1;
	private static final int DUREE_FRAME = 100;
	
	/**
	 * Cr��e une case Oc�an.
	 * @param position La position de la case (dans la fen�tre) relative � l'origine de la map.
	 */
	public Ocean(Point position) {
		super(0, NB_FRAMES_ANIMATION, DUREE_FRAME, position);
	}
}
