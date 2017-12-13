package map;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import utility.FileUtility;

public class Map {
	public static final String FICHIER_SPRITE_SHEET_MAP = "spriteSheetMap.png";
	
	// Constantes pour la construction d'un maillage d'hexagones
	private static final int LONGUEUR_COTE_TUILE = 64;
	private static final int DECALAGE_X = (int) ((float) LONGUEUR_COTE_TUILE * 3f / 4f);
	private static final int DENIVELE_Y = (int) (Math.sin(1) * (float) LONGUEUR_COTE_TUILE / 2f);//60 deg = 1.0472 rad
	private static final int DECALAGE_Y = (int) 2 * DENIVELE_Y;
	private static final int MARGE_ORIGINE_Y = - (int) (LONGUEUR_COTE_TUILE / 2f - DENIVELE_Y);

	private Class<?> typeDeCaseParId[];
	private Vector< Vector<Case> > grille;
	private SpriteSheet spriteSheet;
	private int sensPremierDenivele = -1;
	
	private Point position = new Point();
	private SelecteurCase selecteurCase;
	
	public Map() throws SlickException {
		init();
	}

	public void init() throws SlickException {
		
		typeDeCaseParId = new Class[3];
		typeDeCaseParId[0] = Ocean.class;
		typeDeCaseParId[1] = Terre.class;
		typeDeCaseParId[2] = Phare.class;
		
		selecteurCase = new SelecteurCase(position, typeDeCaseParId.length);

		spriteSheet = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_MAP, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
	}
	
	//////////////////
	/// ACCESSEURS ///
	//////////////////
	public Point getPosition() {
		return position;
	}
	
	public void setPosition(int x, int y) {
		position.x = x;
		position.y = y + MARGE_ORIGINE_Y;
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
				grille.get(j).set(i, new Ocean(spriteSheet, position, coordTabToMaillage(coordTab)));
			}
		}
		
		//redimensionnement sur y
		grille.setSize(y);
		for (int j = oldY; j < grille.size(); j++) {
			grille.set(j, new Vector<Case>());
			for (int i = 0; i < x; i++) {
				coordTab = new Point(i, j);
				grille.get(j).add(new Ocean(spriteSheet, position, coordTabToMaillage(coordTab)));
			}
		}
	}
	
	public Point getTaille() {
		Point taille = new Point();
		Point nbCases = getNbCases();

		taille.x = DECALAGE_X * (nbCases.x - 1) + LONGUEUR_COTE_TUILE;
		taille.y = DECALAGE_Y * nbCases.y + DENIVELE_Y;
		
		return taille;
	}
	
	public boolean isAgencementMaillageHaut() {
		return (sensPremierDenivele == -1);
	}
	
	public boolean isAgencementMaillageBas() {
		return (sensPremierDenivele == 1);
	}
	
	public void setAgencementMaillageHaut() {
		reagencerMaillage(-1);
	}
	
	public void setAgencementMaillageBas() {
		reagencerMaillage(1);
	}
	
	public void changerAgencementMaillage() {
		reagencerMaillage(-sensPremierDenivele);
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
		Point nbCases = getNbCases();

		coordMaillage.x = DECALAGE_X * coordTab.x;
		coordMaillage.y = DECALAGE_Y * coordTab.y;

		if (sensPremierDenivele == -1 && coordTab.x % 2 == 0) {
			coordMaillage.y += DENIVELE_Y;
		}
		
		return coordMaillage;
	}
	
	public Point coordMaillageToTab(Point coordMaillage) {
		//TODO
		Point coordTab = new Point();
		return coordTab;
	}
	
	public void load(String nomMap) {
		Vector < Vector<Integer> > tabMap = FileUtility.getInstance().loadMap(nomMap);
		int sensDenivele = sensPremierDenivele;
		int x = 0;
		int y = 0;
		
		if (sensDenivele == -1) {
			y = DENIVELE_Y;
		}
		
		FileUtility.getInstance().printTabMap(tabMap);

		setPosition(0, 0);
		grille = new Vector< Vector<Case> >();

		for (int j = 0; j < tabMap.size(); j++) {
			
			x = 0;
			grille.add(new Vector<Case>());
			for (int i = 0; i < tabMap.get(j).size(); i++) {
				Case caseCourante;
				try {
					if (tabMap.get(j).get(i) < typeDeCaseParId.length) {
						caseCourante = (Case) (typeDeCaseParId[ tabMap.get(j).get(i) ])
								.getConstructor(SpriteSheet.class, Point.class, Point.class)
								.newInstance(spriteSheet, position, new Point(x, y));
					} else {
						throw new ArrayIndexOutOfBoundsException();
					}
					
				} catch (InstantiationException e1) {
					caseCourante = new Ocean(spriteSheet, position, new Point(x, y));
				} catch (IllegalAccessException e1) {
					caseCourante = new Ocean(spriteSheet, position, new Point(x, y));
				} catch (IllegalArgumentException e1) {
					caseCourante = new Ocean(spriteSheet, position, new Point(x, y));
				} catch (InvocationTargetException e1) {
					caseCourante = new Ocean(spriteSheet, position, new Point(x, y));
				} catch (NoSuchMethodException e1) {
					caseCourante = new Ocean(spriteSheet, position, new Point(x, y));
				} catch (SecurityException e1) {
					caseCourante = new Ocean(spriteSheet, position, new Point(x, y));
				} catch (ArrayIndexOutOfBoundsException e1) {
					caseCourante = new Ocean(spriteSheet, position, new Point(x, y));
				}
				
				grille.lastElement().add(caseCourante);
				x += DECALAGE_X;
				y += DENIVELE_Y * sensDenivele;
				sensDenivele = -sensDenivele;
			}

			if (sensDenivele != sensPremierDenivele) {
				sensDenivele = sensPremierDenivele;
				y += DENIVELE_Y;
			}
			y += DECALAGE_Y;
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
		selecteurCase.setIdCaseSelectionnee(idCase);
		
		if (coordTab.x < 0) coordTab.x = 0;
		if (coordTab.y < 0) coordTab.y = 0;
		
		if (grille.size() > 0) {
			if (coordTab.y < grille.size()) {
				selecteurCase.setSelecteurVisible(true);
			} else {
				coordTab.y = grille.size() - 1;
			}
			if (coordTab.x < grille.get(coordTab.y).size()) {
				selecteurCase.setSelecteurVisible(true);
			} else if (grille.get(coordTab.y).size() > 0) {
				coordTab.x = grille.get(coordTab.y).size() - 1;
			} else {
				coordTab.x = 0;
				selecteurCase.setSelecteurVisible(false);
			}
		} else {
			coordTab.y = 0;
			selecteurCase.setSelecteurVisible(false);
		}

		if (selecteurCase.isSelecteurVisible()) {
			selecteurCase.setPosition(grille.get(coordTab.y).get(coordTab.x).getPosition());
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
								.getConstructor(SpriteSheet.class, Point.class, Point.class)
								.newInstance(spriteSheet, position, posCase));
					} else {
						throw new ArrayIndexOutOfBoundsException();
					}
					
				} catch (InstantiationException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(spriteSheet, position, posCase));
				} catch (IllegalAccessException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(spriteSheet, position, posCase));
				} catch (IllegalArgumentException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(spriteSheet, position, posCase));
				} catch (InvocationTargetException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(spriteSheet, position, posCase));
				} catch (NoSuchMethodException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(spriteSheet, position, posCase));
				} catch (SecurityException e) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(spriteSheet, position, posCase));
				} catch (ArrayIndexOutOfBoundsException e1) {
					grille.get(coordTab.y).set(coordTab.x, new Ocean(spriteSheet, position, posCase));
				}
			}
		}
	}
	
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
	private void reagencerMaillage(int _sensPremierDenivele) {
		int sensDenivele;
		
		if (sensPremierDenivele != _sensPremierDenivele) {
			sensPremierDenivele = _sensPremierDenivele;
			sensDenivele = -sensPremierDenivele;
			selecteurCase.setSelecteurVisible(false);
			
			for (Vector <Case> ligne : grille) {
				for (Case caseCourante : ligne) {
					caseCourante.move(0, DENIVELE_Y * sensDenivele);
					sensDenivele = -sensDenivele;
				}
				if (sensDenivele == sensPremierDenivele) {
					sensDenivele = -sensPremierDenivele;
				}
			}
		}
	}
}
