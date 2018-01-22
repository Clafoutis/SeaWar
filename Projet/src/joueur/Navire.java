package joueur;

import java.awt.Point;

import map.Direction;
import map.Map;

import map.SelecteurCase;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Navire {
    private int pv, nbDeplacements, nbDeplacementsRestants, dmgCannonPrincipal, dmgCanonSecondaire,
            nbTourRechargeCanonPrincipal, nbTourRechargeCanonSecondaire;
    private int direction;
    private SpriteSheet spriteSheet;
    private Animation[] animations = new Animation[6];
    private Point position = new Point();
    // Pour les déplacements animés
    private boolean deplacementEnCours;
    private int nbDeplacementsAnimRestants;
    private double tempX, tempY, deltaX, deltaY;
    private Point destination;

    public Navire(int direction, SpriteSheet spriteSheet) throws SlickException {
        this.direction = direction;
        deplacementEnCours = false;
        this.animations[0] = loadAnimation(spriteSheet, 0, 0);
        this.animations[1] = loadAnimation(spriteSheet, 0, 1);
        this.animations[2] = loadAnimation(spriteSheet, 0, 2);
        this.animations[3] = loadAnimation(spriteSheet, 0, 3);
        this.animations[4] = loadAnimation(spriteSheet, 0, 4);
        this.animations[5] = loadAnimation(spriteSheet, 0, 5);
    }

    public int getPv(){
        return this.pv;
    }

    private Animation loadAnimation(SpriteSheet spriteSheet, int x, int y) {
        Animation animation = new Animation();
        animation.addFrame(spriteSheet.getSprite(x, y), 100);
        return animation;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }
    public int getNbDeplacements(){
        return this.nbDeplacements;
    }

    public void setNbDeplacements(int nbDeplacements) {
        this.nbDeplacements = nbDeplacements;
    }

    public int getNbDeplacementsRestants() {
        return nbDeplacementsRestants;
    }

    public void setNbDeplacementsRestants(int nbDeplacementsRestants) {
        this.nbDeplacementsRestants = nbDeplacementsRestants;
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

    public Point getDestination() {
        return (Point)destination.clone();
    }

    public boolean isDeplacementEnCours() {
        return deplacementEnCours;
    }

    public void getPossibleDeplacements(SelecteurCase[] selecteurs){
        Point coordPosTab = Map.getInstance().coordMaillageToTab(position);
        int X = (int) coordPosTab.getX();
        int Y = (int) coordPosTab.getY();
        Point newPoint[] = new Point[3];
        int sensPremierDenivele = Map.getInstance().getSensPremierDenivele();
        boolean xPaire = (coordPosTab.getX() % 2) == 0;
        boolean deniveleInverse = sensPremierDenivele == 1;
        boolean ohMyGodness = xPaire != deniveleInverse;
        if(!deplacementEnCours && nbDeplacementsRestants>0){
            switch(direction){
                case 0:
                    if(ohMyGodness){
                        newPoint[0] = new Point(X-1, Y);
                        newPoint[2] = new Point(X+1, Y);
                    }
                    else{
                        newPoint[0] = new Point(X-1, Y-1);
                        newPoint[2] = new Point(X+1, Y-1);
                    }
                    newPoint[1] = new Point(X, Y-1);
                    break;
                case 1:
                    newPoint[0] = new Point(X, Y-1);
                    if(ohMyGodness){
                        newPoint[1] = new Point(X+1, Y);
                        newPoint[2] = new Point(X+1, Y+1);
                    }
                    else{
                        newPoint[1] = new Point(X+1, Y-1);
                        newPoint[2] = new Point(X+1, Y);
                    }
                    break;
                case 2:
                    newPoint[2] = new Point(X, Y+1);
                    if(ohMyGodness){
                        newPoint[0] = new Point(X+1, Y);
                        newPoint[1] = new Point(X+1, Y+1);
                    }
                    else{
                        newPoint[0] = new Point(X+1, Y-1);
                        newPoint[1] = new Point(X+1, Y);
                    }
                    break;
                case 3:
                    newPoint[1] = new Point(X, Y+1);
                    if(ohMyGodness){
                        newPoint[0] = new Point(X+1, Y+1);
                        newPoint[2] = new Point(X-1, Y+1);
                    }
                    else{
                        newPoint[0] = new Point(X+1, Y);
                        newPoint[2] = new Point(X-1, Y);
                    }
                    break;
                case 4:
                    newPoint[0] = new Point(X, Y+1);
                    if(ohMyGodness){
                        newPoint[1] = new Point(X-1, Y);
                        newPoint[2] = new Point(X-1, Y+1);
                    }
                    else{
                        newPoint[1] = new Point(X-1, Y);
                        newPoint[2] = new Point(X-1, Y-1);
                    }
                    break;
                case 5:
                    newPoint[2] = new Point(X, Y-1);
                    if(ohMyGodness){
                        newPoint[0] = new Point(X-1, Y);
                        newPoint[1] = new Point(X-1, Y+1);
                    }
                    else{
                        newPoint[0] = new Point(X-1, Y-1);
                        newPoint[1] = new Point(X-1, Y);
                    }
                    break;
            }
            for (int i=0;i<selecteurs.length;i++) {
                if(newPoint[i].getX()<0 || newPoint[i].getY()<0 ||
                        newPoint[i].getX()>Map.getInstance().getNbCases().getX()-1 ||
                        newPoint[i].getY()>Map.getInstance().getNbCases().getY()-1){
                    selecteurs[i].setSelecteurVisible(false);
                }else if(Map.getInstance().getGrille().get((int) newPoint[i].getY()).get((int) newPoint[i].getX()).getId()==1){
                    selecteurs[i].setSelecteurVisible(false);
                }else{
                    selecteurs[i].setPosition(Map.getInstance().coordTabToMaillage(newPoint[i]));
                    selecteurs[i].setSelecteurVisible(true);
                }
            }
        }else{
            for (SelecteurCase selecteur:selecteurs) {
                selecteur.setSelecteurVisible(false);
            }
        }
    }

    public void tryAccess(Point coordCibleTab) {
        if(!deplacementEnCours && nbDeplacementsRestants>0){
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
    }

    public void deplacer(Direction direction) {
        Map.getInstance().deplacer(this, direction);
    }

    public void draw() {
        animations[direction].draw(Map.getInstance().getPosition().x + position.x, Map.getInstance().getPosition().y + position.y);
    }

    public void initialiserDeplacement(Point position, int direction){
        if(!position.equals(this.position)){
            this.setDirection(direction);
            deltaX = (position.getX() - this.position.getX())/30;
            deltaY = (position.getY() - this.position.getY())/30;
            tempX = this.position.getX();
            tempY = this.position.getY();
            destination = position;
            nbDeplacementsAnimRestants = 30;
            nbDeplacementsRestants--;
            deplacementEnCours = true;
        }
    }

    public void animationDeplacement(){
        tempX += deltaX;
        tempY += deltaY;
        position.x = (int) tempX;
        position.y = (int) tempY;
        nbDeplacementsAnimRestants--;
        if(nbDeplacementsAnimRestants==0){
            deplacementEnCours = false;
            position = destination;
        }
    }
}