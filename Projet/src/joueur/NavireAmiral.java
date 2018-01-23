package joueur;

import map.Map;
import map.SelecteurCase;
import org.newdawn.slick.SlickException;
import utility.FileUtility;

import java.awt.*;

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
		setNbTourMaxRechargeCanonPrincipal(4);
		setNbTourMaxRechargeCanonSecondaire(2);
		setIdProprietaire(id_proprietaire);
	}

	public void zoneTirCannonPrincipal(boolean activation){
		Point coordPosTab = Map.getInstance().coordMaillageToTab(getPosition());
		int X = (int) coordPosTab.getX();
		int Y = (int) coordPosTab.getY();
		Point newPoint[] = new Point[4];
		int sensPremierDenivele = Map.getInstance().getSensPremierDenivele();
		boolean xPaire = (coordPosTab.getX() % 2) == 0;
		boolean deniveleInverse = sensPremierDenivele == 1;
		boolean ohMyGodness = xPaire != deniveleInverse;
		if(activation){
			switch(getDirection()){
				case 0:
					newPoint[0] = new Point(X, Y-1);
					newPoint[1] = new Point(X, Y-2);
					newPoint[2] = new Point(X, Y-3);
					newPoint[3] = new Point(X, Y-4);
					break;
				case 1:
					if(ohMyGodness){
						newPoint[0] = new Point(X+1, Y);
						newPoint[1] = new Point(X+2, Y-1);
						newPoint[2] = new Point(X+3, Y-1);
						newPoint[3] = new Point(X+4, Y-2);
					}
					else{
						newPoint[0] = new Point(X+1, Y-1);
						newPoint[1] = new Point(X+2, Y-1);
						newPoint[2] = new Point(X+3, Y-2);
						newPoint[3] = new Point(X+4, Y-2);
					}
					break;
				case 2:
					if(ohMyGodness){
						newPoint[0] = new Point(X+1, Y+1);
						newPoint[1] = new Point(X+2, Y+1);
						newPoint[2] = new Point(X+3, Y+2);
						newPoint[3] = new Point(X+4, Y+2);
					}
					else{
						newPoint[0] = new Point(X+1, Y);
						newPoint[1] = new Point(X+2, Y+1);
						newPoint[2] = new Point(X+3, Y+1);
						newPoint[3] = new Point(X+4, Y+2);
					}
					break;
				case 3:
					newPoint[0] = new Point(X, Y+1);
					newPoint[1] = new Point(X, Y+2);
					newPoint[2] = new Point(X, Y+3);
					newPoint[3] = new Point(X, Y+4);
					break;
				case 4:
					if(ohMyGodness){
						newPoint[0] = new Point(X-1, Y+1);
						newPoint[1] = new Point(X-2, Y+1);
						newPoint[2] = new Point(X-3, Y+2);
						newPoint[3] = new Point(X-4, Y+2);
					}
					else{
						newPoint[0] = new Point(X-1, Y);
						newPoint[1] = new Point(X-2, Y+1);
						newPoint[2] = new Point(X-3, Y+1);
						newPoint[3] = new Point(X-4, Y+2);
					}
					break;
				case 5:
					if(ohMyGodness){
						newPoint[0] = new Point(X-1, Y);
						newPoint[1] = new Point(X-2, Y-1);
						newPoint[2] = new Point(X-3, Y-1);
						newPoint[3] = new Point(X-4, Y-2);
					}
					else{
						newPoint[0] = new Point(X-1, Y-1);
						newPoint[1] = new Point(X-2, Y-1);
						newPoint[2] = new Point(X-3, Y-2);
						newPoint[3] = new Point(X-4, Y-2);
					}
					break;
			}
			for (int i=0;i<4;i++) {
				if (newPoint[i].getX() < 0 || newPoint[i].getY() < 0 ||
						newPoint[i].getX() > Map.getInstance().getNbCases().getX() - 1 ||
						newPoint[i].getY() > Map.getInstance().getNbCases().getY() - 1) {
					selecteursCasesTir[i].setSelecteurVisible(false);
				} else if (Map.getInstance().getGrille().get((int) newPoint[i].getY()).get((int) newPoint[i].getX()).getId() == 1) {
					selecteursCasesTir[i].setSelecteurVisible(false);
				} else {
					selecteursCasesTir[i].setPosition(Map.getInstance().coordTabToMaillage(newPoint[i]));
					selecteursCasesTir[i].setSelecteurVisible(true);
				}
			}
		}else{
			for (SelecteurCaseTir selecteur:selecteursCasesTir) {
				selecteur.setSelecteurVisible(false);
			}
		}
	}

	public void zoneTirCannonSecondaire(boolean activation){
		Point coordPosTab = Map.getInstance().coordMaillageToTab(getPosition());
		int X = (int) coordPosTab.getX();
		int Y = (int) coordPosTab.getY();
		Point newPoint[] = new Point[6];
		int sensPremierDenivele = Map.getInstance().getSensPremierDenivele();
		boolean xPaire = (coordPosTab.getX() % 2) == 0;
		boolean deniveleInverse = sensPremierDenivele == 1;
		boolean ohMyGodness = xPaire != deniveleInverse;
		if(activation){
			switch(getDirection()){
				case 0:
					if(ohMyGodness){
						newPoint[0] = new Point(X-1, Y);
						newPoint[2] = new Point(X+1, Y);
						newPoint[3] = new Point(X-1, Y-1);
						newPoint[5] = new Point(X+1, Y-1);
					}
					else{
						newPoint[0] = new Point(X-1, Y-1);
						newPoint[2] = new Point(X+1, Y-1);
						newPoint[3] = new Point(X-1, Y-2);
						newPoint[5] = new Point(X+1, Y-2);
					}
					newPoint[1] = new Point(X, Y-1);
					newPoint[4] = new Point(X, Y-2);
					break;
				case 1:
					newPoint[0] = new Point(X, Y-1);
					newPoint[4] = new Point(X+2, Y-1);
					newPoint[5] = new Point(X+2, Y);
					if(ohMyGodness){
						newPoint[1] = new Point(X+1, Y);
						newPoint[2] = new Point(X+1, Y+1);
						newPoint[3] = new Point(X+1, Y-1);
					}
					else{
						newPoint[1] = new Point(X+1, Y-1);
						newPoint[2] = new Point(X+1, Y);
						newPoint[3] = new Point(X+1, Y-2);
					}
					break;
				case 2:
					newPoint[2] = new Point(X, Y+1);
					newPoint[3] = new Point(X+2, Y);
					newPoint[4] = new Point(X+2, Y+1);
					if(ohMyGodness){
						newPoint[0] = new Point(X+1, Y);
						newPoint[1] = new Point(X+1, Y+1);
						newPoint[5] = new Point(X+1, Y+2);
					}
					else{
						newPoint[0] = new Point(X+1, Y-1);
						newPoint[1] = new Point(X+1, Y);
						newPoint[5] = new Point(X+1, Y+1);
					}
					break;
				case 3:
					newPoint[1] = new Point(X, Y+1);
					newPoint[4] = new Point(X, Y+2);
					if(ohMyGodness){
						newPoint[0] = new Point(X+1, Y+1);
						newPoint[2] = new Point(X-1, Y+1);
						newPoint[3] = new Point(X+1, Y+2);
						newPoint[5] = new Point(X-1, Y+2);
					}
					else{
						newPoint[0] = new Point(X+1, Y);
						newPoint[2] = new Point(X-1, Y);
						newPoint[3] = new Point(X+1, Y+1);
						newPoint[5] = new Point(X-1, Y+1);
					}
					break;
				case 4:
					newPoint[0] = new Point(X, Y+1);
					newPoint[4] = new Point(X-2, Y+1);
					newPoint[5] = new Point(X-2, Y);
					if(ohMyGodness){
						newPoint[1] = new Point(X-1, Y);
						newPoint[2] = new Point(X-1, Y+1);
						newPoint[3] = new Point(X-1, Y+1);
					}
					else{
						newPoint[1] = new Point(X-1, Y);
						newPoint[2] = new Point(X-1, Y-1);
						newPoint[3] = new Point(X-1, Y+1);
					}
					break;
				case 5:
					newPoint[2] = new Point(X, Y-1);
					newPoint[3] = new Point(X-2, Y);
					newPoint[4] = new Point(X-2, Y-1);
					if(ohMyGodness){
						newPoint[0] = new Point(X-1, Y);
						newPoint[1] = new Point(X-1, Y+1);
						newPoint[5] = new Point(X-1, Y-1);
					}
					else{
						newPoint[0] = new Point(X-1, Y-1);
						newPoint[1] = new Point(X-1, Y);
						newPoint[5] = new Point(X-1, Y-2);
					}
					break;
			}
			for (int i=0;i<6;i++) {
				if (newPoint[i].getX() < 0 || newPoint[i].getY() < 0 ||
						newPoint[i].getX() > Map.getInstance().getNbCases().getX() - 1 ||
						newPoint[i].getY() > Map.getInstance().getNbCases().getY() - 1) {
					selecteursCasesTir[i].setSelecteurVisible(false);
				} else if (Map.getInstance().getGrille().get((int) newPoint[i].getY()).get((int) newPoint[i].getX()).getId() == 1) {
					selecteursCasesTir[i].setSelecteurVisible(false);
				} else {
					selecteursCasesTir[i].setPosition(Map.getInstance().coordTabToMaillage(newPoint[i]));
					selecteursCasesTir[i].setSelecteurVisible(true);
				}
			}
		}else{
			for (SelecteurCaseTir selecteur:selecteursCasesTir) {
				selecteur.setSelecteurVisible(false);
			}
		}
	}
}
