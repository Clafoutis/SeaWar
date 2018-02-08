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

import joueur.Joueur;
import joueur.Navire;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import game.Game;
import utility.FileUtility;

/**
 * Classe repr�sentant la map du  jeu. 
 * Design pattern Singleton : une seul instance est cr��e et utilis�e dans l'application. 
 * L'instance de Map devra �tre initialis� au d�but de l'application en appelant la fonction initInstance(). 
 * L'instance de Map peut �tre utiliser pour jouer sur une carte ou pour �diter une carte. Il faut pour cela appeler startGameMode() ou startEditeurMode() pour 
 * passer dans le mode voulu. 
 * La map du jeu est mod�lis�e par un tableau � deux dimensions ce qui fait que l'on peut parler de coordonn�es dans la fen�tre 
 * (pour parler de la position dans la fenetre dans la map par exemple) mais aussi de coordonn�es dans le tableau.
 * Pour �viter toute ambigu�t�, on pose d�s � pr�sent comme principe que quand on parle de "la position" on parle des "coordonn�es (x, y) dans la fen�tre" 
 * et que quand on d�signe les "coordonn�es dans le tableau", on parle de "� telle colonne et telle ligne du tableau qui mod�lise la Map".
 * Par convention, l'origine de la Map se situe en haut � gauche.
 */
public class Map implements Serializable {
	private static Map INSTANCE;
	
	/**
	 * Constante pour rep�rer facilement le fichier du spriteSheet de la map si besoin.
	 */
	public static final String FICHIER_SPRITE_SHEET_MAP = "spriteSheetMap.png";
	
	/**
	 * Constante pour r�cup�rer facilement la longueur du c�t� d'un sprite repr�sentant une case.
	 */
	public static final int LONGUEUR_COTE_TUILE = 64;
	
	// Constantes pour la construction d'un maillage d'hexagones
	private static final int DECALAGE_X = (int) ((float) LONGUEUR_COTE_TUILE * 3f / 4f);
	private static final int DENIVELE_Y = (int) (Math.sin(1) * (float) LONGUEUR_COTE_TUILE / 2f);//60 deg = 1.0472 rad
	private static final int DECALAGE_Y = (int) 2 * DENIVELE_Y;
	private static final int MARGE_ORIGINE_Y = (int) (LONGUEUR_COTE_TUILE / 2f - DENIVELE_Y);

	private Class<?> typeDeCaseParId[] = {
		Ocean.class, 
		Terre.class,
		Phare.class
	};
	private Vector< Vector<Case> > grille = new Vector< Vector<Case> >();
	private Vector<Phare> phares = new Vector<Phare>();
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
	
	/**
	 * Renvoie l'unique instance de Map.
	 * @return l'unique instance de Map
	 */
	public static Map getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Initialise l'instance, cette fonction doit �tre appel�e en statique au tout d�but du programme 
	 * (typiquement dans la fonction "initStatesList" qui initialise les vues de l'application).
	 * @throws SlickException si le chargement de SpriteSheet a �chou� (mauvais nom de fichier de texture, ...)
	 */
	public static void initInstance() throws SlickException {
		INSTANCE = new Map();
	}

	private void init() throws SlickException {
		selecteurCase = new SelecteurCase();
		spriteSheet = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_MAP, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
	}
	
	/**
	 * Passe la map en mode "Game", � appeler au d�but d'une partie pour initialiser correctement la map.
	 */
	public void startGameMode() {
		selecteurCase.setSelecteurVisible(false);
	}

	/**
	 * Passe la map en mode "Editeur", � appeler au d�but de l'�diteur de map pour initialiser correctement la map.
	 */
	public void startEditeurMode() {
		this.setNbCases(1, 1);
		this.selectionnerCase(0, new Point(0, 0));
	}
	
	
	//////////////////
	/// ACCESSEURS ///
	//////////////////
	/**
	 * Ajoute un navire sur la map.
	 * @param navire le navire � ajouter
	 * @param coordTabX la coordonn�e en X dans le tableau o� placer le navire
	 * @param coordTabY la coordonn�e en Y dans le tableau o� placer le navire
	 */
	public void addNavire(Navire navire, int coordTabX, int coordTabY) {
		Point coordTab = new Point(coordTabX, coordTabY);
		navire.setPosition(coordTabToMaillage(coordTab));
		navires.put(navire, coordTab);
	}

	/**
	 * Supprime le navire dont la r�f�rence est pass�e en param�tre.
	 * @param navire la r�f�rence du navire � supprimer
	 */
	public void removeNavire(Navire navire) {
		navires.remove(navire);
	}

	/**
	 * Renvoie la liste des navires avec leurs coordonn�es dans la tableau.
	 * @return la liste des navires avec leurs coordonn�es dans la tableau
	 */
	public java.util.Map<Navire, Point> getNavires(){
		return navires;
	}

	/**
	 * Renvoie le tableau contenant toutes les cases de la map, utile si l'on veut v�rifier le contenu d'une case pr�cise en dehors de la classe Map.
	 * @return le tableau contenant toutes les cases de la map.
	 */
	public Vector<Vector<Case>> getGrille() {
		return grille;
	}
	
	/**
	 * Renvoie le tableau contenant toutes les classes repr�sentant des Cases sp�cifiques (Ocean, Terre, ...), 
	 * l'indice o� est stock� la Case repr�sente l'ID de cette m�me case.
	 * @return le tableau contenant toutes les classes repr�sentant des Cases sp�cifiques (Ocean, Terre, ...)
	 */
	public Class<?>[] getTypeCasesParId() {
		return typeDeCaseParId;
	}
	
	/**
	 * Renvoie le spriteSheet sur lequel se trouve la texture de toutes les cases, 
	 * ce spriteSheet n'est charg� qu'une fois dans le jeu et on n'y acc�de par cette fonction.
	 * @return le spriteSheet sur lequel se trouve la texture de toutes les cases
	 */
	public SpriteSheet getSpriteSheet() {
		return spriteSheet;
	}
	
	/**
	 * Renvoie la longueur du cot� d'un sprite (carr�) d'une case sans prendre en compte le changement d'�chelle.
	 * @return la longueur du cot� d'un sprite (carr�) d'une case sans prendre en compte le changement d'�chelle
	 */
	public final float getLongueurAbsolueCoteTuile() {
		return LONGUEUR_COTE_TUILE;
	}

	/**
	 * Permet de v�rifier la fa�on dont est agenc� le maillage de la map.
	 * Renvoie 1 si l'agencement de la map fait qu'il y a une seule case qui est la plus en haut � gauche de la map  
	 * et renvoie -1 si l'agencement de la map fait qu'il y a deux cases qui sont autant en haut � gauche de la map.
	 * @return 1 si l'agencement de la map fait qu'il y a une seule case qui est la plus en haut � gauche de la map 
	 * et -1 si l'agencement de la map fait qu'il y a deux cases qui sont autant en haut � gauche de la map.
	 */
	public int getSensPremierDenivele() {
		return sensPremierDenivele;
	}

	/**
	 * Renvoie la position du coin en haut � gauche de la Map.
	 * @return la position du coin en haut � gauche de la Map
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Modifie la position du coin en haut � gauche de la Map.
	 * @param x la position en abscisse du coin en haut � gauche de la Map
	 * @param y la position en ordonn�e du coin en haut � gauche de la Map
	 */
	public void setPosition(int x, int y) {
		position.x = x;
		position.y = y - Math.round(MARGE_ORIGINE_Y * scaleY);
	}

	/**
	 * Renvoie, sous la forme d'un objet Point, le nombre de ligne et de colonne du tableau mod�lisant la Map.
	 * @return sous la forme d'un objet Point, le nombre de ligne et de colonne du tableau mod�lisant la Map
	 */
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
	
	/**
	 * Modifie le nombre de ligne et de colonne du tableau mod�lisant la Map.
	 * @param x le nombre de colonnes (peut �tre vu comme la taille du tableau sur l'axe des x) du tableau mod�lisant la Map 
	 * @param y le nombre de lignes (peut �tre vu comme la taille du tableau sur l'axe des y) du tableau mod�lisant la Map
	 */
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
	
	/**
	 * Renvoie la hauteur et la largeur que prend la map dans la fenetre sans prendre en compte le changement d'�chelle.
	 * @return la hauteur et la largeur que prend la map dans la fenetre sans prendre en compte le changement d'�chelle
	 */
	public Point getTailleAbsolue() {
		Point taille = new Point();
		Point nbCases = getNbCases();

		taille.x = DECALAGE_X * (nbCases.x - 1) + LONGUEUR_COTE_TUILE;
		taille.y = DECALAGE_Y * nbCases.y + DENIVELE_Y + LONGUEUR_COTE_TUILE - DECALAGE_Y;
		
		return taille;
	}
	
	/**
	 * Renvoie la hauteur et la largeur que prend la map dans la fenetre en prenant en compte le changement d'�chelle.
	 * @return la hauteur et la largeur que prend la map dans la fenetre en prenant en compte le changement d'�chelle
	 */
	public Point getTaille() {
		Point taille = new Point();
		Point nbCases = getNbCases();

		taille.x = Math.round(DECALAGE_X * scaleX) * (nbCases.x - 1) + Math.round(LONGUEUR_COTE_TUILE * scaleX);
		taille.y = Math.round(DECALAGE_Y * scaleY) * nbCases.y + Math.round(DENIVELE_Y * scaleY)  + Math.round(LONGUEUR_COTE_TUILE * scaleY) - Math.round(DECALAGE_Y * scaleY);
		
		return taille;
	}
	
	/**
	 * Renvoie le rectangle dans lequel la map est inscrit.
	 * @return le rectangle dans lequel la map est inscrit
	 */
	public Rectangle getRect() {
		Point pos = this.getPosition();
		Point taille = this.getTaille();
		return new Rectangle(pos.x, pos.y, taille.x, taille.y);
	}
	
	/**
	 * Modifie l'�chelle de la map (permet d'effectuer des zoom).
	 * @param x l'�chelle en x de la map (compris entre 0 et 1)
	 * @param y l'�chelle en y de la map (compris entre 0 et 1)
	 */
	public void setScale(float x, float y) {
		scaleX = x;
		scaleY = y;
		
		reagencerMaillage();
		for (Navire navire : navires.keySet()) {
			navire.setPosition(coordTabToMaillage(navires.get(navire)));
		}
	}
	
	/**
	 * Modifie l'�chelle de la map en changeant la hauteur et la largeur de la map dans la fenetre.
	 * @param hauteur la nouvelle hauteur de la map dans la fenetre
	 * @param largeur la nouvelle largeur de la map dans la fenetre
	 */
	public void setTaille(int hauteur, int largeur) {
		Point tailleAbsolue = getTailleAbsolue();
		setScale((float)(hauteur) / tailleAbsolue.x, (float)(largeur) / tailleAbsolue.y);
	}
	
	/**
	 * Modifie l'�chelle de la map en changeant la hauteur, la largeur s'adapte de sorte � ce que la map ne se d�forme pas (ne "s'aplatisse" pas).
	 * @param hauteur la nouvelle hauteur de la map dans la fenetre
	 */
	public void setHauteur(int hauteur) {
		Point tailleAbsolue = getTailleAbsolue();
		setScale((float)(hauteur) / tailleAbsolue.y, (float)(hauteur) / tailleAbsolue.y);
	}
	
	/**
	 * Modifie l'�chelle de la map en changeant la largeur, la hauteur s'adapte de sorte � ce que la map ne se d�forme pas (ne "s'aplatisse" pas).
	 * @param largeur la nouvelle largeur de la map dans la fenetre
	 */
	public void setLargeur(int largeur) {
		Point tailleAbsolue = getTailleAbsolue();
		setScale((float)(largeur) / tailleAbsolue.x, (float)(largeur) / tailleAbsolue.x);
	}
	
	/**
	 * Renvoie l'�chelle en x de la map.
	 * @return l'�chelle en x de la map
	 */
	public float getScaleX() {
		return scaleX;
	}
	
	/**
	 * Renvoie l'�chelle en y de la map.
	 * @return l'�chelle en y de la map
	 */
	public float getScaleY() {
		return scaleY;
	}
	
	/**
	 * Permet de v�rifier la fa�on dont est agenc� le maillage de la map. 
	 * Renvoie false si l'agencement de la map fait qu'il y a une seule case qui est la plus en haut � gauche de la map  
	 * et renvoie true si l'agencement de la map fait qu'il y a deux cases qui sont autant en haut � gauche de la map.
	 * @return false si l'agencement de la map fait qu'il y a une seule case qui est la plus en haut � gauche de la map 
	 * et true si l'agencement de la map fait qu'il y a deux cases qui sont autant en haut � gauche de la map.
	 */
	public boolean isAgencementMaillageHaut() {
		return (sensPremierDenivele == -1);
	}
	
	/**
	 * Permet de v�rifier la fa�on dont est agenc� le maillage de la map. 
	 * Renvoie true si l'agencement de la map fait qu'il y a une seule case qui est la plus en haut � gauche de la map  
	 * et renvoie false si l'agencement de la map fait qu'il y a deux cases qui sont autant en haut � gauche de la map.
	 * @return true si l'agencement de la map fait qu'il y a une seule case qui est la plus en haut � gauche de la map 
	 * et false si l'agencement de la map fait qu'il y a deux cases qui sont autant en haut � gauche de la map.
	 */
	public boolean isAgencementMaillageBas() {
		return (sensPremierDenivele == 1);
	}
	
	/**
	 * Change l'agencement du maillage pour qu'il passe � "l'agencement haut" (cf. fonction isAgencementMaillageHaut).
	 */
	public void setAgencementMaillageHaut() {
		sensPremierDenivele = -1;
		reagencerMaillage();
	}
	
	/**
	 * Change l'agencement du maillage pour qu'il passe � "l'agencement bas" (cf. fonction isAgencementMaillageBas).
	 */
	public void setAgencementMaillageBas() {
		sensPremierDenivele = 1;
		reagencerMaillage();
	}
	
	/**
	 * Passe l'agencement du maillage � "l'agencement haut" si il �tait en "agencement bas" et vice versa (cf. fonction isAgencementMaillageHaut et isAgencementMaillageBas).
	 */
	public void changerAgencementMaillage() {
		sensPremierDenivele = -sensPremierDenivele;
		reagencerMaillage();
	}
	
	/**
	 * Renvoie le navire pr�sent sur la case entr�e en param�tre.
	 * @param coordTab Les coordonn�es dans le tableau o� l'on veut v�rifier si il y a un navire 
	 * @return le navire dans la case � coordTab ou null si il n'y a pas de navire a cette case
	 */
	public Navire getNavireAtCoord(Point coordTab) {
		Set<Entry<Navire, Point>> set = navires.entrySet();
		Iterator<Entry<Navire, Point>> it = set.iterator();
		while(it.hasNext()){
			Entry<Navire, Point> e = it.next();
			if (coordTab.equals(e.getValue())) {
				return e.getKey();
			}
		}
		return null;
	}
	
	///////////////
	/// ACTIONS ///
	///////////////
	/**
	 * Centre la position de la map dans la fenetre.
	 * @param container l'objet GameContainer g�r� par Slick2D (� r�cup�rer des param�tres des fonctions comme BasicGameState.init ou BasicGameState.update ou BasicGameState.render)
	 */
	public void centrerDansFenetre(GameContainer container) {
		Point tailleMap = this.getTaille();
		this.setPosition((container.getWidth() - tailleMap.x) / 2, (container.getHeight() - tailleMap.y) / 2);
	}
	
	/**
	 * Convertie les coordonn�es dans le tableau en position (x,y) dans la fen�tre relatives � la position de la map.
	 * @param coordTab coordonn�es dans le tableau
	 * @return position (x,y) dans la fen�tre relatives � la position de la map
	 */
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
	
	/**
	 * Convertie la position (x,y) dans la fen�tre (relatives � la position de la map) en coordonn�es dans le tableau.
	 * @param coordMaillage position (x,y) dans la fen�tre relatives � la position de la map
	 * @return coordonn�es dans le tableau
	 */
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

	/**
	 * Charge la map ayant pour nom nomMap.
	 * @param nomMap le nom de la map � charger
	 */
	public void load(String nomMap) {
		Vector < Vector<Integer> > tabMap;
		int sensDenivele = sensPremierDenivele;
		int x = 0;
		int y = 0;
		
		if (sensDenivele == -1) {
			y = Math.round(DENIVELE_Y * scaleY);
		}

		setPosition(0, 0);
		grille = new Vector< Vector<Case> >();
		phares = new Vector<Phare>();
		navires = new HashMap<Navire, Point>();
		
		if (nomMap != null) {
			tabMap = FileUtility.getInstance().loadMap(nomMap);

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
					
					if (caseCourante.getClass() == Phare.class) {
						phares.add((Phare)caseCourante);
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
		} else {
			setNbCases(1, 1);
		}
	}

	/**
	 * Enregistre la map avec pour nom nomMap.
	 * @param nomMap le nom de la map � enregistrer
	 */
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

	/**
	 * Affiche un s�lecteur qui prend une couleur associ�e au type de Case ayant l'ID idCase.
	 * @param idCase l'ID de la Case � laquelle est associ�e la couleur du s�lecteur <br>
	 * - 0 pour un selecteur cyan, correspondant � la case Oc�an <br>
	 * - 1 pour un selecteur de case Terre <br>
	 * - 2 pour un s�lecteur de case Phare <br>
	 * - 3 pour un s�lecteur de couleur rouge (ne correspond pas � un type de case mais peut tout de m�me �tre utile)
	 */
	public void selectionnerCase(int idCase) {
		selecteurCase.setSelecteurVisible(true);
		selecteurCase.setIdCaseSelectionnee(idCase);
	}

	/**
	 * Affiche un s�lecteur aux coordonn�es coordTab et dont la couleur est associ�e au type de Case ayant l'ID idCase.
	 * @param idCase l'ID de la Case � laquelle est associ�e la couleur du s�lecteur <br>
	 * - 0 pour un selecteur cyan, correspondant � la case Oc�an <br>
	 * - 1 pour un selecteur de case Terre <br>
	 * - 2 pour un s�lecteur de case Phare <br>
	 * - 3 pour un s�lecteur de couleur rouge (ne correspond pas � un type de case mais peut tout de m�me �tre utile)
	 * @param coordTab les coordonn�es dans le tableau o� placer le s�lecteur
	 */
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
	
	/**
	 * Place sur la map une case ayant pour ID idCase aux coordonn�es coordTab, utile pour �diter la map.
	 * @param idCase l'ID de la case � placer <br>
	 * - 0 pour une case Oc�an <br>
	 * - 1 pour une case Terre <br>
	 * - 2 pour une case Phare
	 * @param coordTab les coordonn�es dans le tableau o� placer la case
	 */
	public void mettreCase(int idCase, Point coordTab) {
		if (coordTab.y < grille.size()) {
			if (coordTab.x < grille.get(coordTab.y).size() && 
				coordTab.x >= 0 && coordTab.y >= 0) {
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

	/**
	 * D�place un navire d'une case dans une direction donn�e.
	 * @param navire le navire � d�placer
	 * @param direction la direction dans laquelle d�placer le navire
	 */
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
			if (grille.get(coordCible.y).get(coordCible.x).getId()==Terre.ID) {//grid[][]
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
	
	/**
	 * Assigner un phare � un joueur.
	 * @param numPhare le num�ro du phare � assigner (l'ordre croissant des num�ros part de gauche � droite puis de haut en bas de la map)
	 * @param idJoueur l'id du joueur qui r�cup�re le phare <br>
	 * - 0 pour le joueur 1 <br>
	 * - 1 pour le joueur 2
	 */
	public void assignerPhare(int numPhare, int idJoueur) {
		if (numPhare >= 0 && numPhare < phares.size()) {
			phares.get(numPhare).setJoueurPossesseur(idJoueur);
		}
	}
	
	/**
	 * Renvoie un tableau contenant pour chaque indice (correspondant � un num�ro de phare) l'id du joueur poss�dant le phare ou -1 si aucun joueur ne poss�de le phare
	 * (l'ordre croissant des num�ros de phare part de gauche � droite puis de haut en bas de la map).
	 * @return un tableau contenant pour chaque indice (correspondant � un num�ro de phare) l'id du joueur poss�dant le phare ou -1 si aucun joueur ne poss�de le phare
	 * (l'ordre croissant des num�ros de phare part de gauche � droite puis de haut en bas de la map)
	 */
	public Vector<Integer> getPossessionPhares() {
		Vector<Integer> possessionPhares = new Vector<Integer>();
		for (Phare phare : phares) {
			possessionPhares.add(phare.getJoueurPossesseur());
		}
		return possessionPhares;
	}

	/**
	 * Le joueur prend le ou les phares sur lequel est son ou ses navires (si le joueur n'a pas de navire sur un phare, rien ne se passe).
	 * @param joueur le joueur dont on g�re la prise de phare
	 */
	public void verifierPriseDePhare(Joueur joueur) {
		for (int i = 0; i < joueur.getNbNavires(); i++) {
			if(!joueur.getNavire(i).isEtatDetruit()){
				Point coordNavire = navires.get(joueur.getNavire(i));
				Case caseACoordNavire = grille.get(coordNavire.y).get(coordNavire.x);

				if (caseACoordNavire.getClass() == Phare.class) {
					((Phare)(caseACoordNavire)).setJoueurPossesseur(joueur.getId());
				}
			}
		}
	}

	/**
	 * Renvoie le nombre de phare poss�d�s par un joueur ayant pour id idJoueur (0 ou 1)
	 * @param idJoueur l'id du joueur dont on veut conna�tre le nombre de phare en sa possession
	 * @return le nombre de phare poss�d�s par un joueur ayant pour id idJoueur (0 ou 1)
	 */
	public int nombrePharePossede(int idJoueur) {
		int nbPharePossede = 0;
		for (Phare phare : phares) {
			if (phare.getJoueurPossesseur() == idJoueur) {
				nbPharePossede++;
			}
		}
		return nbPharePossede;
	}

	/**
	 * Renvoie true si le joueur ayant pour id idJoueur (0 ou 1) a pris tout les phares
	 * @param idJoueur l'id du joueur dont on v�rifie si il a pris tout les phares
	 * @return true si le joueur ayant pour id idJoueur (0 ou 1) a pris tout les phares
	 */
	public boolean victoire(int idJoueur) {
		return nombrePharePossede(idJoueur) == phares.size();
	}

	/**
	 * Dessine la vue de la map, fonction � appeler dans une fonction render
	 */
	public void draw() {
		for (Vector <Case> ligne : grille) {
			for (Case caseCourante : ligne) {
				caseCourante.draw();
			}
		}
		selecteurCase.draw();
	}

	//////////////////////////
	/// FONCTIONS INTERNES ///
	//////////////////////////
	/*private boolean checkCollisions(Point coordTab) {
		return false;
	}*/

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
