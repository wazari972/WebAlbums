<?xml version="1.0" encoding="ISO-8859-1"?>
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
    <script type="text/javascript" src="static/scripts/Index.js"/>
  </xsl:template>
  <xsl:template match="themes/themeList/theme">
      <form class="themeForm" method="POST" action="Choix">
        <input type='hidden' name='themeId' value='SUBMIT'>
            <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
        </input>
        <a rel="singlepage[no]">
            <xsl:attribute name="href"><xsl:if test="/webAlbums/affichage/@static"><xsl:value-of select="@name"/>/</xsl:if>Choix__<xsl:value-of select="@id"/>__<xsl:value-of select="@name"/></xsl:attribute>
            <img class="index_img">
                <xsl:attribute name="alt">
                  <xsl:value-of select="@name"/>
                </xsl:attribute>
                <xsl:if test="not(picture)">
                    <xsl:attribute name="src">static/images/rien.jpg</xsl:attribute>
                </xsl:if>
                  <xsl:if test="picture">
                      <xsl:if test="/webAlbums/affichage/@directAccess">
                          <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picture/text()" />.png</xsl:attribute>
                      </xsl:if>
                      <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                          <xsl:attribute name="src">Miniature__<xsl:value-of select="picture/@id" />.png</xsl:attribute>
                      </xsl:if>
                  </xsl:if>
            </img>
            <br/>
            <xsl:value-of select="@name"/>
        </a>
    </form><br/><br/>
  </xsl:template>
</xsl:stylesheet>
