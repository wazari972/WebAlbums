<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet  [
  <!ENTITY % xhtml-lat1 SYSTEM
     "http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent">
  <!ENTITY % xhtml-special SYSTEM
     "http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent">
  <!ENTITY % xhtml-symbol SYSTEM
     "http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent">
  %xhtml-lat1;
  %xhtml-special;
  %xhtml-symbol;
  ]>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="carnets">
      <xsl:apply-templates select="edit"/>
      <xsl:apply-templates select="display"/>
  </xsl:template>

  <xsl:template match="carnets/display">
    <xsl:apply-templates select="exception"/>
    <xsl:apply-templates select="message"/>
    <xsl:apply-templates select="carnet"/>

    <xsl:apply-templates select="page"/>
  </xsl:template>

  <xsl:template match="carnet">
    <div class="item">
      <div class="date">
	<A><xsl:attribute name="name"><xsl:value-of select="@id" /></xsl:attribute></A>
	<div><xsl:value-of select="date/month" /></div>
	<span><xsl:value-of select="date/day" /></span>
      </div>
      <div class="content">
	<h1>
	  <a>
            <xsl:attribute name="href">
Carnet?carnetCount=<xsl:value-of select="@count" />&amp;id=<xsl:value-of select="@id" />
             </xsl:attribute>
	    <xsl:value-of select="title" />
	  </a>
          <xsl:if test="/webAlbums/affichage/@edit">
              &#160;
              <a rel="singlepage[no]" title="Edition du carnet">
                <xsl:attribute name="href">
Carnet?action=EDIT
&amp;id=<xsl:value-of select="@id" />
&amp;count=<xsl:value-of select="@count"/>
                </xsl:attribute>
                <img src="static/images/edit.png" height="30px"/>
              </a>
            </xsl:if>
	</h1>
	<div class="body">
            <xsl:apply-templates select="message"/>
            <xsl:apply-templates select="details"/>
	</div>
      </div>
    </div>
  </xsl:template>
  
  <xsl:template match="message">
    <i><xsl:value-of select="."/></i><br/>
  </xsl:template>

</xsl:stylesheet>
