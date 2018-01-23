package game;

import java.awt.Point;

import joueur.Navire;
import map.SelecteurCase;
import org.newdawn.slick.*;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import joueur.Joueur;
import map.CouleurSelecteur;
import map.Direction;
import map.Map;
import utility.Music;
import java.util.ArrayList;
import java.util.HashMap;
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
    private Point position = new Point();
    // Déroulement partie
    private Joueur[] joueurs = new Joueur[NB_JOUEURS];
    private Joueur joueurCourant;
    private Navire navireAffiche; // forcément ennemi
    private SelecteurCase selecteurCaseNavireCourant;
    private SelecteurCase selecteurCaseNavireAffiche; // forcément ennemi
    private SelecteurCase[] selecteurCasesDeplacement;
    private java.util.Map<SelecteurCase, Point> selecteursCasesTirs = new HashMap<SelecteurCase, Point>();
    // Pause
    private Image parchemin;
    private MouseOverArea quitterArea;


    @Override
    public void init(GameContainer _container, StateBasedGame _game) throws SlickException {
        //spriteSheetdeNavire = new SpriteSheet("resources/images/AmiralPirate.png",200 , 250);
        //AmiralPirate=new Animation(spriteSheetdeNavire,8000);
        //this.AmiralPirate = ImageIO.read(getClass().getResource("resources/images/AmiralPirate.png"));
        this.game = _game;
        this.container = _container;
        this.background = new Image("resources/images/backgroundGame.png");
        bufferClick = new ArrayList<>();
        parchemin = new Image("resources/images/parchemin.png");
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
        newGame();
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
        selecteurCaseNavireAffiche.setPosition(navireAffiche.getPosition());
        joueurCourant.getNavireCourant().getPossibleDeplacements(selecteurCasesDeplacement);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
        background.draw(0, 0, container.getWidth(), container.getHeight());
        Map.getInstance().draw();
        
        // draw les sélecteurs des déplacements et des tirs possibles
        for (SelecteurCase selecteur:selecteurCasesDeplacement) selecteur.draw();
        for (SelecteurCase selecteur:selecteursCasesTirs.keySet()) selecteur.draw();
        
        // draw le sélecteur du navire courant et affiche
        selecteurCaseNavireAffiche.draw();
        selecteurCaseNavireCourant.draw();
        
        // draw les navires
        for (Joueur joueur:joueurs) {
            joueur.getNavire(0).draw();
            joueur.getNavire(1).draw();
        }
        
        // Render de l'interface
        // Affichage de la barre de vie en % de la vie maximale (du navire courant)
        graphics.setColor(Color.red);
        graphics.drawGradientLine(1, 1, Color.red, 1, 1, Color.red); // Indispensable de tracer un premier point inutile avec la couleur selectionnée, sinon la couleur n'est pas prise en compte dans le dessin #Obviously
        graphics.fillRect(145, 275, 20, -100);
        graphics.setColor(Color.green);
        graphics.drawGradientLine(1, 1, Color.green, 1, 1, Color.green); // LOL MDR POURQUOI QUAND JE METS CETTE LIGNE, LA LIGNE DE CODE PRÉCÉDENTE MARCHE ???
        graphics.fillRect(145, 275, 20, -(joueurCourant.getNavireCourant().getPv() * 100 / joueurCourant.getNavireCourant().getPvMax() ));

        // Affichage de l'image représentant le navire sélectionné (courant)
        joueurCourant.getNavireCourant().drawStatus(false);

        // Affichage de la barre de vie en % de la vie maximale (du navire sélectionné)
        graphics.setColor(Color.red);
        graphics.drawGradientLine(1, 1, Color.red, 1, 1, Color.red); // Indispensable de tracer un premier point inutile avec la couleur selectionnée, sinon la couleur n'est pas prise en compte dans le dessin #Obviously
        graphics.fillRect(915, 275, 20, -100);
        graphics.setColor(Color.green);
        graphics.drawGradientLine(1, 1, Color.green, 1, 1, Color.green); // LOL MDR POURQUOI QUAND JE METS CETTE LIGNE, LA LIGNE DE CODE PRÉCÉDENTE MARCHE ???
        graphics.fillRect(915, 275, 20, -(navireAffiche.getPv() * 100 / navireAffiche.getPvMax() ));

        // Affichage de l'image représentant le navire sélectionné (sélectionné)
        navireAffiche.drawStatus(true);

        // Affichage déplacement
        graphics.setColor(Color.cyan);
        graphics.drawGradientLine(1, 1, Color.cyan, 1, 1, Color.cyan);
        // A terminer : afficher les autres infos comme le nombre de déplacements restant et le temps de recharge d'un tir

        if(etat == PAUSE){
            parchemin.draw(container.getWidth()/2 - 180, container.getHeight()/2 - 225, 360, 450);
            quitterArea.render(container, graphics);
            // @TODO les boutons retour à la partie et sauvegarde du jeu
        }else if(etat == END_OF_GAME){
            parchemin.draw(container.getWidth()/2 - 180, container.getHeight()/2 - 225, 360, 450);
            // @TODO un message de félicitation au gagnant + un bouton restart (et placer le quitter)
        }
    }

    public void newGame() throws SlickException {
        Map.getInstance().load("test.txt");
        Map.getInstance().centrerDansFenetre(container);
        joueurs[0] = new Joueur(0, "Joueur 1", Color.red, 1);
        joueurs[1] = new Joueur(1, "Joueur 2", Color.blue, 2);
        joueurCourant = joueurs[0];
        navireAffiche = joueurs[1].getNavire(0);
        // Selecteurs de case
        selecteurCaseNavireCourant = new SelecteurCase();
        selecteurCaseNavireCourant.setCouleurSelecteur(CouleurSelecteur.ROUGE);
        selecteurCaseNavireCourant.setSelecteurVisible(true);
        selecteurCaseNavireAffiche = new SelecteurCase();
        selecteurCaseNavireAffiche.setCouleurSelecteur(CouleurSelecteur.CYAN);
        selecteurCaseNavireAffiche.setSelecteurVisible(true);
        selecteurCasesDeplacement = new SelecteurCase[3];
        for (int i =0;i<selecteurCasesDeplacement.length;i++){
            selecteurCasesDeplacement[i] = new SelecteurCase();
            selecteurCasesDeplacement[i].setCouleurSelecteur(CouleurSelecteur.JAUNE);
        }
        etat = GAME;
    }

    public void nextTurn() {
    	// fin du tour du joueur courant : on verifie si il prend un phare
    	Map.getInstance().verifierPriseDePhare(joueurCourant);
    	
    	// debut du tour du prochain joueur courant
    	joueurCourant = joueurs[(joueurCourant.getId() + 1) % NB_JOUEURS];
        navireAffiche = joueurs[(joueurCourant.getId() + 1) % NB_JOUEURS].getNavire(0);
        joueurCourant.newTurn();

        // switch des couleurs de sélecteurs
        int tmp;
        if(joueurCourant.getId()==0) tmp = 2;
        else tmp = 0;
        selecteurCaseNavireCourant.setCouleurSelecteur(CouleurSelecteur.values()[tmp]);
        selecteurCaseNavireAffiche.setCouleurSelecteur(CouleurSelecteur.values()[2-tmp]);
    	
        // un joueur gagne seulement au debut de son tour s'il a possede 3 phares
    	if (Map.getInstance().victoire(joueurCourant.getId())) {
    		System.out.println("Victoire de " + joueurCourant.getNom() + " !!");
    		// etat = END_OF_GAME;
    	}
    }

    @Override
    public void keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_SPACE:
                nextTurn();
                break;

            case Input.KEY_RETURN:
                game.enterState(EditeurMap.ID);
                break;

            case Input.KEY_ESCAPE:
                if(etat == GAME) etat = PAUSE;
                else if(etat == PAUSE) etat = GAME;
                break;

            case Input.KEY_NUMPAD8:
                joueurCourant.getNavireCourant().deplacer(Direction.HAUT);
                break;

            case Input.KEY_NUMPAD9:
                joueurCourant.getNavireCourant().deplacer(Direction.HAUT_DROITE);
                break;

            case Input.KEY_NUMPAD3:
                joueurCourant.getNavireCourant().deplacer(Direction.BAS_DROITE);
                break;

            case Input.KEY_NUMPAD2:
                joueurCourant.getNavireCourant().deplacer(Direction.BAS);
                break;

            case Input.KEY_NUMPAD1:
                joueurCourant.getNavireCourant().deplacer(Direction.BAS_GAUCHE);
                break;

            case Input.KEY_NUMPAD7:
                joueurCourant.getNavireCourant().deplacer(Direction.HAUT_GAUCHE);
                break;

            case Input.KEY_1:
            	joueurCourant.getNavireCourant().deselectionnerCanon(selecteursCasesTirs);
                joueurCourant.setNavireCourant(joueurCourant.getNavire(0));
                break;

            case Input.KEY_2:
            	joueurCourant.getNavireCourant().deselectionnerCanon(selecteursCasesTirs);
                joueurCourant.setNavireCourant(joueurCourant.getNavire(1));
                break;
                
            case Input.KEY_A:
            	joueurCourant.getNavireCourant().selectionnerCanonPrincipal(selecteursCasesTirs);
                break;
                
            case Input.KEY_Z:
            	joueurCourant.getNavireCourant().selectionnerCanonSecondaire(selecteursCasesTirs);
                break;
                
            case Input.KEY_E:
            	joueurCourant.getNavireCourant().deselectionnerCanon(selecteursCasesTirs);
                break;
        }
    }

    public void mouseClicked(int button, int x, int y, int clickCount){
        if(etat == GAME){
            Point coordMaillage = new Point(x - (int) Map.getInstance().getPosition().getX(), y - (int) Map.getInstance().getPosition().getY());
            Point coordPosTab = Map.getInstance().coordMaillageToTab(coordMaillage); // Coordonnées de la case cliquée

            if(Map.getInstance().getNavires().containsValue(coordPosTab)){ // On selectionne le bateau cliqué
                Navire navire = Map.getInstance().getNavireAtCoord(coordPosTab);
                if(navire.getIdProprietaire() == joueurCourant.getId()){
                	joueurCourant.getNavireCourant().deselectionnerCanon(selecteursCasesTirs);
                    joueurCourant.setNavireCourant(navire);
                }else{
                    navireAffiche = navire;
                }
            }else{
                if(!bufferClick.contains(Map.getInstance().coordMaillageToTab(coordMaillage))){ // On enregistre le clic dans une "liste d'attente d'action"
                    bufferClick.add(Map.getInstance().coordMaillageToTab(coordMaillage));
                }
            }
        }
    }

    public void executeClick(){
        if (joueurCourant.getNavireCourant().modeTirCanonActive()) {
    		joueurCourant.getNavireCourant().tirer(selecteursCasesTirs, bufferClick.get(0));
    	} else {
    		joueurCourant.getNavireCourant().tryAccess(bufferClick.get(0));
    	}
        bufferClick.remove(0); // On supprime l'action enregistrée dans tous les cas. Si la méthode try access s'est bien déroulée et si elle ne s'est pas bien déroulée. Il ne faut pas encombrer le buffer avec des actions invalides.
    }
    @Override
    public int getID() {
        return ID;
    }
}
