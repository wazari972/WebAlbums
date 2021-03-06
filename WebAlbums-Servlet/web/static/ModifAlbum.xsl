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
	      <xsl:attribute name="src">Miniature__<xsl:value-of select="details/photoId/@id" />.png</xsl:attribute>
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
	      <xsl:attribute name="VALUE"><xsl:value-of select="date/@date" /></xsl:attribute>
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
            <xsl:if test="not(/webAlbums/loginInfo/root)">
                <br/>
                <label for="chTheme">Changer de theme:</label>
                <select name="newTheme">
                    <xsl:for-each select="/webAlbums/albums/edit/themes/theme">
                        <xsl:if test="not(@id = 1)">
                            <option>
                                <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:if test="/webAlbums/loginInfo/themeid = @id">
                                    <xsl:attribute name="selected">true</xsl:attribute>    
                                </xsl:if>
                                <xsl:value-of select="@name" /><xsl:if test="/webAlbums/loginInfo/themeid = @id">*</xsl:if>
                            </option>
                        </xsl:if>
                    </xsl:for-each>
                </select>
            </xsl:if>
	    <br/>
	    <label for="uniq">Uniquement?</label><input id="uniq" type='checkbox' name='force' value='yes' />
	    <br/>
            <label for="sure">"Oui je veux supprimer cet album" (d�finitif!)</label>
	    <input id="sure" type='text' autocomplete='off' name='suppr' size='31' maxlength='31'
                   placeholder="Oui je veux supprimer cet album"/>
	    <br/>
	    Droits de visibilit� : <xsl:apply-templates select="rights"/>
	    <br/>
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
</xsl:stylesheet>
