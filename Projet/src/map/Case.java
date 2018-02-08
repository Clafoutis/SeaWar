package map;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

/**
 * Classe abstraite qui repr�sente le contenu d'un �l�ment du maillage de la map. Un tableau de Case peut mod�liser une map.
 */
public abstract class Case extends Animation {
	private int id;
	private Point position;
	
	/**
	 * Cr��e une case ; les caract�ristiques sp�cifiques de la case sont entr�es en param�tre.
	 * @param idCase l'identifiant de la case
	 * @param nbFrames le nombre de frame de l'animation de la case si la case est anim�e (sinon, mettre la valeur 1)
	 * @param dureeFrame le nombre de millisecondes � attendre avant de passer � la frame suivante 
	 * (la valeur de ce param�tre n'a aucune importance si la case n'est pas anim�e)
	 * @param _position la position de la Case relative � l'origine de la map dans la fenetre
	 */
	public Case(int idCase, int nbFrames, int dureeFrame, Point _position) {
		position = _position;
		id = idCase;

		for (int i = 0; i < nbFrames; i++) {
            this.addFrame(Map.getInstance().getSpriteSheet().getSprite(i, idCase), dureeFrame);
        }
		this.setCurrentFrame((int)(Math.random() * nbFrames));
	}
	
	/**
	 * Renvoie l'ID de la Case.
	 * @return l'ID de la Case
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Renvoie la position de la Case par rapport � l'origine de la Map.
	 * @return la position de la Case par rapport � l'origine de la Map
	 */
	public Point getPosition() {
		return (Point) position.clone();
	}
	
	/**
	 * Modifie la position de la Case par rapport � l'origine de la Map.
	 * @param _position la nouvelle position de la Case par rapport � l'origine de la Map
	 */
	public void setPosition(Point _position) {
		position.x = _position.x;
		position.y = _position.y;
	}
	
	/**
	 * Renvoie le rectangle dans lequel la case est inscrit.
	 * @return le rectangle dans lequel la case est inscrit
	 */
	public Rectangle getRect() {
		return new Rectangle(position.x, position.y, 
				Map.getInstance().getLongueurAbsolueCoteTuile(), 
				Map.getInstance().getLongueurAbsolueCoteTuile());
	}
	
	/**
	 * D�place la case dans la fenetre.
	 * @param _x la coordonn�e en x du d�placement de la case dans la fenetre
	 * @param _y la coordonn�e en y du d�placement de la case dans la fenetre
	 */
	public void move(int _x, int _y) {
		position.x += _x;
		position.y += _y;
	}
	
	/**
	 * Dessine la case sur la fen�tre, dans le r�f�rentiel de la map, en prenant en compte l'�chelle
	 */
	@Override
	public void draw() {
		this.draw(Map.getInstance().getPosition().x + position.x, 
			Map.getInstance().getPosition().y + position.y,
			Map.getInstance().getLongueurAbsolueCoteTuile() * Map.getInstance().getScaleX(),
			Map.getInstance().getLongueurAbsolueCoteTuile() * Map.getInstance().getScaleY());
	}
	
	/**
	 * Dessine la case sur la fenetre, dans le r�f�rentiel de la fenetre, sans prendre en compte l'�chelle
	 */
	public void drawAbsolu() {
		this.draw(position.x, position.y);
	}
}
