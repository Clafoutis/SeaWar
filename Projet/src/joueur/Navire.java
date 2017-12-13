package joueur;

import java.awt.Point;

import map.Map;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import utility.FileUtility;

public class Navire extends Animation {
	public static final String FICHIER_SPRITE_SHEET_NAVIRE = "boat.png";
	public static final int LONGUEUR_COTE_TUILE = 64;
	
	private SpriteSheet spriteSheet;
	
	private Point origine;
	private Point position = new Point();
	
	public Navire(Point _origine) throws SlickException {
		origine = _origine;
		spriteSheet = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_NAVIRE, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
		addFrame(spriteSheet.getSprite(0, 0), 100);
	}
	
	public Point getPosition() {
		return (Point)position.clone();
	}
	
	public void setPosition(Point _position) {
		position = _position;
	}
	
	public void allerHaut() {
		Map.getInstance().allerHaut(this);
	}
	
	@Override
	public void draw() {
		this.draw(origine.x + position.x, origine.y + position.y);
	}
}
