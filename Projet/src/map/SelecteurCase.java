package map;

import java.awt.Point;

import joueur.Navire;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import utility.FileUtility;

public class SelecteurCase {
	public static final String DOSSIER_SPRITE = "resources/sprites/";
	public static final String FICHIER_SPRITE_SHEET_SELECTEUR = "spriteSheetSelecteur.png";
	private static final int LONGUEUR_COTE_TUILE = 64;
	private static final int NB_TYPES_CASES = 4;
	
	private SpriteSheet spriteSheetSelecteur;
	private Animation selecteurs[];
	private boolean visible = false;
	private int idCaseSelectionnee = 0;
	private Point position = new Point(0, 0);
	
	
	
	public SelecteurCase(int nbTypesCases) throws SlickException {
		selecteurs = new Animation[NB_TYPES_CASES];
		spriteSheetSelecteur = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_SELECTEUR, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
		for (int i = 0; i < selecteurs.length; i++) {
			selecteurs[i] = new Animation();
			selecteurs[i].addFrame(spriteSheetSelecteur.getSprite(0, i), 500);
		}
	}
	
	public int getIdCaseSelectionnee() {
		return idCaseSelectionnee;
	}
	
	public void setIdCaseSelectionnee(int _idCaseSelectionnee) {
		idCaseSelectionnee = _idCaseSelectionnee;
	}
	
	public Point getPosition() {
		return (Point) position.clone();
	}
	
	public void setPosition(Point _position) {
		position = _position;
	}
	
	public boolean isSelecteurVisible() {
		return visible;
	}
	
	public void setSelecteurVisible(boolean _visible) {
		visible = _visible;
	}
	
	public void draw() {
		if (isSelecteurVisible()) {
			selecteurs[idCaseSelectionnee].draw(Map.getInstance().getPosition().x + position.x, 
					Map.getInstance().getPosition().y + position.y,
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleX(),
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleY());
		}
	}
}
