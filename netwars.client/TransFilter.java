import java.awt.* ;
import java.awt.image.* ;

public class TransFilter extends RGBImageFilter{
	public TransFilter(){
		canFilterIndexColorModel = true ;
	}
	public int filterRGB(int x,int y,int rgb ){
		return	((rgb & 0xffffff) != 0xffffff) ? (0xff000000 | rgb) : (rgb & 0xffffff) ;
	}
}

