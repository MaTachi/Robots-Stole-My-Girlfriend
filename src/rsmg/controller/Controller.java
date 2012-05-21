package rsmg.controller;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Contains more or less the whole game, and manages the different states of the
 * game.
 * @author Daniel Jonsson
 *
 */
class Controller extends StateBasedGame {

	static final int MAINMENU_STATE = 0;
	
	static final int LEVEL_STATE = 1;
	
	static final int LEVEL_SELECTION_STATE = 2;
	
	static final int OPTIONS_STATE = 3;
	
	static final int PAUSE_MENU_STATE = 4;
	
	static final int UPGRADES_STATE = 5;
	
	static final int CREDITS_STATE = 6;
	
	private static LevelState levelState = new LevelState(LEVEL_STATE);
	
	/**
	 * 
	 */
	Controller() {
		super("Robots Stole My Girlfriend");
	}

	/**
	 * Initializes all states in the game.
	 */
	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		this.addState(new MainMenuState(MAINMENU_STATE));
		this.addState(levelState);
		this.addState(new LevelSelectionState(LEVEL_SELECTION_STATE));
		this.addState(new OptionsState(OPTIONS_STATE));
		this.addState(new PauseMenuState(PAUSE_MENU_STATE));
		this.addState(new UpgradesState(UPGRADES_STATE));
		this.addState(new CreditsState(CREDITS_STATE));
	}
	

	/**
	 * Initialize level data in LevelState.
	 * @param levelNumber The level number.
	 */
	static void initLevel(int levelNumber) {
		levelState.initLevel(levelNumber);
	}
}
