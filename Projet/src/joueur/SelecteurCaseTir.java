package joueur;

import java.awt.Point;
import map.Map;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class SelecteurCaseTir {
	public static final String DOSSIER_IMAGE = "resources/images/";
	private static final int LONGUEUR_COTE_TUILE = 64;

	private Animation selecteur;
	private boolean visible = false;
	private Point position = new Point(0, 0);
	
	
	
	public SelecteurCaseTir() throws SlickException {
		selecteur = new Animation();
		selecteur.addFrame(new Image(DOSSIER_IMAGE + "crosshair.png"), 500);
	}

	public Point getPosition() {
		return (Point) position.clone();
	}
	
	public void setPosition(Point _position) {
		position = _position;
	}
	
	public boolean isSelecteurVisible() {
		return visible;
	}
	
	public void setSelecteurVisible(boolean _visible) {
		visible = _visible;
	}
	
	public void draw() {
		if (isSelecteurVisible()) {
			selecteur.draw(Map.getInstance().getPosition().x + position.x,
					Map.getInstance().getPosition().y + position.y,
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleX(),
					LONGUEUR_COTE_TUILE * Map.getInstance().getScaleY());
		}
	}
}
