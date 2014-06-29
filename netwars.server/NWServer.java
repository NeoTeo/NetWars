import java.awt.*  ;
import java.io.*  ;
import java.net.*  ;
import java.util.*  ;

/*
*
* The NWServer class handles all distribution of status packets from client to clients.
* It sends the received datagram packets to all subscribed clients except for the sender itself.
* Id numbers are generated and distributed by the NWServer.
*/

public class NWServer extends Thread{

    /**	
     * Constructs a new NWServer. If no arguments are given then a dialog is put up to ask 	
     * for the input and output ports.	
     * @param argv the command line arguments.	
     */	
    public NWServer(String[] argv){
        Frame f = new Frame("Welcome to Sirius Cybernetics Corporation.")  ;
        if(argv.length == 2){
            inport = Integer.parseInt(argv[0])  ;
            outport = Integer.parseInt(argv[1])  ;
            runner()  ;
        } else 
            new NWSDialog(f,true,"Port Setup",this)  ;
    }

    /**	
     * Prints a packet to the standard output.	
     * @param pkt the packet to print.	
     */	
    public void printPacket(DatagramPacket pkt){
        byte[] buffer = pkt.getData()  ;
        System.out.println("Server received :")  ;
        System.out.println("packet type : "+buffer[0])  ;
        System.out.println("id no : "+((int)(buffer[8] <<8) | (buffer[9] & 0xff)))  ;
        System.out.println("x pos : "+(((buffer[4] << 24) & 0xff000000) | ((buffer[3] << 16) & 0xff0000) | ((buffer[2] << 8) &0xff00) | (buffer[1] & 0xff)))  ;
        System.out.println("y pos : "+(((buffer[8] << 24) & 0xff000000) | ((buffer[7] << 16) & 0xff0000) | ((buffer[6] << 8) & 0xff00) | (buffer[5] & 0xff)))  ;
        System.out.println("x speed : "+((int)(buffer[12] <<8) | (buffer[11] & 0xff)))  ;
        System.out.println("y speed : "+((int)(buffer[14] <<8) | (buffer[13] & 0xff)))  ;
        System.out.println("type : "+buffer[15])  ;
        System.out.println("heading : "+buffer[16])  ;
        System.out.println("energy : "+buffer[17])  ;
        System.out.println("-----------------------------------------------------------")  ;
    }	

    /**	
     * Initializes the sockets with the inport and outport numbers and starts the server.	
     */	

    public void runner(){
        try{
            insocket = new DatagramSocket(inport)  ;
            outsocket = new DatagramSocket(outport)  ;
        }catch(Exception e){
            System.out.println("NWServer error : "+e)  ;
        }			
        start()  ;
    }		

    /**	
     * Sets the input and output ports of the NWServer.	
     * @param dat the data object containing the in and outports (from the dialog).	
     */		
    public void setData(NWSNetData dat){
        inport = Integer.parseInt(dat.inport)  ;
        outport = Integer.parseInt(dat.outport)  ;
        if(inport + outport < 1) 
            System.exit(0)  ;
    }			

    /**	
     * Returns a newly generated id number if the client is unknown or if the client is known	
     * returns its previous id number. A client is known for the duration of the server uptime.	
     * @param length the size of the id packet.	
     * @param addr the IP number of the receiving client.	
     * @param port the input port number of the receiving client.	
     * @return a datagrampacket of type id with the id number in the 9th and 10th byte.	
     */		
    public DatagramPacket makeIDPacket(int length,InetAddress addr,int port){
        System.out.println("Server - packet length "+length)  ;
        if(length < 7) 
            return null  ;
        byte[] iddata = new byte[length]  ;
        int idno  ;
        iddata[0] = 2  ;
        // port is added one because the given port is the receive port of the client		
        // whereas we will know the client by its send port		
        clientId tmp_id = new clientId(addr,port+1)  ;
        if((idno = idlist.indexOf(tmp_id)) == -1){
            idlist.addElement(tmp_id)  ;
            activelist.addElement(new Integer(15))  ;
            System.out.println("added "+tmp_id.ipno+" - "+tmp_id.portno+"to clients")  ;
            idno = idlist.size()  ;
        }else	
            idno++  ;
        System.out.println("idno : "+idno)  ;
        iddata[9] = (byte)(idno & 0xff)  ;
        iddata[10] = (byte)(idno >>> 8)  ;
        return new DatagramPacket(iddata,length,addr,port) ;
    }	

    /**	
     * Listens to the given input port for incoming packets. It identifies the type of packet 	
     * and acts accordingly :	
     * The first byte of a packet indicates its type.	
     * If the received packet is of type 2 the packet is a request for an id number. An id number 	
     * is generated or if the client, which is identified uniquely by the combination of its IP 	
     * and port number, is already in the list of subscribed clients it is simply re-sent its old 	
     * id number.	
     * If the packet type is 3 then the packet means the client is dead and is removed from the 	
     * active clients list (not the subscribed).	
     * If the packet type is 2 then the packet is a PDU packet and is sent to all subscribed	
     * clients.	
     */	
    public void run(){
        DatagramPacket tmp = null  ;
        DatagramPacket idpacket  ;
        // ls_pos is the position in the list where the packet was found		
        int ls_pos  ;
        System.out.println("NetWars server is running.")  ;
        for( ; ;){
            byte[] data = new byte[dat_len]  ;
            DatagramPacket pkt = new DatagramPacket(data,dat_len)  ;
            // wait until a packet is received 			
            try{
                insocket.receive(pkt)  ;
                clientId pkt_id = new clientId(pkt.getAddress(),pkt.getPort())  ;
                ls_pos = idlist.indexOf(pkt_id)  ;
                // If the packet is an id request generate an id packet and send it back.				
                if(data[0] == 2) {
                    idpacket = makeIDPacket(dat_len,pkt.getAddress(),pkt.getPort()-1)  ;
                    if(idpacket != null) {
                        outsocket.send(idpacket)  ;
                        System.out.println("Server - sent an id packet to "+idpacket.getAddress().toString())  ;
                    }else System.out.println("Server - didn't send an id packet ")  ;
                }else if((data[0] == 1) || (data[0] == 3)){
                    // if a 'I'm dead' message is sent remove client from list					
                    if((data[0] == 3) && (data[16] == 10)) {
                        System.out.println("setting client "+pkt.getAddress().toString()+"to false")  ;
                        // set the activelist element corresponding to this client to false 						
                        activelist.setElementAt(new Integer(0), idlist.indexOf(pkt_id))  ;
                        sendToAll(data,dat_len,ls_pos)  ;
                    }else{
                        activelist.setElementAt(new Integer(15),idlist.indexOf(pkt_id))  ;
                        // if its a new client add it to the list						
                        // the following if statement should never be true,						
                        // because a new client always asks for a new id first						
                        // and if it already has one it isn't equal to -1						
                        if(ls_pos == -1){
                            System.out.println("something's wrong")  ;
                        } else {
                            // otherwise send the message on to everyone
                            sendToAll(data,dat_len,ls_pos)  ;
                        }											
                    }				
                }			
            }catch(IOException e){
                System.out.println(e)  ;
            }		
        }	
    }		

    public void sendToAll(byte[] data,int dat_len,int start){
        int list_size = idlist.size()  ;
        int x = start  ;
        int ls_pos,cur_stat  ;
        clientId cur_addr  ;
        for(int i=0 ; i<list_size-1 ; i++){
            x = (x+1)%list_size  ;
            cur_stat = ((Integer)activelist.elementAt(x)).intValue()  ;
            cur_addr = (clientId)idlist.elementAt(x)  ;
            if(cur_stat > 0){
                activelist.setElementAt(new Integer(--cur_stat),x)  ;
                DatagramPacket outpk = new DatagramPacket(data,dat_len,cur_addr.ipno,					cur_addr.portno-1)  ;
                try{
                    outsocket.send(outpk)  ;
                }catch(Exception e){ }			
            } 		
        }		
    }			


    /**	
     * Starts up the server with any command line arguments available.	
     */		
    public static void main(String[] argv){
        //System.out.println("Welcome to Sirius Cybernetics Corporation.")  ;
        NWServer server = new NWServer(argv)  ;
    }		

    protected static int inport,outport  ;
    protected static DatagramSocket insocket,outsocket  ;
    protected static int dat_len = 18  ;
    protected static Vector activelist = new Vector()  ;
    protected static int G_idNo = 0  ;
    protected static Vector idlist = new Vector()  ;
    protected static boolean debug = false  ;
}
