package game;

import java.awt.*;

import joueur.Navire;
import joueur.Tir;
import map.SelecteurCase;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import joueur.Joueur;
import map.Map;
import utility.FileUtility;
import utility.Music;
import utility.Save;

import java.util.ArrayList;
import java.util.Vector;

public class Game extends BasicGameState {
    public static final int ID = 2;
    public static final int NB_JOUEURS = 2;
    private final int GAME = 0, PAUSE = 1, END_OF_GAME = 2;
    private int etat;

    private GameContainer container;
    private StateBasedGame game;
    private Image background;
    private ArrayList<Point> bufferClick; // Changer le type
    private String nomMap;
    private Point position = new Point();
    // Déroulement partie
    private Joueur[] joueurs = new Joueur[NB_JOUEURS];
    private Joueur joueurCourant;
    private Navire navireAffiche; // forcément ennemi
    private Tir tir;
    private SelecteurCase selecteurCaseNavireCourant;
    private SelecteurCase selecteurCaseNavireAffiche; // forcément ennemi
    private SelecteurCase[] selecteurCasesDeplacement;
    private String gagnant;
    // Pause
    private Image parchemin;
    private MouseOverArea retourArea, enregistrerArea, quitterArea;
    private SpriteSheet interfaceInformations;



    @Override
    public void init(GameContainer _container, StateBasedGame _game) throws SlickException {
        //spriteSheetdeNavire = new SpriteSheet("resources/images/AmiralPirate.png",200 , 250);
        //AmiralPirate=new Animation(spriteSheetdeNavire,8000);
        //this.AmiralPirate = ImageIO.read(getClass().getResource("resources/images/AmiralPirate.png"));
        this.game = _game;
        this.container = _container;
        this.background = new Image("resources/images/backgroundGame.png");
        bufferClick = new ArrayList<Point>();
        parchemin = new Image("resources/images/parchemin.png");
        // Bouton retour
        Image retour = new Image("resources/images/retour.png");
        Image retourHover = new Image("resources/images/retour-hover.png");
        this.retourArea = new MouseOverArea(container, retour, container.getWidth()/2 - 112, container.getHeight()/2 - 140, 225, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
                etat = GAME;
            }
        });
        retourArea.setMouseOverImage(retourHover);
        // Bouton enregistrer
        Image enregistrer = new Image("resources/images/enregistrer.png");
        Image enregistrerHover = new Image("resources/images/enregistrer-hover.png");
        this.enregistrerArea = new MouseOverArea(container, enregistrer, container.getWidth()/2 - 112, container.getHeight()/2 - 55, 225, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
            	for (int j = 0; j < NB_JOUEURS; j++) {
            		for (int i = 0; i < joueurs[j].getNbNavires(); i++) {
            			Navire navire = joueurs[j].getNavire(i);
	            		Point coordPosTab = Map.getInstance().coordMaillageToTab(navire.getPosition());
	            		Save.getInstance().setNavireSave(j, i, coordPosTab, navire.getPv(), 
	            				navire.getNbTourRechargeCanonPrincipal(), navire.getNbTourRechargeCanonSecondaire(),
	            				navire.getDirection(), navire.getNbDeplacementsRestants());
	            		Save.getInstance().setNomMap(nomMap);
	            		Save.getInstance().setJoueurCourant(joueurCourant.getId());
	            		if (joueurCourant.getNavireCourant() == joueurCourant.getNavire(0)) {
	            			Save.getInstance().setNavireCourant(0);
	            		} else if (joueurCourant.getNavireCourant() == joueurCourant.getNavire(1)) {
	            			Save.getInstance().setNavireCourant(1);
	            		}
	            		Save.getInstance().setPossessionPhares(Map.getInstance().getPossessionPhares());
            		}
            	}
            	Save.getInstance().save();
            	Music.playMenu();
                game.enterState(MainScreen.ID);
            }
        });
        enregistrerArea.setMouseOverImage(enregistrerHover);
        // Bouton quitter
        Image quitter = new Image("resources/images/quitter.png");
        Image quitterHover = new Image("resources/images/quitter-hover.png");
        this.quitterArea = new MouseOverArea(container, quitter, container.getWidth()/2 - 112, container.getHeight()/2 + 30, 225, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
                Music.playMenu();
                game.enterState(MainScreen.ID);
            }
        });
        quitterArea.setMouseOverImage(quitterHover);
        interfaceInformations = new SpriteSheet(FileUtility.DOSSIER_SPRITE + "interfaceInformations.png",64,64);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        if(etat == GAME){
            if(joueurCourant.deplacementEnCours()){
                joueurCourant.getNavireCourant().animationDeplacement();
            }
            if(!bufferClick.isEmpty() && !joueurCourant.getNavireCourant().isDeplacementEnCours()){
                executeClick();
            }
        }
        // Selecteurs de case
        if(joueurCourant.getNavireCourant().isDeplacementEnCours()) {
            selecteurCaseNavireCourant.setPosition(joueurCourant.getNavireCourant().getDestination());
        }else{
            selecteurCaseNavireCourant.setPosition(joueurCourant.getNavireCourant().getPosition());
        }
        if(navireAffiche != null) selecteurCaseNavireAffiche.setPosition(navireAffiche.getPosition());
        joueurCourant.getNavireCourant().getPossibleDeplacements(selecteurCasesDeplacement);
        joueurCourant.getNavireCourant().update();
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
        background.draw(0, 0, container.getWidth(), container.getHeight());
        Map.getInstance().draw();
        // draw les sélecteurs des déplacements possibles
        if(joueurCourant.getNavireCourant().isEtatDeplacement()){
            for (SelecteurCase selecteur:selecteurCasesDeplacement) selecteur.draw();
        }
        // draw le sélecteur du navire courant et affiche
        if(navireAffiche != null) selecteurCaseNavireAffiche.draw();
        selecteurCaseNavireCourant.draw();
        // draw les navires
        for (Joueur joueur:joueurs) {
            joueur.getNavire(0).draw();
            joueur.getNavire(1).draw();
        }
        // draw les zones de tir
        joueurCourant.getNavireCourant().drawZoneTir();
        // Render de l'interface
        drawInterface(graphics, joueurCourant.getNavireCourant(), 145, 275);
        // Affichage de l'image représentant le navire sélectionné (courant)
        joueurCourant.getNavireCourant().drawStatus(false);

        if(navireAffiche != null){
            // Affichage de la barre de vie en % de la vie maximale (du navire sélectionné)
            drawInterface(graphics, navireAffiche, 915, 275);
            // Affichage de l'image représentant le navire sélectionné (sélectionné)
            navireAffiche.drawStatus(true);
        }

        // Affichage déplacement
        graphics.setColor(Color.cyan);
        graphics.drawGradientLine(1, 1, Color.cyan, 1, 1, Color.cyan);
        // A terminer : afficher les autres infos comme le nombre de déplacements restant et le temps de recharge d'un tir

        if(etat == PAUSE){
            parchemin.draw(container.getWidth()/2 - 250, container.getHeight()/2 - 315, 500, 630);
            retourArea.render(container, graphics);
            enregistrerArea.render(container, graphics);
            quitterArea.render(container, graphics);
        }else if(etat == END_OF_GAME){
        	Save.getInstance().effacerSauvegarde();
            parchemin.draw(container.getWidth()/2 - 180, container.getHeight()/2 - 225, 360, 450);
            parchemin.draw(container.getWidth()/2 - 180, container.getHeight()/2 - 225, 360, 450);
            graphics.setColor(Color.black);
            graphics.drawGradientLine(1, 1, Color.black, 1, 1, Color.black);
            graphics.drawString("Félicitation, " + gagnant + " !", container.getWidth()/2 - 105, container.getHeight()/2 - 140);
            graphics.drawString("vous avez gagné !", container.getWidth()/2 - 80, container.getHeight()/2 - 110);
            quitterArea.render(container, graphics);
        }
        tir.draw(tir.getX(), tir.getY(), 64 * Map.getInstance().getScaleX(), 64 * Map.getInstance().getScaleY());
    }

    public void newGame(String nomMap) throws SlickException {
    	this.nomMap = nomMap;
    	Map.getInstance().startGameMode();
        Map.getInstance().load(nomMap);
        Map.getInstance().centrerDansFenetre(container);
        
        if (Save.getInstance().isSauvegardePresente()) {
	        joueurs[0] = new Joueur(0, "Joueur 1", Color.red, 1, 
	        		Save.getInstance().getPosTabX(0, 0), Save.getInstance().getPosTabY(0, 0),
	        		Save.getInstance().getPosTabX(0, 1), Save.getInstance().getPosTabY(0, 1));
	        joueurs[1] = new Joueur(1, "Joueur 2", Color.blue, 2, 
	        		Save.getInstance().getPosTabX(1, 0), Save.getInstance().getPosTabY(1, 0),
	        		Save.getInstance().getPosTabX(1, 1), Save.getInstance().getPosTabY(1, 1));
	        
	        for (int j = 0; j < NB_JOUEURS; j++) {
	        	for (int i = 0; i < joueurs[j].getNbNavires(); i++) {
	        		joueurs[j].getNavire(i).setPv(Save.getInstance().getPv(j, i));
	        		joueurs[j].getNavire(i).setNbTourRechargeCanonPrincipal(Save.getInstance().getNbTourRechargeCanonPrincipal(j, i));
	        		joueurs[j].getNavire(i).setNbTourRechargeCanonSecondaire(Save.getInstance().getNbTourRechargeCanonSecondaire(j, i));
	        		joueurs[j].getNavire(i).setNbDeplacementsRestants(Save.getInstance().getNbDeplacementsRestants(j, i));
	        		joueurs[j].getNavire(i).setDirection(Save.getInstance().getDirection(j, i));
	        		if(joueurs[j].getNavire(i).getPv()<1) {
					Map.getInstance().removeNavire(joueurs[j].getNavire(i));
					navireAffiche = null;
				}
	        	}
	        }
	        joueurCourant = joueurs[Save.getInstance().getJoueurCourant()];
	        joueurCourant.setNavireCourant(joueurCourant.getNavire(Save.getInstance().getNavireCourant()));
	        
	        Vector<Integer> possessionPhares = Save.getInstance().getPossessionPhares();
	        for (int i = 0; i < possessionPhares.size(); i++) {
	        	Map.getInstance().assignerPhare(i, possessionPhares.get(i));
	        }
        } else {
        	joueurs[0] = new Joueur(0, "Joueur 1", Color.red, 1);
	        joueurs[1] = new Joueur(1, "Joueur 2", Color.blue, 2);
	        joueurCourant = joueurs[0];
        }

        if(navireAffiche != null) navireAffiche = joueurs[1].getNavire(0);
        // Animation de tir
        tir = new Tir();
        tir.stopAt(16);
        tir.setCurrentFrame(16);
        // Selecteurs de case
        selecteurCaseNavireCourant = new SelecteurCase();
        selecteurCaseNavireCourant.setIdCaseSelectionnee(3);
        selecteurCaseNavireCourant.setSelecteurVisible(true);
        selecteurCaseNavireAffiche = new SelecteurCase();
        selecteurCaseNavireAffiche.setIdCaseSelectionnee(0);
        selecteurCaseNavireAffiche.setSelecteurVisible(true);
        selecteurCasesDeplacement = new SelecteurCase[3];
        for (int i =0;i<selecteurCasesDeplacement.length;i++){
            selecteurCasesDeplacement[i] = new SelecteurCase();
            selecteurCasesDeplacement[i].setIdCaseSelectionnee(1);
        }
        etat = GAME;
    }

    public void nextTurn() {
        if(conditionsRemplies()){
            // fin du tour du joueur courant : on verifie si il prend un phare
            Map.getInstance().verifierPriseDePhare(joueurCourant);

            // debut du tour du prochain joueur courant
            joueurCourant = joueurs[(joueurCourant.getId() + 1) % NB_JOUEURS];
            //navireAffiche = joueurs[(joueurCourant.getId() + 1) % NB_JOUEURS].getNavire(0);
            navireAffiche = null;
            joueurCourant.newTurn();

            // switch des couleurs de sélecteurs
            int tmp;
            if(joueurCourant.getId()==0) tmp = 3;
            else tmp = 0;
            selecteurCaseNavireCourant.setIdCaseSelectionnee(tmp);
            selecteurCaseNavireAffiche.setIdCaseSelectionnee(3-tmp);

            // un joueur gagne seulement au debut de son tour s'il a possede 3 phares
            if (Map.getInstance().victoire(joueurCourant.getId())) {
                System.out.println("Victoire de " + joueurCourant.getNom() + " !!");
                gagnant = joueurCourant.getNom();
                etat = END_OF_GAME;
            }
        }
    }

    public boolean conditionsRemplies(){
        for(int i=0;i<joueurCourant.getNbNavires();i++){
            if(!joueurCourant.getNavire(i).aBouge() && !joueurCourant.getNavire(i).isBloque(selecteurCasesDeplacement) && !joueurCourant.getNavire(i).isEtatDetruit()){
                return false;
            }
        }
        return true;
    }

    public void drawInterface(Graphics graphics, Navire navire, float x, float y) {
        // Affichage de la barre de vie en % de la vie maximale (du navire courant)
        graphics.setColor(Color.red);
        graphics.drawGradientLine(1, 1, Color.red, 1, 1, Color.red); // Indispensable de tracer un premier point inutile avec la couleur selectionnée, sinon la couleur n'est pas prise en compte dans le dessin #Obviously
        graphics.fillRect(x, y, 20, -100);
        graphics.setColor(Color.green);
        graphics.drawGradientLine(1, 1, Color.green, 1, 1, Color.green);
        graphics.fillRect(x, y, 20, -(navire.getPv() * 100 / navire.getPvMax() ));
        graphics.setColor(Color.black);
        graphics.drawGradientLine(1, 1, Color.black, 1, 1, Color.black);
        graphics.drawRect(x, y, 20, -100);

        if(navire == joueurCourant.getNavireCourant()){
        	// Draw du nombre de déplacements restant
        	interfaceInformations.getSprite(0, 0).draw(x-130, y+10, 24, 24);
        	graphics.setColor(Color.blue);
        	graphics.drawGradientLine(1, 1, Color.blue, 1, 1, Color.blue);
        	graphics.drawString(""+joueurCourant.getNavireCourant().getNbDeplacementsRestants(), x-100, y+13);
        
        	// Draw du temps de recharge restant
        	int nb = navire.getNbTourRechargeCanonPrincipal();
        	int nbMax = navire.getNbTourMaxRechargeCanonPrincipal() -1;
        	
        	int nbSec = navire.getNbTourRechargeCanonSecondaire();
        	int nbMaxSec = navire.getNbTourMaxRechargeCanonSecondaire() -1;
        	
        	
        	/* Deprecated
        	int numeroImage = 64 / nbMax;
        	
        	int xImage= (numeroImage * (nbMax - nb)) % 8;
        	int yImage= (numeroImage * (nbMax - nb)) / 8;
        	
        	
        	//System.out.println("max:"+nbMax+" left:"+nb+"x:"+xImage+" | y:"+yImage);
        	//interfaceInformations.getSprite(xImage,yImage).draw(x-80, y+10, 100, 100);
        	*/
        	
        	// Interface rechargement principal
        	// Dessiner le temps restant
        	graphics.setColor(Color.darkGray);
        	graphics.drawGradientLine(1, 1, Color.darkGray, 1, 1, Color.darkGray);
        	try {
        		graphics.fillArc(x-70, y+13, 24, 24, 0,  360 / (nbMax+1) * nb);
        	}catch (java.lang.ArithmeticException e) {
        		graphics.fillArc(x-70, y+13, 24, 24, 0,  0);
        	}
        	// Dessiner le contour
        	graphics.setColor(Color.black);
        	graphics.drawGradientLine(1, 1, Color.black, 1, 1, Color.black);
        	graphics.drawArc(x-70, y+13, 24, 24, 0,  360);
        	//Dessiner le chiffre
        	graphics.setColor(Color.white);
        	graphics.drawGradientLine(1, 1, Color.white, 1, 1, Color.white);
        	graphics.drawString(""+nb, x-63, y+15);
        	
        	// Interface rechargement secondaire
        	graphics.setColor(Color.red);
       		graphics.drawGradientLine(1, 1, Color.red, 1, 1, Color.red);
       		graphics.fillArc(x-70, y+13, 24, 24, 0,  0);
        	try{
        	graphics.fillArc(x-40, y+13, 24, 24, 0,  360 / (nbMaxSec+1) * nbSec);
        	}catch (java.lang.ArithmeticException e) {
        		graphics.fillArc(x-40, y+13, 24, 24, 0, 0);
        	}
        	// Dessiner le contour
        	graphics.setColor(Color.black);
        	graphics.drawGradientLine(1, 1, Color.black, 1, 1, Color.black);
        	graphics.drawArc(x-40, y+13, 24, 24, 0,  360);
        	//Dessiner le chiffre
        	graphics.setColor(Color.white);
        	graphics.drawGradientLine(1, 1, Color.white, 1, 1, Color.white);
        	graphics.drawString(""+nbSec, x-33, y+15);
        }
    }

    public void startTir(Point point) {
        tir.setXY(point);
        tir.setCurrentFrame(0);
        tir.start();
    }

    @Override
    public void keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_SPACE:
                nextTurn();
                break;

            case Input.KEY_ESCAPE:
                if(etat == GAME) etat = PAUSE;
                else if(etat == PAUSE) etat = GAME;
                break;
        }
    }

    public void mouseClicked(int button, int x, int y, int clickCount){
        if(etat == GAME){
            Point coordMaillage = new Point(x - (int) Map.getInstance().getPosition().getX(), y - (int) Map.getInstance().getPosition().getY());
            Point coordPosTab = Map.getInstance().coordMaillageToTab(coordMaillage); // Coordonnées de la case cliquée

            if(Map.getInstance().getNavires().containsValue(coordPosTab) && !joueurCourant.getNavireCourant().isDeplacementEnCours()){ // On selectionne le bateau cliqué
                Navire navire = Map.getInstance().getNavireAtCoord(coordPosTab);
                if(navire.getIdProprietaire() == joueurCourant.getId()){
                    if(navire.equals(joueurCourant.getNavireCourant())
                            && !joueurCourant.getNavireCourant().isTirEffectue()){
                        navire.switchMode();
                    }else{
                        joueurCourant.setNavireCourant(navire);
                    }
                }else{
                    navireAffiche = navire;
                }
                if(!navire.equals(joueurCourant.getNavireCourant())
                        && (joueurCourant.getNavireCourant().isEtatTirPrincipal()
                        || joueurCourant.getNavireCourant().isEtatTirSecondaire())){
                    boolean success = joueurCourant.getNavireCourant().tryShoot(coordPosTab, navire, joueurCourant.getNavireCourant().isEtatTirPrincipal());
                    if(success){
                        startTir(Map.getInstance().coordTabToMaillage(coordPosTab));
                        if(navire.getPv()<1){
                            Map.getInstance().removeNavire(navire);
                            navireAffiche = null;
                            // le joueur courant gagne si tous les bateaux adverses sont détruits, autrement dit si les siens sont les seuls restants
                            if(Map.getInstance().getNavires().size()==joueurCourant.nbBateauEnVie()) {
                                System.out.println("Victoire de " + joueurCourant.getNom() + " !!");
                                gagnant = joueurCourant.getNom();
                                etat = END_OF_GAME;
                            }
                        }
                    }
                }
            }else if(joueurCourant.getNavireCourant().isEtatDeplacement()){
                if(!bufferClick.contains(Map.getInstance().coordMaillageToTab(coordMaillage))
                        && joueurCourant.getNavireCourant().isEtatDeplacement()){ // On enregistre le clic dans une "liste d'attente d'action"
                    bufferClick.add(Map.getInstance().coordMaillageToTab(coordMaillage));
                }
            }
        }
    }

    public void executeClick(){
        joueurCourant.getNavireCourant().tryAccess(bufferClick.get(0));
        bufferClick.remove(0); // On supprime l'action enregistrée dans tous les cas. Si la méthode try access s'est bien déroulée et si elle ne s'est pas bien déroulée. Il ne faut pas encombrer le buffer avec des actions invalides.
    }
    @Override
    public int getID() {
        return ID;
    }
}
