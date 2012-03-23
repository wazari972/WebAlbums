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
    <xsl:apply-templates select="albumList/page"/>
    <xsl:apply-templates select="albumList/album"/>
    <xsl:apply-templates select="albumList/page"/>
  </xsl:template>

  <xsl:template match="album">
    <div class="item">
      <div class="date">
	<a><xsl:attribute name="id">anchor_<xsl:value-of select="@id" /></xsl:attribute></a>
	<div><xsl:value-of select="date/month" /></div>
	<span><xsl:value-of select="date/day" /></span>
      </div>
      <div class="content">
	<h1>
	  <a>
	    <xsl:if test="/webAlbums/albums">
	      <xsl:attribute name="href">
Photos__<xsl:value-of select="@id" />_p0_pa<xsl:value-of select="/webAlbums/albums/display/albumList/page/@current" />__<xsl:value-of select="title" />
	      </xsl:attribute>
	    </xsl:if> 
	    <xsl:if test="/webAlbums/photos">
	      <xsl:attribute name="href">
Albums__p<xsl:value-of select="/webAlbums/photos/display/photoList/page/url/albmPage" />#<xsl:value-of select="@id" />
	      </xsl:attribute>
	    </xsl:if> 
	    <xsl:value-of select="title" />
	  </a>
	</h1>
	<xsl:if test="/webAlbums/photos or /webAlbums/tags">
	  <h2 class="details">
            <div class="title_opt">
                <div class="album_opt">
                    <a rel="singlepage[no]">
                      <xsl:attribute name="title"><xsl:value-of select="title" /> en visionneuse</xsl:attribute>
                      <xsl:attribute name="href">
Visio__<xsl:value-of select="@id" />_p0_pa<xsl:value-of select="/webAlbums/photos/display/photoList/page/url/albmPage" />__<xsl:value-of select="title" />
                    </xsl:attribute>
                    <img src="static/images/slide.png" height="30px"/>
                    </a>
                    <xsl:if test="/webAlbums/affichage/remote or /webAblums/affichage/@static">
                        &#160;
                        <img src="static/images/out.png" height="30px"
                          class='fullscreen'>
                          <xsl:attribute name="title"><xsl:value-of select="title" /> en plein-écran</xsl:attribute>
                          <!-- no url rewritting -->
                          <xsl:attribute name="rel">
Photos?album=<xsl:value-of select="@id" />
&amp;page=<xsl:value-of select="../photoList/page/@current" />
&amp;special=FULLSCREEN
                          </xsl:attribute>
                          
                        </img>
                    </xsl:if>
                    <xsl:if test="/webAlbums/loginInfo/@admin">
                          &#160;
                          <a rel="singlepage[no]" title="Edition de l'album" class="edit">
                            <!-- no url rewritting -->
                            <xsl:attribute name="href">
Albums?action=EDIT
&amp;id=<xsl:value-of select="@id" />
&amp;count=<xsl:value-of select="@count"/>
                            </xsl:attribute>
                            <img src="static/images/edit.png" height="30px"/>
                          </a>
                    </xsl:if>
                </div>
            </div>
            <xsl:if test="carnet">
               <div class="carnets_opt">
                   <xsl:apply-templates select="carnet"/>
                </div>
            </xsl:if>
            <xsl:if test="gpx">
               <div class="gpx_opt">
                   <xsl:apply-templates select="gpx"/>
                </div>
            </xsl:if>
	  </h2>
          <hr/>
	</xsl:if>
        <xsl:if test="not(/webAlbums/photos or /webAlbums/tags)">
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
        </xsl:if>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="message">
    <i><xsl:value-of select="."/></i><br/>
  </xsl:template>

  <xsl:template match="album/carnet">
    <p> 
    <small>
        <a>
            <xsl:attribute name="href">Carnet__<xsl:value-of select="@id" />_pc0__<xsl:value-of select="name" /></xsl:attribute>
            <img class="mini-carnet">
                <xsl:attribute name="src">
                      <xsl:if test="/webAlbums/affichage/@directAccess">
                          <xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picture/text()" />.png
                      </xsl:if>
                      <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                          Miniature__<xsl:value-of select="picture/@id" />.png
                      </xsl:if>
                </xsl:attribute>
            </img>&#160;
            <xsl:value-of select="name" />
        </a>
        </small>
    </p>
    <br/>
  </xsl:template>
  <xsl:template match="album/gpx">
    <p> 
    <small>
        <a target="_blank" rel="singlepage[no]">
            <xsl:attribute name="href">GPX__<xsl:value-of select="@id" />.gpx</xsl:attribute>
            <xsl:value-of select="description" />
        </a>
        </small>
    </p>
    <br/>
  </xsl:template>
</xsl:stylesheet>
