package game;

import java.awt.Point;
import java.util.Vector;

import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.*;
import org.newdawn.slick.state.*;

import utility.FileUtility;
import utility.Music;


public class MainScreen extends BasicGameState {
    public static final int ID = 1;
    private StateBasedGame game;
    private GameContainer container;
    private Image background, parchemin;
    private MouseOverArea jouerArea, editerArea, quitterArea;
    private final int MENU = 0, CHOIX_MAP_JEU = 1, CHOIX_MAP_EDITION = 2;
	private int etat;
	private UnicodeFont font;
	private Vector<Rectangle> nomMapRects = new Vector<Rectangle>();
	private Vector<Point> nomMapPoints = new Vector<Point>();
	private Vector<String> nomMaps = new Vector<String>();

    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
        this.container = container;
        this.etat = MENU;
        // background et parchemin
        this.background = new Image("resources/images/menu.jpg");
        this.parchemin = new Image("resources/images/parchemin.png");
        // boutons jouer et quitter
        Image jouer = new Image("resources/images/jouer.png");
        Image jouerHover = new Image("resources/images/jouer-hover.png");
        Image editer = new Image("resources/images/jouer.png");
        Image editerHover = new Image("resources/images/jouer-hover.png");
        Image quitter = new Image("resources/images/quitter.png");
        Image quitterHover = new Image("resources/images/quitter-hover.png");
        this.jouerArea = new MouseOverArea(container, jouer, 100, 360, 150, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
                startGame();
            }
        });
        jouerArea.setMouseOverImage(jouerHover);
        this.editerArea = new MouseOverArea(container, editer, 100, 450, 150, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
            	startEditeur();
            }
        });
        editerArea.setMouseOverImage(editerHover);
        this.quitterArea = new MouseOverArea(container, quitter, 75, 540, 225, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
                exit();
            }
        });
        quitterArea.setMouseOverImage(quitterHover);
        Music.playMenu();
        
        font = new UnicodeFont(new java.awt.Font("DejaVu Serif", java.awt.Font.PLAIN, 20));
		font.addAsciiGlyphs();
		font.addGlyphs(400, 600);
		font.getEffects().add(new ColorEffect(java.awt.Color.black));
		font.loadGlyphs();
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        background.draw(0, 0, container.getWidth(), container.getHeight());

        switch (etat) {
        case MENU:
	        parchemin.draw(0, 275, 360, 450);
	        jouerArea.render(container, g);
	        editerArea.render(container, g);
	        quitterArea.render(container, g);
	        break;
	        
        case CHOIX_MAP_JEU:
	        parchemin.draw((container.getWidth()-360)/2, (container.getHeight()-450)/2, 360, 450);
	        for (int i = 0; i < nomMaps.size(); i++) {
	        	font.drawString(nomMapPoints.get(i).x, nomMapPoints.get(i).y, nomMaps.get(i), Color.black);
	        }
	        break;
	        
        case CHOIX_MAP_EDITION:
	        parchemin.draw((container.getWidth()-360)/2, (container.getHeight()-450)/2, 360, 450);
	        break;
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int i) throws SlickException {

    }

    public void keyReleased(int key, char c) {
    	switch (key) {
    	case Input.KEY_ESCAPE:
    		switch (etat) {
            case MENU:
    	        exit();
    	        break;
    	        
            case CHOIX_MAP_JEU:
    	        etat = MENU;
    	        break;
    	        
            case CHOIX_MAP_EDITION:
            	etat = MENU;
    	        break;
            }
			break;
		}
    }

    private void startGame() {
        Music.playGame();
        try {
            ((Game) game.getState(Game.ID)).newGame("test");
        } catch (SlickException e) {
            e.printStackTrace();
        }
        game.enterState(Game.ID);
    }
    
    private void startEditeur() {
    	etat = CHOIX_MAP_JEU;
    	nomMaps = FileUtility.getInstance().loadMapNames();
    	nomMaps.insertElementAt("<Nouvelle Map>", 0);
    	for (int i = 0; i < nomMaps.size(); i++) {
    		nomMapPoints.add(new Point((container.getWidth()-360)/2 + 75, (container.getHeight()-450)/2 + 80 + i*30));
    		nomMapRects.add(new Rectangle(nomMapPoints.lastElement().x, nomMapPoints.lastElement().y, 200, 30));
    	}
    }
    
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount){
        if(etat == CHOIX_MAP_JEU) {
        	for (int i = 0; i < nomMaps.size(); i++) {
        		if (nomMapRects.get(i).contains(x, y)) {
        			if (i == 0) {
        				((EditeurMap) game.getState(EditeurMap.ID)).startEditeur(null);
        			} else {
        				((EditeurMap) game.getState(EditeurMap.ID)).startEditeur(nomMaps.get(i));
        			}

    				game.enterState(EditeurMap.ID);
        			etat = MENU;
        		}
        	}
        }
    }

    private void exit(){
        container.exit();
    }

    @Override
    public int getID() {
        return ID;
    }
}
