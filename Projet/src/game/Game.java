package game;

import java.awt.Color;
import java.awt.Point;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
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
    private GameContainer container;
    private StateBasedGame game;
    private Animation background;
    private ArrayList<Point> bufferClick; // Changer le type
    private Point position = new Point();
    // Déroulement partie
    private final int NB_JOUEURS = 2;
    private Joueur[] joueurs = new Joueur[NB_JOUEURS];
    private Joueur joueurCourant;


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
        bufferClick = new ArrayList<>();
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
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
        graphics.drawAnimation(background, 0, 0);
        Map.getInstance().draw();
        graphics.drawString( "Joueur 1 :", 0, 30);
        graphics.drawString( "Points de vie Amiral :"+joueurs[0].getNavire(0).getPv(), 0, 50);
        graphics.drawString( "Points de vie Frégate :"+joueurs[0].getNavire(1).getPv(), 0, 70);
        graphics.drawString( "nbDeplacements Amiral :"+joueurs[0].getNavire(0).getNbDeplacementsRestants(), 0, 90);
        graphics.drawString( "nbDeplacements Frégate :"+joueurs[0].getNavire(1).getNbDeplacementsRestants(), 0, 110);
        graphics.drawString( "Joueur 2 :", 800, 30);
        graphics.drawString( "Points de vie Amiral :"+joueurs[1].getNavire(0).getPv(), 800, 50);
        graphics.drawString( "Points de vie Frégate :"+joueurs[1].getNavire(1).getPv(), 800, 70);
        graphics.drawString( "nbDeplacements Amiral :"+joueurs[1].getNavire(0).getNbDeplacementsRestants(), 800, 90);
        graphics.drawString( "nbDeplacements Frégate :"+joueurs[1].getNavire(1).getNbDeplacementsRestants(), 800, 110);
    }

    public void newGame() throws SlickException {
        Map.getInstance().load("test.txt");
        Map.getInstance().centrerDansFenetre(container);
        joueurs[0] = new Joueur(0, "Joueur 1", Color.RED, 1);
        joueurs[1] = new Joueur(1, "Joueur 2", Color.BLUE, 2);
        joueurCourant = joueurs[0];
    }

    public void nextTurn() {
        joueurCourant = joueurs[(joueurCourant.getId() + 1) % 2];
        joueurCourant.newTurn();
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
        if(!bufferClick.contains(Map.getInstance().coordMaillageToTab(coordMaillage))){
            bufferClick.add(Map.getInstance().coordMaillageToTab(coordMaillage));
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