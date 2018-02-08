package joueur;

import java.util.ArrayList;
import map.Map;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;

/**
 * Element qui défini un joueur dans le jeu
 */
public class Joueur {
    private int id;
    private String nom;
    private Color couleur;
    private ArrayList<Navire> navires;
	private Navire navireCourant;
	
	/**
	 * créé un joueur dans le jeu
	 * @param id représente l'id du joueur
	 * @param nom représente le nom du joueur
	 * @param couleur représente la couleur du joueur
	 * @param numero représente le numéro du joueur
	 */
	public Joueur(int id, String nom, Color couleur, int numero) throws SlickException{
		this.id = id;
        this.nom = nom;
        this.couleur = couleur;
        this.navires = new ArrayList<Navire>();
        this.navires.add(new NavireAmiral((2 * numero + numero - 1), id));
        this.navires.add(new NavireFregate((2 * numero + numero - 1), id));
        this.navireCourant = navires.get(0);
        int xDepart, yDepart;
        if(numero==2){
            xDepart = (int) Map.getInstance().getNbCases().getX() - 2;
            yDepart = (int) Map.getInstance().getNbCases().getY() - 2;
        }else{
            xDepart = 2;
            yDepart = 2;
        }
    	Map.getInstance().addNavire(navires.get(0), xDepart - 1, yDepart);
        Map.getInstance().addNavire(navires.get(1), xDepart, yDepart - 1);
	}

    public Joueur(int id, String nom, Color couleur, int numero, 
    		int posTabXNavire1, int posTabYNavire1, int posTabXNavire2, int posTabYNavire2) throws SlickException{
    	this.id = id;
        this.nom = nom;
        this.couleur = couleur;
        this.navires = new ArrayList<Navire>();
        this.navires.add(new NavireAmiral((2 * numero + numero - 1), id));
        this.navires.add(new NavireFregate((2 * numero + numero - 1), id));
        this.navireCourant = navires.get(0);
        Map.getInstance().addNavire(navires.get(0), posTabXNavire1, posTabYNavire1);
        Map.getInstance().addNavire(navires.get(1), posTabXNavire2, posTabYNavire2);
    }

	/**
	 * Retourne l'id du joueur
	 * @return l'id du joueur
	 */
    public int getId() {
        return id;
    }

	/**
	 * Retourne le nom du joueur
	 * @return le nom du joueur
	 */
    public String getNom() {
        return nom;
    }

	/**
	 * Retourne la couleur du joueur
	 * @return la couleur du joueur
	 */
    public Color getCouleur() {
        return couleur;
    }

	/**
	 * Retourne un navire du joueur
	 * @param id représente l'id du navire à sélectionner
	 * @return le navire sélectionné
	 */
    public Navire getNavire(int id) {
        return navires.get(id);
    }

	/**
	 * Retourne le nombre de navires du joueur
	 * @return le nombre de navires du joueur
	 */
    public int getNbNavires() {
    	return navires.size();
    }

	/**
	 * modifie le navire courant du joueur (navire sélectionné)
	 * @param navireCourant représente le navire
	 */
    public void setNavireCourant(Navire navireCourant) {
        this.navireCourant = navireCourant;
    }

	/**
	 * Retourne le navire courant du joueur
	 * @return le navire courant du joueur
	 */
    public Navire getNavireCourant() {
		return navireCourant;
	}

	/**
	 * Permet de savoir si le joueur a son navire courant en cours de déplacement
	 * @return booléen permettant de savoir si le joueur a son navire courant en cours de déplacement
	 */
	public boolean deplacementEnCours(){
        return navireCourant.isDeplacementEnCours();
    }

	/**
	 * Retourne le nombre de bateaux en vie du joueur
	 * @return le nombre de bateaux en vie du joueur
	 */
    public int nbBateauEnVie() {
        int nb = 0;
        for (Navire navire : navires) if (!navire.isEtatDetruit()) nb++;
        return nb;
    }

	/**
	 * met à jour l'état du joueur lors d'un nouveau tour
	 */
    public void newTurn(){
	    navires.get(0).setNbDeplacementsRestants(navires.get(0).getNbDeplacements());
	    navires.get(0).newTurn();
	    navires.get(1).setNbDeplacementsRestants(navires.get(1).getNbDeplacements());
	    navires.get(1).newTurn();
	    while(navireCourant.isEtatDetruit() && nbBateauEnVie()>0) navireCourant = navires.get((navires.indexOf(navireCourant) + 1) % getNbNavires());
    }
}
