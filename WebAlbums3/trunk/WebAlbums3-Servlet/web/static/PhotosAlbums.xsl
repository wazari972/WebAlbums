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
  <xsl:template match="details">
    <a>
      <xsl:if test="/webAlbums/photos or /webAlbums/tags">
	<xsl:attribute name="rel">shadowbox[page];player=img</xsl:attribute>
      </xsl:if>
      <xsl:attribute name="HREF">	
	<xsl:if test="/webAlbums/photos or /webAlbums/tags">
	  Images?id=<xsl:value-of select="photoId" />&amp;mode=GRAND
	</xsl:if> 
	<xsl:if test="/webAlbums/albums">
	  Photos?albmCount=<xsl:value-of select="../@count" />&amp;album=<xsl:value-of select="../@id" />
	</xsl:if> 
      </xsl:attribute>
      <img class="photo">
	<xsl:attribute name="alt">
	  <xsl:value-of select="title" />
	</xsl:attribute>

	<xsl:if test="not(miniWidth &gt; miniHeight)">
	  <xsl:attribute name="width">
	    <xsl:value-of select="miniWidth"/>
	  </xsl:attribute>
	  <xsl:attribute name="style">
	    padding-right:<xsl:value-of select="(250 - miniWidth) div 2"/>px;
	    padding-left:<xsl:value-of select="(250 - miniWidth) div 2"/>px
	  </xsl:attribute>
	</xsl:if>

	<xsl:if test="miniWidth &gt; miniHeight">
	  <xsl:attribute name="width">
	    <xsl:text>250</xsl:text>
	  </xsl:attribute>
	</xsl:if>

	<xsl:if test="../exif">
	  <xsl:attribute name="onmouseout">UnTip()</xsl:attribute>
	  <xsl:attribute name="onmouseover">TagToTip('tip<xsl:value-of select="photoId" />')</xsl:attribute>
	</xsl:if>
	<xsl:attribute name="src">
	  <xsl:if test="normalize-space(photoId) = ''">
	    static/images/rien.jpg
	  </xsl:if>
	  <xsl:if test="normalize-space(photoId) != ''">
	    Images?id=<xsl:value-of select="photoId" />&amp;mode=PETIT
	  </xsl:if>
	</xsl:attribute>
      </img>
    </a>
    <xsl:if test="../exif">
      <span style="display: none;">
	<xsl:attribute name="id">tip<xsl:value-of select="photoId" /></xsl:attribute>
	<xsl:apply-templates select="../exif" />
      </span>
    </xsl:if>
    <div class="details">
      <xsl:apply-templates select="tagList">
	<xsl:with-param name="style">none</xsl:with-param>
	<xsl:with-param name="mode">TAG_USED</xsl:with-param>
	<xsl:with-param name="box">NONE</xsl:with-param>
      </xsl:apply-templates>
      <div class="description"><xsl:value-of select="description" /></div>
      <xsl:apply-templates select="user" />
      <xsl:if test="not(/webAlbums/albums)">
	<a title="Photo réduite">
	  <xsl:attribute name="href">Images?id=<xsl:value-of select="photoId" />&amp;mode=SHRINK&amp;width=800&amp;borderWidth=10&amp;borderColor=white</xsl:attribute>
	  <img src="static/images/reduire.gif" width="30px"/>
	</a>
      </xsl:if>
      <xsl:if test="not(/webAlbums/albums)">
        <xsl:if test="/webAlbums/affichage/remote">
            <a href='#'
               title="Photo en plein-ecran"
               class="fullscreen">
                <xsl:attribute name="rel">
                    Images?id=<xsl:value-of select="photoId" />&amp;mode=FULLSCREEN
                </xsl:attribute>
                <img src="static/images/out.png" width="30px"/>
            </a>
        </xsl:if>
      </xsl:if>
      <xsl:if test="/webAlbums/tags or /webAlbums/photos/random">
	<a title="Liens vers l'album">
	  <xsl:attribute name="href">Photos?album=<xsl:value-of select="albumId" /></xsl:attribute>
	  <img src="static/images/dossier.gif" width="30px"/>
	</a>
      </xsl:if>
      <xsl:if test="/webAlbums/affichage/@edit">
	<a title="Edition">
	  <xsl:attribute name="href">
	    <xsl:if test="/webAlbums/photos">
	      Photos?action=EDIT
&amp;id=<xsl:value-of select="photoId" />
&amp;count=<xsl:value-of select="../@count"	/>
&amp;albmCount=<xsl:value-of select="../../../album/@count" />
&amp;album=<xsl:value-of select="../../../album/@id"	/>
	      </xsl:if>
	      <xsl:if test="/webAlbums/tags">
		Tags?action=EDIT
&amp;id=<xsl:value-of select="photoId" />
		<xsl:for-each select="/webAlbums/tags/title/tags/*">
&amp;tagAsked=<xsl:value-of select="@id" />
		</xsl:for-each>
		<xsl:if test="/webAlbums/*/page/current">
&amp;page=<xsl:value-of select="/webAlbums/*/page/current" />
		</xsl:if>
	      </xsl:if>
              <xsl:if test="/webAlbums/albums">
                  Albums?action=EDIT
&amp;id=<xsl:value-of select="../@id" />
&amp;count=<xsl:value-of select="../@count"/>
              </xsl:if>
	  </xsl:attribute>
	  <img src="static/images/edit.png" height="30px"/>
	</a>
      </xsl:if>
    </div>
  </xsl:template>
  
  <xsl:template match="rights">
    <SELECT name="user">
      <xsl:apply-templates select="user"/>
    </SELECT>
  </xsl:template>
  
  <xsl:template match="rights/user">
    <OPTION>
      <xsl:attribute name="value"><xsl:value-of select="@id" /></xsl:attribute>
      <xsl:if test="@selected">
	<xsl:attribute name="selected">true</xsl:attribute>
      </xsl:if>
      <xsl:value-of select="." />
    </OPTION>
  </xsl:template>
  
  <xsl:template match="user">
    <xsl:if test="/webAlbums/albums">
      <div class="allowed"><xsl:value-of select="."/><xsl:apply-templates select="../userInside"/></div>
    </xsl:if>
    <xsl:if test="/webAlbums/photos and not(@album)">
      <div class="allowed"><xsl:value-of select="."/></div>
    </xsl:if>
    <xsl:if test="/webAlbums/tags and @album">
      <div class="allowed">[<xsl:value-of select="."/>]</div>
    </xsl:if>
    <xsl:if test="/webAlbums/tags and not(@album)">
     <div class="allowed"><xsl:value-of select="."/></div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="userInside">(<xsl:value-of select="."/>)</xsl:template>
</xsl:stylesheet>
