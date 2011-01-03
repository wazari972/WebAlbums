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
  <xsl:template match="photos">
    <xsl:apply-templates select="display"/>
    <xsl:apply-templates select="edit"/>
  </xsl:template>

  <xsl:template match="photos/display">
    <xsl:apply-templates select="album"/>
    <xsl:apply-templates select="photoList"/>
  </xsl:template>


  <xsl:template match="photoList">
    <form method="post" id="massEditForm">
      <xsl:attribute name="action">
	<xsl:call-template name="get_validate_addr"/>
      </xsl:attribute>

      <xsl:apply-templates select="exception"/>
      <xsl:apply-templates select="message"/>
      <xsl:apply-templates select="photo"/>
      <xsl:apply-templates select="massEdit"/>

      <xsl:apply-templates select="page"/>
    </form>
  </xsl:template>

  <xsl:template match="photo">
    <div class="item">
      <div class="body">
	<a><xsl:attribute name="name"><xsl:value-of select="details/photoId" /></xsl:attribute></a>
	<xsl:apply-templates select="message"/>
        <xsl:value-of select="submit/message" />
        <xsl:value-of select="submit/exception" />
	<xsl:apply-templates select="details"/>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="massEdit">
      <xsl:if test="/webAlbums/affichage/@massedit">
      <script type='text/javascript' src="static/scripts/MassEdit.js" />
      <div class="item">
	<div class="body">
	  <xsl:apply-templates select="message"/>
	  <input type='hidden' name='action' value='MASSEDIT' />	  
	  <table>
	    <tr><td colspan='2'>
		<input type='button' id="selectAllBt" value="Toutes"/>
	    </td></tr>
	    <tr>
	      <td><input id="turnRight" type='radio' name='turn' value='RIGHT'/></td>
	      <td><label for="turnRight">Tourner vers la droite</label></td>
	    </tr>
	    <tr>
	      <td><input id="turnLeft" type='radio' name='turn' value='LEFT' /></td>
	      <td><label for="turnLeft">Tourner vers la gauche</label></td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td><input id="turnTag" type='radio' name='turn' value='TAG' /></td>
	      <td><label for="turnTag">Tagger avec</label></td>
	      <td rowspan='3'>
		<xsl:apply-templates select="tagList">
		  <xsl:with-param name="style">list</xsl:with-param>
		  <xsl:with-param name="name">addTag</xsl:with-param>
		  <xsl:with-param name="id">massTagList</xsl:with-param>
                  <xsl:with-param name="mode">TAG_USED</xsl:with-param>
                  <xsl:with-param name="mode2">TAG_NEVER</xsl:with-param>
		</xsl:apply-templates>
	      </td>
	    </tr>
	    <tr>
	      <td><input id="turnUntag" type='radio' name='turn' value='UNTAG' /></td>
	      <td><label for="turnUntag">Enlever le tag</label></td>
	    </tr>
	    <xsl:if test="/webAlbums/tags">
	      <tr>
		<input type="hidden" name="rmTag">
		  <xsl:attribute name="value">
		    <xsl:value-of select="/webAlbums/tags/display/title/tagList/*[1]/@id"/>
		  </xsl:attribute>
		</input>
		<td><input type='radio' name='turn' value='MVTAG' /></td>
		<td><label for="massTagList">Déplacer depuis </label><b><xsl:value-of select="/webAlbums/tags/display/title/tagList/*[1]"/></b> vers</td>
	      </tr>
	    </xsl:if>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td colspan='3'><center><input type='button' onClick='javascript:validMass()' value='Valider' /></center></td>
	    </tr>
	  </table>
	</div>
      </div>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
