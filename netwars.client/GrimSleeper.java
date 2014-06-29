
/**
* Will wait sleepTime milliseconds before adding a given entity to a given collision engine.
* This effectively makes the entity which is added to the game invulnerable from other entities
* for a time.
*/
public class GrimSleeper extends Thread {
	/**
	* Constructs a grim sleeper.
	* @param entity the entity which becomes invulnerable for a time.
	* @param collEng the collision engine to which its addition is delayed.
	*/
	public GrimSleeper(Sprite entity,CollisionEngine collEng){
		this.entity = entity ;
		this.collEng = collEng ;
	}

	/**
	* the main loop for the grim sleeper. It waits a given amount of milliseconds and then
	* adds the entity to the collision engine thus making it vulnerable.
	*/
	public void run(){
		try{
			this.sleep(sleepTime); // sleep three seconds
		}catch(Exception e){System.out.println("The Grim Sleeper has had a little problem :"+e);}
		collEng.addSprite(entity) ;
	}
	
	private static int sleepTime = 3000 ;
	private Sprite entity ;
	private CollisionEngine collEng ; 
}