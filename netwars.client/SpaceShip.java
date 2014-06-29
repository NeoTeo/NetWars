import java.awt.* ;
/**
* SpaceShip is a GameObject with the ability to turn left and right, thrust
* forward, calculate its next position, paint and clear itself.
*/
public class SpaceShip extends GameObject{
	/**
	* Constructs a spaceship.
	* @param id_no the id number of the spaceship
	* @param shape a polygon representing the shape of the spaceship. The
	* representation is assumed to be of the spaceship pointing upwards at 
	* 0 degrees.
	* @param rotate is true if the polygon needs to have its polygon rotated
	*/
	public SpaceShip(int id_no,Polygon shape,boolean rotate){
		super(rotate) ;
		calcAppearance(shape) ;
		id = id_no ;
	}

	/**
	* Thrusts the spaceship forward by increasing the x and/or y speed.
	*/
	public void thrust(){
		speed.x += (coords[heading].x)/16 ;
		speed.y -= (coords[heading].y)/16 ;
	}

	/**
	* Turns the spaceship-counter clockwise or clockwise by changing its heading.
	* @param direction ccw if 1 and cw if anything else.
	*/
	public void turn(int direction){
		if(direction == 1)
			heading = (heading+19)%20 ;
			
		else 
			heading = (heading+1)%20 ;
	}

	/**
	* Calculates the next position of the spaceship.
	* An offset is added to a spaceships position. This offset is the distance
	* the playership.
	*/
	public void move(){
		position.y += speed.y  + acceleration.y ;
		position.x += speed.x  + acceleration.x ;
		old_pos.x = position.x +xoff;
		old_pos.y = position.y +yoff;
		old_heading = heading ;
		oldEnergy = energy ;
	}

	/**
	* Clears the spaceship from its old position.
	*/
	public void clear(Graphics g){
		if(old_pos != null) display(g,old_heading,old_pos.x,old_pos.y,Color.black) ;
	}

	/**
	* paints the spaceship at its current position.
	*/
	public void paint(Graphics g){ 
		display(g,heading,old_pos.x,old_pos.y,Color.red) ;
	}

}
