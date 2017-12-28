package joueur;

import java.awt.Point;

import map.Direction;
import map.Map;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import utility.FileUtility;

public class Navire {
    public static final String FICHIER_SPRITE_SHEET_NAVIRE = "boat2.png";
    public static final int LONGUEUR_COTE_TUILE = 64;
    private int pv, nbDeplacements, dmgCannonPrincipal, dmgCanonSecondaire,
            nbTourRechargeCanonPrincipal, nbTourRechargeCanonSecondaire;
    private int direction = 0;
    private SpriteSheet spriteSheet;
    private Animation[] animations = new Animation[6];
    private Point position = new Point();

    public Navire() throws SlickException {
        spriteSheet = new SpriteSheet(FileUtility.DOSSIER_SPRITE + FICHIER_SPRITE_SHEET_NAVIRE, LONGUEUR_COTE_TUILE, LONGUEUR_COTE_TUILE);
        this.animations[0] = loadAnimation(spriteSheet, 1, 1);
        this.animations[1] = loadAnimation(spriteSheet, 0, 1);
        this.animations[2] = loadAnimation(spriteSheet, 1, 0);
        this.animations[3] = loadAnimation(spriteSheet, 0, 0);
        this.animations[4] = loadAnimation(spriteSheet, 2, 1);
        this.animations[5] = loadAnimation(spriteSheet, 2, 0);
        //addFrame(spriteSheet.getSprite(0, 0), 100);
    }

    private Animation loadAnimation(SpriteSheet spriteSheet, int x, int y) {
        Animation animation = new Animation();
        animation.addFrame(spriteSheet.getSprite(x, y), 100);
        return animation;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public void setNbDeplacements(int nbDeplacements) {
        this.nbDeplacements = nbDeplacements;
    }

    public void setDmgCannonPrincipal(int dmgCannonPrincipal) {
        this.dmgCannonPrincipal = dmgCannonPrincipal;
    }

    public void setDmgCanonSecondaire(int dmgCanonSecondaire) {
        this.dmgCanonSecondaire = dmgCanonSecondaire;
    }

    public void setNbTourRechargeCanonPrincipal(int nbTourRechargeCanonPrincipal) {
        this.nbTourRechargeCanonPrincipal = nbTourRechargeCanonPrincipal;
    }

    public void setNbTourRechargeCanonSecondaire(int nbTourRechargeCanonSecondaire) {
        this.nbTourRechargeCanonSecondaire = nbTourRechargeCanonSecondaire;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public Point getPosition() {
        return (Point)position.clone();
    }

    public void setPosition(Point _position) {
        position = _position;
    }

    public void tryAccess(Point coordCibleTab) {
        Point coordPosTab = Map.getInstance().coordMaillageToTab(position);
        int difX = (int) (coordCibleTab.getX()-coordPosTab.getX());
        int difY = (int) (coordCibleTab.getY()-coordPosTab.getY());
        int sensPremierDenivele = Map.getInstance().getSensPremierDenivele();
        boolean xPaire = (coordPosTab.getX() % 2) == 0;
        boolean deniveleInverse = sensPremierDenivele == 1;
        boolean wtfIsThisShit = xPaire == deniveleInverse;
        boolean ohMyGodness = xPaire != deniveleInverse;
        switch(difX){
            case -1:
                switch(difY){
                    case -1:
                        if(wtfIsThisShit) deplacer(Direction.HAUT_GAUCHE);
                        break;
                    case 0:
                        if(ohMyGodness) deplacer(Direction.HAUT_GAUCHE);
                        else deplacer(Direction.BAS_GAUCHE);
                        break;
                    case 1:
                        if(ohMyGodness) deplacer(Direction.BAS_GAUCHE);
                        break;
                }
                break;
            case 0:
                switch(difY){
                    case -1:
                        deplacer(Direction.HAUT);
                        break;
                    case 1:
                        deplacer(Direction.BAS);
                        break;
                }
                break;
            case 1:
                switch(difY){
                    case -1:
                        if(wtfIsThisShit) deplacer(Direction.HAUT_DROITE);
                        break;
                    case 0:
                        if(ohMyGodness) deplacer(Direction.HAUT_DROITE);
                        else deplacer(Direction.BAS_DROITE);
                        break;
                    case 1:
                        if(ohMyGodness) deplacer(Direction.BAS_DROITE);
                        break;
                }
                break;
        }
    }

    public void deplacer(Direction direction) {
        Map.getInstance().deplacer(this, direction);
    }

    public void draw() {
        animations[direction].draw(Map.getInstance().getPosition().x + position.x, Map.getInstance().getPosition().y + position.y);
    }
}