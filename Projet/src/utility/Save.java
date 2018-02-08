package utility;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import game.Game;
import map.Map;

import org.newdawn.slick.SlickException;

/**
 * Utilitaire permettant d'avoir une API simple pour l'enregistrement et le chargement de l'état d'une partie. Design pattern Singleton. <br> 
 * Pour enregistrer : on alimente l'instance de Save en faisant appel à des setteurs puis on appelle la fonction save() <br>
 * Pour charger : on fait appelle à la fonction load() puis on récupère les informations de la partie par les getteurs
 */
public class Save {
	/**
	 * Constante pour récupérer facilement le chemin vers le fichier de sauvegarde
	 */
	public static final String FICHIER_SAVE = "resources/save";
	
	private boolean sauvegardePresente = false;
	private String nomMap;
	private int joueurCourant;
	private int navireCourant;
	private NavireSave navireSaves[][] = new NavireSave[Game.NB_JOUEURS][Game.NB_JOUEURS];
	private Vector<Integer> possessionPhares = new Vector<Integer>();

	private static Save INSTANCE = new Save();
	
	private Save() {
		for (int j = 0; j < Game.NB_JOUEURS; j++) {
			for (int i = 0; i < Game.NB_JOUEURS; i++) {
				navireSaves[j][i] = new NavireSave();
			}
		}
	}
 
	/**
	 * Renvoie l'unique instance de Save.
	 * @return L'unique instance de Save
	 */
	public static Save getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Initialise l'instance de Save, cette fonction doit être appelée en statique au tout début du programme 
	 * (typiquement dans la fonction "initStatesList" qui initialise les vues de l'application).
	 */
	public static void initInstance() {
		INSTANCE = new Save();
	}
	
	/**
	 * Charge les données de l'état de la partie enregistré. Ces données doivent ensuite être récupérées par les getteurs
	 */
	public void load() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(FICHIER_SAVE));
			String line;
			possessionPhares.clear();

			if ((line = br.readLine()) != null) {
				sauvegardePresente = true;
				nomMap = line;
				if ((line = br.readLine()) != null) joueurCourant = Integer.parseInt(line);
				if ((line = br.readLine()) != null) navireCourant = Integer.parseInt(line);
				
				while (!(line = br.readLine()).equals("#")) {
					possessionPhares.add(Integer.parseInt(line));
				}
	
				for (int j = 0; j < Game.NB_JOUEURS; j++) {
					for (int i = 0; i < Game.NB_JOUEURS; i++) {
						navireSaves[j][i] = new NavireSave(br);
					}
				}
			} else {
				sauvegardePresente = false;
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			sauvegardePresente = false;
		    e.printStackTrace();
		} catch (IOException e) {
			sauvegardePresente = false;
		    e.printStackTrace();
		}
	}
	
	/**
	 * Enregistre les données de l'état de la partie (à appeler après avoir alimenté l'instance de Save par les setteurs)
	 */
	public void save() {
		BufferedOutputStream bos;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(FICHIER_SAVE)));

			writeStringLn(bos, nomMap);
			writeNumberLn(bos, joueurCourant);
			writeNumberLn(bos, navireCourant);
			for (Integer possessionPhare : possessionPhares) {
				writeNumberLn(bos, possessionPhare);
			}
			writeStringLn(bos, "#");

			for (int j = 0; j < Game.NB_JOUEURS; j++) {
				for (int i = 0; i < Game.NB_JOUEURS; i++) {
					navireSaves[j][i].save(bos);
				}
			}
			sauvegardePresente = true;

			bos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Efface les données enregistrées de l'état de la partie
	 */
	public void effacerSauvegarde() {
		sauvegardePresente = false;
		BufferedOutputStream bos;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(FICHIER_SAVE)));
			bos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeStringLn(BufferedOutputStream bos, String str) throws IOException {
		for (int i = 0; i < str.length(); i++) {
			bos.write(str.charAt(i));
		}
		bos.write('\r');
		bos.write('\n');
	}
	
	private void writeNumberLn(BufferedOutputStream bos, Integer num) throws IOException {
		String id_str;
		id_str = num.toString();

		for (int i = 0; i < id_str.length(); i++) {
			bos.write(id_str.charAt(i));
		}
		bos.write('\r');
		bos.write('\n');
	}
	
	/**
	 * Setteur pour alimenter l'instance de Save par des informations de l'état de la partie (à utiliser avant l'appelle à save()). <br>
	 * Ce setteur récupère des données relatives à un navire particulier.
	 * @param joueur Id du joueur courant à qui appartient le navire(0 ou 1)
	 * @param navire Id du navire (0 ou 1)
	 * @param posTab Coordonnées dans le tableau du navire
	 * @param pv Nombre de pv du navire
	 * @param nbTourRechargeCanonPrincipal Nombre de tour restant avant le rechargement du canon principal du navire
	 * @param nbTourRechargeCanonSecondaire Nombre de tour restant avant le rechargement du canon secondaire du navire
	 * @param direction Direction courante du navire
	 * @param nbDeplacementsRestants Nombre de déplacements restant au navire
	 */
	public void setNavireSave(int joueur, int navire, Point posTab, int pv, 
			int nbTourRechargeCanonPrincipal, int nbTourRechargeCanonSecondaire,
			int direction, int nbDeplacementsRestants) {
		navireSaves[joueur][navire] = new NavireSave(posTab, pv, nbTourRechargeCanonPrincipal, nbTourRechargeCanonSecondaire, direction, nbDeplacementsRestants);
	}
	
	/**
	 * Setteur pour alimenter l'instance de Save par des informations de l'état de la partie (à utiliser avant l'appelle à save()).
	 * @param nomMap Le nom de la map chargé pour la partie en cours
	 */
	public void setNomMap(String nomMap) {
		this.nomMap = nomMap;
	}
	
	/**
	 * Setteur pour alimenter l'instance de Save par des informations de l'état de la partie (à utiliser avant l'appelle à save()).
	 * @param joueurCourant Le joueur pour qui c'est le tour dans la partie
	 */
	public void setJoueurCourant(int joueurCourant) {
		this.joueurCourant = joueurCourant;
	}
	
	/**
	 * Setteur pour alimenter l'instance de Save par des informations de l'état de la partie (à utiliser avant l'appelle à save()).
	 * @param navireCourant Le navire sélectionné par le joueur pour qui c'est le tour dans la partie
	 */
	public void setNavireCourant(int navireCourant) {
		this.navireCourant = navireCourant;
	}
	
	/**
	 * Setteur pour alimenter l'instance de Save par des informations de l'état de la partie (à utiliser avant l'appelle à save()).
	 * @param possessionPhares Un tableau contenant pour chaque indice (correspondant à un numéro de phare) l'id du joueur possédant le phare ou -1 si aucun joueur ne possède le phare 
	 * (l'ordre croissant des numéros de phare part de gauche à droite puis de haut en bas de la map) <br>
	 * Ce tableau est récupérable par l'appelle de Map.getInstance().getPossessionPhares()
	 */
	public void setPossessionPhares(Vector<Integer> possessionPhares) {
		this.possessionPhares = possessionPhares;
	}
	
	/**
	 * Renvoie true si une sauvegarde de l'état de la partie est présente ou false sinon
	 * @return True si une sauvegarde de l'état de la partie est présente ou false sinon
	 */
	public boolean isSauvegardePresente() {
		return sauvegardePresente;
	}
	
	/**
	 * Renvoie le nom de la map de la partie chargée
	 * @return
	 */
	public String getNomMap() {
		return nomMap;
	}
	
	/**
	 * Renvoie le joueur pour qui c'est le tour dans la partie chargée.
	 * @return Le joueur pour qui c'est le tour dans la partie chargée
	 */
	public int getJoueurCourant() {
		return joueurCourant;
	}
	
	/**
	 * Renvoie le navire courant du joueur pour qui c'est le tour dans la partie chargée.
	 * @return Le navire courant du joueur pour qui c'est le tour dans la partie chargée
	 */
	public int getNavireCourant() {
		return navireCourant;
	}
	
	/**
	 * Renvoie la coordonnée en X dans le tableau d'un navire voulu.
	 * @param joueur L'id du Joueur à qui appartient le navire
	 * @param navire L'id du navire
	 * @return La coordonnée en X dans le tableau d'un navire voulu
	 */
	public int getPosTabX(int joueur, int navire) {
		return navireSaves[joueur][navire].getPosTabX();
	}
	
	/**
	 * Renvoie la coordonnée en Y dans le tableau d'un navire voulu.
	 * @param joueur L'id du Joueur à qui appartient le navire
	 * @param navire L'id du navire
	 * @return La coordonnée en Y dans le tableau d'un navire voulu
	 */
	public int getPosTabY(int joueur, int navire) {
		return navireSaves[joueur][navire].getPosTabY();
	}
	
	/**
	 * Renvoie le nombre de pv du navire courant d'un navire voulu.
	 * @param joueur L'id du Joueur à qui appartient le navire
	 * @param navire L'id du navire
	 * @return Le nombre de pv d'un navire voulu
	 */
	public int getPv(int joueur, int navire) {
		return navireSaves[joueur][navire].getPv();
	}
	
	/**
	 * Renvoie le nombre de tours restants avant le rechargement du canon principal d'un navire voulu.
	 * @param joueur L'id du Joueur à qui appartient le navire
	 * @param navire L'id du navire
	 * @return Le nombre de tours restants avant le rechargement du canon principal d'un navire voulu
	 */
	public int getNbTourRechargeCanonPrincipal(int joueur, int navire) {
		return navireSaves[joueur][navire].getNbTourRechargeCanonPrincipal();
	}
	
	/**
	 * Renvoie le nombre de tours restants avant le rechargement du canon secondaire d'un navire voulu.
	 * @param joueur L'id du Joueur à qui appartient le navire
	 * @param navire L'id du navire
	 * @return Le nombre de tours restants avant le rechargement du canon secondaire d'un navire voulu
	 */
	public int getNbTourRechargeCanonSecondaire(int joueur, int navire) {
		return navireSaves[joueur][navire].getNbTourRechargeCanonSecondaire();
	}
	
	/**
	 * Renvoie la direction courante d'un navire voulu.
	 * @param joueur L'id du Joueur à qui appartient le navire
	 * @param navire L'id du navire
	 * @return La direction courante d'un navire voulu
	 */
	public int getDirection(int joueur, int navire) {
		return navireSaves[joueur][navire].getDirection();
	}
	
	/**
	 * Renvoie le nombre de déplacements restants d'un navire voulu.
	 * @param joueur L'id du Joueur à qui appartient le navire
	 * @param navire L'id du navire
	 * @return Le nombre de déplacements restants d'un navire voulu
	 */
	public int getNbDeplacementsRestants(int joueur, int navire) {
		return navireSaves[joueur][navire].getNbDeplacementsRestants();
	}

	/**
	 * Renvoie les informations relatives aux phares pris par les joueurs.
	 * @return Un tableau contenant pour chaque indice (correspondant à un numéro de phare) l'id du joueur possédant le phare ou -1 si aucun joueur ne possède le phare 
	 * (l'ordre croissant des numéros de phare part de gauche à droite puis de haut en bas de la map)
	 */
	public Vector<Integer> getPossessionPhares() {
		return possessionPhares;
	}
	
	private class NavireSave {
		Integer posTabX, posTabY, pv,
			nbTourRechargeCanonPrincipal, 
			nbTourRechargeCanonSecondaire,
			direction, nbDeplacementsRestants;
		
		public NavireSave() {
			
		}
		
		public NavireSave(BufferedReader br) throws IOException {
			load(br);
		}
		
		public NavireSave(Point posTab, int pv, 
				int nbTourRechargeCanonPrincipal, int nbTourRechargeCanonSecondaire,
				int direction, int nbDeplacementsRestants) {
			this.posTabX = posTab.x;
			this.posTabY = posTab.y;
			this.pv = pv;
			this.nbTourRechargeCanonPrincipal = nbTourRechargeCanonPrincipal;
			this.nbTourRechargeCanonSecondaire = nbTourRechargeCanonSecondaire;
			this.direction = direction;
			this.nbDeplacementsRestants = nbDeplacementsRestants;
		}
		
		public void load(BufferedReader br) throws IOException {
			String line;
			if ((line = br.readLine()) != null) posTabX = Integer.parseInt(line);
			if ((line = br.readLine()) != null) posTabY = Integer.parseInt(line);
			if ((line = br.readLine()) != null) pv = Integer.parseInt(line);
			if ((line = br.readLine()) != null) nbTourRechargeCanonPrincipal = Integer.parseInt(line);
			if ((line = br.readLine()) != null) nbTourRechargeCanonSecondaire = Integer.parseInt(line);
			if ((line = br.readLine()) != null) direction = Integer.parseInt(line);
			if ((line = br.readLine()) != null) nbDeplacementsRestants = Integer.parseInt(line);
			line = br.readLine();
		}
		
		public void save(BufferedOutputStream bos) throws IOException {
			writeNumberLn(bos, posTabX);
			writeNumberLn(bos, posTabY);
			writeNumberLn(bos, pv);
			writeNumberLn(bos, nbTourRechargeCanonPrincipal);
			writeNumberLn(bos, nbTourRechargeCanonSecondaire);
			writeNumberLn(bos, direction);
			writeNumberLn(bos, nbDeplacementsRestants);
			writeStringLn(bos, "#");
		}
		
		public int getPosTabX() {
			return posTabX;
		}
		
		public int getPosTabY() {
			return posTabY;
		}
		
		public int getPv() {
			return pv;
		}
		
		public int getNbTourRechargeCanonPrincipal() {
			return nbTourRechargeCanonPrincipal;
		}
		
		public int getNbTourRechargeCanonSecondaire() {
			return nbTourRechargeCanonSecondaire;
		}
		
		public int getDirection() {
			return direction;
		}
		
		public int getNbDeplacementsRestants() {
			return nbDeplacementsRestants;
		}
	}
}
