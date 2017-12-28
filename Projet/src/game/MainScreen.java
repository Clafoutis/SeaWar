package game;

import org.newdawn.slick.*;
import org.newdawn.slick.gui.*;
import org.newdawn.slick.state.*;
import utility.Music;


public class MainScreen extends BasicGameState {
    public static final int ID = 1;
    private StateBasedGame game;
    private GameContainer container;
    private Image background, parchemin;
    private MouseOverArea jouerArea, quitterArea;

    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
        this.container = container;
        // background et parchemin
        this.background = new Image("resources/images/menu.jpg");
        this.parchemin = new Image("resources/images/parchemin.png");
        // boutons jouer et quitter
        Image jouer = new Image("resources/images/jouer.png");
        Image jouerHover = new Image("resources/images/jouer-hover.png");
        Image quitter = new Image("resources/images/quitter.png");
        Image quitterHover = new Image("resources/images/quitter-hover.png");
        this.jouerArea = new MouseOverArea(container, jouer, 100, 360, 150, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
                startGame();
            }
        });
        jouerArea.setMouseOverImage(jouerHover);
        this.quitterArea = new MouseOverArea(container, quitter, 75, 540, 225, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
                exit();
            }
        });
        quitterArea.setMouseOverImage(quitterHover);
        Music.playMenu();
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        background.draw(0, 0, container.getWidth(), container.getHeight());
        parchemin.draw(0, 275, 360, 450);
        jouerArea.render(container, g);
        quitterArea.render(container, g);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int i) throws SlickException {

    }

    public void keyReleased(int key, char c) {
        if (Input.KEY_ESCAPE == key) {
            exit();
        }
    }

    private void startGame(){
        Music.playGame();
        game.enterState(Game.ID);
    }

    private void exit(){
        container.exit();
    }

    @Override
    public int getID() {
        return ID;
    }
}
