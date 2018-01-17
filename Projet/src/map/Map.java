package map;

import java.awt.Point;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import joueur.Navire;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import utility.FileUtility;

public class Map implements Serializable {
	private static Map INSTANCE;
	public static final String FICHIER_SPRITE_SHEET_MAP = "spriteSheetMap.png";
	
	// Constantes pour la construction d'un maillage d'hexagones
	public static final int LONGUEUR_COTE_TUILE = 64;
	private static final int DECALAGE_X = (int) ((float) LONGUEUR_COTE_TUILE * 3f / 4f);
	private static final int DENIVELE_Y = (int) (Math.sin(1) * (float) LONGUEUR_COTE_TUILE / 2f);//60 deg = 1.0472 rad
	private static final int DECALAGE_Y = (int) 2 * DENIVELE_Y;
	private static final int MARGE_ORIGINE_Y = (int) (LONGUEUR_COTE_TUILE / 2f - DENIVELE_Y);

	private Class<?> typeDeCaseParId[] = {
		Ocean.class, 
		Terre.class,
		Phare.class,
	};
		/*PhareJ1.class,
		PhareJ2.class,
	};*/
	private Vector< Vector<Case> > grille;
	private java.util.Map<Navire, Point> navires = new HashMap<Navire, Point>();
	private SpriteSheet spriteSheet;
	private int sensPremierDenivele = -1;
	
	private Point position = new Point();
	private float scaleX = 1f;
	private float scaleY = 1f;
	private SelecteurCase selecteurCase;
	
	private Map() throws SlickException {
		init();
	}
	
	public static Map getInstance() {
		return INSTANCE;
	}
	
	public static void initInstance() throws SlickException {
		INSTANCE = new Map();
	}

	public void init() throws SlickException {
		
		selecteurCase = new SelecteurCase(typeDeCaseParId.length);

		spriteSheet = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_MAP, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
	}
	
	//////////////////
	/// ACCESSEURS ///
	//////////////////
	public void addNavire(Navire navire, int coordTabX, int coordTabY) {
		Point coordTab = new Point(coordTabX, coordTabY);
		navire.setPosition(coordTabToMaillage(coordTab));
		navires.put(navire, coordTab);
	}
	
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}
	
	public final float getLongueurAbsolueCoteTuile() {
		return LONGUEUR_COTE_TUILE;
	}

	public int getSensPremierDenivele() {
		return sensPremierDenivele;
	}

	public Point getPosition() {
		return position;
	}
	
	public void setPosition(int x, int y) {
		position.x = x;
		position.y = y - Math.round(MARGE_ORIGINE_Y * scaleY);
	}
	
	public Point getNbCases() {
		Point nbCases = new Point();
		
		if (grille.size() > 0) {
			nbCases.x = grille.get(0).size();
		} else {
			nbCases.x = 0;
		}
		nbCases.y = grille.size();
		
		return nbCases;
	}
	
	public void setNbCases(int x, int y) {
		int oldX;
		int oldY;
		Point coordTab;
		
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (grille.size() > 0) {
			oldX = grille.get(0).size();
		} else {
			oldX = 0;
		}
		oldY = grille.size();

		//redimensionnement sur x
		for (int j = 0; j < grille.size(); j++) {
			grille.get(j).setSize(x);
			for (int i = oldX; i < grille.get(j).size(); i++) {
				coordTab = new Point(i, j);
				grille.get(j).set(i, new Ocean(coordTabToMaillage(coordTab)));
			}
		}
		
		//redimensionnement sur y
		grille.setSize(y);
		for (int j = oldY; j < grille.size(); j++) {
			grille.set(j, new Vector<Case>());
			for (int i = 0; i < x; i++) {
				coordTab = new Point(i, j);
				grille.get(j).add(new Ocean(coordTabToMaillage(coordTab)));
			}
		}
	}
	
	public Point getTailleAbsolue() {
		Point taille = new Point();
		Point nbCases = getNbCases();

		taille.x = DECALAGE_X * (nbCases.x - 1) + LONGUEUR_COTE_TUILE;
		taille.y = DECALAGE_Y * nbCases.y + DENIVELE_Y + LONGUEUR_COTE_TUILE - DECALAGE_Y;
		
		return taille;
	}
	
	public Point getTaille() {
		Point taille = new Point();
		Point nbCases = getNbCases();

		taille.x = Math.round(DECALAGE_X * scaleX) * (nbCases.x - 1) + Math.round(LONGUEUR_COTE_TUILE * scaleX);
		taille.y = Math.round(DECALAGE_Y * scaleY) * nbCases.y + Math.round(DENIVELE_Y * scaleY)  + Math.round(LONGUEUR_COTE_TUILE * scaleY) - Math.round(DECALAGE_Y * scaleY);
		
		return taille;
	}
	
	public void setScale(float x, float y) {
		scaleX = x;
		scaleY = y;
		
		reagencerMaillage();
		for (Navire navire : navires.keySet()) {
			navire.setPosition(coordTabToMaillage(navires.get(navire)));
		}
	}
	
	public void setTaille(int hauteur, int largeur) {
		Point tailleAbsolue = getTailleAbsolue();
		setScale((float)(hauteur) / tailleAbsolue.x, (float)(largeur) / tailleAbsolue.y);
	}
	
	public void setHauteur(int hauteur) {
		Point tailleAbsolue = getTailleAbsolue();
		setScale((float)(hauteur) / tailleAbsolue.x, (float)(hauteur) / tailleAbsolue.x);
	}
	
	public void setLargeur(int largeur) {
		Point tailleAbsolue = getTailleAbsolue();
		setScale((float)(largeur) / tailleAbsolue.x, (float)(largeur) / tailleAbsolue.x);
	}
	
	public float getScaleX() {
		return scaleX;
	}
	
	public float getScaleY() {
		return scaleY;
	}
	
	public boolean isAgencementMaillageHaut() {
		return (sensPremierDenivele == -1);
	}
	
	public boolean isAgencementMaillageBas() {
		return (sensPremierDenivele == 1);
	}
	
	public void setAgencementMaillageHaut() {
		sensPremierDenivele = -1;
		reagencerMaillage();
	}
	
	public void setAgencementMaillageBas() {
		sensPremierDenivele = 1;
		reagencerMaillage();
	}
	
	public void changerAgencementMaillage() {
		sensPremierDenivele = -sensPremierDenivele;
		reagencerMaillage();
	}
	
	///////////////
	/// ACTIONS ///
	///////////////
	public void centrerDansFenetre(GameContainer container) {
		Point tailleMap = this.getTaille();
		this.setPosition((container.getWidth() - tailleMap.x) / 2, (container.getHeight() - tailleMap.y) / 2);
	}
	
	public Point coordTabToMaillage(Point coordTab) {
		Point coordMaillage = new Point();

		coordMaillage.x = coordTab.x * Math.round(DECALAGE_X * scaleX);
		coordMaillage.y = coordTab.y * Math.round(DECALAGE_Y * scaleY);

		if (sensPremierDenivele == -1 && coordTab.x % 2 == 0 || 
			sensPremierDenivele ==  1 && Math.abs(coordTab.x % 2) == 1) {
			coordMaillage.y += Math.round(DENIVELE_Y * scaleY);
		}
		
		return coordMaillage;
	}
	
	public Point coordMaillageToTab(Point coordMaillage) {
		Point coordTab = new Point();
		Point coordMaillageClone = new Point(coordMaillage);

		coordTab.x = coordMaillageClone.x / Math.round(DECALAGE_X * scaleX);

		if (sensPremierDenivele == -1 && coordTab.x % 2 == 0 ||
			sensPremierDenivele ==  1 && Math.abs(coordTab.x % 2) == 1) {
			coordMaillageClone.y -= Math.round(DENIVELE_Y * scaleY);
		}

		coordTab.y = coordMaillageClone.y / Math.round(DECALAGE_Y * scaleY);
		
		return coordTab;
	}
	
	public void load(String nomMap) {
		Vector < Vector<Integer> > tabMap = FileUtility.getInstance().loadMap(nomMap);
		int sensDenivele = sensPremierDenivele;
		int x = 0;
		int y = 0;
		
		if (sensDenivele == -1) {
			y = Math.round(DENIVELE_Y * scaleY);
		}
		
		FileUtility.getInstance().printTabMap(tabMap);

		setPosition(0, 0);
		grille = new Vector< Vector<Case> >();
		navires = new HashMap<Navire, Point>();

		for (int j = 0; j < tabMap.size(); j++) {
			
			x = 0;
			grille.add(new Vector<Case>());
			for (int i = 0; i < tabMap.get(j).size(); i++) {
				Case caseCourante;
				try {
					if (tabMap.get(j).get(i) < typeDeCaseParId.length) {
						caseCourante = (Case) (typeDeCaseParId[ tabMap.get(j).get(i) ])
								.getConstructor(Point.class)
								.newInstance(new Point(x, y));
					} else {
						throw new ArrayIndexOutOfBoundsException();
					}
					
				} catch (InstantiationException e1) {
					caseCourante = new Ocean(new Point(x, y));
				} catch (IllegalAccessException e1) {
					caseCourante = new Ocean(new Point(x, y));
				} catch (IllegalArgumentException e1) {
					caseCourante = new Ocean(new Point(x, y));
				} catch (InvocationTargetException e1) {
					caseCourante = new Ocean(new Point(x, y));
				} catch (NoSuchMethodException e1) {
					caseCourante = new Ocean(new Point(x, y));
				} catch (SecurityException e1) {
					caseCourante = new Ocean(new Point(x, y));
				} catch (ArrayIndexOutOfBoundsException e1) {
					caseCourante = new Ocean(new Point(x, y));
				}
				
				grille.lastElement().add(caseCourante);
				x += Math.round(DECALAGE_X * scaleX);
				y += Math.round(DENIVELE_Y * scaleY * sensDenivele);
				sensDenivele = -sensDenivele;
			}

			if (sensDenivele != sensPremierDenivele) {
				sensDenivele = sensPremierDenivele;
				y += Math.round(DENIVELE_Y * scaleY);
			}
			y += Math.round(DECALAGE_Y * scaleY);
		}
	}
	
	public void save(String nomMap) {
		Vector< Vector<Integer> > tabMap = new Vector< Vector<Integer> >();
		for (int j = 0; j < grille.size(); j++) {
			tabMap.add(new Vector<Integer>());
			for (int i = 0; i < grille.get(j).size(); i++) {
				tabMap.get(j).add(grille.get(j).get(i).getId());
			}
		}
		FileUtility.getInstance().saveMap(nomMap, tabMap);
	}
	
	public void selectionnerCase(int idCase, Point coordTab) {
		boolean bordsTabAtteint = true;
		selecteurCase.setIdCaseSelectionnee(idCase);
		
		bordsTabAtteint = checkBordsTabEtAjusterCoord(coordTab);

		if (!bordsTabAtteint) {
			selecteurCase.setSelecteurVisible(true);
			selecteurCase.setPosition(grille.get(coordTab.y).get(coordTab.x).getPosition());
		} else {
			selecteurCase.setSelecteurVisible(false);
		}
	}
	
	//TODO : factoriser avec selectionnerCase la gestion du risque outOfBounds
	public void mettreCase(int idCase, Point coordTab) {
		if (coordTab.y < grille.size()) {
			if (coordTab.x < grille.get(coordTab.y).size()) {
				Point posCase = grille.get(coordTab.y).get(coordTab.x).getPosition();
				
				try {
					if (idCase < typeDeCaseParId.length) {
						grille.get(coordTab.y).set(coordTab.x, (Case)(typeDeCaseParId[idCase])
								.getConstructor(Point.class)
								.newInstance(posCase));
					} else {
						throw new ArrayIndexOutOfBoundsException();
					}
					
				} catch (InstantiationException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(posCase));
				} catch (IllegalAccessException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(posCase));
				} catch (IllegalArgumentException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(posCase));
				} catch (InvocationTargetException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(posCase));
				} catch (NoSuchMethodException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(posCase));
				} catch (SecurityException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(posCase));
				} catch (ArrayIndexOutOfBoundsException e1) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(posCase));
				}
			}
		}
	}

	//TODO : factoriser avec selectionnerCase la gestion du risque outOfBounds
	public void deplacer(Navire navire, Direction direction) {
		Point coordTab = navires.get(navire);
		Point coordCible = new Point(coordTab);
		int directionCible = navire.getDirection();
		
		if (coordTab != null) {
			switch (direction) {
			case HAUT:
				if(navire.getDirection()==5 || navire.getDirection()==0 || navire.getDirection()==1){
					coordCible.y--;
					directionCible = 0;
				}
				break;
			case HAUT_DROITE:
				if(navire.getDirection()==0 || navire.getDirection()==1 || navire.getDirection()==2){
					coordCible.x++;
					if (sensPremierDenivele == -1 && Math.abs(coordTab.x % 2) == 1 ||
							sensPremierDenivele ==  1 && coordTab.x % 2 == 0) {
						coordCible.y--;
					}
					directionCible = 1;
				}
				break;
			case BAS_DROITE:
				if(navire.getDirection()==1 || navire.getDirection()==2 || navire.getDirection()==3){
					coordCible.x++;
					if (sensPremierDenivele == -1 && coordTab.x % 2 == 0 ||
							sensPremierDenivele ==  1 && Math.abs(coordTab.x % 2) == 1) {
						coordCible.y++;
					}
					directionCible = 2;
				}
				break;
			case BAS:
				if(navire.getDirection()==2 || navire.getDirection()==3 || navire.getDirection()==4){
					coordCible.y++;
					directionCible = 3;
				}
				break;
			case BAS_GAUCHE:
				if(navire.getDirection()==3 || navire.getDirection()==4 || navire.getDirection()==5){
					coordCible.x--;
					if (sensPremierDenivele == -1 && coordTab.x % 2 == 0 ||
							sensPremierDenivele ==  1 && Math.abs(coordTab.x % 2) == 1) {
						coordCible.y++;
					}
					directionCible = 4;
				}
				break;
			case HAUT_GAUCHE:
				if(navire.getDirection()==4 || navire.getDirection()==5 || navire.getDirection()==0){
					coordCible.x--;
					if (sensPremierDenivele == -1 && Math.abs(coordTab.x % 2) == 1 ||
							sensPremierDenivele ==  1 && coordTab.x % 2 == 0) {
						coordCible.y--;
					}
					directionCible = 5;
				}
				break;
			default:
				break;
			}
			
			// GESTION DES ERREURS DE COLLISIONS :
			// bords de la map
			if (checkBordsTab(coordCible)) {
				System.out.println("Throw : Collision avec les bords de la map");
				return;
			}
			
			// case interdite (terre)
			if (grille.get(coordCible.y).get(coordCible.x).getId()==1) {//grid[][]
				System.out.println("Throw : Collision avec une case interdite");
				return;
			}
			
			// navire sur la case
			//checkCollisions(coordCible);
			Set<Entry<Navire, Point>> set = navires.entrySet();
			Iterator<Entry<Navire, Point>> it = set.iterator();
			while(it.hasNext()){
				Entry<Navire, Point> e = it.next();
				if (e.getKey() != navire && coordCible.equals(e.getValue())) {
					System.out.println("Throw : Collision avec navire : "+e.getKey());
					return;
				}
			}
			
			// le deplacement s'effectue si il n'y a pas d'erreur
			coordTab.setLocation(coordCible);
			// tentative d'animation à la place du changement direct des coordonnées
			// navire.setPosition(coordTabToMaillage(coordTab));
			navire.initialiserDeplacement(coordTabToMaillage(coordTab), directionCible);

		} else {
			System.out.println("Erreur : Deplacement d'un navire qui n'a pas ete ajoute dans la map");
		}
	}
	
	public void draw() {
		for (Vector <Case> ligne : grille) {
			for (Case caseCourante : ligne) {
				caseCourante.draw();
			}
		}
		selecteurCase.draw();

		for (Navire navire : navires.keySet()) {
			navire.draw();
		}
    }
	
	//////////////////////////
	/// FONCTIONS INTERNES ///
	//////////////////////////
	private boolean checkCollisions(Point coordTab) {
		return false;
	}

	private boolean checkBordsTab(Point coordTab) {
		boolean coordHorsBordsTab = true;
		
		if (grille.size() > 0) {
			if (coordTab.y >= 0 && coordTab.y < grille.size()) {
				if (coordTab.x >= 0 && coordTab.x < grille.get(coordTab.y).size()) {
					coordHorsBordsTab = false;
				}
			}
		}
		return coordHorsBordsTab;
	}
	
	private boolean checkBordsTabEtAjusterCoord(Point coordTab) {
		boolean coordHorsBordsTab = true;
		
		if (coordTab.x < 0) coordTab.x = 0;
		if (coordTab.y < 0) coordTab.y = 0;
		
		if (grille.size() > 0) {
			if (coordTab.y < grille.size()) {
				coordHorsBordsTab = false;
			} else {
				coordTab.y = grille.size() - 1;
			}
			if (coordTab.x < grille.get(coordTab.y).size()) {
				coordHorsBordsTab =  false;
			} else if (grille.get(coordTab.y).size() > 0) {
				coordTab.x = grille.get(coordTab.y).size() - 1;
			} else {
				coordTab.x = 0;
				coordHorsBordsTab = true;
			}
		} else {
			coordTab.y = 0;
			coordHorsBordsTab = true;
		}
		return coordHorsBordsTab;
	}
	
	private void reagencerMaillage() {
		int sensDenivele = sensPremierDenivele;
		int x = 0;
		int y = 0;
		
		if (sensDenivele == -1) {
			y = Math.round(DENIVELE_Y * scaleY);
		}
		
		//setPosition(0, 0);


		for (Vector <Case> ligne : grille) {
			x = 0;
			for (Case caseCourante : ligne) {
				caseCourante.setPosition(new Point(x, y));
					
				x += Math.round(DECALAGE_X * scaleX);
				y += Math.round(DENIVELE_Y * scaleY * sensDenivele);
				sensDenivele = -sensDenivele;
			}
			if (sensDenivele != sensPremierDenivele) {
				sensDenivele = sensPremierDenivele;
				y += Math.round(DENIVELE_Y * scaleY);
			}
			y += Math.round(DECALAGE_Y * scaleY);
		}
	}
}
