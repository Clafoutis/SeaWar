package joueur;

import map.Map;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import utility.FileUtility;

import java.awt.*;

/**
 * Created by mdeazeve on 23/01/18.
 */
public class Tir extends Animation {
    private float x, y;

    /**
	 * Constructeur, construit les tirs, qui sont des animations d'explosion.
	 */
    public Tir() throws SlickException {
        SpriteSheet spritesheet = new SpriteSheet(FileUtility.DOSSIER_SPRITE + "explosion.png", 64, 64);
        this.addFrame(spritesheet.getSprite(0, 0), 75);
        this.addFrame(spritesheet.getSprite(1, 0), 75);
        this.addFrame(spritesheet.getSprite(2, 0), 75);
        this.addFrame(spritesheet.getSprite(3, 0), 75);
        this.addFrame(spritesheet.getSprite(0, 1), 75);
        this.addFrame(spritesheet.getSprite(1, 1), 75);
        this.addFrame(spritesheet.getSprite(2, 1), 75);
        this.addFrame(spritesheet.getSprite(3, 1), 75);
        this.addFrame(spritesheet.getSprite(0, 2), 75);
        this.addFrame(spritesheet.getSprite(1, 2), 75);
        this.addFrame(spritesheet.getSprite(2, 2), 75);
        this.addFrame(spritesheet.getSprite(3, 2), 75);
        this.addFrame(spritesheet.getSprite(0, 3), 75);
        this.addFrame(spritesheet.getSprite(1, 3), 75);
        this.addFrame(spritesheet.getSprite(2, 3), 75);
        this.addFrame(spritesheet.getSprite(3, 3), 75);
        // euuuh, voilà
        this.addFrame(new SpriteSheet(FileUtility.DOSSIER_SPRITE + "spriteSheetMap.png", 64, 64).getSprite(1, 0), 75);
        this.x = 10;
        this.y = 10;
    }

    /**
     * retourne la position du tir
	 * @return position X du tir
	 */
    public float getX() {
        return x;
    }

    /**
     * retourne la position du tir
	 * @return position Y du tir
	 */
    public float getY() {
        return y;
    }

    /**
     * modifie la position du tir
	 * @param point du tir
	 */
    public void setXY(Point point) {
        this.x = (float) (Map.getInstance().getPosition().x + point.getX());
        this.y = (float) (Map.getInstance().getPosition().y + point.getY());
    }
}
