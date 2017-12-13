package joueur;

import java.awt.*;
import java.util.ArrayList;

public class Joueur {
    private String nom;
    private Color couleur;
    private ArrayList<Navire> navires;

    public Joueur(String nom, Color couleur){
        this.nom = nom;
        this.couleur = couleur;
        this.navires = new ArrayList<Navire>();
        this.navires.add(new NavireAmiral());
        this.navires.add(new NavireFregate());
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
}
