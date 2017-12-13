package joueur;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

//MAP WITH ANIMATION CLASS (less opti)
public class Boat {
	//private final int ANIM_FRAME_COUNT; 
	private Animation[] animations = new Animation[8];
	private float x = 300, y = 300;
	private int direction = 0;
	private boolean moving = false;
	private int speed = 5;

	public Boat() throws SlickException {
		init();
	}

	public void init() throws SlickException {
		SpriteSheet spriteSheet = new SpriteSheet("resources/sprites/boat.png", 64, 64);

		this.animations[0] = loadAnimation(spriteSheet, 0, 1, 3);
        this.animations[1] = loadAnimation(spriteSheet, 0, 1, 1);
        this.animations[2] = loadAnimation(spriteSheet, 0, 1, 0);
        this.animations[3] = loadAnimation(spriteSheet, 0, 1, 2);
        
        this.animations[4] = loadAnimation(spriteSheet, 0, 2, 3);
        this.animations[5] = loadAnimation(spriteSheet, 0, 2, 1);
        this.animations[6] = loadAnimation(spriteSheet, 0, 2, 0);
        this.animations[7] = loadAnimation(spriteSheet, 0, 2, 2);
	}
	
	private Animation loadAnimation(SpriteSheet spriteSheet, int startX, int endX, int y) {
        Animation animation = new Animation();
        for (int x = startX; x < endX; x++) {
            animation.addFrame(spriteSheet.getSprite(x, y), 100);
        }
        return animation;
    }
	
	public void update(GameContainer container, int delta) throws SlickException {
		if (this.moving) {
            switch (this.direction) {
                case 0: this.y -= .1f * delta * speed; break;
                case 1: this.x -= .1f * delta * speed; break;
                case 2: this.y += .1f * delta * speed; break;
                case 3: this.x += .1f * delta * speed; break;
            }
        }
	}
	
	public void render(GameContainer container, Graphics g) throws SlickException {
		//g.setColor(new Color(0, 0, 0, .5f));
        //g.fillOval(x - 16, y - 8, 32, 16);
        //g.drawAnimation(animations[direction + (moving ? 4 : 0)], x, y);
        animations[direction + (moving ? 4 : 0)].draw(x, y);
    }
	
	public void keyReleased(int key, char c) {
    	this.moving = false;
    }
	
	public void keyPressed(int key, char c) {
		switch (key) {
	        case Input.KEY_UP:    this.direction = 0; this.moving = true; break;
	        case Input.KEY_LEFT:  this.direction = 1; this.moving = true; break;
	        case Input.KEY_DOWN:  this.direction = 2; this.moving = true; break;
	        case Input.KEY_RIGHT: this.direction = 3; this.moving = true; break;
	    }
	}
}
