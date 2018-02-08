package map;

import java.awt.Point;

import joueur.Navire;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import utility.FileUtility;

/**
 * Element qui met en surbrillance les contours d'une Case. Utile pour montrer la s�lection d'une case courante.
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
	 * Cr��e un s�l�cteur de case positionn� aux coordonn�es (0, 0) par rapport � l'origine de la map.
	 * @throws SlickException si le SpriteSheet du s�lecteur n'a pas pu se charger (nom du fichier erron�, ...)
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
	 * Renvoie l'Id courant repr�sentant la couleur du s�lecteur (qui peut correspondre par exemple l'Id de la Case � laquelle est associ�e la couleur du s�lecteur).
	 * @return L'Id courant repr�sentant la couleur du s�lecteur (qui peut correspondre par exemple l'Id de la Case � laquelle est associ�e la couleur du s�lecteur)
	 * - 0 pour un selecteur cyan (peut correspondre � la case Oc�an) <br>
	 * - 1 pour un selecteur marron-beige (peut correspondre � la case Terre) <br>
	 * - 2 pour un s�lecteur blanc (peut correspondre � la case Phare) <br>
	 * - 3 pour un s�lecteur rouge (ne correspond pas � un type de case mais peut tout de m�me �tre utile)
	 */
	public int getIdCaseSelectionnee() {
		return idCaseSelectionnee;
	}
	
	/**
	 * Modifie l'Id courant repr�sentant la couleur du s�lecteur (qui peut correspondre par exemple l'Id de la Case � laquelle est associ�e la couleur du s�lecteur).
	 * @param _idCaseSelectionnee Le nouvel Id repr�sentant la couleur du s�lecteur (qui peut correspondre par exemple l'Id de la Case � laquelle est associ�e la couleur du s�lecteur)
	 * - 0 pour un selecteur cyan (peut correspondre � la case Oc�an) <br>
	 * - 1 pour un selecteur marron-beige (peut correspondre � la case Terre) <br>
	 * - 2 pour un s�lecteur blanc (peut correspondre � la case Phare) <br>
	 * - 3 pour un s�lecteur rouge (ne correspond pas � un type de case mais peut tout de m�me �tre utile)
	 */
	public void setIdCaseSelectionnee(int _idCaseSelectionnee) {
		idCaseSelectionnee = _idCaseSelectionnee;
	}
	
	/**
	 * Renvoie la position du s�lecteur de case par rapport � l'origine de la Map.
	 * @return la position du s�lecteur de case par rapport � l'origine de la Map
	 */
	public Point getPosition() {
		return (Point) position.clone();
	}
	
	/**
	 * Modifie la position du s�lecteur de case par rapport � l'origine de la Map.
	 * @param _position la nouvelle position du s�lecteur de case par rapport � l'origine de la Map
	 */
	public void setPosition(Point _position) {
		position = _position;
	}
	
	/**
	 * Renvoie true si le s�lecteur est visible ou false sinon.
	 * @return True si le s�lecteur est visible ou false sinon
	 */
	public boolean isSelecteurVisible() {
		return visible;
	}
	
	/**
	 * Masque ou rend visible le s�lecteur.
	 * @param _visible True pour rendre le s�lecteur visible ou false pour le masquer
	 */
	public void setSelecteurVisible(boolean _visible) {
		visible = _visible;
	}
	
	/**
	 * Dessine le s�lecteur de case sur la fen�tre, dans le r�f�rentiel de la map, en prenant en compte l'�chelle
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
	 * Dessine le s�lecteur de case sur la fenetre, dans le r�f�rentiel de la fenetre, sans prendre en compte l'�chelle
	 */
	public void drawAbsolu() {
		if (isSelecteurVisible()) {
			selecteurs[idCaseSelectionnee].draw(position.x, position.y);
		}
	}
}