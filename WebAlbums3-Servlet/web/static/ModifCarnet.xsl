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
  <xsl:template match="carnets/edit">
    <div class="item">
      <div class="date">
	<span>*</span>
      </div>
      <div class="content">
	<h1>Modification d'un carnet</h1>
	<div class="body">
	  <center>
	    <img>
	      <xsl:attribute name="src">Images?id=<xsl:value-of select="@picture" />&amp;mode=PETIT</xsl:attribute>
	    </img>
	  </center>
	</div>
      </div>
      <div class="content">
	<div class="body">
	  <form method='post'>
	    <xsl:attribute name="action">Carnets?
&amp;count=<xsl:value-of select="@count" />
&amp;id=<xsl:value-of select="@id" />
#<xsl:value-of select="@id" />
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
	    <textarea id="desc" name='desc' rows='2' cols='65'>
	      <xsl:value-of select="description" />
	    </textarea>
	    <textarea id="carnetText" name='carnetText' rows='20' cols='65'>
	      <xsl:value-of select="text" />
	    </textarea>
	    <br/>
            <div id="textPreview">Preview available soon!</div>
            <br/>
            <label for="sure">"Oui je veux supprimer ce carnet" (définitif!)</label>
	    <input id="sure" type='text' autocomplete='off' name='suppr' size='31' maxlength='31'/>
	    <br/>
	    Droits de visibilité : <xsl:apply-templates select="rights"/>
	    <br/>
	    <input type='submit' value='Valider'/>
	  </form>
	  <br/>
	  <br/>
          <center>
              <a>
                <xsl:attribute name="HREF">
                  Carnets?count=<xsl:value-of select="@count"/>#<xsl:value-of select="@id" />
                </xsl:attribute>
                Retour aux carnets
              </a>
          </center>
	</div>
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>
