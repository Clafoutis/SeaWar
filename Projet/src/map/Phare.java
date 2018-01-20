package map;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

import game.Game;
import joueur.Joueur;

public class Phare extends Case {
	public static final int ID = 2;
	private static final int NB_FRAMES_ANIMATION = 6;
	private static final int DUREE_FRAME = 250;
	
	private Animation animPharesJoueurs[] = new Animation[Game.NB_JOUEURS];
	private int joueurPossesseur = 0;//le phare appartient a personne par defaut
	
	public Phare(Point position) {
		super(ID, NB_FRAMES_ANIMATION, DUREE_FRAME, position);
		for (int j = 0; j < animPharesJoueurs.length; j++) {
			animPharesJoueurs[j] = new Animation();
			for (int i = 0; i < NB_FRAMES_ANIMATION; i++) {
				animPharesJoueurs[j].addFrame(Map.getInstance().getSpriteSheet().getSprite(i, ID + j+1), DUREE_FRAME);
				animPharesJoueurs[j].start();
				animPharesJoueurs[j].setCurrentFrame(this.getFrame());
	        }
		}
		this.start();
	}
	
	public int getJoueurPossesseur() {
		return joueurPossesseur;
	}
	
	public void setJoueurPossesseur(int joueur) {
		if (joueur > 0 && joueur <= Game.NB_JOUEURS) {
			joueurPossesseur = joueur;
		} else if (joueur == 0) {
			rendrePhareNeutre();
		}
	}
	
	public void rendrePhareNeutre() {
		joueurPossesseur = 0;
	}
	
	@Override
	public void draw() {
		if (joueurPossesseur == 0) {
			super.draw();
			for (int i = 0; i < Game.NB_JOUEURS; i++) {
				animPharesJoueurs[i].updateNoDraw();
			}
		} else {
			this.updateNoDraw();
			animPharesJoueurs[joueurPossesseur-1].draw(Map.getInstance().getPosition().x + getPosition().x,
					Map.getInstance().getPosition().y + getPosition().y,
					Map.getInstance().getLongueurAbsolueCoteTuile() * Map.getInstance().getScaleX(),
					Map.getInstance().getLongueurAbsolueCoteTuile() * Map.getInstance().getScaleY());
		}
	}
}
