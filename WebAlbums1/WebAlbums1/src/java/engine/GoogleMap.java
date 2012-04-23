package engine ;

import constante.Path ;

public abstract class GoogleMap {
    protected int height = 200 ;
    protected int width = 250 ;

    public abstract String getInit() ;
    public abstract boolean isEmpty() ;
    public abstract String getMapName() ;
    public String getFunctions() {return "";} ;

    public void setSize(int height, int width) {
	this.height = height ;
	this.width = width ;
    }

    public String getBody() {
	if (!Path.hasInternet()) return "" ;
	
	return "<div id='"+getMapName()+"' "+
	    "style='width: "+this.width+"px; height: "+this.height+"px'></div>" ;
  }
}