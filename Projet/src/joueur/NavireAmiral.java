package joueur;

import java.awt.Point;

import org.newdawn.slick.SlickException;

public class NavireAmiral extends Navire {

	public NavireAmiral(int direction) throws SlickException {
		super(direction);
		setNbDeplacements(3);
		setPv(100);
		setDmgCannonPrincipal(50);
		setDmgCanonSecondaire(30);
		setNbTourRechargeCanonPrincipal(3);
		setNbTourRechargeCanonSecondaire(1);
		// TODO Auto-generated constructor stub
	}

}
