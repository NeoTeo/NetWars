import java.awt.* ;

/**
* The playership is the primary ship of which the player has control. It has special 
* drawing and moving methods to keep the playership in the centre of the screen and to 
* set the offset by which everyone else in the universe must be drawn.
*/
public class PlayerShip extends SpaceShip{
	int midx,midy ;

	/**
	* Constructs a playership. The middle of the screen is also calculated based on the
	* size of the ship and the current scaling factor.
	* @param id_no the id of the playership.
	* @param shape a polygon representing the shape of the spaceship. The
	* representation is assumed to be of the spaceship pointing upwards at 
	* 0 degrees.
	*/
	public PlayerShip(int id_no,Polygon shape){
		super(id_no,shape,true) ;		
		midx = screen_size/2-(entityRadius/scale) ;
		midy = screen_size/2-(entityRadius/scale) ;
	}

	/**
	* Calculates the next position of the playership.
	* The offset from the top left of the "universe" is set here.
	*/	
	public void move(){
		position.y += speed.y  + acceleration.y ;
		position.x += speed.x  + acceleration.x ;
		
		yoff += -speed.y ;
		xoff += -speed.x ;
		
		old_heading = heading ;
		oldEnergy = energy ;
	}
	
	/**
	* places the playership in the world and calculates the offset.
	*/	
	public void setPos(Point p){
		// calculate the current offset : given position - half a screen
		xoff = midx*factor - p.x ;
		yoff = midy*factor - p.y ;
		super.setPos(p) ;
	}

	/**
	* paints the spaceship in the middle of the screen.
	* @param g the graphics to draw the playership on.
	*/	
	public void paint(Graphics g){ 
		// convert the screen coords to game coords. This is a bit silly
		// because they are converted right back to screen coords in display. 
		// Since all other entities pass their pos to display in screen coords
		// screen will need to convert those and is thus more general.
		display(g,heading,midx*factor,midy*factor,Color.yellow) ;
	}

	/**
	* erases the spaceship from its previous position in the middle of the screen.
	* @param g the graphics to erase the playership from.
	*/	
	public void clear(Graphics g){
		if (old_pos != null)
			display(g,old_heading,midx*factor,midy*factor,Color.black) ;
	}

}
