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
  <xsl:template match="tags">
    <xsl:apply-templates select="edit"/>
    <xsl:apply-templates select="display"/>
  </xsl:template>

  <xsl:template match="tags/display">
    <xsl:apply-templates select="title"/>
    <xsl:apply-templates select="photoList"/>
  </xsl:template>

  <xsl:template match="title">
    <div class="item">
      <div class="date">
	<span>*</span>
      </div>
      <div class="content">
	<h1>Affichage par tags</h1>
	<h2>
	  <a href='#'
             class="fullscreen">
	    <xsl:attribute name="title">"<xsl:for-each select="tagList/*">&#160;<xsl:value-of select="name" /></xsl:for-each>" en plein-écran</xsl:attribute>
	    <xsl:attribute name="rel">
	      Tags?<xsl:for-each select="tagList/*">&amp;tagAsked=<xsl:value-of select="@id" /></xsl:for-each>&amp;page=<xsl:value-of select="../photoList/page/@current"/>&amp;special=FULLSCREEN
	    </xsl:attribute>
	      <img src="static/images/out.png" height="30px"/>
	  </a>
	  &#160;
	  <a rel="singlepage[no]">
	    <xsl:attribute name="title">"<xsl:for-each select="tagList/*">&#160;<xsl:value-of select="name" /></xsl:for-each>" en visionneuse</xsl:attribute>
	    <xsl:attribute name="href">Tags?
<xsl:for-each select="tagList/*">
&amp;tagAsked=<xsl:value-of select="@id" />
</xsl:for-each>
&amp;page=<xsl:value-of select="../photoList/page/@current"/>
&amp;special=VISIONNEUSE</xsl:attribute>
	    <img src="static/images/slide.png" height="30px"/>
	  </a>
	  <center>
	    <xsl:apply-templates select="tagList"/>
	  </center>
	</h2>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
