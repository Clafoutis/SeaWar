package joueur;

import java.awt.Point;

import map.Direction;
import map.Map;

import map.SelecteurCase;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * Element qui défini un navire dans le jeu
 */
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

    /**
	 * Créé un navire
	 * @param _direction représente la direction du bateau
	 * - 0 pour haut
	 * - 1 pour haut droite
	 * - 2 pour bas droite
	 * - 3 pour bas
	 * - 4 pour bas gauche
	 * - 5 pour haut gauche
     * @param _nomSpriteSheet représente le nom du spritesheet à prendre pour dessiner ce navire
     * @param _spriteSheetMiniature représente le nom du spritesheet à prendre pour dessiner la miniature de ce navire
     * @param _longueurCoteTuile représente la longueur d'une tuile (hexagone) de la carte, et donc du navire
	 */
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

    /**
	 * Renvoie les points de vie restant du navire
	 * @return les points de vie restant du navire
	 */
    public int getPv(){
        return this.pv;
    }

    /**
	 * charge une animation à l'emplacement x et y du spritesheet
	 * @return la nouvelle animation créée
	 */
    private Animation loadAnimation(SpriteSheet spriteSheet, int x, int y) {
        Animation animation = new Animation();
        animation.addFrame(spriteSheet.getSprite(x, y), 100);
        return animation;
    }

    /**
	 * modifie les points de vie du navire
     * @param pv représente les points de vie
	 */
    public void setPv(int pv) {
        this.pv = pv;
        if(pv<1){
            this.pv = 0;
            etat = DETRUIT;
        }
    }

    /**
	 * Renvoie les points de vie maximum du navire
	 * @return les points de vie maximum du navire
	 */
    public int getPvMax(){
        return this.pvMax;
    }

    /**
	 * modifie les points de vie maximum du navire
     * @param _pvMax représente les points de vie
	 */
    public void setPvMax(int _pvMax){
        this.pvMax = _pvMax;
    }

    /**
	 * modifie l'id du propriétaire du navire
     * @param id représente l'id du joueur propriétaire
	 */
    public void setIdProprietaire(int id){
        this.id_proprietaire = id;
    }

    /**
	 * Renvoie l'id du propriétaire du navire
	 * @return l'id du propriétaire du navire
	 */
    public int getIdProprietaire(){
        return this.id_proprietaire;
    }

    /**
	 * Renvoie le nombre de déplacements maximum du navire
	 * @return le nombre de déplacements maximum du navire
	 */
    public int getNbDeplacements(){
        return this.nbDeplacements;
    }

    /**
	 * modifie le nombre de déplacements maximum du navire
     * @param nbDeplacements représente le nombre de déplacements maximum du navire
	 */
    public void setNbDeplacements(int nbDeplacements) {
        this.nbDeplacements = nbDeplacements;
    }

    /**
	 * Renvoie le nombre de déplacements restant du navire
	 * @return le nombre de déplacements restant du navire
	 */
    public int getNbDeplacementsRestants() {
        return nbDeplacementsRestants;
    }

    /**
	 * modifie le nombre de déplacements restant du navire
     * @param nbDeplacementsRestants représente le nombre de déplacements restant du navire
	 */
    public void setNbDeplacementsRestants(int nbDeplacementsRestants) {
        this.nbDeplacementsRestants = nbDeplacementsRestants;
    }

    /**
	 * modifie les dégats du cannon principal du navire
     * @param dmgCannonPrincipal représente les dégats du cannon
	 */
    public void setDmgCannonPrincipal(int dmgCannonPrincipal) {
        this.dmgCannonPrincipal = dmgCannonPrincipal;
    }

    /**
	 * modifie les dégats du cannon secondaire du navire
     * @param dmgCanonSecondaire représente les dégats du cannon
	 */
    public void setDmgCanonSecondaire(int dmgCanonSecondaire) {
        this.dmgCanonSecondaire = dmgCanonSecondaire;
    }
    
    /**
	 * Renvoie le nombre de tours restant avant la recharge du cannon principal
	 * @return le nombre de tours restant avant la recharge du cannon principal
	 */
    public int getNbTourRechargeCanonPrincipal() {
    	return nbTourRechargeCanonPrincipal;
    }
    
    /**
	 * Renvoie le nombre de tours restant avant la recharge du cannon secondaire
	 * @return le nombre de tours restant avant la recharge du cannon secondaire
	 */
    public int getNbTourRechargeCanonSecondaire() {
    	return nbTourRechargeCanonSecondaire;
    }
    
    /**
	 * Renvoie le nombre de tours maximum de la recharge du cannon principal
	 * @return le nombre de tours maximum de la recharge du cannon principal
	 */
    public int getNbTourMaxRechargeCanonPrincipal() {
    	return nbTourMaxRechargeCanonPrincipal;
    }

    /**
	 * Renvoie le nombre de tours maximum de la recharge du cannon secondaire
	 * @return le nombre de tours maximum de la recharge du cannon secondaire
	 */
    public int getNbTourMaxRechargeCanonSecondaire() {
    	return nbTourMaxRechargeCanonSecondaire;
    }

    /**
	 * modifie le nombre de tours restants de la recharge du cannon principal
     * @param nbTourRechargeCanonPrincipal représente le nombre de tours restants
	 */
    public void setNbTourRechargeCanonPrincipal(int nbTourRechargeCanonPrincipal) {
        this.nbTourRechargeCanonPrincipal = nbTourRechargeCanonPrincipal;
    }

    /**
	 * modifie le nombre de tours restants de la recharge du cannon secondaire
     * @param nbTourRechargeCanonSecondaire représente le nombre de tours restants
	 */
    public void setNbTourRechargeCanonSecondaire(int nbTourRechargeCanonSecondaire) {
        this.nbTourRechargeCanonSecondaire = nbTourRechargeCanonSecondaire;
    }

    /**
	 * modifie le nombre de tours maximum de la recharge du cannon principal
     * @param nbTourMaxRechargeCanonPrincipal représente le nombre de tours restants
	 */
    public void setNbTourMaxRechargeCanonPrincipal(int nbTourMaxRechargeCanonPrincipal) {
        this.nbTourMaxRechargeCanonPrincipal = nbTourMaxRechargeCanonPrincipal;
    }

    /**
	 * modifie le nombre de tours maximum de la recharge du cannon secondaire
     * @param nbTourMaxRechargeCanonSecondaire représente le nombre de tours restants
	 */
    public void setNbTourMaxRechargeCanonSecondaire(int nbTourMaxRechargeCanonSecondaire) {
        this.nbTourMaxRechargeCanonSecondaire = nbTourMaxRechargeCanonSecondaire;
    }

    /**
	 * Renvoie la direction du navire
	 * @return la direction du navire
	 */
    public int getDirection() {
        return direction;
    }

    /**
	 * modifie la direction du navire
     * @param direction représente la direction du navire
	 */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
	 * Renvoie la position du navire
	 * @return la position du navire
	 */
    public Point getPosition() {
        return (Point)position.clone();
    }

    /**
	 * modifie la position du navire
     * @param _position représente la position du navire
	 */
    public void setPosition(Point _position) {
        position = _position;
    }

    /**
	 * Indique si le navire est dans l'état déplacement
	 * @return booleen indiquant si le navire est dans l'état déplacement
	 */
    public boolean isEtatDeplacement() {
        return etat==DEPLACEMENT;
    }

    /**
	 * Indique si le navire est dans l'état tir principal
	 * @return booleen indiquant si le navire est dans l'état tir principal
	 */
    public boolean isEtatTirPrincipal() {
        return etat==TIR_PRINCIPAL;
    }

    /**
	 * Indique si le navire est dans l'état tir principal
	 * @return booleen indiquant si le navire est dans l'état tir principal
	 */
    public boolean isEtatTirSecondaire() {
        return etat==TIR_SECONDAIRE;
    }

    /**
	 * Indique si le navire est dans l'état détruit
	 * @return booleen indiquant si le navire est dans l'état détruit
	 */
    public boolean isEtatDetruit() {
        return etat==DETRUIT;
    }

    /**
	 * Indique si le navire est en train de se déplacer
	 * @return booleen indiquant si le navire est en train de se déplacer
	 */
    public boolean isDeplacementEnCours() {
        return deplacementEnCours;
    }

    /**
	 * Renvoie la destination du navire
	 * @return point indiquant la destination du navire
	 */
    public Point getDestination() {
        return destination;
    }

    /**
	 * Indique si le navire a effectué un tir pendant son tour
	 * @return booleen indiquant si le navire a effectué un tir pendant son tour
	 */
    public boolean isTirEffectue() {
        return tirEffectue;
    }

    /**
	 * modifie les sélecteurs de cases montrant les déplacements possible pour ce navire
	 * @param selecteurs représente la liste des sélecteurs de cases où le navire peut se déplacer
	 */
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

    /**
	 * tente d'accéder à la case passée en paramètre
	 * @param coordCibleTab réprésentant les coordonnées cible du navire
	 */
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

    /**
	 * effectue un déplacement (instantané) du navire dans une direction donnée
	 * @param direction représentant la direction vers laquelle le navire doit se déplacer
	 */
    private void deplacer(Direction direction) {
        Map.getInstance().deplacer(this, direction);
    }

    /**
	 * Indique si le navire a bougé pendant son tour
	 * @return booleen indiquant si le navire a bougé pendant son tour
	 */
    public boolean aBouge(){
        return nbDeplacementsRestants < nbDeplacements;
    }
    
    /**
	 * Indique si le navire est bloqué
	 * @return booleen indiquant si le navire est bloqué
	 */
    public boolean isBloque(SelecteurCase[] selecteurCasesDeplacement) {
        boolean bloque = true;
        getPossibleDeplacements(selecteurCasesDeplacement);
        for(SelecteurCase selecteur:selecteurCasesDeplacement){
            if(selecteur.isSelecteurVisible()) bloque = false;
        }
        return bloque;
    }

    /**
	 * dessine le navire sur la fenêtre de jeu
	 */
    public void draw() {
        if(etat != DETRUIT){
            animations[direction].draw(Map.getInstance().getPosition().x + position.x,
                    Map.getInstance().getPosition().y + position.y,
                    longueurCoteTuile * Map.getInstance().getScaleX(),
                    longueurCoteTuile * Map.getInstance().getScaleY());
        }
    }

    /**
	 * dessine la miniature du navire (pour les informations)
	 * @param enemy indiquant si le navire est un navire ennemi
	 */
    public void drawStatus(boolean enemy){
        if(enemy){
            spriteSheetMiniature.getSprite(0, id_proprietaire).draw(940, 150, 130, 139);
        }else{
            spriteSheetMiniature.getSprite(0, id_proprietaire).draw(10, 150, 130, 139);
        }
    }

    /**
	 * dessine la zone de tir (principal ou secondaire)
	 */
    public void drawZoneTir() {
        if(etat!=DEPLACEMENT){
            for (SelecteurCaseTir selecteur:selecteursCasesTir) {
                selecteur.draw();
            }
        }
    }

    /**
	 * tente d'effectuer un tir sur la case passée en paramètre
	 * @param coordPosTab représentant la case sur lequel ce navire doit tirer
     * @param navire représentant le navire sur lequel ce navire doit tirer
     * @param principal booléen permettant de savoir si c'est un tir en cannon principal ou secondaire
     * @return booléen indiquant si le tir a réussi ou non
	 */
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

    /**
	 * Met à jour la liste des sélecteurs des zones de tir du cannon principal du navire
	 * @param activation permet de savoir si les zones sont à afficher ou non
	 */
    abstract public void zoneTirCannonPrincipal(boolean activation);

    /**
	 * Met à jour la liste des sélecteurs des zones de tir du cannon secondaire du navire
	 * @param activation permet de savoir si les zones sont à afficher ou non
	 */
    abstract public void zoneTirCannonSecondaire(boolean activation);

    /**
	 * Switch le mode du navire (déplacement - tir (principal - secondaire))
	 */
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

    /**
	 * Met à jour les zones de tir du navire
	 */
    public void update(){
        if(etat == TIR_PRINCIPAL){
            zoneTirCannonPrincipal(true);
        }else if(etat == TIR_SECONDAIRE){
            zoneTirCannonSecondaire(true);
        }else zoneTirCannonSecondaire(false);
    }

    /**
	 * Initialise un déplacement du navire vers une destination (animation)
	 * @param position représente le point cible du navire
     * @param direction représenta la direction du déplacement
	 */
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

    /**
	 * avance d'une image l'animation de déplacement du navire
	 */
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

    /**
	 * Met à jour l'état du navire lors d'un nouveau tour
	 */
    public void newTurn(){
        if(nbTourRechargeCanonPrincipal>0) nbTourRechargeCanonPrincipal--;
        if(nbTourRechargeCanonSecondaire>0) nbTourRechargeCanonSecondaire--;
        tirEffectue = false;
    }
}
