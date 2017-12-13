package utility;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 * Created by mdeazeve on 07/12/17.
 */
public final class Music{
    private static Sound menu;
    private static Sound game;

    public static void init() throws SlickException{
        menu = new Sound("resources/sounds/Heros.wav");
        game = new Sound("resources/sounds/Doutes.wav");
    }

    public static void playMenu(){
        stopMusic();
        menu.play();
    }

    public static void playGame(){
        stopMusic();
        game.play();
    }

    public static void stopMusic(){
        menu.stop();
        game.stop();
    }
}
