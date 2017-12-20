package joueur;

import java.awt.Point;

import map.Direction;
import map.Map;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import utility.FileUtility;

public class Navire extends Animation {
	public static final String FICHIER_SPRITE_SHEET_NAVIRE = "boat.png";
	public static final int LONGUEUR_COTE_TUILE = 64;
	private int pv, nbDeplacements, dmgCannonPrincipal, dmgCanonSecondaire,
            nbTourRechargeCanonPrincipal, nbTourRechargeCanonSecondaire;
	
	private SpriteSheet spriteSheet;
	
	private Point origine;
	private Point position = new Point();
	
	public Navire() throws SlickException {
		spriteSheet = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_NAVIRE, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
		addFrame(spriteSheet.getSprite(0, 0), 100);
	}
	
	public void setPv(int pv) {
		this.pv = pv;
	}

	public void setNbDeplacements(int nbDeplacements) {
		this.nbDeplacements = nbDeplacements;
	}

	public void setDmgCannonPrincipal(int dmgCannonPrincipal) {
		this.dmgCannonPrincipal = dmgCannonPrincipal;
	}

	public void setDmgCanonSecondaire(int dmgCanonSecondaire) {
		this.dmgCanonSecondaire = dmgCanonSecondaire;
 	}

	public void setNbTourRechargeCanonPrincipal(int nbTourRechargeCanonPrincipal) {
		this.nbTourRechargeCanonPrincipal = nbTourRechargeCanonPrincipal;
	}

	public void setNbTourRechargeCanonSecondaire(int nbTourRechargeCanonSecondaire) {
		this.nbTourRechargeCanonSecondaire = nbTourRechargeCanonSecondaire;
	}
	
	public Point getPosition() {
		return (Point)position.clone();
	}
	
	public void setPosition(Point _position) {
		position = _position;
	}
	
	public void deplacer(Direction direction) {
		Map.getInstance().deplacer(this, direction);
	}
	
	@Override
	public void draw() {
		this.draw(Map.getInstance().getPosition().x + position.x, Map.getInstance().getPosition().y + position.y);
	}
}
