import java.awt.* ;
import java.awt.image.* ;

public class SplashFrame extends Frame{
	public SplashFrame(){
		toolkt = getToolkit() ;
		Dimension screen = toolkt.getScreenSize() ;
		System.out.println(""+this.size().width) ;
		// using two magic numbers...shame on me.
		move((screen.width/2)-320,(screen.height/2)-240) ;
		SplashImage = loadImages("splashImage",1,false) ;
	}
	
	public void paint(Graphics g){
		g.drawImage(SplashImage[0],0,0,null) ;
	}

	public void update(Graphics g){
		;
	}

	public Image[] loadImages(String basename,int no_of_sprites,boolean filterOn){
		MediaTracker tracker = new MediaTracker(this) ;
		Image tempImg ;
		Image[] images ;
		images = new Image[no_of_sprites] ;
		
		ImageFilter f = new TransFilter() ;
		
		for(int i = 0;i<no_of_sprites;i++){
			tempImg = toolkt.getImage(basename+i) ;
			tracker.addImage(tempImg,i) ;
			try{
				tracker.waitForID(i) ;
			} catch(InterruptedException e){}
			if(tracker.isErrorID(i)){
				return null ;
			}
			if(filterOn){
				ImageProducer producer = new FilteredImageSource(tempImg.getSource(),f) ;
				images[i] = createImage(producer) ;
			}else images[i] = tempImg ;
		}
		return images ;
	}	
	private Image[] SplashImage ;
	private Toolkit toolkt;
}

