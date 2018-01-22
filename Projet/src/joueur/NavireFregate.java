package joueur;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import utility.FileUtility;

public class NavireFregate extends Navire {

	public NavireFregate(int direction, int id_proprietaire) throws SlickException {
		super(direction, FileUtility.DOSSIER_SPRITE + "navire_fregate.png", 64,
				FileUtility.DOSSIER_SPRITE + "Sprite_Miniature_Fregate.png");
		setNbDeplacements(7);
		setNbDeplacementsRestants(7);
		setPv(50);
		setPvMax(50);
		setDmgCannonPrincipal(30);
		setDmgCanonSecondaire(10);
		setNbTourRechargeCanonPrincipal(1);
		setNbTourRechargeCanonSecondaire(0);
		setIdProprietaire(id_proprietaire);
	}

}
