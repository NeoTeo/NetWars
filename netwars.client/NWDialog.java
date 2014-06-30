/*
  An exploration of the use of "dead reckoning" for multiplayer games in the form of
  an MMO Shootem up inspired by Asteroids and Gravity Wars.
  This is the client application source.
  Teo Sartori, 1996-97

  Update:
  Added new focus methods to get the app working again after the Java Dialog event stuff hard
  been deprecated and disabled.
  Teo 2014
*/

import java.awt.* ;
import java.awt.event.*;

/**
* The NWDialog asks for the server IP and port numbers, the input port to use, which
* level to start from, whether to fill the background and whether to use double buffering.
*/
public class NWDialog extends Dialog implements FocusListener {
    public void focusGained(FocusEvent e){}
    public void focusLost(FocusEvent e) {
      System.out.println("WTH ");
      lostFocus2(e);
    }
	/**
	* Constructs a new NWDialog.
	* @param modal if true the dialog blocks the input to other windows when shown.
	* @param text the title of the dialog.
	* @param main the parent of the dialog.
	*/
	public NWDialog(boolean modal,String text,NetWars main){

		super((Frame)main,text,modal) ;
		Dimension dlg_size,screen ;
		Toolkit tk = Toolkit.getDefaultToolkit() ;
		screen = tk.getScreenSize() ;
		this.main = main ;
		this.frm = frm ;
		setBackground(Color.lightGray) ;
		setForeground(Color.black) ;
		setLayout(new BorderLayout(15,15)) ;

		// button stuff
		b = new Button("OK") ;

		// define the fields and the labels

		serverIP = new TextField(20);
    serverIP.addFocusListener(this);

		Label serverIPLab = new Label("Server IP address : ") ;

		serverPort = new TextField(4) ;
    serverPort.addFocusListener(this);
		Label serverPortLab = new Label("Server Port number : ") ;

		inPort = new TextField(4) ;
    inPort.addFocusListener(this);
		Label inPortLab = new Label("Your input port : ") ;
		Label outPortLab = new Label("The output port is always input+1. ");

		startLevel = new TextField(3) ;
    startLevel.addFocusListener(this);
		Label startLevelLab = new Label("Start level : ") ;

		filled = new Checkbox("Filled Background.") ;
		nobuffer = new Checkbox("No Double Buffering.") ;

		b.setFont(new Font("courier",Font.BOLD,10)) ;
		b.resize(15,5) ;
		b.setBackground(Color.lightGray) ;

		contents = new Panel() ;	// will hold everything
		buttons = new Panel() ;		// will hold buttons
		fields = new Panel() ;		// will hold fields
		message = new Panel() ;		// will hold messages

		contents.setLayout(new BorderLayout(15,15)) ;
		fields.setLayout(new GridLayout(6,2)) ;
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER,15,15)) ;

		fields.add(serverIPLab) ;
		fields.add(serverIP) ;

		fields.add(serverPortLab) ;
		fields.add(serverPort) ;

		fields.add(inPortLab) ;
		fields.add(inPort) ;

		fields.add(outPortLab) ;
		fields.add(new Label("")) ;

		fields.add(startLevelLab) ;
		fields.add(startLevel) ;

		fields.add(filled) ;

		fields.add(nobuffer) ;

		message.add(new Label("Welcome to NetWars Client.",Label.CENTER)) ;

		buttons.add(b) ;
		contents.add("North",message) ;
		contents.add("Center",fields) ;
		contents.add("South",buttons) ;
		add("Center",contents) ;
		pack() ;
		dlg_size = this.size() ;
		move((screen.width/2)-(dlg_size.width/2),(screen.height/2)-(dlg_size.height/2)) ;
		show() ;
		serverIP.requestFocus() ;
	}

	/**
	* Extracts the data from the dialog and puts it into a local NetData object.
	* @return NetData the encapsulated startup data.
	*/
	public NetData getData(){
		NetData dat = new NetData() ;
		dat.serverIP = serverIP.getText() ;
		dat.serverPort = Integer.parseInt(serverPort.getText()) ;
		dat.inPort = Integer.parseInt(inPort.getText()) ;
		dat.startLevel = Integer.parseInt(startLevel.getText()) ;
		dat.filled = filled.getState() ;
		dat.nobuffer = nobuffer.getState() ;
		return dat ;
	}

	/**
	* Prints a short description whenever a textfield in the dialog is entered.
	*/
	public boolean gotFocus(Event evt, Object what){
		//if(allValid()){
		if(evt.target == serverIP) setMessage("The IP number you want to connect to.") ;
		else if(evt.target == serverPort) setMessage("The port number the server is listening to.") ;
		else if(evt.target == inPort) setMessage("The port this program will listen to.") ;
		else if(evt.target == startLevel) setMessage("1 is easy. 2 is normal. 3 is hard.") ;
		else if(evt.target == filled) setMessage("If your machine is up to it this is cool.") ;
		else if(evt.target == nobuffer) setMessage("Speeds up but flickers.") ;
		//}
		return super.gotFocus(evt,what) ;
	}

	/**
	* Checks the input for validity when a textfield is left.
	*/
	public boolean lostFocus(Event evt, Object arg){
		String tmp ;
		int val = 0;
        System.out.println("So far...");
		if(evt.target == serverIP){
			if(isIP(serverIP.getText()))
                valid[0] = true ;
			else {
				setMessage("Not a valid IP number : "+arg);
				valid[0] = false ;
			}

		}else if(evt.target == serverPort) {
			if((tmp = serverPort.getText())!= null)
				if(tmp.length() >0)
					val = Integer.parseInt(tmp) ;
				else val = 0 ;
			if((val > 0) && (val < 65535)) valid[1] = true ;
			else {
				setMessage("Not a valid port number.") ;
				valid[1] = false ;
			}
		}else if(evt.target == inPort){
			if((tmp = inPort.getText())!= null)
				if(tmp.length() >0)
					val = Integer.parseInt(tmp) ;
				else val = 0 ;
			if((val > 0) && (val < 65535)) valid[2] = true ;
			else {
				setMessage("Not a valid port number.") ;
				valid[2] = false ;
			}
		}else if(evt.target == startLevel){
			if((tmp = startLevel.getText()) != null)
				if(tmp.length() >0)
					val = Integer.parseInt(tmp) ;
				else val = 0 ;
			if((val > 1) && (val < 4)) valid[3] = true ;
			else {
				setMessage("Not a valid level.") ;
				valid[3] = false ;
			}
		}
		return super.lostFocus(evt,arg) ;
	}

	public void lostFocus2(FocusEvent evt) {
		String tmp ;
		int val = 0;
    Object source = evt.getSource();

        System.out.println("Source.."+source);
        System.out.println("serverIP.."+serverIP);

		if(source == serverIP){
			if(isIP(serverIP.getText())) {
                valid[0] = true ;
      } else {
				setMessage("Not a valid IP number : "+serverIP.getText());
				valid[0] = false ;
			}

		}else if(source == serverPort) {
			if((tmp = serverPort.getText())!= null)
				if(tmp.length() >0)
					val = Integer.parseInt(tmp) ;
				else val = 0 ;
			if((val > 0) && (val < 65535)) valid[1] = true ;
			else {
				setMessage("Not a valid port number.") ;
				valid[1] = false ;
			}
		}else if(source == inPort){
			if((tmp = inPort.getText())!= null)
				if(tmp.length() >0)
					val = Integer.parseInt(tmp) ;
				else val = 0 ;
			if((val > 0) && (val < 65535)) valid[2] = true ;
			else {
				setMessage("Not a valid port number.") ;
				valid[2] = false ;
			}
		}else if(source == startLevel){
			if((tmp = startLevel.getText()) != null)
				if(tmp.length() >0)
					val = Integer.parseInt(tmp) ;
				else val = 0 ;
			if((val > 0) && (val < 4)) valid[3] = true ;
			else {
				setMessage("Not a valid level.") ;
				valid[3] = false ;
			}
		}
	}
	/**
	* Detects when the user presses the ok button. If the input is not valid it prints a message
	* saying so and doesn't quit the dialog. If the input is valid it puts the data into
	* the parent and calls the parents runner method.
	*/
	public boolean action(Event evt,Object arg){
		if(evt.target == b){
			if(allValid()){
				main.setData(getData()) ;
				hide() ;
				dispose () ;
				main.runner() ;
			}else setMessage("One or more fields are invalid. Please fix it so we can play") ;
		}
		return super.action(evt,arg) ;
	}

	/**
	* Updates the message in the dialog.
	* @param str the new string to put in the dialog.
	*/
	public void setMessage(String str){
	//System.out.println("so far... ") ;
		Label lab = (Label)message.getComponent(0) ;
		lab.setText(str) ;
		lab.resize(lab.preferredSize()) ;
		pack() ;
		show() ;
	}

	/**
	* Checks that a given string is a valid IP number.
	* @return true if the string translates to a valid IP number, false otherwise.
	* A valid IP number is defined as four unsigned bytes each separated by a full stop.
	*/
	private boolean isIP(String IP){
		int dotpos,val ;
		try{
			for(int i=0;i<3;i++){
				dotpos = IP.indexOf(".") ;
				val = Integer.parseInt(IP.substring(0,dotpos)) ;
				if((val > 255) || (val < 0)) return false ;
				IP = IP.substring(dotpos+1) ;
			}

			val = Integer.parseInt(IP) ;
            System.out.println("the val is "+val);
			if((val > 255) || (val < 0)) return false ;
		}catch(Exception e){return false;}
		return true ;
	}

	private boolean allValid(){
		for(int i=0;i<field_no;i++){
			if(!valid[i]) {System.out.println("false at "+i) ;return false;}
		}
		return true ;
	}

	protected Button b ;
	protected Label serverIPLab,serverPortLab,startLevelLab,inportlab, outportlab ;
	protected Frame frm ;
	protected NetWars main ;
	protected TextField serverIP,serverPort,startLevel,inPort ;
	protected Checkbox filled,nobuffer ;
	protected Panel message,contents,fields,buttons ;
	private static final int field_no = 4 ;
	private boolean[] valid = new boolean[field_no] ;

}
