import java.awt.* ;
import java.io.* ;

/**
* The level is the area within which the entities move. Different level backgrounds can be 
* loaded from disk and are contained in level.dat files.A level is essentially a grid of blocks
* of different shapes. The level knows how to draw itself in relation to the player and can check
* any entity against collision with the background.
*/
public class Level {

	/**
	* Constructs a level.
	* @param gridsize the size of each block in the level.
	* @param filled if true the blocks are filled. If false they are not.
	*/
	public Level(int gridsize,boolean filled){
		level_no = 1 ;
		this.filled = filled ;
		// the magic number decides the no of blocks you can see
		clipper = 1+256/gridsize ;
		clipper = 1+256/((gridsize+1)-(64/clipper)) ;
		this.gridsize = gridsize*factor ;
		background = Color.black ;
		Color earth = new Color(104,89,62) ;
		if(filled)foreground = earth ;
		else foreground = Color.white ;
	}
	
	/**
	* Checks whether a given entity is colliding with the background.
	* @param enti the entity to check against the background.
	* @return True if a collision has occurred. False otherwise.
	*/
	public boolean checkBG(Sprite enti){
		Point tpos = enti.getPos() ;
		Polygon shape = enti.getShape() ;
		// for each of the points in the entity shape check
		// against the background.
		for(int i = 0;i<shape.npoints;i++) {
			int yoff = ((tpos.y+shape.ypoints[i])) ;
			int xoff = ((tpos.x+shape.xpoints[i])) ;
			
			int row = (yoff/gridsize) ;
			int col = (xoff/gridsize) ;
			if((row<0)||(col<0)) {
				System.out.println("problem with "+enti.getGOId()) ;
				System.out.println("xoff,yoff : "+xoff+","+yoff) ;
			}
						
			int ypos = (yoff - (row*gridsize)) ;
			int xpos = (xoff - (col*gridsize)) ;
			if(check(row,col,xpos,ypos)) return true ;
		}
		return false ;  
	}
		
	/**
	* Checks whether a coordinate position is outside the bounds of a particular block shape.
	* The block is given by the position checked and the shape is given by the corresponding 
	* block type.
	* @param row the row to check.
	* @param col the column to check.
	* @param xpos the x positon to check.
	* @param ypos the y position to check. 
	* @return true if the position is inside the bounds and false otherwise.
	*/	
	boolean check(int row,int col,int xpos,int ypos){
		switch(level[row][col]){
			case 0 :
				return false ;
			case 1 :
				return true ;
			case 2 :
				if(xpos+ypos < gridsize)return true ;
				else return false ;
			case 3 :
				if(xpos > ypos) 
					return true ;
				else return false ;
			case 4 :
				if(xpos+ypos > gridsize) return true ;
				else return false ;
			case 5 :
				if(xpos < ypos) return true ;
				else return false ;
			default :
				return false ;
		}
	}

	/**
	* Loads a level by level number.
	* @param level_no the level number.
	* @return true if the level was successfully loaded, false otherwise.
	*/
	public boolean newLevel(int level_no){
		if((level = levelLoader("level"+level_no+".dat")) == null) return false ;
		this.level_no = level_no ;
		return true ;
	}
	
	/**
	* Loads a level by filename.
	* @param filename the name of the level to load.
	* @return the level.
	*/
	public int[][] levelLoader(String filename){
		DataInputStream in = null ;
		int[][] level = null ;
		try{
			in = new DataInputStream(new FileInputStream(filename)) ;
			// the first byte must be the number of columns
			rows = in.readByte() ;
			cols = in.readByte() ;
			level = new int[rows][cols] ;
			for(int i=0;i<rows;i++)
				for(int j=0;j<cols;j++)
					level[i][j] = in.readByte() ;
				
		}catch(IOException e){System.out.println("Loader : "+e) ; }
		return level ;
	}

	/**
	* Clears the level from the screen.Done by calling lpaint with the background color.
	* @param g the graphics from which to clear.
	* @param ship the player (used to calculate the offset).
	*/	
	public void clear(Graphics g,PlayerShip ship){
		lpaint(g,oldxoff,oldyoff,ship,background) ;
	}

	/**
	* Paints the level on the screen.Done by calling lpaint with the foreground color.
	* @param g the graphics onto which the level is drawn.
	* @param ship the player (used to calculate the offset).
	*/
	public void paint(Graphics g,PlayerShip ship){		
		lpaint(g,ship.xoff,ship.yoff,ship,foreground) ;
		oldxoff = ship.xoff ;
		oldyoff = ship.yoff ;
	}
	
	/**
	* Does the drawing and clearing of the level.
	* @param g the graphics onto which the level is drawn and cleared from.
	* @param xoff the x offset of the player.
	* @param yoff the y offset of the player.
	* @param ship the player ship (to get the position from it.)
	* @param col the color of the pen.
	*/
	public void lpaint(Graphics g,int xoff,int yoff,PlayerShip ship,Color col){
		g.setColor(col) ;
		Point pos = ship.getPos() ;
		int xpos = pos.x ;
		int ypos = pos.y ;
		
		int left,right,upper,lower ;
		// convert from game coords to screen coords.
		int nxoff = (xoff*screen_size)/scale ;
		int nyoff = (yoff*screen_size)/scale ;
		int ngridsize = (gridsize*screen_size)/scale ;
		int ycol = ((ypos+(32*factor))/gridsize) ;
		int xcol = ((xpos+(32*factor))/gridsize) ;
		Polygon two,three,four,five ;	
		
		for(int i = 0;i< rows;i++){
			if((i >= ycol-clipper)&&(i <= ycol+clipper))
			for(int j = 0;j < cols;j++){
				if((j>=xcol-clipper)&&(j <= xcol+clipper)){
					left = (j*ngridsize)+nxoff ;
					right = (j*ngridsize+ngridsize)+nxoff ;
					upper = (i* ngridsize)+nyoff ;
					lower = (i * ngridsize+ngridsize)+nyoff ;
					switch(level[i][j]){
						case 0 :
							break ;
						case 1 :
							if(filled) g.fillRect(left,upper,ngridsize,ngridsize) ;
							else g.drawRect(left,upper,ngridsize,ngridsize) ;
							break ;
						case 2 :
							two = new Polygon() ;
							two.addPoint(left,upper) ;
							two.addPoint(right,upper) ;
							two.addPoint(left,lower) ;
							two.addPoint(left,upper) ;
							if(filled) g.fillPolygon(two) ;
							else g.drawPolygon(two) ;
							break ;
						case 3 :
							three = new Polygon() ;
							three.addPoint(left,upper) ;
							three.addPoint(right,upper) ;
							three.addPoint(right,lower) ;
							three.addPoint(left,upper) ;
							if(filled) g.fillPolygon(three) ;
							else g.drawPolygon(three) ;
							break ;
						case 4 :
							four = new Polygon() ;
							four.addPoint(right,upper) ;
							four.addPoint(right,lower) ;
							four.addPoint(left,lower) ;
							four.addPoint(right,upper) ;
							if(filled) g.fillPolygon(four) ;
							else g.drawPolygon(four) ;
							break ;
						case 5:
							five = new Polygon() ;
							five.addPoint(left,upper) ;
							five.addPoint(right,lower) ;
							five.addPoint(left,lower) ;
							five.addPoint(left,upper) ;
							if(filled) g.fillPolygon(five) ;
							else g.drawPolygon(five) ;
						default :
							break ;
					}
				}
			}
		}
	}


	protected int level_no ;
	protected int[][] level ;
	protected int cols,rows,gridsize ;
	protected int oldxoff = 0 ;
	protected int oldyoff = 0 ;
	protected int factor = 16 ;
	public static int screen_size = 800 ;
	protected int scale = 512*factor ;
	protected static int clipper ;
	protected static boolean filled ;	
	protected static Color background,foreground ;
}
