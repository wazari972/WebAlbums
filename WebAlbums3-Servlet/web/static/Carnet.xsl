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
      <xsl:if test="/webAlbums/carnets">
          <link rel="stylesheet" type="text/css" href="static/scripts/lib/pagedown/demo.css" />
          <script type="text/javascript" src="static/scripts/lib/pagedown/Markdown.Converter.js"></script>
          <script type="text/javascript" src="static/scripts/lib/pagedown/Markdown.Sanitizer.js"></script>
          <script type="text/javascript" src="static/scripts/lib/pagedown/Markdown.Editor.js"></script>
      </xsl:if>
      <xsl:apply-templates select="edit"/>
      <xsl:apply-templates select="display"/>
  </xsl:template>

  <xsl:template match="carnets/display">
    <xsl:apply-templates select="exception"/>
    <xsl:apply-templates select="message"/>
    <xsl:if test="not(/webAlbums/carnets/display/carnet/text)">
    <center><a class="edit" href="Carnets?action=EDIT" rel="singlepage[no]">Nouveau carnet</a></center>
    </xsl:if>
    <xsl:apply-templates select="carnet"/>
    <xsl:apply-templates select="page"/>
    <script type="text/javascript" src="static/scripts/Carnet.js"></script>
  </xsl:template>

  <xsl:template match="carnet">
    <div class="item">
      <div class="date">
	<a><xsl:attribute name="id">anchor_<xsl:value-of select="@id" /></xsl:attribute></a>
	<div><xsl:value-of select="date/month" /></div>
	<span><xsl:value-of select="date/day" /></span>
      </div>
      <div class="content">
	<h1>
	  <a>
            <xsl:attribute name="href">
Carnets?carnetCount=<xsl:value-of select="@count" />&amp;
<xsl:if test="not(/webAlbums/carnets/display/carnet/text)">
carnet=<xsl:value-of select="@id" />
</xsl:if>
<xsl:if test="/webAlbums/carnets/display/carnet/text">
#<xsl:value-of select="@id" />
</xsl:if>
             </xsl:attribute>
	    <xsl:value-of select="name" />
	  </a>
	</h1>
	<div class="body">
            <xsl:apply-templates select="message"/>
            <xsl:apply-templates select="details"/>
            <br/>
            <xsl:apply-templates select="text"/>
	</div>
      </div>
    </div>
  </xsl:template>
  
  <xsl:template match="message">
    <i><xsl:value-of select="."/></i><br/>
  </xsl:template>
   <xsl:template match="text">
       <br/>
       <hr/>
       <br/>
        <div id="carnet_text" class="carnet_text">
            <xsl:value-of select="."/>
        </div>
  </xsl:template>
</xsl:stylesheet>
