package joueur;

import java.awt.Point;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import utility.FileUtility;

public class NavireFregate extends Navire {

	public NavireFregate(int direction) throws SlickException {
		super(direction, new SpriteSheet(FileUtility.DOSSIER_SPRITE + "navire_fregate.png", 64, 64));
		setNbDeplacements(7);
		setPv(50);
		setDmgCannonPrincipal(30);
		setDmgCanonSecondaire(10);
		setNbTourRechargeCanonPrincipal(1);
		setNbTourRechargeCanonSecondaire(0);
		// TODO Auto-generated constructor stub
	}

}
