import java.awt.* ;
import java.util.* ;
import java.net.* ;

/**
* The Game class is the coordinating class which controls the interaction
* between the player, the non-player entities, the collision engine, 
* the network interfaces, the UI classes, and the levels. 
* The main loop, which is responsible for the game running from frame to frame,
* also resides in the Game class. 
*/
public class Game extends Thread {
	
	/**
	* Constructs a new game. This involves the instantiation, creation and 
	* initialization of screens and offscreens, various lists holding entities 
	* and information about them, the collision engine, and a level.
	* @param looper the main canvas from which the various on- and offscreens are 
	* created.
	* @param init an initialization packet used for initialization.
	*/
	public Game(Canvas looper,NetData init){
		System.out.println("so far") ;
		this.looper = looper ;
		this.screen = looper.size() ;
		this.nobuffer = init.nobuffer ;
		onscreen = looper.getGraphics() ;
		img = looper.createImage(screen.width,screen.height) ;
		offscreen = img.getGraphics() ;
		offscreen.setColor(Color.black) ;
		offscreen.fillRect(0,0,screen.width,screen.height) ;
		if(nobuffer)offscreen = onscreen ; //use if machine is slow
		entities = new EntityList(5) ;
		orders = new Vector() ;
		level = new Level(blocksize,init.filled) ; 
		coll_eng = new CollisionEngine() ;
		level_no = init.startLevel ;
		System.out.println("so good") ;		
		try{
			netin = new NetIn(this,init.inPort) ;
			netout = new NetOut(InetAddress.getByName(init.serverIP),init.inPort+1,init.serverPort) ;
		}catch(Exception e){System.out.println("Game : "+e) ;}

		String filename = "entities.dat" ;
		entity_album = GameObject.entityLoader(filename) ;
		//start up the network classes
		netin.start() ;
		netout.start() ;
		//addEntity(0,1,300*factor,300*factor,1*factor,1*factor,20) ;
		//addEntity(2,1,440*factor,500*factor,0*factor,0*factor,20) ;
		//addEntity(3,1,1500*factor,700*factor,0*factor,0*factor,20) ;
		//addEntity(4,1,1600*factor,1600*factor,-1*factor,0*factor,20) ;
		// initialize game
		init() ;
	}

	/**
	* A separate initialization method which is not only used at the beginning
	* of the game but also every time a player has died and opts to restart.
	* It resets the player to the start position, making the player invulnerable 
	* from other players for three seconds, resets the stats of the player, makes
	* sure the server is still responding and gets a new id (which will be the 
	* same as long as the server hasn't been restarted)
	*/
	public void init(){
		int loopCounter = 0 ;
		thrustKey = leftKey = rightKey = fire = false ;
		if(firstTime){
			player = new PlayerShip(-1,entity_album[0]) ;
			firstTime = false ;
		}
		// keep requesting an id from the server until I get it or the counter runs out
		while(player.getGOId() == -1){
			if(loopCounter > 16){ // we've waited four seconds
				System.out.println("Sorry but there was no answer from the server.") ;
				System.exit(0) ;
			}
			netout.send(new PDU(0,0,0,0,8,0,0,(byte)2,(byte)10)) ;
			loopCounter++ ;
			try{
				sleep(250);
			}catch(Exception e){System.out.println("Run : "+e) ;}
		}
		loopCounter = 0 ;
		// reset the various stats.
		player.setEnergy(10) ;
		player.reloadBullets(maxAmmo) ;
		player.setSpeed(new Point(0,0)) ;
		player.setHeading(0) ;
		player.setPos(new Point((2*blocksize+10)*factor,(2*blocksize+10)*factor)) ;

		if(!level.newLevel(level_no)) {
			System.out.println("There was an error loading the level") ;
			System.exit(0) ;
		} 
		level.clear(offscreen,player) ;		
		refresh() ;
		
		// give the player three seconds to appear in the game without dying
		mort = new GrimSleeper(player,coll_eng) ;
		mort.start() ;

	}
	
	/**
	* clears the screen and makes sure that the current image is correctly scaled in proportion
	* to the window size.
	*/
	public void refresh(){
		this.screen = looper.size() ;
		onscreen = looper.getGraphics() ;
		img = looper.createImage(screen.width,screen.height) ;
		if(!nobuffer)offscreen = img.getGraphics() ;
		offscreen.setColor(Color.black) ;
		offscreen.fillRect(0,0,screen.width,screen.height) ;
		resizeNow = false ;
	}

	/**
	* Updates the state of a gameobject. If the gameobject already exists in the local list
	* then its state is updated (dead reckoned) otherwise a new entity is created with the
	* given parameters.
	* @param pdu the protocol data unit containing the state information.
	*/
	public void setPDU(PDU pdu){
		Sprite it ;
		if((it = entities.findById(pdu.idno)) != null){
			if((pdu.xpos < 0)||(pdu.ypos < 0)) System.out.println("ALERT !!!");  
			it.setPos(new Point(pdu.xpos,pdu.ypos)) ;
			it.setSpeed(new Point(pdu.xspeed,pdu.yspeed)) ;
			it.setHeading(pdu.heading) ;
			it.setType(pdu.type) ;
			it.setEnergy((int)pdu.energy) ;		
		}else {
			addEntity(pdu.idno,pdu.type,pdu.xpos,pdu.ypos,pdu.xspeed,pdu.yspeed,pdu.energy) ;		

		}
	}
	
	/**
	* prints the contents of a protocol data unit. Used for debugging only.
	* @param pdu the pdu to print.
	*/
	public void printPDU(PDU pdu){
		System.out.println("packet type "+pdu.pktype) ;
		System.out.println("x position "+pdu.xpos) ;
		System.out.println("y position "+pdu.ypos) ;
		System.out.println("x speed "+pdu.xspeed) ;
		System.out.println("y speed "+pdu.yspeed) ;
		System.out.println("id number "+pdu.idno) ;
		System.out.println("type "+pdu.type) ;				
		System.out.println("heading "+pdu.heading) ;
	}
	
	/**
	* Sets the player id.
	* @param id the id number.
	*/
	public void setPlayerId(int id){
		System.out.println("setting player id : "+id) ;
		player.setGOId(id) ;
	}
	
	/**
	* Creates a new spaceship with the given properties.
	* @param id the entity id.
	* @param type the graphical repesentation of the entity. 1 = triShip, 2 = Borg, 3 = TieFighter
	* @param x x position.
	* @param y y position.
	* @param xsp the x speed.
	* @param ysp the y speed.
	* @param energy the entity energy.
	*/
	public void addEntity(int id,int type,int x,int y,int xsp,int ysp,int energy){
		SpaceShip sp = new SpaceShip(id,entity_album[type],true) ;		
		sp.setType(type) ;
		sp.setPos(new Point(x,y)) ;
		sp.setSpeed(new Point(xsp,ysp)) ;
		sp.setEnergy(energy) ;
		entities.addElement(sp) ;
		coll_eng.addSprite(sp) ;
		changed = true ;
	}
	
	
	/**
	* Checks for any keypresses from the player. If any are detected it acts on them
	* by telling the player object to turn, thrust, or fire.It also send the action to 
	* the server as an action packet.
	*/
	public void movement(){
		boolean send = false ;
		// I decided to send each action as a single command.That means that
		// the client sends a packet for every cycle as long as the button is 
		// pressed. The alternative was to only send when an action was 
		// initiated and terminated.This was not chosen because a single lost 
		// "stop turning" packet would make the game unplayable - especially 
		// considering that using UDP packets are neither guaranteed to arrive nor
		// to do so in the right order (you might receive the stop before the start!)
		if(thrustKey) {
			player.thrust() ;
			// when the pktype is 3 the last arg is a command
			netout.send(player.getPDU()) ;
			netout.send(new PDU(0,0,0,0,player.getGOId(),0,7,(byte)3,(byte)0)) ;
		}
		
		if(leftKey) {
			player.turn(1) ; 
			netout.send(player.getPDU()) ;
			netout.send(new PDU(0,0,0,0,player.getGOId(),0,5,(byte)3,(byte)0)) ;
		}
		
		if(rightKey) {
			player.turn(2) ; 
			netout.send(player.getPDU()) ;
			netout.send(new PDU(0,0,0,0,player.getGOId(),0,3,(byte)3,(byte)0)) ;
		}
		
		if(fire) {
			if(fireAndAdd(player)){
				netout.send(player.getPDU()) ;
				netout.send(new PDU(0,0,0,0,player.getGOId(),0,1,(byte)3,(byte)0)) ;
			}
		}
		
		if(reload) {
			player.reloadBullets(maxAmmo) ;
			netout.send(new PDU(0,0,0,0,player.getGOId(),0,2,(byte)3,(byte)0)) ;			
			reload = false ;
		}
	}
		
		
	/**
	* Tells the object to fire and puts the resulting bullet into the collision engine
	* and entity list. 
	* @param obj the entity that fires the bullet.
	*/	
	public boolean fireAndAdd(Sprite obj){
		Bullet bullet ;
		changed = false ;
		if((bullet = obj.fire())!= null){
			entities.addElement(bullet) ;
			coll_eng.addSprite(bullet) ;
			changed = true ;
		}
		return changed ;
	}

	/**
	* Removes an entity from the screen and replaces it with an explosion.
	* @param npc the entity to be removed.
	* @param index the index of the entity in the entitylist .
	* @param entityno the number of entities currently in the game.
	*/
	public int removeAndExplode(Sprite npc,int index,int entityno){
		npc.setSpeed(new Point(0,0)) ;
		entities.removeElementAt(index) ;
		coll_eng.removeSprite(npc)  ;
		npc.clear(offscreen) ;
		if(npc.getGOId() != -1){
			Vector bits = npc.explode() ;
			Enumeration flak = bits.elements() ;
			while(flak.hasMoreElements()){
				Sprite bit = (Sprite)flak.nextElement() ;
				entities.addElement(bit) ;
				entityno++ ;
			}
		}
		changed = true ;
		return --entityno ;
	}

	public void fade(Sprite npc){
		entities.removeElement(npc) ;
		coll_eng.removeSprite(npc) ;
		npc.clear(offscreen) ;
		changed = true ;
	}

	/**
	* goes through the order list and executes the orders on the relevant entities. 
	* Since the receipt of packets is asynchronous there may have arrived more than one 
	* order in one game cycle.This way they are buffered and for each game cycle they are 
	* executed.
	*/
	public void orderEntities(){
		SpaceShip t_shp ;
		for(int i=0;i<orders.size();i++){
			PDU tmp = (PDU)orders.elementAt(i) ;
			orders.removeElementAt(i) ;
			if((t_shp = (SpaceShip)entities.findById(tmp.idno)) != null){
				// the heading is used as the message.
				switch(tmp.heading){
					case 1 :	// fire
						fireAndAdd(t_shp) ;
						changed = true ;
						break ;
					case 2 : // reload
						t_shp.reloadBullets(maxAmmo) ;
						break ;
					case 7 :	// thrust
						t_shp.thrust() ;
						break ;
					case 5 :	// turn left
						t_shp.turn(1);
						break ;
					case 3 :	// turn right
						t_shp.turn(2) ;
						break ;
					case 10 : // die
						t_shp.setEnergy(0) ;
						break ;
					case 11 : // fade out
						fade(t_shp) ;
					default :
						;
				}
			}
		}
	}
	
	/**
	* The main loop. For each game loop : the level is cleared. The player and the entities are
	* cleared. They are all moved. They are all checked against the background and against 
	* each other. If anyone hits anything they are detracted energy points and if anyone dies 
	* they are removed and exploded (except for the player who is removed and asked whether he/she
	* wants to play again). Then everyone is redrawn, the level is redrawn and the game sleeps 
	* for the appropriate amount of time. The sleep time varies with the speed with which the 
	* rest of the game loop was executed. The faster the execution loop the more it sleeps so 
	* as to keep a constant game speed. The minimum sleep time has been set to 10 ms. and the 
	* maximum to 50 ms. With a sleep time set to 50 ms the game will run at : 
	*  1000 ms / 50ms.per cycle = 20 cycles/ sec or 20 frames per sec.
	* The last thing that is done is to draw the whole offscreen onto the onscreen (unless 
	* double-buffering is switched off, in which case everything is drawn on the screen as it 
	* happens.)
	*/
	public void run(){
		int entityno = entities.size() ;
		long oldtime,counter = 0 ;
		long res ;
		byte[][] collisions ;
		while(true){
			oldtime = System.currentTimeMillis() ;
			// everything after this line 
						
			level.clear(offscreen,player) ;
			if(resizeNow) refresh() ;
			player.clear(offscreen) ;
						
			movement() ;
			// send a pdu at least every three seconds.
			if(oldtime > counter+3000) {
				netout.send(player.getPDU()); 
				counter=oldtime;
			}
			player.move() ;
			
			player.paint(offscreen) ;
			if(level.checkBG(player)) {
				player.addEnergy(-5);
				// if we hit the bg before being added to the coll_eng we 
				// want to stop being added anyway.
				mort.stop() ;
			}
			// update entity counter only if changed
			if(changed) {
				entityno = entities.size() ;
				changed = false ;
			}
			// if the player has no more energy he is dead
			if(player.getEnergy() <1) {
				// send an "I'm dead message"
				netout.send(new PDU(0,0,0,0,player.getGOId(),0,10,(byte)3,(byte)0)) ;
				coll_eng.removeSprite(player) ;
				player.clear(onscreen) ;
				new TryAgainDialog(new Frame(),true,this) ;
				this.suspend() ;
			}
			
			// if any incoming orders for the entites are available do them
			orderEntities() ;


			// clear, move and draw each npc and check against background collision
			for(int i=0;i<entityno;i++){
				npc = (Sprite)entities.elementAt(i) ;
				npc.clear(offscreen) ;
				npc.move() ;
				npc.paint(offscreen) ;
				if(level.checkBG(npc)) npc.addEnergy(-5) ;
				if(npc.getEnergy() < 1) {
					//the npc is dead.
					entityno = removeAndExplode(npc,i,entityno) ;
				} 	
			}
			
			if((collisions = coll_eng.detect())!= null){
				int no = coll_eng.spritelist.size() ;
				for(int y=0;y<no;y++)
					for(int x=0;x<no;x++)
						if(collisions[y][x] == 1) {
							npc = (Sprite)coll_eng.spritelist.elementAt(y) ;
							npc.addEnergy(-2) ;
						}
			
			}	
			
			level.paint(offscreen,player) ; 
				
			try{
				res = delay-(System.currentTimeMillis()-oldtime) ;
				if(res < 10) sleep(10) ;
				else sleep(res) ;
			} catch (Exception e) {}
			
			if(!nobuffer)onscreen.drawImage(img,0,0,null) ;
		}
	}
	
	// global variables
	protected Canvas looper ;
	protected boolean nobuffer = false ;
	protected static Graphics onscreen,offscreen ;
	protected Image img ;
	protected int level_no ;
	protected EntityList entities ;
	protected Level level ;
	protected Polygon[] entity_album ;
	protected PlayerShip player ;
	protected Sprite npc ;
	protected boolean changed = false ;
	protected CollisionEngine coll_eng ;
	protected static Dimension screen ;
	public boolean thrustKey,leftKey,rightKey,fire,reload ;
	protected int factor = 16 ;
	protected NetIn netin ;
	protected NetOut netout ;
	public static boolean resizeNow = false ;
	protected int counter = 0 ; // to hold the delay between packets
	public PDU outpacket = null ;
	public Vector orders ;
	public static final boolean debug = false ;
	public static final int blocksize = 320 ;
	public int delay = 50 ;
	public static boolean firstTime = true ;
	protected static int maxAmmo = 25 ;
	protected GrimSleeper mort ;
}
