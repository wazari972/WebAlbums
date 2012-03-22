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
  <xsl:template match="/webAlbums/themes">
    <div class="item">
      <div class="date">
	<span>*</span>
      </div>
      <div class="content">
	<h1>Selection du Thème</h1>
	<div class="body">
	  <center>
	  <xsl:apply-templates select="themeList/theme"/>
	  </center>
	</div>
      </div>
    </div>
    <script type="text/javascript" src="static/scripts/Index.js"></script>
  </xsl:template>
  <xsl:template match="themes/themeList/theme">
      <form class="themeForm" method="POST" action="Choix">
        <input type='hidden' name='themeId' value='SUBMIT'>
            <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
        </input>
        <img class="index_img">
            <xsl:attribute name="alt">
              <xsl:value-of select="name"/>
            </xsl:attribute>

            <xsl:attribute name="src">
              <xsl:if test="not(@picture)">
                static/images/rien.jpg
              </xsl:if>
              <xsl:if test="@picture">
                  <xsl:if test="/webAlbums/affichage/@directAccess">
                      <xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picturePath" />.png
                  </xsl:if>
                  <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                      Miniature__<xsl:value-of select="@picture" />.png
                  </xsl:if>
              </xsl:if>
            </xsl:attribute>
        </img>
        <input type="submit">
            <xsl:attribute name="value">
              <xsl:value-of select="name"/>
            </xsl:attribute>    
        </input>
    </form><br/><br/>
  </xsl:template>
</xsl:stylesheet>
