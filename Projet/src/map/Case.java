package map;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

/**
 * Classe abstraite qui représente le contenu d'un élément du maillage de la map. Un tableau de Case peut modéliser une map.
 */
public abstract class Case extends Animation {
	private int id;
	private Point position;
	
	/**
	 * Cr��e une case ; les caractéristiques spécifiques de la case sont entrées en paramètre.
	 * @param idCase l'identifiant de la case
	 * @param nbFrames le nombre de frame de l'animation de la case si la case est animée (sinon, mettre la valeur 1)
	 * @param dureeFrame le nombre de millisecondes à attendre avant de passer à la frame suivante
	 * (la valeur de ce paramètre n'a aucune importance si la case n'est pas animée)
	 * @param _position la position de la Case relative à l'origine de la map dans la fenetre
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
	 * Renvoie la position de la Case par rapport à l'origine de la Map.
	 * @return la position de la Case par rapport à l'origine de la Map
	 */
	public Point getPosition() {
		return (Point) position.clone();
	}
	
	/**
	 * Modifie la position de la Case par rapport à l'origine de la Map.
	 * @param _position la nouvelle position de la Case par rapport à l'origine de la Map
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
	 * @param _x la coordonnée en x du déplacement de la case dans la fenetre
	 * @param _y la coordonnée en y du déplacement de la case dans la fenetre
	 */
	public void move(int _x, int _y) {
		position.x += _x;
		position.y += _y;
	}
	
	/**
	 * Dessine la case sur la fenêtre, dans le référentiel de la map, en prenant en compte l'échelle
	 */
	@Override
	public void draw() {
		this.draw(Map.getInstance().getPosition().x + position.x, 
			Map.getInstance().getPosition().y + position.y,
			Map.getInstance().getLongueurAbsolueCoteTuile() * Map.getInstance().getScaleX(),
			Map.getInstance().getLongueurAbsolueCoteTuile() * Map.getInstance().getScaleY());
	}
	
	/**
	 * Dessine la case sur la fenetre, dans le référentiel de la fenetre, sans prendre en compte l'échelle
	 */
	public void drawAbsolu() {
		this.draw(position.x, position.y);
	}
}
