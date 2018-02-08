package utility;

import java.awt.Point;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Vector;

import org.newdawn.slick.geom.Vector2f;

/**
 * Utilitaire permettant d'avoir une API simple pour l'écriture et la lecture dans un fichier 
 * (contient par exemple des fonctions de chargement ou sauvegarde d'une map dans un fichier).  Design pattern Singleton
 */
public class FileUtility implements Serializable {
	/**
	 * Constante pour récupérer facilement le chemin vers le dossier de sprites
	 */
	public static final String DOSSIER_SPRITE = "resources/sprites/";
	
	/**
	 * Constante pour récupérer facilement le chemin vers le dossier de maps
	 */
	public static final String DOSSIER_MAP = "resources/map/";
	
	private static FileUtility INSTANCE = new FileUtility();
	
	private FileUtility() {
		
	}
 
	/**
	 * Renvoie l'unique instance de FileUtility.
	 * @return L'unique instance de FileUtility
	 */
	public static FileUtility getInstance() {
		return INSTANCE;
	}
 
	/// Securite anti-deserialisation
	private Object readResolve() {
		return INSTANCE;
	}

	//////////////////////////////////////////////////
	//                   METHODES                   //
	//////////////////////////////////////////////////
	
	/**
	 * Renvoie la liste des noms des maps.
	 * @return La liste des noms des maps
	 */
	public Vector<String> loadMapNames() {
		File repertoire = new File(DOSSIER_MAP);
		return new Vector<String>(Arrays.asList(repertoire.list()));
	}
	
	/**
	 * Charge la map nomMap.
	 * @param nomMap Le nom de la map à charger
	 * @return Les informations de la map sous forme d'un tableau dynamique à deux dimensions contenant les ID des cases
	 */
	public Vector< Vector<Integer> > loadMap(String nomMap) {
		Vector < Vector<Integer> > tabMap = new Vector < Vector<Integer> >();
	    BufferedInputStream bis;
	    
	    try {
	    	bis = new BufferedInputStream(new FileInputStream(new File(DOSSIER_MAP + nomMap)));
	    	byte[] buf = new byte[8];

	    	tabMap.add(new Vector<Integer>());
	    	while(bis.read(buf) != -1) {
		    	for (byte bit : buf) {
		    		if (bit == '\r' || bit == '\n') {
		    			if (tabMap.lastElement().size() > 0) {
		    				tabMap.add(new Vector<Integer>());
		    			}
		    		} else if (bit >= '0' && bit <= '9') {
		    			tabMap.lastElement().add(Character.getNumericValue(bit));
		    		}
		    	}
		    	buf = new byte[8];
	    	}

	    	if (tabMap.lastElement().size() <= 0) {
	    		tabMap.removeElementAt(tabMap.size()-1);
	    	}
	    	rendreTabMapRectangle(tabMap);

	    	bis.close();

    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

	    return tabMap;
	}
	
	/**
	 * Supprime la map ayant pour nom nomMap.
	 * @param nomMap Le nom de la map à supprimer
	 */
	public void supprimer(String nomMap) {
		File f = new File(DOSSIER_MAP + nomMap);
		f.delete();
	}
	
	/**
	 * Enregistre la map nomMap.
	 * @param nomMap le nom de la map à enregistrer
	 * @param tabMap Les informations de la map sous forme d'un tableau dynamique à deux dimensions contenant les ID des cases
	 */
	public void saveMap(String nomMap, Vector< Vector<Integer> > tabMap) {
		BufferedOutputStream bos;
		String id_str;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(DOSSIER_MAP + nomMap)));

			for (int j = 0; j < tabMap.size(); j++) {
				for (int i = 0; i < tabMap.get(j).size(); i++) {
					id_str = (tabMap.get(j).get(i)).toString();
					if (id_str.length() > 0) {
						bos.write(id_str.charAt(0));
					} else {
						bos.write('0');
					}
				}
				if (j < tabMap.size() - 1) {
					bos.write('\r');
					bos.write('\n');
				}
			}

			bos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	
	/**
	 * Renvoie le nombre de colonnes et de lignes de la map nomMap.
	 * @param nomMap Le nom de la map dont on veut le nombre de colonne et de ligne
	 * @return Le nombre de colonnes et de lignes sous formes de coordonnées (nbColonnes, nbLignes)
	 */
	public Point getNbCasesMap(String nomMap) {
		Point nbCases = new Point(0, 0);
	    BufferedInputStream bis;
	    int nbCasesLigneMax = 0;
	    
	    try {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(DOSSIER_MAP + nomMap);
		bis = new BufferedInputStream(in);
	    	byte[] buf = new byte[8];

	    	while(bis.read(buf) != -1) {
		    	for (byte bit : buf) {
		    		if (bit == '\r' || bit == '\n') {
		    			if (nbCases.getX() > 0) {
		    				if (nbCasesLigneMax < nbCases.x) {
		    					nbCasesLigneMax = nbCases.x;
			    			}
		    				nbCases.y++;
		    			}
		    			nbCases.x = 0;
		    		} else if (bit >= '0' && bit <= '9') {
		    			nbCases.x++;
		    		}
		    	}
		    	buf = new byte[8];
	    	}
	    	if (nbCases.x > 0) {
	    		if (nbCasesLigneMax > nbCases.x) {
	    			nbCasesLigneMax = nbCases.x;
    			}
	    		nbCases.y++;
	    	}
	    	nbCases.x = nbCasesLigneMax;

	    	bis.close();

    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
		
		return nbCases;
	}
	
	/**
	 * Réajuste le tableau à deux dimensions d'ID de cases de sorte à ce qu'il soit rectangle 
	 * (toute les lignes contiendront le même nombre de cases c'est à dire nbCasesLigne cases).
	 * @param tabMap Le tableau à rectangulariser
	 * @param nbCasesLigne Le nombre de cases que contiendra chaque ligne
	 */
	public void rendreTabMapRectangle(Vector < Vector<Integer> > tabMap, int nbCasesLigne) {
		for (Vector<Integer> ligneTab : tabMap) {
			if (nbCasesLigne < ligneTab.size()) {
				ligneTab.setSize(nbCasesLigne);
			} else {
				for (int i = ligneTab.size(); i < nbCasesLigne; i++) {
					ligneTab.add(0);
				}
			}
		}
	}
	
	/**
	 * Réajuste le tableau à deux dimensions d'ID de cases de sorte à ce qu'il soit rectangle 
	 * (toute les lignes contiendront le même nombre de cases c'est à dire autant que la ligne qui contenait le plus de cases).
	 * @param tabMap Le tableau à rectangulariser
	 */
	public void rendreTabMapRectangle(Vector < Vector<Integer> > tabMap) {
		int nbCasesLigneMax = 0;
		for (Vector<Integer> ligneTab : tabMap) {
			if (ligneTab.size() > nbCasesLigneMax) {
				nbCasesLigneMax = ligneTab.size();
			}
		}
		rendreTabMapRectangle(tabMap, nbCasesLigneMax);
	}
	
	/**
	 * Affiche sur la console le tableau à deux dimensions d'ID de cases entré en paramètre (utile pour du débugage).
	 * @param tabMap Le tableau à deux dimensions d'ID de cases
	 */
	public void printTabMap(Vector < Vector<Integer> > tabMap) {
		for (Vector<Integer> ligneTab : tabMap) {
			for (int caseTab : ligneTab) {
				System.out.print(caseTab);
			}
			System.out.println();
		}
	}
}
