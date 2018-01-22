package game;
 
import java.awt.Point;

import map.SelecteurCase;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import joueur.Joueur;
import map.Direction;
import map.Map;
import utility.Music;
import java.util.ArrayList;

public class Game extends BasicGameState {
    public static final int ID = 2;
    public static final int NB_JOUEURS = 2;
    
    private GameContainer container;
    private StateBasedGame game;
    private Animation background;
    private ArrayList<Point> bufferClick; // Changer le type
    private Point position = new Point();
    // Déroulement partie
    private Joueur[] joueurs = new Joueur[NB_JOUEURS];
    private Joueur joueurCourant;
    private SelecteurCase selecteurCaseNavireCourant;
    private SelecteurCase[] selecteurCasesDeplacement;


    @Override
    public void init(GameContainer _container, StateBasedGame _game) throws SlickException {
        //spriteSheetdeNavire = new SpriteSheet("resources/images/AmiralPirate.png",200 , 250);
        //AmiralPirate=new Animation(spriteSheetdeNavire,8000);
        //this.AmiralPirate = ImageIO.read(getClass().getResource("resources/images/AmiralPirate.png"));
        this.game = _game;
        this.container = _container;
        SpriteSheet spriteSheet = new SpriteSheet("resources/images/seaAnimation.png", 1080, 810);
        this.background = new Animation();
        for (int x = 0; x < 2; x++) {
            background.addFrame(spriteSheet.getSprite(x, 0), 1200);
        }
        bufferClick = new ArrayList<Point>();
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        background.update(delta);
        if(joueurCourant.deplacementEnCours()){
            joueurCourant.getNavireCourant().animationDeplacement();
        }

        if(!bufferClick.isEmpty() && !joueurCourant.getNavireCourant().isDeplacementEnCours()){
            executeClick();
        }
        // Selecteurs de case
        if(joueurCourant.getNavireCourant().isDeplacementEnCours()) {
            selecteurCaseNavireCourant.setPosition(joueurCourant.getNavireCourant().getDestination());
        }else{
            selecteurCaseNavireCourant.setPosition(joueurCourant.getNavireCourant().getPosition());
        }
        joueurCourant.getNavireCourant().getPossibleDeplacements(selecteurCasesDeplacement);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
        graphics.drawAnimation(background, 0, 0);
        Map.getInstance().draw();
        selecteurCaseNavireCourant.draw();
        for (SelecteurCase selecteur:selecteurCasesDeplacement) selecteur.draw();
        
    	
        // Render de l'interface
        // Affichage de la barre de vie en % de la vie maximale
        graphics.setColor(Color.red);
    	graphics.drawGradientLine(1, 1, Color.red, 1, 1, Color.red); // Indispensable de tracer un premier point inutile avec la couleur selectionnée, sinon la couleur n'est pas prise en compte dans le dessin #Obviously
    	graphics.fillRect(145, 110, 20, -100);
    	graphics.setColor(Color.green);
    	graphics.drawGradientLine(1, 1, Color.green, 1, 1, Color.green); // LOL MDR POURQUOI QUAND JE METS CETTE LIGNE, LA LIGNE DE CODE PRÉCÉDENTE MARCHE ???
    	graphics.fillRect(145, 110, 20, -(joueurCourant.getNavireCourant().getPv() * 100 / joueurCourant.getNavireCourant().getPvMax() ));
    	
    	// Affichage de l'image représentant le navire sélectionné
    	joueurCourant.getNavireCourant().drawStatus();
    	
    	// Affichage déplacement
    	graphics.setColor(Color.cyan);
    	graphics.drawGradientLine(1, 1, Color.cyan, 1, 1, Color.cyan);
    	// A terminer : afficher les autres infos comme le nombre de déplacements restant et le temps de recharge d'un tir
    	
    
    
        
        
        
    }

    public void newGame() throws SlickException {
        Map.getInstance().load("test.txt");
        Map.getInstance().centrerDansFenetre(container);
        joueurs[0] = new Joueur(0, "Joueur 1", java.awt.Color.RED, 1);
        joueurs[1] = new Joueur(1, "Joueur 2", java.awt.Color.BLUE, 2);
        joueurCourant = joueurs[0];
        // Selecteurs de case
        selecteurCaseNavireCourant = new SelecteurCase(3);
        selecteurCaseNavireCourant.setIdCaseSelectionnee(0);
        selecteurCaseNavireCourant.setSelecteurVisible(true);
        selecteurCasesDeplacement = new SelecteurCase[3];
        for (int i =0;i<selecteurCasesDeplacement.length;i++){
            selecteurCasesDeplacement[i] = new SelecteurCase(3);
            selecteurCasesDeplacement[i].setIdCaseSelectionnee(1);
        }
    }

    public void nextTurn() {
        // fin du tour du joueur courant : on verifie si il prend un phare
    	Map.getInstance().verifierPriseDePhare(joueurCourant);
    	
    	// debut du tour du prochain joueur courant
    	joueurCourant = joueurs[(joueurCourant.getId() + 1) % NB_JOUEURS];
        joueurCourant.newTurn();
    	
        // un joueur gagne seulement au debut de son tour s'il a possede 3 phares
    	if (Map.getInstance().victoire(joueurCourant.getId())) {
    		System.out.println("Victoire de " + joueurCourant.getNom() + " !!");
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
                Music.playMenu();
                game.enterState(MainScreen.ID);
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
                joueurCourant.setNavireCourant(joueurCourant.getNavire(0));
                break;

            case Input.KEY_2:
                joueurCourant.setNavireCourant(joueurCourant.getNavire(1));
                break;
        }
    }

    public void mouseClicked(int button, int x, int y, int clickCount){
        Point coordMaillage = new Point(x - (int) Map.getInstance().getPosition().getX(), y - (int) Map.getInstance().getPosition().getY());
        Point coordPosTab = Map.getInstance().coordMaillageToTab(coordMaillage); // Coordonnées de la case cliquée
        
        if(Map.getInstance().getNavires().containsValue(coordPosTab)){ // On selectionne le bateau cliqué
        	joueurCourant.setNavireCourant(Map.getInstance().getNavireAtCoord(coordPosTab));
        	
        }else{
        	
        	if(!bufferClick.contains(Map.getInstance().coordMaillageToTab(coordMaillage))){ // On enregistre le clic dans une "liste d'attente d'action"
        		bufferClick.add(Map.getInstance().coordMaillageToTab(coordMaillage));
        	}
        }
    }

    public void executeClick(){
    	if(joueurCourant.getId() == joueurCourant.getNavireCourant().getIdProprietaire()){ // Si le bateau appartient au bon joueur
    		joueurCourant.getNavireCourant().tryAccess(bufferClick.get(0));
    	}
        
    	bufferClick.remove(0); // On supprime l'action enregistrée dans tous les cas. Si la méthode try access s'est bien déroulée et si elle ne s'est pas bien déroulée. Il ne faut pas encombrer le buffer avec des actions invalides.
    }
    @Override
    public int getID() {
        return ID;
    }
}
