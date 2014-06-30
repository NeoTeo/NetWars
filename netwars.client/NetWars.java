/*
  An exploration of the use of "dead reckoning" for multiplayer games in the form of
  an MMO Shootem up inspired by Asteroids and Gravity Wars.
  This is the client application source.
  Teo Sartori, 1996-97
*/

import java.awt.* ;
import java.awt.image.* ;
import java.util.* ;
import java.io.* ;


/**
* The 'wrapper' class which starts up the game, puts up the start dialog,
* and reads the user input from the keyboard.
*/
public class NetWars extends Frame {

	/**
	* Constructs a netwars object. Also puts up a startup dialog.
	*/
	public NetWars(){
		SplashScreen splash = new SplashScreen() ;
		splash.display() ;
		new NWDialog(true,"Game Options",this) ;
	}

	/**
	* Called by the startup dialog to copy the dialog data into the
	* local data object.
	* @param in the data object from the startup dialog.
	*/
	public void setData(NetData in){
		System.out.println("setData.") ;
		initData = in ;
	}

	/**
	* Sets up a game window and starts the game proper with the data from
	* the startup dialog.
	*/
	public void runner(){
		screen = new Dimension(scr,scr) ;
		iset = this.insets() ;
		System.out.println(iset) ;
		setLayout(new GridLayout(1,1));
		c = new Canvas() ;
		add(c) ;
		resize(scr+(iset.left+iset.right),scr+(iset.top+iset.bottom)) ;
		//pack() ;
		show() ;
		mygame = new Game(c,initData) ;
		mygame.start() ;
	}

	/**
	* Called when the user resizes the game window. It calculates the proper size
	* of the game window (it must be square) and resizes according to the smallest
	* of the two sides. It also sets the screen size value for gameobjects and the
	* level class.
	* @param x the width of the window.
	* @param y the height of the window.
	*/
	public void resize(int x, int y){
		int w = x-(iset.left+iset.right) ;
		int h = y-(iset.top+iset.bottom) ;
		// pick the smallest of the two
		int z = w<h ? w : h ;
		GameObject.screen_size = z ;
		Level.screen_size = z ;
		super.resize(z+(iset.left+iset.right),z+(iset.top+iset.bottom)) ;
		if (Game.offscreen != null) Game.resizeNow = true ;

	}

	/**
	* Detects when a window is resized or the window is in focus and calls resize.
	*/
 	public boolean handleEvent(Event e){
		boolean ans = super.handleEvent(e);
		if (e.id==503) resize(size());
		return ans;
	}

	/**
	* Overrides the Java resize.
	*/
	public void resize(Dimension d){
		resize(d.width, d.height);
	}


	/**
	* Creates an instance of itself.
	*/
	public static void main(String[] args){
		new NetWars() ;

	}

	/**
	* Calls the resize method.
	*/
	public void update(Graphics g){
		System.out.println("Trying to clear the screen") ;
		resize(size()) ;

	}


	/**
	* Detects when the player releases a game key and sets the appropriate
	* flag in the game.
	*/
	public boolean keyUp(Event e,int key){
		switch(key){
			case 1004  :
			case 56	:
				mygame.thrustKey = false ;
				break ;
			case 1006 :
			case 52 :
				mygame.leftKey = false ;
				break ;
			case 1007 :
			case 54 :
				mygame.rightKey = false ;
				break ;
			case fire :
				mygame.fire = false ;
				break ;
			case 90 : // shift - z
				mygame.delay-- ;
				System.out.println("Delay : "+mygame.delay) ;
				break ;
			case 88 :	// shift - x
				mygame.delay++ ;
				System.out.println("Delay : "+mygame.delay) ;
				break ;
			default :
				//System.out.println(key) ;
		}
		return true ;
	}

	/**
	* Detects when the player presses a game key and sets the appropriate
	* flag in the game.
	*/
	public boolean keyDown(Event e, int key){
		switch(key){
			case 1004  :
			case 56	:
				mygame.thrustKey = true ;
				break ;
			case 1006 :
			case 52 :
				mygame.leftKey = true ;
				break ;
			case 1007 :
			case 54 :
				mygame.rightKey = true ;
				break ;
			case fire :
				mygame.fire = true ;
				break ;
			case 1005 :	// down arrow
			case 53 :	// 5 num-key
				mygame.reload = true ;
				break ;
			default :
				;
		}
		return true ;
	}
	protected int lastKey = 0 ;
	protected Game mygame ;
	protected Dimension screen ;
	protected Insets iset ;
	protected Canvas c ;
	protected int scr = 400 ;
	protected NetData initData ;
	private final static int fire = 32 ;
}

