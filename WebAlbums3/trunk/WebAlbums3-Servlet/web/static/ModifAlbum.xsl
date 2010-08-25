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
  <xsl:template match="albm_edit">
    <div class="item">
      <div class="date">
	<span>*</span>
      </div>
      <div class="content">
	<h1>Modification d'un album</h1>
	<div class="body">
	  <center>
	    <img>
	      <xsl:attribute name="SRC">Images?id=<xsl:value-of select="picture" />&amp;mode=PETIT</xsl:attribute>
	    </img>
	  </center>
	</div>
      </div>
      <div class="content">
	<div class="body">
	  <form method='POST'>
	    <xsl:attribute name="ACTION">Albums?
&amp;count=<xsl:value-of select="count" />
&amp;id=<xsl:value-of select="id" />
#<xsl:value-of select="id" />
	    </xsl:attribute>
	    <input type='hidden' name='action' value='SUBMIT' />
	    <label for="nom">Nom:</label>
	    <input id="nom" type='text' size='40' maxlength='60' name='nom'>
	      <xsl:attribute name="VALUE"><xsl:value-of select="name" /></xsl:attribute>
	    </input>
	    <br/>
            <label for="date">Date:</label> 
	    <input id="date" type='text' size='10' name='date' maxlength='10'>
	      <xsl:attribute name="VALUE"><xsl:value-of select="date" /></xsl:attribute>
	    </input>
	    <br/>
            <label for="desc">Description:</label>
	    <textarea id="desc" name='desc' rows='5' cols='60'>
	      <xsl:value-of select="description" />
	    </textarea>
	    <br/>
	    <xsl:apply-templates select="tags">
	      <xsl:with-param name="mode">TAG_USED</xsl:with-param>
	      <xsl:with-param name="style">multiple</xsl:with-param>
	      <xsl:with-param name="name">tags</xsl:with-param>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="tags">
	      <xsl:with-param name="mode">TAG_NUSED</xsl:with-param>
	      <xsl:with-param name="style">multiple</xsl:with-param>
	      <xsl:with-param name="name">tags</xsl:with-param>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="tags">
	      <xsl:with-param name="mode">TAG_NEVER</xsl:with-param>
	      <xsl:with-param name="style">multiple</xsl:with-param>
	      <xsl:with-param name="name">tags</xsl:with-param>
	    </xsl:apply-templates>
	    <br/>
	    <label for="uniq">Uniquement?</label><input id="uniq" type='checkbox' name='force' value='yes' />
	    <br/>
            <label for="sure">"Oui je veux supprimer cet album" (définitif!)</label>
	    <input id="sure" type='text' autocomplete='off' name='suppr' size='31' maxlength='31'/>
	    <br/>
	    Droits de visibilité : <xsl:apply-templates select="userList"/>
	    <br/>
	    <input type='submit' value='Valider'/>
	  </form>
	  <br/>
	  <br/>
	  <a>
	    <xsl:attribute name="HREF">
	      Albums?count=<xsl:value-of select="count"/>#<xsl:value-of select="id" />
	    </xsl:attribute>
	    Retour aux albums
	  </a>
	</div>
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>
