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
	
	private SpriteSheet spriteSheetSelecteur;
	private Animation selecteurs[];
	private boolean visible = true;
	private CouleurSelecteur couleurSelecteur = CouleurSelecteur.CYAN;
	private Point position = new Point(0, 0);
	
	public SelecteurCase(CouleurSelecteur couleurSelecteur, int coordTabX, int coordTabY) throws SlickException {
		this();
		this.couleurSelecteur = couleurSelecteur;
		this.setCoordTab(new Point(coordTabX, coordTabY));
	}
	
	public SelecteurCase(CouleurSelecteur couleurSelecteur, Point position) throws SlickException {
		this();
		this.couleurSelecteur = couleurSelecteur;
		this.position = (Point) position.clone();
	}
	
	public SelecteurCase() throws SlickException {
		selecteurs = new Animation[CouleurSelecteur.values().length];
		spriteSheetSelecteur = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_SELECTEUR, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
		for (int i = 0; i < selecteurs.length; i++) {
			selecteurs[i] = new Animation();
			selecteurs[i].addFrame(spriteSheetSelecteur.getSprite(0, i), 500);
		}
	}
	
	public CouleurSelecteur getCouleurSelecteur() {
		return couleurSelecteur;
	}
	
	public void setCouleurSelecteur(CouleurSelecteur couleurSelecteur) {
		this.couleurSelecteur = couleurSelecteur;
	}
	
	public Point getPosition() {
		return (Point) position.clone();
	}
	
	public void setPosition(Point _position) {
		position = _position;
	}
	
	public Point getCoordTab() {
		return Map.getInstance().coordMaillageToTab(position);
	}
	
	public void setCoordTab(Point coordTab) {
		position = Map.getInstance().coordTabToMaillage((Point) coordTab.clone());
	}
	
	public boolean isSelecteurVisible() {
		return visible;
	}
	
	public void setSelecteurVisible(boolean _visible) {
		visible = _visible;
	}
	
	public void draw() {
		if (isSelecteurVisible()) {
			selecteurs[couleurSelecteur.ordinal()].draw(Map.getInstance().getPosition().x + position.x, 
					Map.getInstance().getPosition().y + position.y,
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleX(),
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleY());
		}
	}
}
