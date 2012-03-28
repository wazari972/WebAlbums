<?xml version="1.0" encoding="ISO-8859-1"?>
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
            <xsl:if test="not(/webAlbums/affichage/@static)">
              <a href='#' class="fullscreen">
                <xsl:attribute name="title">"<xsl:for-each select="tagList/*">&#160;<xsl:value-of select="name" /></xsl:for-each>" en plein-écran</xsl:attribute>
                <xsl:attribute name="rel">Tags?<xsl:for-each select="tagList/*">&amp;tagAsked=<xsl:value-of select="@id" /></xsl:for-each>&amp;page=<xsl:value-of select="../photoList/page/@current"/>&amp;special=FULLSCREEN</xsl:attribute>
                  <img src="static/images/out.png" height="30px"/>
              </a>
            </xsl:if>
	  <span>&#160;</span>
          <xsl:if test="not(/webAlbums/affichage/@static)">
              <a rel="singlepage[no]">
                <xsl:attribute name="title">"<xsl:for-each select="tagList/*">&#160;<xsl:value-of select="name" /></xsl:for-each>" en visionneuse</xsl:attribute>
                <xsl:attribute name="href">Tags?<xsl:for-each select="tagList/*">&amp;tagAsked=<xsl:value-of select="@id" /></xsl:for-each>&amp;page=<xsl:value-of select="../photoList/page/@current"/>&amp;special=VISIONNEUSE</xsl:attribute>
                <img src="static/images/slide.png" height="30px"/>
              </a>
          </xsl:if>
	  <center>
	    <xsl:apply-templates select="tagList">
            <xsl:with-param name="incMinor">true</xsl:with-param>    
            </xsl:apply-templates>
	  </center>
	</h2>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
