package joueur;

import map.Map;
import org.newdawn.slick.SlickException;
import utility.FileUtility;

import java.awt.*;

/**
 * Element qui défini un navire frégate dans le jeu
 */
public class NavireFregate extends Navire {
	
	/**
	 * Créé un navire avec les caractéristiques d'un navire frégate
	 * @param direction représente la direction du bateau
	 * - 0 pour haut
	 * - 1 pour haut droite
	 * - 2 pour bas droite
	 * - 3 pour bas
	 * - 4 pour bas gauche
	 * - 5 pour haut gauche
	 * @param id_proprietaire représente l'id du joueur propriétaire
	 */
	public NavireFregate(int direction, int id_proprietaire) throws SlickException {
		super(direction, FileUtility.DOSSIER_SPRITE + "navire_fregate.png",
				FileUtility.DOSSIER_SPRITE + "Sprite_Miniature_Fregate.png",
				64);
		selecteursCasesTir = new SelecteurCaseTir[6];
		for(int i=0;i<6;i++) selecteursCasesTir[i] = new SelecteurCaseTir();
		setNbDeplacements(7);
		setNbDeplacementsRestants(7);
		setPv(50);
		setPvMax(50);
		setDmgCannonPrincipal(30);
		setDmgCanonSecondaire(10);
		setNbTourMaxRechargeCanonPrincipal(2);
		setNbTourMaxRechargeCanonSecondaire(1);
		setIdProprietaire(id_proprietaire);
	}

	/**
	 * Renvoie le tableau des sélecteurs de case de tir du navire
	 * @return le tableau des sélecteurs de case de tir du navire
	 */
	public SelecteurCaseTir[] getSelecteursCasesTir() {
		return selecteursCasesTir;
	}

	/**
	 * Met à jour la liste des sélecteurs des zones de tir du cannon principal du navire
	 * @param activation permet de savoir si les zones sont à afficher ou non
	 */
	public void zoneTirCannonPrincipal(boolean activation){
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
					newPoint[1] = new Point(X, Y-1);
					newPoint[4] = new Point(X, Y-2);
					if(ohMyGodness){
						newPoint[0] = new Point(X-1, Y);
						newPoint[2] = new Point(X+1, Y);
						newPoint[3] = new Point(X-1, Y+1);
						newPoint[5] = new Point(X+1, Y+1);
					}
					else{
						newPoint[0] = new Point(X-1, Y-1);
						newPoint[2] = new Point(X+1, Y-1);
						newPoint[3] = new Point(X-1, Y);
						newPoint[5] = new Point(X+1, Y);
					}
					break;
				case 1:
					newPoint[0] = new Point(X, Y-1);
					newPoint[4] = new Point(X+2, Y-1);
					newPoint[5] = new Point(X, Y+1);
					if(ohMyGodness){
						newPoint[1] = new Point(X+1, Y);
						newPoint[2] = new Point(X+1, Y+1);
						newPoint[3] = new Point(X-1, Y);
					}
					else{
						newPoint[1] = new Point(X+1, Y-1);
						newPoint[2] = new Point(X+1, Y);
						newPoint[3] = new Point(X-1, Y-1);
					}
					break;
				case 2:
					newPoint[2] = new Point(X, Y+1);
					newPoint[3] = new Point(X, Y-1);
					newPoint[4] = new Point(X+2, Y+1);
					if(ohMyGodness){
						newPoint[0] = new Point(X+1, Y);
						newPoint[1] = new Point(X+1, Y+1);
						newPoint[5] = new Point(X-1, Y+1);
					}
					else{
						newPoint[0] = new Point(X+1, Y-1);
						newPoint[1] = new Point(X+1, Y);
						newPoint[5] = new Point(X-1, Y);
					}
					break;
				case 3:
					newPoint[1] = new Point(X, Y+1);
					newPoint[4] = new Point(X, Y+2);
					if(ohMyGodness){
						newPoint[0] = new Point(X+1, Y+1);
						newPoint[2] = new Point(X-1, Y+1);
						newPoint[3] = new Point(X+1, Y);
						newPoint[5] = new Point(X-1, Y);
					}
					else{
						newPoint[0] = new Point(X+1, Y);
						newPoint[2] = new Point(X-1, Y);
						newPoint[3] = new Point(X+1, Y-1);
						newPoint[5] = new Point(X-1, Y-1);
					}
					break;
				case 4:
					newPoint[0] = new Point(X, Y+1);
					newPoint[4] = new Point(X-2, Y+1);
					newPoint[5] = new Point(X, Y-1);
					if(ohMyGodness){
						newPoint[1] = new Point(X-1, Y);
						newPoint[2] = new Point(X-1, Y+1);
						newPoint[3] = new Point(X+1, Y+1);
					}
					else{
						newPoint[1] = new Point(X-1, Y);
						newPoint[2] = new Point(X-1, Y-1);
						newPoint[3] = new Point(X+1, Y);
					}
					break;
				case 5:
					newPoint[2] = new Point(X, Y-1);
					newPoint[3] = new Point(X, Y+1);
					newPoint[4] = new Point(X-2, Y-1);
					if(ohMyGodness){
						newPoint[0] = new Point(X-1, Y);
						newPoint[1] = new Point(X-1, Y+1);
						newPoint[5] = new Point(X+1, Y);
					}
					else{
						newPoint[0] = new Point(X-1, Y-1);
						newPoint[1] = new Point(X-1, Y);
						newPoint[5] = new Point(X+1, Y-1);
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

	/**
	 * Met à jour la liste des sélecteurs des zones de tir du cannon secondaire du navire
	 * @param activation permet de savoir si les zones sont à afficher ou non
	 */
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
			newPoint[0] = new Point(X, Y-1);
			newPoint[3] = new Point(X, Y+1);
			if(ohMyGodness){
				newPoint[1] = new Point(X+1, Y);
				newPoint[2] = new Point(X+1, Y+1);
				newPoint[4] = new Point(X-1, Y+1);
				newPoint[5] = new Point(X-1, Y);
			}
			else{
				newPoint[1] = new Point(X+1, Y-1);
				newPoint[2] = new Point(X+1, Y);
				newPoint[4] = new Point(X-1, Y);
				newPoint[5] = new Point(X-1, Y-1);
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
