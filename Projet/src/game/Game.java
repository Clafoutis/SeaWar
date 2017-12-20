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
import joueur.Navire;
import map.Direction;
import map.Map;

public class Game extends BasicGameState {
	public static final int ID = 2;
	private GameContainer container;
	private StateBasedGame game;

	private Animation background;
	private final int NB_JOUEURS = 2;
	private Joueur[] joueurs = new Joueur[NB_JOUEURS];
	private Joueur joueurCourant;
	
	public Game() {
    }

	@Override
	public void init(GameContainer _container, StateBasedGame _game) throws SlickException {
		SpriteSheet spriteSheet = new SpriteSheet("resources/images/seaAnimation.png", 1080, 810);
		this.game = _game;
		this.container = _container;

		Map.getInstance().load("test.txt");
		Map.getInstance().centrerDansFenetre(container);
		joueurs[0] = new Joueur("Joueur 1", Color.RED);
		joueurs[1] = new Joueur("Joueur 2", Color.BLUE);
		joueurCourant = joueurs[0]; 

	    this.background = new Animation();
	    for (int x = 0; x < 2; x++) {
	    	background.addFrame(spriteSheet.getSprite(x, 0), 1200);
        }
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		background.update(delta);
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		graphics.drawAnimation(background, 0, 0);
		Map.getInstance().draw();
		//joueurCourant.getNavireCourant().draw();
	}
	
	@Override
    public void keyReleased(int key, char c) {
    }
    
    @Override
    public void keyPressed(int key, char c) {
    	switch (key) {
			case Input.KEY_SPACE:
				Map.getInstance().load("test.txt");
				Map.getInstance().centrerDansFenetre(container);
				Map.getInstance().selectionnerCase(0, new Point(0, 0));
				game.enterState(EditeurMap.ID);
				break;
				
			case Input.KEY_RETURN:
				game.enterState(EditeurMap.ID);
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
    	}
    }

	@Override
	public int getID() {
		return ID;
	}
}
