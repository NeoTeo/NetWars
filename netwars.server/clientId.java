import java.net.* ;

/**
* Holds the clients IP and port numbers as the id and offers a method for comparing two ClientId
* objects.
*/
public class clientId{
	
	/**
	* Constructs a new ClientId.
	* @param addr the clients IP number.
	* @param port the clients port number.
	*/
	public clientId(InetAddress addr,int port){
		ipno = addr ;
		portno = port ;
	}

	/**
	* Compares two ClientId objects.
	* @return true if the two object have the same IP and the same port number. False otherwise.
	*/
	public boolean equals(Object obj){
		clientId id = (clientId)obj ;
		return ((ipno.equals(id.ipno)) && (portno == id.portno)) ;
	}

	public InetAddress ipno ;
	public int portno ;
}
