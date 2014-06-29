import java.awt.* ;

/**
* A dialog asking for the input and output port numbers.
*/
public class NWSDialog extends Dialog{

	/**
	* Constructs a new NWSDialog. 
	* @param frm the parent of the dialog.
	* @param modal if true the dialog blocks the input to other windows when shown.
	* @param main the main program which will read the data from the dialog.
	*/	
	public NWSDialog(Frame frm,boolean modal,String text,NWServer main){
		super(frm,"OK Dialog",modal) ;
		this.main = main ;
		this.frm = frm ;
		setBackground(Color.gray) ;
		setLayout(new BorderLayout(15,15)) ;
		resize(100,100) ;
		
		// button stuff
		b = new Button("OK") ;

		inport = new TextField(4) ;		
		Label inportlab = new Label("Input port :") ;
		
		outport = new TextField(4) ;
		Label outportlab = new Label("Output port :") ;
		
		b.setFont(new Font("courier",Font.BOLD,10)) ;
		b.resize(15,5) ;
		b.setBackground(Color.lightGray) ;
		
		Panel contents = new Panel() ;
		Panel buttons = new Panel() ;
		Panel fields = new Panel() ;
		
		contents.setLayout(new GridLayout(2,1)) ;
		fields.setLayout(new GridLayout(4,2)) ;
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER,15,15)) ;
		
		fields.add(inportlab) ;
		fields.add(inport) ;
		
		fields.add(outportlab) ;
		fields.add(outport) ;
				
		buttons.add(b) ;
		contents.add(fields) ;
		contents.add(buttons) ;
		add("South",contents) ;
		pack() ;
		show() ;
	}
	
	/**
	* Extracts the data from the textfields in the dialog and encapsulates it in a 
	* NWSNetData object.
	* @return the data from the dialog.
	*/
	public NWSNetData getData(){
		NWSNetData dat = new NWSNetData() ;
		dat.inport = inport.getText() ;
		dat.outport = outport.getText() ;
		return dat ;
	}
	
	/**
	* When the user presses the ok button the data typed into the dialog fields is
	* extracted and passed back to the main program. The main program is then started
	* from here.
	*/
	public boolean action(Event evt,Object arg){
		if(evt.target == b){
			main.setData(getData()) ;
			main.runner() ;	
			hide() ;
			dispose () ;
		}
		return true ;
	}
	
	protected Button b ;
	protected Label inportlab, outportlab ;
	protected Frame frm ;
	protected NWServer main ;
	protected TextField inport, outport ;
	
}