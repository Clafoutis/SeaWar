package joueur;

import java.awt.Point;

import map.Direction;
import map.Map;

import map.SelecteurCase;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public abstract class Navire {
    private final int DEPLACEMENT = 0, TIR_PRINCIPAL = 1, TIR_SECONDAIRE = 2, DETRUIT = 3;
    private int etat;
    private int longueurCoteTuile = 64;// (par defaut)
    private int id_proprietaire;
    private int pv, pvMax, nbDeplacements, nbDeplacementsRestants, dmgCannonPrincipal, dmgCanonSecondaire,
            nbTourRechargeCanonPrincipal, nbTourMaxRechargeCanonPrincipal,
            nbTourRechargeCanonSecondaire, nbTourMaxRechargeCanonSecondaire;
    private int direction;
    private SpriteSheet spriteSheet;
    public SpriteSheet spriteSheetMiniature;
    private Animation[] animations = new Animation[6];
    private Point position = new Point();
    // Pour les tirs
    protected SelecteurCaseTir selecteursCasesTir[];
    private boolean tirEffectue;
    // Pour les déplacements animés
    private boolean deplacementEnCours;
    private int nbDeplacementsAnimRestants;
    private double tempX, tempY, deltaX, deltaY;
    private Point destination;

    public Navire(int _direction, String _nomSpriteSheet, String _spriteSheetMiniature, int _longueurCoteTuile) throws SlickException {
        this.direction = _direction;
        etat = DEPLACEMENT;
        tirEffectue = false;
        selecteursCasesTir = new SelecteurCaseTir[6];
        for(int i=0;i<6;i++) selecteursCasesTir[i] = new SelecteurCaseTir();
        this.deplacementEnCours = false;
        this.longueurCoteTuile = _longueurCoteTuile;
        this.spriteSheet = new SpriteSheet(_nomSpriteSheet, longueurCoteTuile, longueurCoteTuile);
        this.spriteSheetMiniature = new SpriteSheet(_spriteSheetMiniature, 1300, 1390);
        
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
        if(pv<1){
            this.pv = 0;
            etat = DETRUIT;
        }
    }

    public int getPvMax(){
        return this.pvMax;
    }

    public void setPvMax(int _pvMax){
        this.pvMax = _pvMax;
    }

    public void setIdProprietaire(int id){
        this.id_proprietaire = id;
    }

    public int getIdProprietaire(){
        return this.id_proprietaire;
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
    
    public int getNbTourRechargeCanonPrincipal() {
    	return nbTourRechargeCanonPrincipal;
    }
    
    public int getNbTourRechargeCanonSecondaire() {
    	return nbTourRechargeCanonSecondaire;
    }
    
    public int getNbTourMaxRechargeCanonPrincipal() {
    	return nbTourMaxRechargeCanonPrincipal;
    }

    public int getNbTourMaxRechargeCanonSecondaire() {
    	return nbTourMaxRechargeCanonSecondaire;
    }

    public void setNbTourRechargeCanonPrincipal(int nbTourRechargeCanonPrincipal) {
        this.nbTourRechargeCanonPrincipal = nbTourRechargeCanonPrincipal;
    }

    public void setNbTourRechargeCanonSecondaire(int nbTourRechargeCanonSecondaire) {
        this.nbTourRechargeCanonSecondaire = nbTourRechargeCanonSecondaire;
    }

    public void setNbTourMaxRechargeCanonPrincipal(int nbTourMaxRechargeCanonPrincipal) {
        this.nbTourMaxRechargeCanonPrincipal = nbTourMaxRechargeCanonPrincipal;
    }

    public void setNbTourMaxRechargeCanonSecondaire(int nbTourMaxRechargeCanonSecondaire) {
        this.nbTourMaxRechargeCanonSecondaire = nbTourMaxRechargeCanonSecondaire;
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

    public boolean isEtatDeplacement() {
        return etat==DEPLACEMENT;
    }

    public boolean isEtatTirPrincipal() {
        return etat==TIR_PRINCIPAL;
    }

    public boolean isEtatTirSecondaire() {
        return etat==TIR_SECONDAIRE;
    }

    public boolean isEtatDetruit() {
        return etat==DETRUIT;
    }

    public boolean isDeplacementEnCours() {
        return deplacementEnCours;
    }

    public Point getDestination() {
        return destination;
    }

    public boolean isTirEffectue() {
        return tirEffectue;
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

    private void deplacer(Direction direction) {
        Map.getInstance().deplacer(this, direction);
    }

    public boolean aBouge(){
        return nbDeplacementsRestants < nbDeplacements;
    }
    
    public boolean isBloque(SelecteurCase[] selecteurCasesDeplacement) {
        boolean bloque = true;
        getPossibleDeplacements(selecteurCasesDeplacement);
        for(SelecteurCase selecteur:selecteurCasesDeplacement){
            if(selecteur.isSelecteurVisible()) bloque = false;
        }
        return bloque;
    }

    public void draw() {
        if(etat != DETRUIT){
            animations[direction].draw(Map.getInstance().getPosition().x + position.x,
                    Map.getInstance().getPosition().y + position.y,
                    longueurCoteTuile * Map.getInstance().getScaleX(),
                    longueurCoteTuile * Map.getInstance().getScaleY());
        }
    }

    public void drawStatus(boolean enemy){
        if(enemy){
            spriteSheetMiniature.getSprite(0, id_proprietaire).draw(940, 150, 130, 139);
        }else{
            spriteSheetMiniature.getSprite(0, id_proprietaire).draw(10, 150, 130, 139);
        }
    }

    public void drawZoneTir() {
        if(etat!=DEPLACEMENT){
            for (SelecteurCaseTir selecteur:selecteursCasesTir) {
                selecteur.draw();
            }
        }
    }

    public boolean tryShoot(Point coordPosTab, Navire navire, boolean principal) {
        if(nbTourRechargeCanonPrincipal==0 && principal || nbTourRechargeCanonSecondaire==0 && !principal){
            for (SelecteurCaseTir selecteur:selecteursCasesTir) {
                if(Map.getInstance().coordMaillageToTab(selecteur.getPosition()).getX()==coordPosTab.getX()
                        && Map.getInstance().coordMaillageToTab(selecteur.getPosition()).getY()==coordPosTab.getY()){
                    if(principal){
                        navire.setPv(navire.getPv() - dmgCannonPrincipal);
                        nbTourRechargeCanonPrincipal = nbTourMaxRechargeCanonPrincipal;
                    }
                    else{
                        navire.setPv(navire.getPv() - dmgCanonSecondaire);
                        nbTourRechargeCanonSecondaire = nbTourMaxRechargeCanonSecondaire;
                    }
                    etat = DEPLACEMENT;
                    tirEffectue = true;
                    return true;
                }
            }
        }
        return false;
    }

    abstract public void zoneTirCannonPrincipal(boolean activation);

    abstract public void zoneTirCannonSecondaire(boolean activation);

    public void switchMode(){
        if(etat==DEPLACEMENT){
            if(nbTourRechargeCanonPrincipal==0) etat = TIR_PRINCIPAL;
            else if(nbTourRechargeCanonSecondaire==0) etat = TIR_SECONDAIRE;
        }
        else if(etat==TIR_PRINCIPAL){
            if(nbTourRechargeCanonSecondaire==0) etat = TIR_SECONDAIRE;
            else etat = DEPLACEMENT;
        }
        else if(etat==TIR_SECONDAIRE) etat = DEPLACEMENT;
    }

    public void update(){
        if(etat == TIR_PRINCIPAL){
            zoneTirCannonPrincipal(true);
        }else if(etat == TIR_SECONDAIRE){
            zoneTirCannonSecondaire(true);
        }else zoneTirCannonSecondaire(false);
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

    public void newTurn(){
        if(nbTourRechargeCanonPrincipal>0) nbTourRechargeCanonPrincipal--;
        if(nbTourRechargeCanonSecondaire>0) nbTourRechargeCanonSecondaire--;
        tirEffectue = false;
    }
}
