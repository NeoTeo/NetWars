import java.util.* ;


/** 
* EntityList is a vector of Sprites.
*/
public class EntityList extends Vector {
		/** 
		* Constructs an empty entitylist. 
		*/
	public EntityList(){
		super() ;
	}
	
	/** 
	* Constructs an empty entitylist. Its initial capacity is the specified 
	* argument size. 
	* @param initialCapacity the initial capacity of the entitylist.
	*/
	public EntityList(int initialCapacity){
		super(initialCapacity) ;
	}
	
	/** 
	* Constructs an empty entitylist with the specified capacity and the
	* specified capacity increment 
	* @param initialCapacity  the initial capacity of the entitylist.
	* @param capacityIncrement  the amount by which the capacity is increased
	* when the entitylist overflows.
	*/
	public EntityList(int initialCapacity,int capacityIncrement){
		super(initialCapacity,capacityIncrement) ;
	}
	
	/** 
	* Searches through the entitylist looking for a sprite with the same
	* id as the one given.
	* @param idno the id number to look for
	* @return a sprite that has the same id or null if nothing is found
	*/
	public Sprite findById(int idno){
		Sprite tmp ;
		for(int i=0;i<size();i++){
			tmp = (Sprite) elementAt(i) ;
			if(tmp.getGOId()==idno) return tmp ;
		}
		return null ;
	}
}
