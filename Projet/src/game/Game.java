package game;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import joueur.Boat;
import map.Map;
import utility.Music;

public class Game extends BasicGameState {
	public static final int ID = 2;
	private GameContainer container;
	private StateBasedGame game;
	
	private Map map;
	private Animation background;
	private Boat boat;
	
	public Game() {
    }

	@Override
	public void init(GameContainer container, StateBasedGame _game) throws SlickException {
		SpriteSheet spriteSheet = new SpriteSheet("resources/images/seaAnimation.png", 1080, 810);
		this.game = _game;
		this.boat = new Boat();
		
		this.map = new Map();
		this.map.load("test.txt");
		map.centrerDansFenetre(container);

	    this.background = new Animation();
	    for (int x = 0; x < 2; x++) {
	    	background.addFrame(spriteSheet.getSprite(x, 0), 1200);
        }
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		background.update(delta);
		boat.update(container, delta);
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		graphics.drawAnimation(background, 0, 0);
		map.draw();
		boat.render(container, graphics);
	}
	
	@Override
    public void keyReleased(int key, char c) {
		boat.keyReleased(key, c);
		if(key == Input.KEY_ESCAPE){
			Music.playMenu();
			game.enterState(MainScreen.ID);
		}else if(key == Input.KEY_SPACE){
			//game.enterState(EditeurMap.ID);
		}
    }
    
    @Override
    public void keyPressed(int key, char c) {
    	boat.keyPressed(key, c);
    }

	@Override
	public int getID() {
		return ID;
	}
}