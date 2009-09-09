package util ;

import java.util.List ;
import java.util.ArrayList ;

public class FileWrapper implements ImageUtil  {
    private List<ImageUtil> wrappers = new ArrayList<ImageUtil>(2) ;
    private ImageUtil current ;

    public void addWrapper (ImageUtil wrapper) {
	wrappers.add(wrapper) ;
    }

    public boolean support(String type) {
	for (ImageUtil util : wrappers) {
	    if (util.support(type)) {
		return true ;
	    }
	}
	return false ;
    }

    public boolean setCurrentType(String type) {
	for (ImageUtil util : wrappers) {
	    if (util.support(type)) {
		current = util ;
		return true ;
	    }
	}
	current = null ;
	return false ;
    }


    public boolean thumbnail (int height, String source, String dest) {
	if (current != null) {
	    boolean ret = current.thumbnail(height, source, dest);
	    current = null ;
	    return ret ;
	}
	return false ;
    }

    public boolean rotate (String degrees, String source, String dest) {
	if (current != null) {
	    boolean ret = current.rotate(degrees, source, dest);
	    current = null ;
	    return ret ;
	}
	return false ;
    }
}