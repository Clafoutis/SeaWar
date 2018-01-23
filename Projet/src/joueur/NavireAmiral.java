package joueur;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import utility.FileUtility;

public class NavireAmiral extends Navire {

	public NavireAmiral(int direction, int id_proprietaire) throws SlickException {
		super(direction, FileUtility.DOSSIER_SPRITE + "navire_amiral.png",
				FileUtility.DOSSIER_SPRITE + "Sprite_Miniature_Amiral.png",
				64);
		setNbDeplacements(3);
		setNbDeplacementsRestants(3);
		setPv(100);
		setPvMax(100);
		setDmgCannonPrincipal(50);
		setDmgCanonSecondaire(30);
		setNbTourRechargeCanonPrincipal(3);
		setNbTourRechargeCanonSecondaire(1);
		setIdProprietaire(id_proprietaire);
	}

}
