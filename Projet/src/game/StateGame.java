package game;


import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.state.StateBasedGame;
import utility.Music;

import map.Map;

public class StateGame extends StateBasedGame {
	public StateGame() {
		super("Sea War");
	}
	
	public static void main(String[] args) throws SlickException {
	    AppGameContainer app = new AppGameContainer(new StateGame(), 1080, 810, false);
	    Music.init();
	    app.setTargetFrameRate(60);
	    app.start();
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
		Map.initInstance();
		
		addState(new MainScreen());
	    addState(new Game());
	    addState(new EditeurMap());
	}
}
