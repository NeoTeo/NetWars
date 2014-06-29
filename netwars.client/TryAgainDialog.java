import java.awt.* ;
/**
* A dialog that asks the user whether they want to try again.
*/
public class TryAgainDialog extends Dialog {

	/**
 	* Constructs a new tryAgainDialog.
 	* @param f the parent of the dialog.
 	* @param modal if true the dialog blocks the input to other windows when shown.
 	* @param main the game is called when the data is ready.
	*/
	public TryAgainDialog(Frame f,boolean modal,Game main){
		super(f,"Oh Dear",modal) ;
		this.main = main ;
		setBackground(Color.red) ;
		resize(200,100) ;
		
		yes = new Button("Yes") ;
		no = new Button("No") ;
		
		message = new Label("You died ! Would you like to try again ?") ;
		
		add("North",message) ;
		add("East",yes) ;
		add("West",no) ;
		pack() ;
		show() ;
	}

	/**
	* called when an action occurs within a component. 
	*/
	public boolean action(Event evt,Object arg){
		if(evt.target == yes){
			main.resume() ;
			main.init() ;	
			hide() ;
			dispose () ;
		}
		if(evt.target == no){
			System.exit(0) ;
		}
		return true ;
	}

	protected Button yes, no ;
	protected Game main ;
	protected Label message ;

}