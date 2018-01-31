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

public class Save {
	// Constantes pour le chemin vers les ressources
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
 
	public static Save getInstance() {
		return INSTANCE;
	}
	
	public static void initInstance() {
		INSTANCE = new Save();
	}
	
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
	
	public void setNavireSave(int joueur, int navire, Point posTab, int pv, 
			int nbTourRechargeCanonPrincipal, int nbTourRechargeCanonSecondaire,
			int direction, int nbDeplacementsRestants) {
		navireSaves[joueur][navire] = new NavireSave(posTab, pv, nbTourRechargeCanonPrincipal, nbTourRechargeCanonSecondaire, direction, nbDeplacementsRestants);
	}
	
	public void setNomMap(String nomMap) {
		this.nomMap = nomMap;
	}
	
	public void setJoueurCourant(int joueurCourant) {
		this.joueurCourant = joueurCourant;
	}
	
	public void setNavireCourant(int navireCourant) {
		this.navireCourant = navireCourant;
	}
	
	public void setPossessionPhares(Vector<Integer> possessionPhares) {
		this.possessionPhares = possessionPhares;
	}
	
	public boolean isSauvegardePresente() {
		return sauvegardePresente;
	}
	
	public void setSauvegardePresente(boolean sauvegardePresente) {
		this.sauvegardePresente = sauvegardePresente;
	}
	
	public String getNomMap() {
		return nomMap;
	}
	
	public int getJoueurCourant() {
		return joueurCourant;
	}
	
	public int getNavireCourant() {
		return navireCourant;
	}
	
	public int getPosTabX(int joueur, int navire) {
		return navireSaves[joueur][navire].getPosTabX();
	}
	
	public int getPosTabY(int joueur, int navire) {
		return navireSaves[joueur][navire].getPosTabY();
	}
	
	public int getPv(int joueur, int navire) {
		return navireSaves[joueur][navire].getPv();
	}
	
	public int getNbTourRechargeCanonPrincipal(int joueur, int navire) {
		return navireSaves[joueur][navire].getNbTourRechargeCanonPrincipal();
	}
	
	public int getNbTourRechargeCanonSecondaire(int joueur, int navire) {
		return navireSaves[joueur][navire].getNbTourRechargeCanonSecondaire();
	}
	
	public int getDirection(int joueur, int navire) {
		return navireSaves[joueur][navire].getDirection();
	}
	
	public int getNbDeplacementsRestants(int joueur, int navire) {
		return navireSaves[joueur][navire].getNbDeplacementsRestants();
	}
	
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
