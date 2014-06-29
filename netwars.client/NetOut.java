import java.net.* ;

/**
* The network class for outgoing traffic. It sends all given packets to the address it 
* is initialized with. 
*/
public class NetOut extends Thread {
	
	/**
	* Constructs a new netout instance.
	* @param addr the IP number to send the packets to
	* @param port the port number to send from.
	* @param rec_port the port number the receiver will listen to
	*/
	public NetOut(InetAddress addr,int port,int rec_port){
		this.addr = addr ;
		this.port = port ;
		this.rec_port = rec_port ;
		try{
			out = new DatagramSocket(port) ;
		}catch(Exception e){System.out.println("NetOut : "+e) ;}
	}

	/**
	* sends a pdu to the IP and port number defined in the constructor. It is assumed that
	* the receiver knows how to decode a pdu into its correct fields.
	* @param pdu the information to be transmitted.
	*/
	public void send(PDU pdu){
		byte[] message = new byte[pkt_size] ;

		message[0] = pdu.pktype ;
		
		// break the integers into bytes for the transfer
		message[1] = (byte)(pdu.xpos & 0xff);
		message[2] = (byte)(pdu.xpos >>> 8) ;
		message[3] = (byte)(pdu.xpos >>> 16) ;
		message[4] = (byte)(pdu.xpos >>> 24) ;
		
		message[5] = (byte)(pdu.ypos & 0xff) ;
		message[6] = (byte)(pdu.ypos >>> 8) ;
		message[7] = (byte)(pdu.ypos >>> 16) ;
		message[8] = (byte)(pdu.ypos >>> 24) ;
		
		message[9] = (byte)(pdu.idno & 0xff) ;
		message[10] = (byte)(pdu.idno >>> 8) ;
		
		message[11] = (byte)(pdu.xspeed & 0xff) ;
		message[12] = (byte)(pdu.xspeed >>> 8) ;
		
		message[13] = (byte)(pdu.yspeed & 0xff) ;
		message[14] = (byte)(pdu.yspeed >>> 8) ;		

		// no need to convert the following as they are not going to exceed 
		// the limit imposed by 8 bit - +127 to - 128
		message[15] = (byte) pdu.type ;
		message[16] = (byte) pdu.heading ;
		message[17] = (byte) pdu.energy ;

		try{
			DatagramPacket packet = new DatagramPacket(message,pkt_size,addr,rec_port) ;
			out.send(packet) ;
			sent++ ;
		}catch(Exception e){System.out.println("NetOut : "+e) ;}
		
	}

	/**
	* does nothing but print a ready message to stdout.
	*/
	public void run(){
		System.out.println("NetOut ready.") ;
	}

	protected final int pkt_size = 18 ;
	protected InetAddress addr ;
	protected int port ;
	protected int rec_port ;
	protected DatagramSocket out ;
	protected static int sent = 0 ;
	protected static boolean debug = false ;
}