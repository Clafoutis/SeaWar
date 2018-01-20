package joueur;

import java.awt.*;
import java.util.ArrayList;

import map.Map;

import org.newdawn.slick.SlickException;

public class Joueur {
    private int id;
    private String nom;
    private Color couleur;
    private ArrayList<Navire> navires;
	private Navire navireCourant;

    public Joueur(int id, String nom, Color couleur, int numero) throws SlickException{
        this.id = id;
        this.nom = nom;
        this.couleur = couleur;
        this.navires = new ArrayList<Navire>();
        this.navires.add(new NavireAmiral(2 * numero + numero - 1));
        this.navires.add(new NavireFregate(2 * numero + numero - 1));
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

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public Color getCouleur() {
        return couleur;
    }

    public Navire getNavire(int id) {
        return navires.get(id);
    }

    public int getNbNavires() {
    	return navires.size();
    }

    public void setNavireCourant(Navire navireCourant) {
        this.navireCourant = navireCourant;
    }

    public Navire getNavireCourant() {
		return navireCourant;
	}

	public boolean deplacementEnCours(){
        return navireCourant.isDeplacementEnCours();
    }

    public void newTurn(){
	    Map.getInstance().verifierPriseDePhare(this);
	    navires.get(0).setNbDeplacementsRestants(navires.get(0).getNbDeplacements());
	    navires.get(1).setNbDeplacementsRestants(navires.get(1).getNbDeplacements());
    }
}
