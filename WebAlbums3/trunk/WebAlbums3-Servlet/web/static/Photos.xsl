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
    <form method="post">
      <xsl:attribute name="action">
	<xsl:call-template name="get_validate_addr"/>
      </xsl:attribute> 
      <xsl:apply-templates select="suppr_msg"/>
      <xsl:apply-templates select="Exception"/>
      <xsl:apply-templates select="message"/>
      <xsl:apply-templates select="album"/>
      <xsl:apply-templates select="photo"/>
      <xsl:apply-templates select="massEdit"/>
    </form>
  </xsl:template>
  <xsl:template match="photo">
    <div class="item">
      <div class="body">
	<a><xsl:attribute name="name"><xsl:value-of select="details/photoID" /></xsl:attribute></a>
	<xsl:apply-templates select="message"/>
	<xsl:if test="/root/affichage/massedit">
	  <input>
	    <xsl:attribute name="type">checkbox</xsl:attribute>
	    <xsl:attribute name="class">massEdit</xsl:attribute>
	    <xsl:attribute name="name">chk<xsl:value-of select="details/photoID" /></xsl:attribute>
	    <xsl:attribute name="value">modif</xsl:attribute>
	  </input>
      </xsl:if> 
	<xsl:apply-templates select="details"/>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="massEdit">
    <xsl:if test="/root/affichage/massedit">
      <script type='text/javascript' src="static/scripts/MassEdit.js" />
      <div class="item">
	<div class="body">
	  <xsl:apply-templates select="message"/>
	  <input type='hidden' name='action' value='MASSEDIT' />	  
	  <table>
	    <tr><td colspan='2'>
		<input type='button' id="selectAllBt" value="Toutes"
		onClick="javacript:selectAll()"/>
	    </td></tr>
	    <tr>
	      <td><input type='radio' name='turn' value='RIGHT'/></td>
	      <td>Tourner vers la droite</td>
	    </tr>
	    <tr>
	      <td><input type='radio' name='turn' value='LEFT' /></td>
	      <td>Tourner vers la gauche</td>
	    </tr>
	    <tr><td>&#160;</td></tr>
	    <tr>
	      <td><input type='radio' name='turn' value='TAG' /></td>
	      <td><label for="massTagList">Tagger avec</label></td>
	      <td rowspan='3'>
		<xsl:apply-templates select="tags">
		  <xsl:with-param name="style">list</xsl:with-param>
		  <xsl:with-param name="name">addTag</xsl:with-param>
		  <xsl:with-param name="id">massTagList</xsl:with-param>
                  <xsl:with-param name="mode">TAG_USED</xsl:with-param>
                  <xsl:with-param name="mode2">TAG_NEVER</xsl:with-param>
		</xsl:apply-templates>
	      </td>
	    </tr>
	    <tr>
	      <td><input type='radio' name='turn' value='UNTAG' /></td>
	      <td><label for="massTagList">Enlever le tag</label></td>
	    </tr>
	    <xsl:if test="/root/tags">
	      <tr>
		<input type="hidden" name="rmTag">
		  <xsl:attribute name="value">
		    <xsl:value-of select="/root/tags/title/tags/*[1]/@id"/>
		  </xsl:attribute>
		</input>
		<td><input type='radio' name='turn' value='movtag' /></td>
		<td><label for="massTagList">Déplacer depuis </label><b><xsl:value-of select="/root/tags/title/tags/*[1]"/></b> vers</td>
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
  <xsl:template match="suppr_msg">
    <xsl:apply-templates select="message|Exception"/>
  </xsl:template>
</xsl:stylesheet>
