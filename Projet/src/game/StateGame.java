package game;


import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.state.StateBasedGame;

import utility.FileUtility;
import utility.Music;
import utility.Save;

import map.Map;

/**
* Element qui représente le jeu
*/
public class StateGame extends StateBasedGame {
	/**
    	* Créé l'instance du jeu
    	*/
	public StateGame() {
		super("Sea War");
	}
	
	/**
   	* main du jeu
     	*/
	public static void main(String[] args) throws SlickException {
	    AppGameContainer app = new AppGameContainer(new StateGame(), 1080, 810, false);
	    Music.init();
	    app.setTargetFrameRate(60);
	    app.start();
	}

	/**
   	* initialise toutes les fenêtres du jeu, la musique et la sauvegarde
	* @param container représente la fenêtre contenant les éléments du jeu
     	*/
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
		Map.initInstance();
		Save.initInstance();
		Save.getInstance().load();

		addState(new MainScreen());
	    addState(new Game());
	    addState(new EditeurMap());
	}
}
