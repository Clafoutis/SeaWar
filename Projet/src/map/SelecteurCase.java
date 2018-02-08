package map;

import java.awt.Point;

import joueur.Navire;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import utility.FileUtility;

/**
 * Element qui met en surbrillance les contours d'une Case. Utile pour montrer la sélection d'une case courante.
 */
public class SelecteurCase {
	public static final String DOSSIER_SPRITE = "resources/sprites/";
	public static final String FICHIER_SPRITE_SHEET_SELECTEUR = "spriteSheetSelecteur.png";
	private static final int LONGUEUR_COTE_TUILE = 64;
	
	private SpriteSheet spriteSheetSelecteur;
	private Animation selecteurs[];
	private boolean visible = true;
	private int idCaseSelectionnee = 0;
	private Point position = new Point(0, 0);
	
	/**
	 * Créée un sélécteur de case positionné aux coordonnées (0, 0) par rapport à l'origine de la map.
	 * @throws SlickException si le SpriteSheet du sélecteur n'a pas pu se charger (nom du fichier erroné, ...)
	 */
	public SelecteurCase() throws SlickException {
		selecteurs = new Animation[4];
		spriteSheetSelecteur = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_SELECTEUR, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
		for (int i = 0; i < selecteurs.length; i++) {
			selecteurs[i] = new Animation();
			selecteurs[i].addFrame(spriteSheetSelecteur.getSprite(0, i), 500);
		}
	}
	
	/**
	 * Renvoie l'Id courant représentant la couleur du sélecteur (qui peut correspondre par exemple l'Id de la Case à laquelle est associée la couleur du sélecteur).
	 * @return L'Id courant représentant la couleur du sélecteur (qui peut correspondre par exemple l'Id de la Case à laquelle est associée la couleur du sélecteur)
	 * - 0 pour un selecteur cyan (peut correspondre à la case Océan) <br>
	 * - 1 pour un selecteur marron-beige (peut correspondre à la case Terre) <br>
	 * - 2 pour un sélecteur blanc (peut correspondre à la case Phare) <br>
	 * - 3 pour un sélecteur rouge (ne correspond pas à un type de case mais peut tout de même être utile)
	 */
	public int getIdCaseSelectionnee() {
		return idCaseSelectionnee;
	}
	
	/**
	 * Modifie l'Id courant représentant la couleur du sélecteur (qui peut correspondre par exemple l'Id de la Case à laquelle est associée la couleur du sélecteur).
	 * @param _idCaseSelectionnee Le nouvel Id représentant la couleur du sélecteur (qui peut correspondre par exemple l'Id de la Case à laquelle est associée la couleur du sélecteur)
	 * - 0 pour un selecteur cyan (peut correspondre à la case Océan) <br>
	 * - 1 pour un selecteur marron-beige (peut correspondre à la case Terre) <br>
	 * - 2 pour un sélecteur blanc (peut correspondre à la case Phare) <br>
	 * - 3 pour un sélecteur rouge (ne correspond pas à un type de case mais peut tout de même être utile)
	 */
	public void setIdCaseSelectionnee(int _idCaseSelectionnee) {
		idCaseSelectionnee = _idCaseSelectionnee;
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
			selecteurs[idCaseSelectionnee].draw(Map.getInstance().getPosition().x + position.x, 
					Map.getInstance().getPosition().y + position.y,
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleX(),
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleY());
		}
	}
	
	/**
	 * Dessine le sélecteur de case sur la fenetre, dans le référentiel de la fenetre, sans prendre en compte l'échelle
	 */
	public void drawAbsolu() {
		if (isSelecteurVisible()) {
			selecteurs[idCaseSelectionnee].draw(position.x, position.y);
		}
	}
}