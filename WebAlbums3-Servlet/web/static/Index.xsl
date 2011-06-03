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
  </xsl:template>
  <xsl:template match="themes/themeList/theme">
      <form method="POST" action="Choix">
        <input type='hidden' name='themeId' value='SUBMIT'>
            <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
        </input>
    <button>
      <xsl:attribute name="id">themeId<xsl:value-of select="@id"/></xsl:attribute>
      <xsl:value-of select="name"/>
    </button>
    </form><br/><br/>
  </xsl:template>
</xsl:stylesheet>
