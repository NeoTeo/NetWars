import java.awt.* ;
import java.util.* ;

/**
* The collision engine performs the collision detection between sprites.
*/
public class CollisionEngine {
	/**
	* Constructs a collisionengine.
	*/
	public CollisionEngine(){
		spritelist = new Vector(10) ;
	}

	/**
	* Adds a sprite to the collisionengine.
	* @param sprite the sprite to add
	*/
	public void addSprite(Sprite sprite){
		spritelist.addElement(sprite) ;
	}	
	
	/**
	* Adds several sprites to the collisionengine
	* @param sprites a vector of sprites
	*/
	public void addSprites(Vector sprites){
		Enumeration sprlst = sprites.elements() ;
		while(sprlst.hasMoreElements())
			spritelist.addElement(sprlst.nextElement()) ;
	}
	
	/**
	* Removes a sprite from the collisionengine
	* @param sprite the sprite to remove
	*/
	public void removeSprite(Sprite sprite){
		spritelist.removeElement(sprite) ;
	}
	
	
	
	/* The output of the detect method is an array of arrays of bytes. 
	* Each row in the array corresponds to a sprite and has a column for each 
	* of the other sprites(including itself).
	* If the sprite in row 1 is colliding with the sprite in col 3 then the   
	* opposite must also be true. Thus a 1 is put in row 1 col 3 _and_
	* in row 3 col 1. If no collision is detected between those two then 
	* a 2 is placed in those same slots.
	*/

	/**
	* Detects if any sprites currently in the collisionengine are colliding.
	* @return the sprites that collided.
	*/
	public byte[][] detect(){
		int entityCount = spritelist.size() ;
		boolean anyHits = false ;
		Sprite current,other ;
		// Java byte arrays _are_ inited with 0 and that means check, 1 means hit 
		// and 2 means no hit
		byte[][] hitList = new byte[entityCount][entityCount] ;
		for(int y = 0;y<entityCount;y++){
			// we can use y as an index into the spritelist
			current = (Sprite) spritelist.elementAt(y) ;
			for(int x = y+1;x<entityCount;x++){
				if(hitList[y][x] == 0){
					other = (Sprite) spritelist.elementAt(x) ;
					// if a hits b then b hits a and we set the respective slots
					if(intersect(current,other) && touch(current,other)){
						anyHits = true ;
						hitList[y][x] = 1 ;
						hitList[x][y] = 1 ;
					}else {
						hitList[y][x] = 2 ;
						hitList[x][y] = 2 ;
					}
				}				
			}
		}
		if(anyHits) return hitList ;
		else return null ;
	}
	
	/**
	* Returns true if two sprites have overlapping bounding boxes.
	* False otherwise.
	*/
	public boolean touch(Sprite one,Sprite two){
		int xdiff = two.getPos().x - one.getPos().x ;
		int ydiff = two.getPos().y - one.getPos().y ;
		Polygon a = one.getShape() ; //gets the appearance at the current heading
		Polygon b = two.getShape() ;
		int a_points = a.npoints ;
		int b_points = b.npoints ;
		
		for(int i=0;i<b_points;i++){
			if(a.inside(b.xpoints[i]+xdiff,b.ypoints[i]+ydiff)) return true ;
		}
		xdiff = one.getPos().x - two.getPos().x ;
		ydiff = one.getPos().y - two.getPos().y ;
		for(int i=0;i<a_points;i++){
			if(b.inside(a.xpoints[i]+xdiff,a.ypoints[i]+ydiff)) return true ;
		}
		return false ;
	}
	
	/**
	* Returns true if two sprites intersect each other. False otherwise.
	* @param a first sprite.
	* @param b second sprite.
	*/
	public boolean intersect(Sprite a, Sprite b){
		Rectangle atemp = new Rectangle(a.getPos(),a.getDim()) ;
		Rectangle btemp = new Rectangle(b.getPos(),a.getDim()) ;
		if(atemp.intersects(btemp)) return true ;
		else return false ;
	}

	public Vector spritelist ;
}
