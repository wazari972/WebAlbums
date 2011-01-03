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
  <xsl:template match="albums">
    <xsl:apply-templates select="display"/>
    <xsl:apply-templates select="edit"/>
  </xsl:template>

  <xsl:template match="albums/display">
    <xsl:apply-templates select="exception"/>
    <xsl:apply-templates select="message"/>
    <xsl:apply-templates select="albumList/album"/>

    <xsl:apply-templates select="albumList/page"/>
  </xsl:template>

  <xsl:template match="album">
    <div class="item">
      <div class="date">
	<A><xsl:attribute name="name"><xsl:value-of select="@id" /></xsl:attribute></A>
	<div><xsl:value-of select="date/month" /></div>
	<span><xsl:value-of select="date/day" /></span>
      </div>
      <div class="content">
	<h1>
	  <a>
	    <xsl:if test="/webAlbums/albums">
	      <xsl:attribute name="href">
Photos?albmCount=<xsl:value-of select="@count" />&amp;album=<xsl:value-of select="@id" />
	      </xsl:attribute>
	    </xsl:if> 
	    <xsl:if test="/webAlbums/photos">
	      <xsl:attribute name="href">
Albums?count=<xsl:value-of select="@count" />#<xsl:value-of select="@id" />
	      </xsl:attribute>
	    </xsl:if> 
	    <xsl:value-of select="title" />
	  </a>
	</h1>
	<xsl:if test="/webAlbums/photos or /webAlbums/tags">
	  <h2>
            <div class="title_opt">
                <div class="album_opt">
                    <a rel="singlepage[no]">
                      <xsl:attribute name="title"><xsl:value-of select="title" /> en visionneuse</xsl:attribute>
                      <xsl:attribute name="href">
Photos?albmCount=<xsl:value-of select="@count" />
&amp;album=<xsl:value-of select="@id" />
&amp;page=<xsl:value-of select="../photoList/page/@current" />
&amp;special=VISIONNEUSE
                    </xsl:attribute>
                    <img src="static/images/slide.png" height="30px"/>
                    </a>
                    <xsl:if test="not(/webAlbums/affichage/@remote)">
                        &#160;
                        <a class='fullscreen'
                           href="#">
                          <xsl:attribute name="title"><xsl:value-of select="title" /> en plein-écran</xsl:attribute>
                          <xsl:attribute name="rel">
Photos?album=<xsl:value-of select="@id" />
&amp;page=<xsl:value-of select="../photoList/page/@current" />
&amp;special=FULLSCREEN
                          </xsl:attribute>
                          <img src="static/images/out.png" height="30px"/>
                        </a>
                    </xsl:if>
                    <xsl:if test="/webAlbums/affichage/@edit">
                        <div class="edit_opt">
                          &#160;
                          <a class="edit" title="Edition de l'album">
                            <xsl:attribute name="href">
Albums?action=EDIT
&amp;id=<xsl:value-of select="@id" />
&amp;count=<xsl:value-of select="@count"/>
                            </xsl:attribute>
                            Edit
                          </a>
                       </div>
                    </xsl:if>
                </div>
            </div>
	  </h2>
	</xsl:if>

	<div class="body">
	  <xsl:if test="/webAlbums/photos">
           <h2><div class="description"><xsl:value-of select="details/description" /></div></h2>
	   <xsl:apply-templates select="user" />
	  </xsl:if> 
	  <xsl:if test="/webAlbums/albums">
	    <xsl:apply-templates select="message"/>
	    <xsl:apply-templates select="details"/>
          </xsl:if>
	</div>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="message">
    <i><xsl:value-of select="."/></i><br/>
  </xsl:template>

</xsl:stylesheet>
