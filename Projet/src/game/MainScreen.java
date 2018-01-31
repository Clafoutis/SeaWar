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
import utility.Save;


public class MainScreen extends BasicGameState {
    public static final int ID = 1;
    private StateBasedGame game;
    private GameContainer container;
    private Image background, parchemin;
    private MouseOverArea jouerArea, reprendreArea, editerArea, quitterArea;
    private final int MENU = 0, CHOIX_MAP = 1;
	private int etat;
	private boolean demarrerEditeur = false;
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
        Image reprendre = new Image("resources/images/reprendre.png");
        Image reprendreHover = new Image("resources/images/reprendre-hover.png");
        Image jouer = new Image("resources/images/jouer.png");
        Image jouerHover = new Image("resources/images/jouer-hover.png");
        Image editer = new Image("resources/images/editer.png");
        Image editerHover = new Image("resources/images/editer-hover.png");
        Image quitter = new Image("resources/images/quitter.png");
        Image quitterHover = new Image("resources/images/quitter-hover.png");
        this.reprendreArea = new MouseOverArea(container, reprendre, 100, 340, 150, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
                startGame();
            }
        });
        reprendreArea.setMouseOverImage(reprendreHover);
        this.jouerArea = new MouseOverArea(container, jouer, 100, 400, 150, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
            	Save.getInstance().effacerSauvegarde();
                startGame();
            }
        });
        jouerArea.setMouseOverImage(jouerHover);
        this.editerArea = new MouseOverArea(container, editer, 115, 470, 150, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
            	startEditeur();
            }
        });
        editerArea.setMouseOverImage(editerHover);
        this.quitterArea = new MouseOverArea(container, quitter, 75, 550, 225, 60, new ComponentListener() {
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
	        if (Save.getInstance().isSauvegardePresente())
	        	reprendreArea.render(container, g);
	        jouerArea.render(container, g);
	        editerArea.render(container, g);
	        quitterArea.render(container, g);
	        break;
	        
        case CHOIX_MAP:
	        parchemin.draw((container.getWidth()-360)/2, (container.getHeight()-450)/2, 360, 450);
	        for (int i = 0; i < nomMaps.size(); i++) {
	        	font.drawString(nomMapPoints.get(i).x, nomMapPoints.get(i).y, nomMaps.get(i), Color.black);
	        }
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
    	        
            case CHOIX_MAP:
    	        etat = MENU;
    	        break;
            }
			break;
		}
    }

    private void startGame() {
        etat = CHOIX_MAP;
        demarrerEditeur = false;

        if (Save.getInstance().isSauvegardePresente()) {
			try {
	            ((Game) game.getState(Game.ID)).newGame(Save.getInstance().getNomMap());
	        } catch (SlickException e) {
	            e.printStackTrace();
	        }
	        game.enterState(Game.ID);
	        etat = MENU;
        } else {
	    	nomMapRects = new Vector<Rectangle>();
	    	nomMapPoints = new Vector<Point>();
	    	nomMaps = FileUtility.getInstance().loadMapNames();
	    	for (int i = 0; i < nomMaps.size(); i++) {
	    		nomMapPoints.add(new Point((container.getWidth()-360)/2 + 75, (container.getHeight()-450)/2 + 80 + i*30));
	    		nomMapRects.add(new Rectangle(nomMapPoints.lastElement().x, nomMapPoints.lastElement().y, 200, 30));
	    	}
        }
    }
    
    private void startEditeur() {
    	etat = CHOIX_MAP;
    	demarrerEditeur = true;
    	nomMapRects = new Vector<Rectangle>();
    	nomMapPoints = new Vector<Point>();
    	nomMaps = FileUtility.getInstance().loadMapNames();
    	nomMaps.insertElementAt("<Nouvelle Map>", 0);
    	for (int i = 0; i < nomMaps.size(); i++) {
    		nomMapPoints.add(new Point((container.getWidth()-360)/2 + 75, (container.getHeight()-450)/2 + 80 + i*30));
    		nomMapRects.add(new Rectangle(nomMapPoints.lastElement().x, nomMapPoints.lastElement().y, 200, 30));
    	}
    }
    
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount){
        if(etat == CHOIX_MAP) {
        	for (int i = 0; i < nomMaps.size(); i++) {
        		if (nomMapRects.get(i).contains(x, y)) {
        			if (demarrerEditeur) {
	        			if (i == 0) {
	        				((EditeurMap) game.getState(EditeurMap.ID)).startEditeur(null);
	        			} else {
	        				((EditeurMap) game.getState(EditeurMap.ID)).startEditeur(nomMaps.get(i));
	        			}
	        			game.enterState(EditeurMap.ID);
        			} else {
        				Music.playGame();
        				try {
        		            ((Game) game.getState(Game.ID)).newGame(nomMaps.get(i));
        		        } catch (SlickException e) {
        		            e.printStackTrace();
        		        }
        		        game.enterState(Game.ID);
        			}
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
