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
      <xsl:attribute name="HREF">
	<xsl:if test="/root/photos or /root/tags">
	  Images?id=<xsl:value-of select="photoID" />&amp;mode=GRAND
	</xsl:if> 
	<xsl:if test="/root/albums">
	  Photos?albmCount=<xsl:value-of select="../count" />&amp;album=<xsl:value-of select="../id" />
	</xsl:if> 
      </xsl:attribute>
      <img class="photo">
	<!-- -->
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
	  <xsl:attribute name="onmouseout">
	    UnTip()
	  </xsl:attribute>
	  <xsl:attribute name="onmouseover">
	    TagToTip('tip<xsl:value-of select="photoID" />')
	  </xsl:attribute>
	</xsl:if>
	<xsl:attribute name="src">
	  <xsl:if test="normalize-space(photoID) = ''">
	    static/images/rien.jpg
	  </xsl:if>
	  <xsl:if test="normalize-space(photoID) != ''">
	    Images?id=<xsl:value-of select="photoID" />&amp;mode=PETIT
	  </xsl:if>
	</xsl:attribute>
      </img>
    </a>
    <xsl:if test="../exif">
      <span>
	<xsl:attribute name="id">tip<xsl:value-of select="photoID" /></xsl:attribute>
	<xsl:apply-templates select="../exif" />
      </span>
    </xsl:if>
    <div class="details">
      <xsl:apply-templates select="tags">
	<xsl:with-param name="style">none</xsl:with-param>
	<xsl:with-param name="mode">TAG_USED</xsl:with-param>
	<xsl:with-param name="box">NONE</xsl:with-param>
      </xsl:apply-templates>
      <div class="description"><xsl:value-of select="description" /></div>
      <xsl:apply-templates select="user" />
      <xsl:if test="not(/root/albums)">
	<a title="Photo réduite">
	  <xsl:attribute name="href">Images?id=<xsl:value-of select="photoID" />&amp;mode=SHRINK&amp;width=800</xsl:attribute>
	  <img src="static/images/reduire.gif" width="25px"/>
	</a>
      </xsl:if>
      <xsl:if test="not(/root/albums)">
        <xsl:if test="count(/root/affichage/remote) = 0">
            <a title="Photo en plein-ecran">
                <xsl:attribute name="href">
                    javascript:callURL('Images?id=<xsl:value-of select="photoID" />&amp;mode=FULLSCREEN');
                </xsl:attribute>
                <img src="static/images/out.png" width="25px"/>
            </a>
        </xsl:if>
      </xsl:if>
      <xsl:if test="/root/tags">
	<a title="Liens vers l'album">
	  <xsl:attribute name="href">Photos?album=<xsl:value-of select="albumID" /></xsl:attribute>
	  <img src="static/images/dossier.gif" width="25px"/>
	</a>
      </xsl:if>
      <xsl:if test="/root/affichage/edit">
	<a title="Edition">
	  <xsl:attribute name="href">
	    <xsl:if test="/root/photos">
	      Photos?action=EDIT
&amp;id=<xsl:value-of select="photoID" />
&amp;count=<xsl:value-of select="../count"	/>
&amp;albmCount=<xsl:value-of select="/root/photos/album/count" />
&amp;album=<xsl:value-of select="/root/photos/album/id"	/>
	      </xsl:if>
	      <xsl:if test="/root/tags">
		Tags?action=EDIT
&amp;id=<xsl:value-of select="photoID" />
		<xsl:for-each select="/root/tags/title/tags/*">
&amp;tagAsked=<xsl:value-of select="@id" />
		</xsl:for-each>
		<xsl:if test="/root/*/page/current">
&amp;page=<xsl:value-of select="/root/*/page/current" />
		</xsl:if>
	      </xsl:if>
              <xsl:if test="/root/albums">
                  Albums?action=EDIT
&amp;id=<xsl:value-of select="../id" />
&amp;count=<xsl:value-of select="../count"/>
              </xsl:if>
	  </xsl:attribute>
	  <img src="static/images/edit.png" height="25px"/>
	</a>
      </xsl:if>
    </div>
  </xsl:template>
  
  <xsl:template match="userList">
    <SELECT name="user">
      <xsl:apply-templates select="user"/>
    </SELECT>
  </xsl:template>
  
  <xsl:template match="userList/user">
    <OPTION>
      <xsl:attribute name="value"><xsl:value-of select="@id" /></xsl:attribute>
      <xsl:if test="@selected">
	<xsl:attribute name="selected">true</xsl:attribute>
      </xsl:if>
      <xsl:value-of select="." />
    </OPTION>
  </xsl:template>
  
  <xsl:template match="user">
    <xsl:if test="/root/albums">
      <div class="allowed"><xsl:value-of select="."/><xsl:apply-templates select="../userInside"/></div>
    </xsl:if>
    <xsl:if test="/root/photos and not(@album)">
      <div class="allowed"><xsl:value-of select="."/></div>
    </xsl:if>
    <xsl:if test="/root/tags and @album">
      <div class="allowed">[<xsl:value-of select="."/>]</div>
    </xsl:if>
    <xsl:if test="/root/tags and not(@album)">
     <div class="allowed"><xsl:value-of select="."/></div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="userInside">(<xsl:value-of select="."/>)</xsl:template>
</xsl:stylesheet>
