package map;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

import game.Game;
import joueur.Joueur;

/**
 * Case sp�cifique repr�sentant un Phare (cf. classe Case pour plus d'informations)
 */
public class Phare extends Case {
	public static final int ID = 2;
	private static final int NB_FRAMES_ANIMATION = 6;
	private static final int DUREE_FRAME = 250;
	
	private Animation animPharesJoueurs[] = new Animation[Game.NB_JOUEURS];
	private int joueurPossesseur = -1;//le phare appartient a personne par defaut
	
	/**
	 * Cr��e une case Phare.
	 * @param position La position de la case (dans la fen�tre) relative � l'origine de la map.
	 */
	public Phare(Point position) {
		super(ID, NB_FRAMES_ANIMATION, DUREE_FRAME, position);
		for (int j = 0; j < animPharesJoueurs.length; j++) {
			animPharesJoueurs[j] = new Animation();
			for (int i = 0; i < NB_FRAMES_ANIMATION; i++) {
				animPharesJoueurs[j].addFrame(Map.getInstance().getSpriteSheet().getSprite(i, ID + j+1), DUREE_FRAME);
	        }
		}
		this.start();
	}
	
	/**
	 * Renvoie l'id du joueur possesseur du phare ou -1 si aucun joueur ne poss�de le phare.
	 * @return L'id du joueur possesseur du phare ou -1 si aucun joueur ne poss�de le phare.
	 */
	public int getJoueurPossesseur() {
		return joueurPossesseur;
	}
	
	/**
	 * Asigne le phare � un joueur (ou le rend neutre)
	 * @param joueur L'id du joueur � qui le phare est asign� (ou -1 pour rendre le phare neutre)
	 */
	public void setJoueurPossesseur(int joueur) {
		if (joueur >= 0 && joueur < Game.NB_JOUEURS) {
			joueurPossesseur = joueur;
		} else if (joueur == -1) {
			rendrePhareNeutre();
		}
	}
	
	/**
	 * Rend le phare neutre
	 */
	public void rendrePhareNeutre() {
		joueurPossesseur = -1;
	}
	
	/**
	 * Dessine le phare sur la fen�tre, dans le r�f�rentiel de la map, en prenant en compte l'�chelle ; 
	 * le phare prend la couleur blanche, rouge ou bleu selon qu'il soit neutre, pris par le joueur 1 ou pris par le joueur 2
	 */
	@Override
	public void draw() {
		if (joueurPossesseur == -1) {
			super.draw();
			for (int i = 0; i < Game.NB_JOUEURS; i++) {
				animPharesJoueurs[i].updateNoDraw();
			}
		} else {
			this.updateNoDraw();
			animPharesJoueurs[joueurPossesseur].draw(Map.getInstance().getPosition().x + getPosition().x,
					Map.getInstance().getPosition().y + getPosition().y,
					Map.getInstance().getLongueurAbsolueCoteTuile() * Map.getInstance().getScaleX(),
					Map.getInstance().getLongueurAbsolueCoteTuile() * Map.getInstance().getScaleY());
		}
	}
}
