package game;


import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import utility.Music;

import map.*;

public class EditeurMap extends BasicGameState {
	public static final int ID = 3;
	private GameContainer container;
	private StateBasedGame game;
	private final int EDITION = 0, PAUSE = 1, ENREGISTREMENT = 2, ENREGISTREE = 3;
	private int etat;

	private Animation background;
	private Animation backgroundCases;
	private Image parchemin;
	private MouseOverArea quitterArea;
	private Case cases[] = new Case[Map.getInstance().getTypeCasesParId().length];
	private SelecteurCase selecteurCases;
	
	private UnicodeFont font;
	private TextField champNomMap;
	private String nomMap;
	private final String ENTREZ_NOM = "Entrez le nom de \nla nouvelle map :";
	private final String NOM_EXISTANT = " Le nom entré \nest déjà pris !";
	private final String NOM_INCORRECTE = "  Le nom entré \nn'est pas valide !";
	private final String NOM_ENREGISTRE = "  Modifications \n  enregistrées !";
	private String messageEnregistrement = ENTREZ_NOM;
	private Point posMsgEnreg = new Point();
	
	private int idCour = 0;
	
	@Override
	public void init(GameContainer _container, StateBasedGame _game) throws SlickException {
		SpriteSheet spriteSheet = new SpriteSheet("resources/images/backgroundEditeurMap.png", 1080, 810);
		SpriteSheet spriteSheetBackGroundCase = new SpriteSheet("resources/images/backgroundGame.png", 1080, 810);
		this.container = _container;
		this.game = _game;
		
		font = new UnicodeFont(new java.awt.Font("DejaVu Serif", java.awt.Font.PLAIN, 20));
		font.addAsciiGlyphs();
		font.addGlyphs(400, 600);
		font.getEffects().add(new ColorEffect(java.awt.Color.black));
		font.loadGlyphs();
		
		champNomMap = new TextField(container, font, 425, 450, 225, 40);
		posMsgEnreg.x = 460;
		posMsgEnreg.y = 325;
		
		cases[0] = new Ocean(new Point(18, 100));
		cases[1] = new Terre(new Point(18, 200));
		cases[2] = new Phare(new Point(18, 300));
		selecteurCases = new SelecteurCase();
		selecteurCases.setIdCaseSelectionnee(0);
		selecteurCases.setPosition(cases[0].getPosition());
		
		background = new Animation();
		backgroundCases = new Animation();
    	background.addFrame(spriteSheet.getSprite(0, 0), 1200);
    	backgroundCases.addFrame(spriteSheetBackGroundCase.getSprite(0, 0), 1200);
		parchemin = new Image("resources/images/parchemin.png");
        Image quitter = new Image("resources/images/quitter.png");
        Image quitterHover = new Image("resources/images/quitter-hover.png");
        this.quitterArea = new MouseOverArea(container, quitter, container.getWidth()/2 - 112, container.getHeight()/2 -55, 225, 60, new ComponentListener() {
            @Override
            public void componentActivated(AbstractComponent abstractComponent) {
                Music.playMenu();
                game.enterState(MainScreen.ID);
            }
        });
        quitterArea.setMouseOverImage(quitterHover);
	}
	
	public void startEditeur(String nomMap) {
		this.nomMap = nomMap;
		Map.getInstance().startEditeurMode();
		Map.getInstance().load(nomMap);
		Map.getInstance().setNbCases(13, 11);
		Map.getInstance().centrerDansFenetre(container);
		etat = EDITION;
		Music.stopMusic();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		background.update(delta);
		backgroundCases.update(delta);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		graphics.drawAnimation(background, 0, 0);
		graphics.drawAnimation(backgroundCases, -980, 0);
		Map.getInstance().draw();
		for (Case caseCourante : cases) {
			caseCourante.drawAbsolu();
		}
		selecteurCases.drawAbsolu();

		graphics.setColor(Color.black);
		graphics.drawGradientLine(0, 0, Color.black, 0, 0, Color.black);
        
		if (nomMap == null) {
			graphics.drawString("<Nouvelle Map>", 105, 3);
		} else {
			graphics.drawString(nomMap, 105, 3);
		}

		switch(etat) {
		case EDITION:
			break;
		case PAUSE:
			parchemin.draw(container.getWidth()/2 - 180, container.getHeight()/2 - 225, 360, 450);
            quitterArea.render(container, graphics);
			break;
		case ENREGISTREMENT:
			parchemin.draw(container.getWidth()/2 - 180, container.getHeight()/2 - 225, 360, 450);
			
			graphics.setColor(Color.black);
			graphics.drawGradientLine(0, 0, Color.black, 0, 0, Color.black);
			graphics.drawString(messageEnregistrement, posMsgEnreg.x, posMsgEnreg.y);
			
			graphics.setColor(Color.white);
			graphics.drawGradientLine(0, 0, Color.white, 0, 0, Color.white);
			champNomMap.render(container, graphics);
			break;
		case ENREGISTREE:
			parchemin.draw(container.getWidth()/2 - 180, container.getHeight()/2 - 225, 360, 450);
			
			graphics.setColor(Color.black);
			graphics.drawGradientLine(0, 0, Color.black, 0, 0, Color.black);
			graphics.drawString(messageEnregistrement, posMsgEnreg.x, posMsgEnreg.y);
			break;
		default:
			break;
		}
	}
	
	@Override
    public void keyReleased(int key, char c) {
		switch (key) {
		case Input.KEY_ESCAPE:
			switch(etat) {
			case EDITION:
				etat = PAUSE;
				break;
			case PAUSE:
				etat = EDITION;
				break;
			case ENREGISTREMENT:
				etat = EDITION;
				champNomMap.setFocus(false);
				break;
			case ENREGISTREE:
				etat = EDITION;
				break;
			default:
				break;
			}
            break;

			/*case Input.KEY_Z:
				Map.getInstance().setNbCases(Map.getInstance().getNbCases().x, Map.getInstance().getNbCases().y - 1);
				break;

			case Input.KEY_S:
				Map.getInstance().setNbCases(Map.getInstance().getNbCases().x, Map.getInstance().getNbCases().y + 1);
				break;

			case Input.KEY_Q:
				Map.getInstance().setNbCases(Map.getInstance().getNbCases().x - 1, Map.getInstance().getNbCases().y);
				break;

			case Input.KEY_D:
				Map.getInstance().setNbCases(Map.getInstance().getNbCases().x + 1, Map.getInstance().getNbCases().y);
				break;*/

			case Input.KEY_TAB:
				Map.getInstance().changerAgencementMaillage();
				break;

			case Input.KEY_RETURN:
				switch(etat) {
				case EDITION:
					if (nomMap == null) {
						etat = ENREGISTREMENT;
						messageEnregistrement = ENTREZ_NOM;
						champNomMap.setFocus(true);
					} else {
						etat = ENREGISTREE;
						messageEnregistrement = NOM_ENREGISTRE;
					}
					break;
				case PAUSE:
					break;
				case ENREGISTREMENT:
					etat = ENREGISTREE;
					messageEnregistrement = NOM_ENREGISTRE;
					champNomMap.setFocus(false);
					nomMap = champNomMap.getText();
					Map.getInstance().save(nomMap);
					break;
				case ENREGISTREE:
					etat = EDITION;
					break;
				default:
					break;
				}
				break;
		}
    }
	
	@Override
	public void mousePressed(int button, int x, int y) {
        if(etat == EDITION){
            Point coordMaillage = new Point(x - (int) Map.getInstance().getPosition().getX(), y - (int) Map.getInstance().getPosition().getY());
            Point coordCaseCliquee = Map.getInstance().coordMaillageToTab(coordMaillage);

            //selection type case
            for (int i = 0; i < cases.length; i++) {
            	if (cases[i].getRect().contains(x, y)) {
            		idCour = i;
            		selecteurCases.setIdCaseSelectionnee(idCour);
            		selecteurCases.setPosition(cases[idCour].getPosition());
            		Map.getInstance().selectionnerCase(idCour);
            	}
            }
            
            //placement case dans map
            if (Map.getInstance().getRect().contains(x, y)) {
            	Map.getInstance().mettreCase(idCour, coordCaseCliquee);
            }
        } else {

        }
    }
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if(etat == EDITION) {
			if (Map.getInstance().getRect().contains(newx, newy)) {
	            Point coordMaillage = new Point(newx - (int) Map.getInstance().getPosition().getX(), newy - (int) Map.getInstance().getPosition().getY());
	            Point coordCaseCliquee = Map.getInstance().coordMaillageToTab(coordMaillage);
	            Map.getInstance().selectionnerCase(idCour, coordCaseCliquee);
			}
		} else {
			
		}
	}
	
	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		if(etat == EDITION) {
			if (Map.getInstance().getRect().contains(newx, newy)) {
				Point coordMaillage = new Point(newx - (int) Map.getInstance().getPosition().getX(), newy - (int) Map.getInstance().getPosition().getY());
	            Point coordCaseCliquee = Map.getInstance().coordMaillageToTab(coordMaillage);
	            Map.getInstance().selectionnerCase(idCour, coordCaseCliquee);
	            Map.getInstance().mettreCase(idCour, coordCaseCliquee);
			}
		} else {
			
		}
	}

	@Override
	public int getID() {
		return ID;
	}
}