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
  <xsl:template match="/root/tags">
    <xsl:if test="photo/exif">
      <script type="text/javascript" src="static/scripts/wz_tooltip.js"></script>
    </xsl:if>
    <form method="post">
      <xsl:attribute name="action">
	<xsl:call-template name="get_validate_addr" />
      </xsl:attribute> 

      <xsl:apply-templates select="Exception"/>
      <xsl:apply-templates select="message"/>
      <xsl:apply-templates select="title" />
    </form>
  </xsl:template>

  <xsl:template match="title">
    <div class="item">
      <div class="date">
	<span>*</span>
      </div>
      <div class="content">
	<h1>
	  Affichage par tags
	</h1>
	<h2>
	  <a href="#"> 
	    <xsl:attribute name="title">"<xsl:for-each select="tags/*">&#160;<xsl:value-of select="." /></xsl:for-each>" en plein-écran</xsl:attribute>
	    <xsl:attribute name="onClick">
	      javacript:callURL('Tags?<xsl:for-each select="tags/*">&amp;tagAsked=<xsl:value-of select="@id" /></xsl:for-each>&amp;page=<xsl:value-of select="../page/current"/>&amp;special=FULLSCREEN') ;
	    </xsl:attribute>
	      <img src="static/images/out.png" height="25px"/>
	  </a>
	  &#160;
	  <a>
	    <xsl:attribute name="title">"<xsl:for-each select="tags/*">&#160;<xsl:value-of select="." /></xsl:for-each>" en visionneuse</xsl:attribute>
	    <xsl:attribute name="href">Tags?
<xsl:for-each select="tags/*">
&amp;tagAsked=<xsl:value-of select="@id" />
</xsl:for-each>
&amp;page=<xsl:value-of select="../page/current"/>
&amp;special=VISIONNEUSE</xsl:attribute>
	    <img src="static/images/slide.png" height="25px"/>
	  </a>
	  <center>
	    <xsl:apply-templates select="tags"/>
	  </center>
	</h2>
      </div>
      <xsl:apply-templates select="../photo" />
      <xsl:apply-templates select="../massEdit" />
      
    </div>
  </xsl:template>
  
</xsl:stylesheet>
