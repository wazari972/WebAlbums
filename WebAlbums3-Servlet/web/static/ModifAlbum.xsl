<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="albums/edit">
    <xsl:apply-templates select="album"/>
  </xsl:template>
  
  <xsl:template match="albums/edit/album">
    <div class="item">
      <div class="date">
	<span>*</span>
      </div>
      <div class="content">
	<h1>Modification d'un album</h1>
	<div class="body">
	  <center>
	    <img>
	      <xsl:attribute name="src">Miniature__<xsl:value-of select="picture/@id" />.png</xsl:attribute>
	    </img>
	  </center>
	</div>
      </div>
      <div class="content">
	<div class="body">
	  <form method="post">
	    <xsl:attribute name="action">Albums?&amp;page=<xsl:value-of select="/webAlbums/albums/return_to/page" />&amp;id=<xsl:value-of select="@id" />#<xsl:value-of select="@id" /></xsl:attribute>
	    <input type='hidden' name='action' value='SUBMIT' />
	    <label for="nom">Nom:</label>
	    <input id="nom" type='text' size='40' maxlength='60' name='nom'
                   placeholder="Titre ..." required="true">
	      <xsl:attribute name="VALUE"><xsl:value-of select="name" /></xsl:attribute>
	    </input>
	    <br/>
            <label for="date">Date:</label> 
	    <input id="date" type='date' size='10' name='date' maxlength='10' 
                   placeholder="YYYY-MM-DD" required="true">
	      <xsl:attribute name="VALUE"><xsl:value-of select="albmDate" /></xsl:attribute>
	    </input>
	    <br/>
            <label for="desc">Description:</label>
	    <textarea id="desc" name='desc' rows='5' cols='60' placeholder="Description ...">
	      <xsl:value-of select="details/description" />
	    </textarea>
	    <br/>
	    <xsl:apply-templates select="../tagList">
	      <xsl:with-param name="mode">TAG_USED</xsl:with-param>
	      <xsl:with-param name="style">multiple</xsl:with-param>
	      <xsl:with-param name="name">tags</xsl:with-param>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="../tagList">
	      <xsl:with-param name="mode">TAG_NUSED</xsl:with-param>
	      <xsl:with-param name="style">multiple</xsl:with-param>
	      <xsl:with-param name="name">tags</xsl:with-param>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="../tagList">
	      <xsl:with-param name="mode">TAG_NEVER</xsl:with-param>
	      <xsl:with-param name="style">multiple</xsl:with-param>
	      <xsl:with-param name="name">tags</xsl:with-param>
	    </xsl:apply-templates>
	    <br/>
	    <label for="uniq">Uniquement?</label><input id="uniq" type='checkbox' name='force' value='yes' />
	    <br/>
            <label for="sure">"Oui je veux supprimer cet album" (définitif!)</label>
	    <input id="sure" type='text' autocomplete='off' name='suppr' size='31' maxlength='31'
                   placeholder="Oui je veux supprimer cet album"/>
	    <br/>
	    Droits de visibilité : <xsl:apply-templates select="../rights"/>
	    <br/>
            <xsl:if test="gpx">
                <br/>
                <xsl:apply-templates select="gpx"/>
                <br/>
                <br/>
            </xsl:if>
	    <input type='submit' value='Valider'/>
	  </form>
	  <br/>
	  <br/>
          <center>
              <a>
                <xsl:attribute name="href">Albums?&amp;page=<xsl:value-of select="/webAlbums/albums/return_to/page" />&amp;id=<xsl:value-of select="@id" />#<xsl:value-of select="@id" /></xsl:attribute>
                Retour aux albums
              </a>
          </center>
	</div>
      </div>
    </div>
  </xsl:template>
  <xsl:template match="edit/album/gpx">
    <p>
        <label><xsl:attribute name="for">gpx_<xsl:value-of select="@id"/></xsl:attribute>GPX n°<xsl:value-of select="@id"/> : </label>
        <input type='text'  size='20' maxlength='31'>
            <xsl:attribute name="name">gpx_descr_<xsl:value-of select="@id"/></xsl:attribute>
        <xsl:attribute name="id">gpx_<xsl:value-of select="@id"/></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="description"/></xsl:attribute>
        </input>
        <input type='text' size='16' placeholder="supprimer ce GPX">
            <xsl:attribute name="name">gpx_suppr_<xsl:value-of select="@id"/></xsl:attribute>
        </input>
    </p>
  </xsl:template>
  
</xsl:stylesheet>
