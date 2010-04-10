package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringEscapeUtils;

public class StringUtil {
  
  public static final String escapeSpaces(String s) {
    return s.replace (" ", "\\ ");
  }

  public static final String escapeSQL(String s){
    return StringEscapeUtils.escapeSql(s) ;
  }
  
  public static final String escapeHTML(String s){
    s = StringEscapeUtils.unescapeHtml(s);
    return StringEscapeUtils.escapeHtml(s) ;
  }
  
  public static final String escapeXML(String s){
    s = StringEscapeUtils.unescapeHtml(s);
    s = StringEscapeUtils.escapeHtml(s) ;
    return s ;
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
      if (c >= 'A' && c <= 'Z') {
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

  public static final String escapeSH(String s){
    if (s == null) return null ;
    StringBuffer sb = new StringBuffer(s.length());
    int n = s.length();
    for (int i = 0; i < n; i++) {
      char c = s.charAt(i);
      switch (c) {
      case '\'' : sb.append("\\'");break;
      case '\"' : sb.append("\\\""); break ;
      case ' ' : sb.append("\\ ");break;
      case '&' : sb.append("\\&"); break ;	
      case '!' : sb.append("\\!"); break ;
      default:  sb.append(c); break;
      }
    }
    return sb.toString();
  }

  public static XmlBuilder xmlDate (String strNewDate, String strOldDate) {
    SimpleDateFormat annee = new SimpleDateFormat("yyyy");
    SimpleDateFormat mois = new SimpleDateFormat("MMMM");
    SimpleDateFormat jour = new SimpleDateFormat("dd");
    XmlBuilder temps = new XmlBuilder ("date");
    try {
      Date newDate = new SimpleDateFormat("yyyy-MM-dd").parse(strNewDate) ;
      
      if (strOldDate == null) {
	temps.add("year", annee.format(newDate)) ;
	temps.add("month", mois.format(newDate)) ;
	temps.add("day", jour.format(newDate));
      } else {
	Date oldDate = new SimpleDateFormat("yyyy-MM-dd").parse(strOldDate) ;
    
	if (!annee.format(oldDate).equals(annee.format(newDate))) {
	  temps.add("year", annee.format(newDate)) ;
	  temps.add("month", mois.format(newDate)) ;
	  temps.add("day", jour.format(newDate));
	} else if (!mois.format(oldDate).equals(mois.format(newDate))) {
	  
	  temps.add("month", mois.format(newDate)) ;
	  temps.add("day", jour.format(newDate));
	
	  // 1 jour = 86 400 secondes
	} else if (!jour.format(oldDate).equals(jour.format(newDate))) {
	  temps.add("day", jour.format(newDate));
	  
	} else {
	  //nothing to display
	} 
      }
    } catch (Exception e) {
      temps.add("year", "xx");
      temps.add("month", "xx");
      temps.add("day", "xx");
    }
    
    return temps ;
  }
}
