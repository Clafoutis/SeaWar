package game;


import java.awt.Point;

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

public class EditeurMap extends BasicGameState {
	public static final int ID = 3;
	private GameContainer container;
	private StateBasedGame game;
	
	private Map map;
	private Animation background;
	private int idCour = 0;
	//private Integer coordTab.x = 0;
	//private Integer coordTab.y = 0;
	private Point coordTab = new Point();
	
	public EditeurMap() {
    }

	@Override
	public void init(GameContainer container, StateBasedGame _game) throws SlickException {
		SpriteSheet spriteSheet = new SpriteSheet("resources/images/backgroundEditeurMap.png", 1080, 810);
		this.game = _game;
		this.map = new Map();
		this.map.load("test.txt");
		this.map.centrerDansFenetre(container);
		this.map.selectionnerCase(idCour, coordTab);

	    this.background = new Animation();
	    for (int x = 0; x < 1; x++) {
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
		map.draw();
	}
	
	@Override
    public void keyReleased(int key, char c) {
    }
    
    @Override
    public void keyPressed(int key, char c) {
    	switch (key) {
    	
	    	case Input.KEY_UP:
				if (coordTab.y > 0) {
					coordTab.y--;
				}
				break;
				
			case Input.KEY_DOWN:
				if (coordTab.y < map.getNbCases().y - 1) {
					coordTab.y++;
				}
				break;

			case Input.KEY_LEFT:
				if (coordTab.x > 0) {
					coordTab.x--;
				}
				break;
				
			case Input.KEY_RIGHT:
				if (coordTab.x < map.getNbCases().x - 1) {
					coordTab.x++;
				}
				break;
				
			case Input.KEY_Z:
				map.setNbCases(map.getNbCases().x, map.getNbCases().y - 1);
				break;
				
			case Input.KEY_S:
				map.setNbCases(map.getNbCases().x, map.getNbCases().y + 1);
				break;
				
			case Input.KEY_Q:
				map.setNbCases(map.getNbCases().x - 1, map.getNbCases().y);
				break;
				
			case Input.KEY_D:
				map.setNbCases(map.getNbCases().x + 1, map.getNbCases().y);
				break;
				
			case Input.KEY_1:
				idCour = 0;
				break;
				
			case Input.KEY_2:
				idCour = 1;
				break;
				
			case Input.KEY_3:
				idCour = 2;
				break;
				
			case Input.KEY_TAB:
				map.changerAgencementMaillage();
				break;
				
			case Input.KEY_SPACE:
				map.mettreCase(idCour, coordTab);
				break;
			
			case Input.KEY_RETURN:
				map.save("test.txt");
				System.out.println("Map enregistr� dans test.txt !!");
				game.enterState(Game.ID);
				break;
    	}
    	map.selectionnerCase(idCour, coordTab);
    }

	@Override
	public int getID() {
		return ID;
	}
}
