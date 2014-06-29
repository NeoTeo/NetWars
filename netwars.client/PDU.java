import java.awt.* ;

/**
* Contains the state information of an entity. This information includes :
* x speed, y speed, x position, y position, id number, type, heading, packet type, and energy.
*/
public class PDU {

	/**
	* Constructs a PDU.
	*/
	public PDU(){
		;
	}
	
	/**
	* Constructs a PDU. 
	* @param spr a sprite from which the initialization information can be extracted.
	*/
	public PDU(Sprite spr){
		pktype = (byte)2 ;
		Point pos = spr.getPos() ;
		Point spd = spr.getSpeed() ;
		xpos = pos.x ;
		ypos = pos.y ;
		xspeed = spd.x ;
		yspeed = spd.y ;
		idno = spr.getGOId() ;
		type = spr.getType() ;
		heading = spr.getHeading() ;
		energy = (byte)spr.getEnergy() ;
	}
	
	/**
	* Constructs a PDU.
	* @param xp the x position.
	* @param yp the y position.
	* @param xsp the x speed.
	* @param ysp the y speed.
	* @param id the id number.
	* @param t the type : what the type is depends on the entity.dat file.
	* @param hd the current heading.
	* @param pt the packet type - so that PDU's can be used for message/order transmission 
	* as well the packet type decides what the information means.
	* @param e the current energy.
	*/
	public PDU(int xp,int yp,int xsp, int ysp,int id,
			int t,int hd,byte pt,byte e){		
		pktype = pt ;
		xpos =  xp ;
		ypos = yp ;
		xspeed = xsp ;
		yspeed = ysp ;
		idno = id ;
		type =  t ;
		heading = hd ;
		energy = e ;
	}
	
	public int xspeed,yspeed,xpos,ypos,idno,type,heading ;
	public byte pktype = 1 ;
	public byte energy ;
}
