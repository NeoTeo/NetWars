import java.awt.* ;
import java.awt.image.* ;

public class SplashScreen extends Thread{
	public SplashScreen(){
		frm = new SplashFrame() ;
	}

	public void display(){
		frm.show() ;
		frm.resize(640,480) ;
		try{
			this.sleep(4000) ;
		}catch(Exception e){}
		frm.dispose() ;
		
	}
	private SplashFrame frm ;
	
}



