package joueur;

import java.awt.Point;
import map.Map;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Element qui met en surbrillance une Case. Utile pour montrer la sélection d'une case pour tirer.
 */
public class SelecteurCaseTir {
	public static final String DOSSIER_IMAGE = "resources/images/";
	private static final int LONGUEUR_COTE_TUILE = 64;

	private Animation selecteur;
	private boolean visible = false;
	private Point position = new Point(0, 0);
	
	/**
	 * Créé un sélécteur de case positionné aux coordonnées (0, 0) par rapport à l'origine de la map.
	 * @throws SlickException si le SpriteSheet du sélecteur n'a pas pu se charger (nom du fichier erroné, ...)
	 */
	public SelecteurCaseTir() throws SlickException {
		selecteur = new Animation();
		selecteur.addFrame(new Image(DOSSIER_IMAGE + "crosshair.png"), 500);
	}

	/**
	 * Renvoie la position du sélecteur de case par rapport à l'origine de la Map.
	 * @return la position du sélecteur de case par rapport à l'origine de la Map
	 */
	public Point getPosition() {
		return (Point) position.clone();
	}
	
	/**
	 * Modifie la position du sélecteur de case par rapport à l'origine de la Map.
	 * @param _position la nouvelle position du sélecteur de case par rapport à l'origine de la Map
	 */
	public void setPosition(Point _position) {
		position = _position;
	}
	
	/**
	 * Renvoie true si le sélecteur est visible ou false sinon.
	 * @return True si le sélecteur est visible ou false sinon
	 */
	public boolean isSelecteurVisible() {
		return visible;
	}
	
	/**
	 * Masque ou rend visible le sélecteur.
	 * @param _visible True pour rendre le sélecteur visible ou false pour le masquer
	 */
	public void setSelecteurVisible(boolean _visible) {
		visible = _visible;
	}
	
	/**
	 * Dessine le sélecteur de case sur la fenêtre, dans le référentiel de la map, en prenant en compte l'échelle
	 */
	public void draw() {
		if (isSelecteurVisible()) {
			selecteur.draw(Map.getInstance().getPosition().x + position.x,
					Map.getInstance().getPosition().y + position.y,
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleX(),
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleY());
		}
	}
}
