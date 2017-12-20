package joueur;

import java.awt.*;
import java.util.ArrayList;

import map.Map;

import org.newdawn.slick.SlickException;

public class Joueur {
    private String nom;
    private Color couleur;
    private ArrayList<Navire> navires;
	private Navire navireCourant;

    public Joueur(String nom, Color couleur) throws SlickException{
        this.nom = nom;
        this.couleur = couleur;
        this.navires = new ArrayList<Navire>();
        this.navires.add(new NavireAmiral());
        this.navires.add(new NavireFregate());
        this.navireCourant = navires.get(0);
    	Map.getInstance().addNavire(navires.get(0), 2, 2);
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
    
    public Navire getNavireCourant() {
		return navireCourant;
	}
}
