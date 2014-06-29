import java.awt.* ;


/**
* A bullet is a GameObject which has no control. Once it is created it will keep going until
* it is removed (usually when it hits something). A bullet knows where to appear depending on 
* the given heading.
*/
public class Bullet extends GameObject implements Sprite {
	// a bullet is passed the coords of its owner so that the bullet can actually spout from the 
	// front of the entity
	protected GameObject go ;
	
	/**
	* Constructs a bullet.
	* @param heading the heading of the entity that creates the bullet.
	* @param x the x coordinate of an entity (normally the one that creates the bullet).
	* @param y the y coordinate of an entity (normally the one that creates the bullet).
	* @param parent the entity that creates the bullet. Distinct from the other parameters 
	* because the heading and coordinates need not necessarily match those of the parent 
	* (eg. each piece of flak has the same parent but different headings and start position). 
	*/	
	public Bullet(int heading,int x,int y,Sprite parent){
		super(false) ;
		go = (GameObject) parent ;
		id = -1 ;
		// we need the parents speed to add to the bullet speed.
		Point pspeed = go.getSpeed() ;
		coords = go.coords ;
		if(coords == null) headingToCoords() ;
		// get the x and y coordinates for this heading.
		int xco = coords[heading].x ;
		int yco = coords[heading].y ;
		energy = 2 ;
		speed.x = pspeed.x ;
		speed.y = pspeed.y ;
		// set the initial position and speed
		position.x = x + (32*factor) +xco;
		position.y = y + (32*factor) -yco ;
		speed.x += xco/2 ;
		speed.y -= yco/2 ;
	}

	/**
	* Returns the shape of the bullet.
	*/
	public Polygon getShape(){
		int[] t = {1} ;
		return new Polygon(t,t,1) ;
	}
	
	/**
	* Returns the dimension of the bullet.
	*/
	public Dimension getDim(){
		return new Dimension(1,1) ;
	}

	/**
	* Calculates the next position of the bullet.
	*/
	public void move(){
		position.x += speed.x ;
		position.y += speed.y ;
		old_pos.x = position.x + xoff ;
		old_pos.y = position.y +yoff ;
	}
	
	/**
	* Erases the bullet from its previous position.
	* @param g the Graphics to erase from.
	*/
	public void clear(Graphics g){
		if(old_pos != null) {
			g.setColor(Color.black) ;
			g.drawRect(old_pos.x*screen_size/scale,old_pos.y*screen_size/scale,1,1) ;
		}
	}

	/**
	* Paints the bullet at its current position.
	* @param g the Graphics to paint to.
	*/
	public void paint(Graphics g){
		g.setColor(Color.white) ;
		g.drawRect(old_pos.x*screen_size/scale,old_pos.y*screen_size/scale,1,1) ;
		
	}
}
