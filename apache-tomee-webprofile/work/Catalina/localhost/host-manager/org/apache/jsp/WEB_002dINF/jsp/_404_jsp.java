/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat (TomEE)/7.0.55 (1.7.1)
 * Generated at: 2014-12-11 07:26:21 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.WEB_002dINF.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.catalina.util.RequestUtil;

public final class _404_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\r\n");
      out.write("<html>\r\n");
      out.write(" <head>\r\n");
      out.write("  <title>404 Not found</title>\r\n");
      out.write("  <style type=\"text/css\">\r\n");
      out.write("    <!--\r\n");
      out.write("    BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;font-size:12px;}\r\n");
      out.write("    H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;}\r\n");
      out.write("    PRE, TT {border: 1px dotted #525D76}\r\n");
      out.write("    A {color : black;}A.name {color : black;}\r\n");
      out.write("    -->\r\n");
      out.write("  </style>\r\n");
      out.write(" </head>\r\n");
      out.write(" <body>\r\n");
      out.write("   <h1>404 Not found</h1>\r\n");
      out.write("   <p>\r\n");
      out.write("    The page you tried to access\r\n");
      out.write("    (");
      out.print(RequestUtil.filter((String) request.getAttribute(
            "javax.servlet.error.request_uri")));
      out.write(")\r\n");
      out.write("    does not exist.\r\n");
      out.write("   </p>\r\n");
      out.write("   <p>\r\n");
      out.write("    The Host Manager application has been re-structured for Tomcat 7 onwards and\r\n");
      out.write("    some URLs have changed. All URLs used to access the Manager application\r\n");
      out.write("    should now start with one of the following options:\r\n");
      out.write("   </p>\r\n");
      out.write("    <ul>\r\n");
      out.write("      <li>");
      out.print(request.getContextPath());
      out.write("/html for the HTML GUI</li>\r\n");
      out.write("      <li>");
      out.print(request.getContextPath());
      out.write("/text for the text interface</li>\r\n");
      out.write("    </ul>\r\n");
      out.write("   <p>\r\n");
      out.write("    Note that the URL for the text interface has changed from\r\n");
      out.write("    &quot;");
      out.print(request.getContextPath());
      out.write("&quot; to\r\n");
      out.write("    &quot;");
      out.print(request.getContextPath());
      out.write("/text&quot;.\r\n");
      out.write("   </p>\r\n");
      out.write("   <p>\r\n");
      out.write("    You probably need to adjust the URL you are using to access the Host Manager\r\n");
      out.write("    application. However, there is always a chance you have found a bug in the\r\n");
      out.write("    Host Manager application. If you are sure you have found a bug, and that the\r\n");
      out.write("    bug has not already been reported, please report it to the Apache Tomcat\r\n");
      out.write("    team.\r\n");
      out.write("   </p>\r\n");
      out.write(" </body>\r\n");
      out.write("</html>\r\n");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
