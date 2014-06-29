import java.awt.* ;
import java.util.Vector ;
import java.io.* ;

/** 
* The sprite interface defines the methods that a class must have to be
* a sprite. A gameobject needs to be a sprite to get added to the collision
* engine.
*/
interface Sprite {
	void paint(Graphics g) ;
	void move() ;
 	public Bullet fire() ;
	void clear(Graphics g) ;
	int getEnergy() ;
	void setEnergy(int e) ;	
	void addEnergy(int e) ;
	Point getPos() ;
	Polygon getShape() ;
	Dimension getDim() ;
	public int getType() ;
 	public void setType(int type) ;
	void setSpeed(Point newspeed);
	public Point getSpeed() ;
	public Vector explode() ;
	void setGOId(int id) ;
	int getGOId() ;
	public void setPos(Point newPos) ;
	public void setHeading(int newHeading) ;
	public int getHeading() ;
}

