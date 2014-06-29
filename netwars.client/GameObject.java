import java.awt.* ;
import java.util.* ;
import java.io.* ;

/**
* A GameObject is a generic entity with properties common to all moving  
* entities. 
*/
abstract class GameObject extends Thread implements Sprite {
	
	// Definitions of fields that are common for game objects
	protected int id = -1;
	protected int type = 0 ;
	protected int old_heading ;
	protected int heading ;
	protected int weight ;
	protected int ent_pt_no ;
	protected int energy = 20 ;
	protected int oldEnergy ;
	protected int ammo = 25 ;
	protected Point position = new Point(0,0) ;
	protected Point acceleration = new Point(0,0) ;
	protected Point speed = new Point(0,0) ;
	protected Point old_pos = new Point(0,0) ;
	protected Point[] coords ; //an array of coordinates for each heading
	protected double degrees = -(0.1 * Math.PI) ;
	protected Polygon[] appearance ;
	protected Font energyFont = new Font("helvetica",Font.PLAIN,9) ;
	protected static int xoff,yoff ; // shared variables for all entities
	protected static int factor = 16 ;
	protected static int entityRadius = 32*factor ;	
	protected static int scale = 512*factor ;
	public static int screen_size = 800 ;
	// Rotates dictates whether an entity needs to have its coordinates calculated 
	// for each heading in a full rotation.
	/**
	* Construct a gameobject.
	* @param rotate if true an array of 20 polygons will be created, each rotated
	* 18 degrees.If false no array is created.
	*/
	public GameObject(boolean rotate) {
		if(rotate) headingToCoords() ;
		id = 0 ;
	}
	
	// Definitions of methods that are common for game objects
	/**
	* Gets a protocol data unit (PDU). The PDU is created from the current
	* gameobject data.
	* @return a PDU object.
	*/
	public PDU getPDU(){
		return new PDU(position.x,position.y,speed.x,speed.y,id,
			type,heading,(byte)1,(byte)energy) ;
	}
		
	/**
	* Returns the current position of the gameobject.
	*/
	public Point getPos(){
		return position ;
	}
 
 	/**
	* Sets the id number of the gameobject.
	*/
 	public void setGOId(int id){
 		this.id = id ;
 	}
 	
 	/**
	* Returns the id number of the gameobject.
	*/
 	public int getGOId(){
 		return id ;
 	}
 	
 	/**
	* Returns the type of the gameobject. 
	* The type decides the polygon used to show a gameobject on the screen.
	* 0 is typically used for players and 1 and above
	* for varying types of aliens. How many types are available depends on the 
	* data file where they are stored. Types can be added and loaded simply by
	* adding them to the datafile in the proper format.
	*/
 	public int getType() {
 		return type ;
 	}
 	
 	/**
	* Sets the type of the gameobject.
	* @param type the type of the gameobject.
	*/
 	public void setType(int type){
 		this.type = type ;
 	}
 	
 	/**
	* Create twenty bullets (flak) and set them to twenty random directions.
	* This has the general effect of looking like an explosion.
	* @return a vector containing twenty bullets.
	*/
 	public Vector explode(){
 		Random rnd = new Random(System.currentTimeMillis()) ;
 		Vector flak = new Vector(20) ;
 		for(int i=0;i<20;i++){
 			int no = Math.abs(rnd.nextInt()%20) ;
 			flak.addElement(new Bullet(no,position.x,position.y,this)) ;
 		}
 		return flak ;
 	}
 
 	public void paint(Graphics g){
 		;
 	}
 
	/**
	* converts each of the twenty headings into the corresponding coordinates.
	* This is done thusly : x = radius * cosine of the angle(heading), 
	* y = radius * sine of the angle.
	*/ 
 	public void headingToCoords(){
 		coords = new Point[20] ;
 		int radius = 32*factor ;
 		double angle = 0.5 * Math.PI ;
 		
 		for(int i=0;i<20;i++){
			coords[i] = new Point((int)Math.round(radius * Math.cos(angle)),
									(int)Math.round(radius * Math.sin(angle)) );
 		angle += -0.314 ;	
 		}
 	}
 
 	/**
	* Construct a bullet with the gameobjects heading and position.
	* @return a Bullet object.
	*/
 	public Bullet fire(){
 		Bullet proj = null ;
 		if(ammo > 0){
 			proj = new Bullet(heading,position.x,position.y,this);
 			ammo-- ;	
 		}
 		return proj ;
 	}
 	
 	/**
 	* Reloads the bullets into the gameobjects weapon.
 	* @param clip the amount of bullets to reload.
 	*/
 	public void reloadBullets(int clip){
 		ammo = clip ;
 	}
 	
	/**
	* Returns the shape of the gameobject.
	*/
 	public Polygon getShape() {
 		return appearance[heading] ;
 	}
 	
 	/**
	* Sets the position of the gameobject.
	* @param the position (in game coordinates) of the gameobject.
	*/
	public void setPos(Point newPos){
		position.x = newPos.x ;
		position.y = newPos.y ;
	}

	/**
	* Returns the current energy of the gameobject.
	*/
	public int getEnergy(){
		return energy ;
	}
	
	/**
	* Sets the energy of the gameobject.
	* @param e the energy.
	*/
	public void setEnergy(int e){
		energy = e ;
	}
	
	/**
	* Adds to the current energy level.
	* @param e the increment in the energy.
	*/
	public void addEnergy(int e){
		energy += e ;
	}	

	/**
	* Returns the dimension of the gameobject.
	*/
	public Dimension getDim(){
		return new Dimension(64*factor,64*factor) ;
	}

	/**
	* Returns the current heading of the gameobject.
	*/
	public int getHeading(){
		return heading ;
	}

	/**
	* Sets the heading of the gameobject.
	* @param newHeading the heading.
	*/
	public void setHeading(int newHeading){
		heading = newHeading ;
	}

	/**
	* Returns the current speed of the gameobject.
	*/
	public Point getSpeed(){
		return speed ;
	}

	/**
	* Sets the speed of the gameobject.
	* @param newSpeed the new x and y speed.
	*/
	public void setSpeed(Point newSpeed){
		speed.x = newSpeed.x ;
		speed.y = newSpeed.y ;
	}	

	/**
	* Returns the current acceleration values of the gameobject. 
	*/
	public Point getAcceleration(){
		return acceleration ;
	}

	/**
	* Sets the acceleration values of the gameobject.
	* @param newAcc the new x and y acceleration values.
	*/
	public void setAcceleration(Point newAcc){
		acceleration.x = newAcc.x ;
		acceleration.y = newAcc.y ;
	}	

	/**
	* Returns the current weight of the gameobject.
	*/
	public int getWeight(){
		return weight ;
	}

	/**
	* Sets the weight of the gameobject.
	* @param newWeight the weight of the gameobject.
	*/
	public void setWeight(int newWeight){
		weight = newWeight ;
	}

	/**
	* Rotates the given shape through 360 degrees at 18 degree intervals. The
	* resulting 20 polygons are put into an appearance array which is available
	* throughout the class. 
	* @param shape the shape of the gameobject. 
	*/
	public void calcAppearance(Polygon shape){
		ent_pt_no = shape.npoints ;
		// the appearance array
		appearance = new Polygon[20] ;
		int newxpos,newypos ;
		double xval,yval ;
		int radius ;
		double angle = 0 ;
		Polygon realshape ;
		// define degrees in radians
		double R90 = 0.5 * Math.PI ;
		double R180 = Math.PI ;
		double R18 = 0.1 * Math.PI ;
		double R270 = 2 * R90 ;
		double R360 = 2.0 * R180 ;
		// first polygon is already there
		appearance[0] = shape ;
		// for each of the 20 different headings...
		for(int i = 1;i<20;i++){
			realshape = new Polygon() ;
			Polygon tshape = appearance[i-1] ;
			// for each point of the previous polygons calculate the next
			for(int j = 0; j < ent_pt_no;j++){
				angle = 0 ;
				xval = tshape.xpoints[j]-entityRadius ;
				yval = entityRadius-tshape.ypoints[j] ;
				if(xval < 0)
					if(yval>=0) degrees = R180 -R18; 
					else degrees = R270-R18 ;
				else if(xval >= 0)
					if(yval <= 0) degrees = R360 -R18;
					else degrees = -R18;// right half of circle
					
				angle += Math.atan(yval/xval) ; //angle (-90 to 90)CCW in radians
				angle += degrees ;
				radius = (int) Math.round(Math.sqrt(Math.pow(xval,2)+Math.pow(yval,2))) ; // returns the radius 
				newxpos = (int)Math.round(radius * Math.cos(angle)) ;
				newypos = (int)Math.round(radius * Math.sin(angle)) ;
				
				realshape.addPoint(entityRadius+newxpos,entityRadius-newypos) ; 
			}
			appearance[i] = realshape ; //needed so the next calculation can be made based
									//on this.
		}
		degrees = 0 ;
	}

	/**
	* Load entity shapes from data file. The format of the data file must be :
	* The first byte is the number of entity shapes in the file.
	* The first byte of each entity is the number of points it consists of.
	* @param filename the name of the entity.
	* @return the shapes of the gameobject at various headings.
	*/
	public static Polygon[] entityLoader(String filename){
		DataInputStream in = null ;
		int no_ents,no_pts ;
		Polygon tmp_plgn ;
		Polygon[] album = null ;
		try{
			in = new DataInputStream(new FileInputStream(filename)) ;
			// the first byte must be the number of entities
			no_ents = in.readByte() ;
			album = new Polygon[no_ents] ;
			System.out.println("read a "+no_ents) ;
			for(int i = 0;i < no_ents;i++){
				// for each entity the first byte is the no of points that 
				// make up the entity
				no_pts = in.readByte() ;
				int[][] points = new int[2][no_pts] ;
				for(int a = 0;a<2;a++)
					for(int j = 0;j<no_pts;j++){
						points[a][j] = (in.readByte() | 0)*factor ;
						System.out.println("point "+a+","+j+" is "+points[a][j]) ;	
					}
				tmp_plgn = new Polygon(points[0],points[1],no_pts) ;	
				album[i] = tmp_plgn ;
			}
				
		}catch(IOException e){System.out.println("Loader : "+e) ; }
		return album ;		
	}

	/**
	* Displays the gameobject on the screen. It is passed game coordinates
	* which are then translated to screen coordinates and drawn.
	* @params g the graphics onto which the gameobject is drawn.
	* @params heading the heading of the gameobject.
	* @params xpos the x position of the gameobject.
	* @params ypos the y position of the gameobject.
	* @params col the color to draw the gameobject in.
	*/
	public void display(Graphics g,int heading,int xpos,int ypos,Color col){
		int xoff = xpos*screen_size/scale;
		int yoff = ypos*screen_size/scale;
		int locEnergy = energy ;
		
		g.setColor(col) ;
		g.setFont(energyFont) ;
		if(col == Color.black) locEnergy = oldEnergy ;
		// draw the current energy
		g.drawString(""+locEnergy,xoff+((32*factor)/scale),yoff+((32*factor)/scale)) ;
		// draw the polygon that corresponds to the current heading.
		Polygon tmp = appearance[heading] ;
		for(int i=0;i<tmp.npoints-1;i++){
			g.drawLine(((tmp.xpoints[i]*screen_size)/scale)+xoff,
			((tmp.ypoints[i]*screen_size)/scale)+yoff,
			((tmp.xpoints[i+1]*screen_size)/scale)+xoff,
			((tmp.ypoints[i+1]*screen_size)/scale)+yoff) ;
		}
	}
}


