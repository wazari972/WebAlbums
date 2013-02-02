<?xml version="1.0" encoding="ISO-8859-1"?>
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
	      <xsl:attribute name="href">Photos__<xsl:value-of select="@id" />_p0<xsl:if test="not(/webAlbums/affichage/@static)">_pa<xsl:value-of select="/webAlbums/albums/display/albumList/page/@current" /></xsl:if>__<xsl:value-of select="title" /></xsl:attribute>
	    </xsl:if> 
	    <xsl:if test="/webAlbums/photos">
	      <xsl:attribute name="href">Albums__p<xsl:value-of select="/webAlbums/photos/display/photoList/page/url/albmPage" />#<xsl:value-of select="@id" /></xsl:attribute>
	    </xsl:if> 
	    <xsl:value-of select="title" />
	  </a>
	</h1>
	<xsl:if test="/webAlbums/photos or /webAlbums/tags">
	  <h2 class="details">
            <div class="title_opt">
                <div class="album_opt">
                    <xsl:if test="not(/webAlbums/affichage/@static)">
                        <a rel="singlepage[no]">
                            <xsl:attribute name="title"><xsl:value-of select="title" /> en visionneuse</xsl:attribute>
                            <xsl:attribute name="href">Visio__<xsl:value-of select="@id" />_p0<xsl:if test="not(/webAlbums/affichage/@static)">_pa<xsl:value-of select="/webAlbums/photos/display/photoList/page/url/albmPage" /></xsl:if>__<xsl:value-of select="title" /></xsl:attribute>
                            <img src="static/images/slide.png" height="30px"/>
                        </a>
                    </xsl:if>
                    <xsl:if test="/webAlbums/photos/display/album/details/tagList/where">
                        <a rel="singlepage[no]" id="showTagMap">
                            <xsl:attribute name="title">Afficher la carte des tags.</xsl:attribute>
                            <img src="static/images/map.png" height="30px"/>
                        </a>
                    </xsl:if>
                    <xsl:if test="/webAlbums/loginInfo/@admin">
                          <span>&#160;</span>
                          <a rel="singlepage[no]" title="Edition de l'album" class="edit">
                            <!-- no url rewritting -->
                            <xsl:attribute name="href">Albums?action=EDIT&amp;id=<xsl:value-of select="@id" />&amp;page=<xsl:value-of select="/webAlbums/photos/display/photoList/page/url/albmPage"/></xsl:attribute>
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
               <div class="map_opt">
                   <xsl:apply-templates select="gpx"/>
                   <xsl:apply-templates select="details/tagList"/>
                   <div id="gpx_box"></div>
                </div>
            </xsl:if>
	  </h2>
          <hr/>
          <xsl:if test="details/description and details/description/line != ''">
              <span>
                  <xsl:for-each select="details/description/line"><p><xsl:value-of select="." /></p></xsl:for-each>
              </span>
              <hr/>
          </xsl:if>
	</xsl:if>
        <xsl:if test="not(/webAlbums/photos or /webAlbums/tags)">
            <div class="body">
              <xsl:if test="/webAlbums/photos">
               <h2><div class="description"><xsl:for-each select="description/line"><p><xsl:value-of select="." /></p></xsl:for-each></div></h2>
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

  <xsl:template match="/webAlbums/photos/display/album/details/tagList">
    <ul id="tags_visu"><xsl:apply-templates select="where"/><br/></ul>
  </xsl:template>

<xsl:template match="/webAlbums/photos/display/album/details/tagList/where">
    <li>
        <a class="tag_visu">
            <xsl:attribute name="rel"><xsl:value-of select="@lat"/>/<xsl:value-of select="@longit"/></xsl:attribute>
            <xsl:value-of select="."/>
        </a>
    </li>
</xsl:template>

  <xsl:template match="message">
    <i><xsl:value-of select="."/></i><br/>
  </xsl:template>

  <xsl:template match="album/carnet">
    <p> 
        <small>
            <a>
                <xsl:attribute name="href">Carnet__<xsl:value-of select="@id" /><xsl:if test="not(/webAlbums/affichage/@static)">_pc0</xsl:if>__<xsl:value-of select="name" /></xsl:attribute>
                <img class="mini-carnet">
                  <xsl:if test="/webAlbums/affichage/@directAccess">
                      <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picture/text()" />.png</xsl:attribute>
                  </xsl:if>
                  <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                      <xsl:attribute name="src">Miniature__<xsl:value-of select="picture/@id" />.png</xsl:attribute>
                  </xsl:if>
                </img><span>&#160;</span>
                <xsl:value-of select="name" />
            </a>
        </small>
    </p>
    <br/>
  </xsl:template>
  <xsl:template match="display/album/gpx">
    <p> 
        <small>
            <a class="gpx_visu">
                <xsl:attribute name="rel"><xsl:value-of select="@id" /></xsl:attribute>
                <xsl:if test="not(description)">
                    Trace GPS <xsl:value-of select="position()"/>
               </xsl:if>    
                <xsl:if test="description">
                    <xsl:value-of select="position()"/>. <xsl:value-of select="description/line" /> 
                </xsl:if> 
            </a>
            &#160;(<a target="_blank" rel="singlepage[no]">
                <xsl:attribute name="href">GPX__<xsl:value-of select="@id" />.gpx</xsl:attribute>
                <span>dl</span>
            </a>)
        </small>
    </p>
    <br/>
  </xsl:template>
</xsl:stylesheet>
