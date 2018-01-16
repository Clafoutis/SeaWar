package joueur;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import utility.FileUtility;

public class NavireAmiral extends Navire {

	public NavireAmiral(int direction) throws SlickException {
		super(direction, new SpriteSheet(FileUtility.DOSSIER_SPRITE + "navire_amiral.png", 64, 64));
		setNbDeplacements(3);
		setNbDeplacementsRestants(3);
		setPv(100);
		setDmgCannonPrincipal(50);
		setDmgCanonSecondaire(30);
		setNbTourRechargeCanonPrincipal(3);
		setNbTourRechargeCanonSecondaire(1);
		// TODO Auto-generated constructor stub
	}

}
