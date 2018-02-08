package map;

import java.awt.Point;

import org.newdawn.slick.SpriteSheet;

/**
 * Case spécifique représentant la Terre (cf. classe Case pour plus d'informations)
 */
public class Terre extends Case {
	public static final int ID = 1;
	private static final int NB_FRAMES_ANIMATION = 1;
	private static final int DUREE_FRAME = 100;
	
	/**
	 * Créée une case Terre.
	 * @param position La position de la case (dans la fenêtre) relative à l'origine de la map.
	 */
	public Terre(Point position) {
		super(ID, NB_FRAMES_ANIMATION, DUREE_FRAME, position);
	}
}
