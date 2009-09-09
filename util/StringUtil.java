package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {
	
	public static final String escapeSpaces(String s) {
		
		return s.replace (" ", "\\ ");
	}
	
	// from : http://www.rgagnon.com/javadetails/java-0306.html
	public static final String escapeHTML(String s){
	
	   if (s == null) return null ;
	   StringBuffer sb = new StringBuffer(s.length());
	   int n = s.length();
	   for (int i = 0; i < n; i++) {
	      char c = s.charAt(i);
	      switch (c) {
	         case '<': sb.append("&lt;"); break;
	         case '>': sb.append("&gt;"); break;
	         case '&': sb.append("&amp;"); break;
	         case '"': sb.append("&quot;"); break;
	         case 'à': sb.append("&agrave;");break;
	         case 'À': sb.append("&Agrave;");break;
	         case 'â': sb.append("&acirc;");break;
	         case 'Â': sb.append("&Acirc;");break;
	         case 'ä': sb.append("&auml;");break;
	         case 'Ä': sb.append("&Auml;");break;
	         case 'å': sb.append("&aring;");break;
	         case 'Å': sb.append("&Aring;");break;
	         case 'æ': sb.append("&aelig;");break;
	         case 'Æ': sb.append("&AElig;");break;
	         case 'ç': sb.append("&ccedil;");break;
	         case 'Ç': sb.append("&Ccedil;");break;
	         case 'é': sb.append("&eacute;");break;
	         case 'É': sb.append("&Eacute;");break;
	         case 'è': sb.append("&egrave;");break;
	         case 'È': sb.append("&Egrave;");break;
	         case 'ê': sb.append("&ecirc;");break;
	         case 'Ê': sb.append("&Ecirc;");break;
	         case 'ë': sb.append("&euml;");break;
	         case 'Ë': sb.append("&Euml;");break;
	         case 'ï': sb.append("&iuml;");break;
	         case 'Ï': sb.append("&Iuml;");break;
	         case 'ô': sb.append("&ocirc;");break;
	         case 'Ô': sb.append("&Ocirc;");break;
	         case 'ö': sb.append("&ouml;");break;
	         case 'Ö': sb.append("&Ouml;");break;
	         case 'ø': sb.append("&oslash;");break;
	         case 'Ø': sb.append("&Oslash;");break;
	         case 'ß': sb.append("&szlig;");break;
	         case 'ù': sb.append("&ugrave;");break;
	         case 'Ù': sb.append("&Ugrave;");break;         
	         case 'û': sb.append("&ucirc;");break;         
	         case 'Û': sb.append("&Ucirc;");break;
	         case 'ü': sb.append("&uuml;");break;
	         case 'Ü': sb.append("&Uuml;");break;
	         case '®': sb.append("&reg;");break;         
	         case '©': sb.append("&copy;");break;   
	         case '€': sb.append("&euro;"); break;
	         case '\'':sb.append("&#39;"); break ;
	         // be carefull with this one (non-breaking whitee space)
	         //case ' ': sb.append("&nbsp;");break;
	         
	         default:  sb.append(c); break;
	      }
	   }
	   return sb.toString();
	}

  public static final String toAscii(String s) {
    if (s == null) return null ;
    StringBuffer sb = new StringBuffer(s.length());
    int n = s.length();
    int i = 0 ;
    while (i < n)  {
      char c = s.charAt(i);
      if (c >= 'a' && c <= 'z') {
	sb.append(c) ;
      } 
      i++;
    }
    return sb.toString();
  }
  
  public static final String putbackAmp(String s) {
    if (s == null) return null ;
    StringBuffer sb = new StringBuffer(s.length());
    int n = s.length();
    int i = 0 ;
    while (i < n)  {
      char c = s.charAt(i);
      if (c == '%') {
	i++ ;
	c = s.charAt(i);
	if (c == '2') {
	  i++ ;
	  c = s.charAt(i);
	  if (c == '6')
	    sb.append('&');
	  else
	    sb.append(c);
	} else
	  sb.append(c);
	
      } else 
	sb.append(c);
      i++ ;
    }
    return sb.toString();
  }
  public static final String escapeURL(String s){
    if (s == null) return null ;
    StringBuffer sb = new StringBuffer(s.length());
    int n = s.length();
    for (int i = 0; i < n; i++) {
      char c = s.charAt(i);
      switch (c) {
      case '\'' : sb.append("%27");break;
      case ' ' : sb.append("%20");break;
      case 'é' : sb.append("%C3%A9"); break ;
      case 'è' : sb.append("%C3%A8"); break ;
      case 'à' : sb.append("%C3%A0"); break ;
      case '&' : sb.append("%26"); break ;
      default:  sb.append(c); break;
      }
    }
      return sb.toString();
  }
  
  public static String displayDate(Date newDate, Date oldDate) {
    SimpleDateFormat annee = new SimpleDateFormat("yyyy");
    SimpleDateFormat mois = new SimpleDateFormat("MMMM");
    SimpleDateFormat jour = new SimpleDateFormat("dd");
    String temps = "";
    try {
      if (oldDate == null) {
	temps += "<b>"+annee.format(newDate)+"</b><br/>" ;
	temps += mois.format(newDate) +"<br/>" ;
	temps += jour.format(newDate) ;
      } else {
	
	if (!annee.format(oldDate).equals(annee.format(newDate))) {
	  temps += "<b>"+annee.format(newDate)+"</b><br/>" ;
	  temps += mois.format(newDate)+"<br/>" ;
	  temps += jour.format(newDate) ;
	
	} else if (!mois.format(oldDate).equals(mois.format(newDate))) {
	  temps += mois.format(newDate)+"<br/>" ; ;
	  temps += jour.format(newDate) ;
	
	  // 1 jour = 86 400 secondes
	} else if (!jour.format(oldDate).equals(jour.format(newDate))) {
	  temps += jour.format(newDate) ;
	  
	} else {
	  //nothing to display
	} 
      }
    } catch (Exception e) {
      temps = "Exception" ;
    }
    
    return temps;
  }
}
