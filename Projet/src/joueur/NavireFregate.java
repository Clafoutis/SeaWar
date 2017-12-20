package joueur;

import java.awt.Point;

import org.newdawn.slick.SlickException;

public class NavireFregate extends Navire {

	public NavireFregate() throws SlickException {
		setNbDeplacements(7);
		setPv(50);
		setDmgCannonPrincipal(30);
		setDmgCanonSecondaire(10);
		setNbTourRechargeCanonPrincipal(1);
		setNbTourRechargeCanonSecondaire(0);
		// TODO Auto-generated constructor stub
	}

}
