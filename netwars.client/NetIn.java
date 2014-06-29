import java.net.* ;

/**
* The network class for incoming traffic. It is responsible for receiving, decoding and 
* acting upon the input.
*/
public class NetIn extends Thread {

	/**
	* Constructs a netin object.
	* @param game the main game to which it needs to communicate the incoming data.
	* @param port the port to listen to for input.
	*/
	public NetIn(Game game,int port){
		this.game = game ;
		this.debug = game.debug ;
		try{
			in = new DatagramSocket(port) ;
		}catch(Exception e){System.out.println("NetIn : "+e) ;}
	}

	/**
	* Listens for incoming messages, decodes them, and depending on the type of message,
	* sets the appropriate data in the main game. If the message type is 1 then it is a
	* dead reckoning message and setPDU is called.If the message type is 2 then it is an 
	* id message and setPlayerId is called. If the message type is 3 then it is an order
	* message and the order is added to the orderlist in the main game.
	*/
	public void run(){
		System.out.println("NetIn running...") ;
		PDU tpdu ;
		byte[] buffer = new byte[pkt_size] ;
		try{
			for(;;){
				DatagramPacket packet = new DatagramPacket(buffer,buffer.length) ;
				
				in.receive(packet) ;
				if(debug==true){
					System.out.println("NetIn - got one from : "+packet.getAddress().toString()) ;
					System.out.println("NetIn - packet size : "+packet.getLength() ) ;
				}
				buffer = packet.getData() ;				
				
				tpdu = new PDU() ;
				tpdu.pktype = buffer[0] ;
				tpdu.idno = ((buffer[10] << 8) | (buffer[9] & 0xff)) ;
				tpdu.heading = buffer[16] ;
				if(tpdu.pktype == 2) game.setPlayerId(tpdu.idno) ;
				else if(tpdu.pktype == 1){
			 		tpdu.xpos = (((buffer[4] << 24) & 0xff000000) | 
			 						((buffer[3] << 16) & 0xff0000) | 
			 						((buffer[2] << 8) &0xff00) | 
			 						(buffer[1] & 0xff)) ;
			 						
					tpdu.ypos = (((buffer[8] << 24) & 0xff000000) |
									((buffer[7] << 16) & 0xff0000)|
									((buffer[6] << 8) & 0xff00) | 
									(buffer[5] & 0xff)) ;
					
					tpdu.xspeed =  ((buffer[12] << 8) | (buffer[11] & 0xff)) ;
					tpdu.yspeed =  ((buffer[14] << 8) | (buffer[13] & 0xff)) ;
					tpdu.type = buffer[15] ;
					
					tpdu.energy = buffer[17] ;
					if(debug==true){
						System.out.println("NetIn - received : ") ;
						game.printPDU(tpdu) ;
					}
					game.setPDU(tpdu) ;	
				}else if(tpdu.pktype == 3){
					if(tpdu.heading == 11) System.out.println("received a fade package") ;
					game.orders.addElement(tpdu) ;
				}
				
			}
		}catch(Exception e){System.out.println("NetIn : "+e) ;}
	}

	protected DatagramSocket in ;
	protected final int pkt_size = 18 ;
	protected Game game ;
	protected static boolean debug =false;
}


