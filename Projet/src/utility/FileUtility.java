package utility;

import java.awt.Point;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;

import org.newdawn.slick.geom.Vector2f;

public class FileUtility implements Serializable {
	// Constantes pour le chemin vers les ressources
	public static final String DOSSIER_SPRITE = "resources/sprites/";
	public static final String DOSSIER_MAP = "resources/map/";
	
	private static FileUtility INSTANCE = new FileUtility();
	
	private FileUtility() {
		
	}
 
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
	
	public Vector< Vector<Integer> > loadMap(String nomMap) {
		Vector < Vector<Integer> > tabMap = new Vector < Vector<Integer> >();
	    BufferedInputStream bis;
	    
	    try {
	    	InputStream in = this.getClass().getClassLoader().getResourceAsStream(DOSSIER_MAP + nomMap);
		bis = new BufferedInputStream(in);
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

	public Vector< Vector<Integer> > loadMapFromJar(String nomMap) {
		Vector < Vector<Integer> > tabMap = new Vector < Vector<Integer> >();
	    BufferedInputStream bis;
	    
	    try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(DOSSIER_MAP + nomMap);
			bis = new BufferedInputStream(in);
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
	
	public void saveMap(String nomMap, Vector< Vector<Integer> > tabMap) {
		BufferedOutputStream bos;
		String id_str;

		try {
			URL mapURL = getClass().getResource(DOSSIER_MAP + nomMap);
			File file = new File(mapURL.toURI().toString());
			bos = new BufferedOutputStream(new FileOutputStream(file));

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
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
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
	
	public void rendreTabMapRectangle(Vector < Vector<Integer> > tabMap, int nbCasesLigne) {
		for (Vector<Integer> ligneTab : tabMap) {
			for (int i = ligneTab.size(); i < nbCasesLigne; i++) {
				ligneTab.add(0);
			}
		}
	}
	
	public void rendreTabMapRectangle(Vector < Vector<Integer> > tabMap) {
		int nbCasesLigneMax = 0;
		for (Vector<Integer> ligneTab : tabMap) {
			if (ligneTab.size() > nbCasesLigneMax) {
				nbCasesLigneMax = ligneTab.size();
			}
		}
		rendreTabMapRectangle(tabMap, nbCasesLigneMax);
	}
	
	public void printTabMap(Vector < Vector<Integer> > tabMap) {
		for (Vector<Integer> ligneTab : tabMap) {
			for (int caseTab : ligneTab) {
				System.out.print(caseTab);
			}
			System.out.println();
		}
	}
}
