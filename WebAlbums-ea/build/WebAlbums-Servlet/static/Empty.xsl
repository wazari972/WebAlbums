<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  <xsl:include href="Include.xsl" />
  <xsl:template match="/">
    <xsl:apply-templates select="/webAlbums/maint" />

    <xsl:apply-templates select="/webAlbums/tags/cloud" />
    <xsl:apply-templates select="/webAlbums/tags/persons" />
    <xsl:apply-templates select="/webAlbums/tags/places" />
    <xsl:apply-templates select="/webAlbums/tags/display" />
    <xsl:apply-templates select="/webAlbums/tags/about" />

    <xsl:apply-templates select="/webAlbums/albums/topAlbums" />
    <xsl:apply-templates select="/webAlbums/albums/years" />
    <xsl:apply-templates select="/webAlbums/albums/graph" />
    <xsl:apply-templates select="/webAlbums/albums/select" />
    <xsl:apply-templates select="/webAlbums/albums/about" />
    <xsl:apply-templates select="/webAlbums/albums/gpxes" />
    <xsl:apply-templates select="/webAlbums/albums/times_ago" />
    
    <xsl:apply-templates select="/webAlbums/photos/random" />
    <xsl:apply-templates select="/webAlbums/photos/display" />
    <xsl:apply-templates select="/webAlbums/photos/about" />
    <xsl:apply-templates select="/webAlbums/photos/fastedit" />

    <xsl:apply-templates select="/webAlbums/carnets/topCarnets" />
  </xsl:template>

  <xsl:include href="PhotosAlbums.xsl" />
  <xsl:include href="ChoixWidgets.xsl" />
  
  <xsl:template match="photos/random">
    <center>
      <xsl:apply-templates select="details"/>
    </center>
  </xsl:template>
  
  <xsl:template match="photos/about">
    <center>
      <xsl:apply-templates select="details"/>
    </center>
  </xsl:template>
  
  <xsl:template match="about/album">
    <h3>
        <a>
            <xsl:attribute name="href">Photos__<xsl:value-of select="@id"/>_p0_pa__<xsl:value-of select="title"/></xsl:attribute>
            <xsl:value-of select="title"/>
        </a>
    </h3>
    <xsl:apply-templates select="details"/>
  </xsl:template>
  <xsl:key name="tags" match="entry" use="key" />
  <xsl:template match="albums/graph">
      <html>
          <body>
              :
              <script type="text/javascript">
                  graphData = [
                <xsl:for-each select="album">
                    {
                     q: '<xsl:value-of select="date/@date"/>',
                    <xsl:for-each select="photoCount/entry">
                      "<xsl:value-of select="key"/>": <xsl:value-of select="value"/>,
                    </xsl:for-each>
                    },
                </xsl:for-each>
                    ]
               my_ykeys = [
               <xsl:for-each select="album/photoCount/entry[generate-id(.) = generate-id(key('tags', key)[1])]/key">
                   "<xsl:value-of select="."/>",
               </xsl:for-each>
               ]
              </script>
             <div id="graph"/>
         </body>
     </html>
  </xsl:template>

  <xsl:template match="tags/about">
    <xsl:apply-templates select="tag"/>
  </xsl:template>

    <xsl:template match="about/tag">
      <h3>
        <a>
            <xsl:attribute name="href">Tag__<xsl:value-of select="@id"/>__<xsl:value-of select="name"/></xsl:attribute>
            <xsl:value-of select="name"/>
        </a>
    </h3>
    <img class="choix_img">
      <xsl:if test="/webAlbums/affichage/@directAccess">
          <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picture/text()" />.png</xsl:attribute>
      </xsl:if>
      <xsl:if test="not(/webAlbums/affichage/@directAccess)">
          <xsl:attribute name="src">Miniature__<xsl:value-of select="picture/@id" />.png</xsl:attribute>
      </xsl:if>
    </img>
    <div>
    <a>
        <xsl:attribute name="href">Images?mode=RANDOM_TAG&amp;id=<xsl:value-of select="@id"/></xsl:attribute>
        <img src="static/images/random.png" width="30px"/>
    </a>
    </div>
  </xsl:template>

  <xsl:template match="photos/fastedit">
      <p>Description: <xsl:value-of select="@descStatus"/>@ <xsl:value-of select="desc_msg/text()"/></p>
      <p>Tag: <xsl:value-of select="@tagStatus"/>@ <xsl:value-of select="tag_msg/text()"/></p>
      <p>Stars: <xsl:value-of select="@starsStatus"/>@ <xsl:value-of select="stars_msg/text()"/></p>
      <p>Delete: <xsl:value-of select="@deleteStatus"/> @ <xsl:value-of select="delete_msg/text()"/></p>
      
  </xsl:template>

  <xsl:template match="/webAlbums/maint">
    <html>
      <body>
	<xsl:apply-templates select="action"/>
	<xsl:apply-templates select="message"/>
	<xsl:apply-templates select="exception"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="action">
    <b>action=<xsl:value-of select="."/></b>
    <br/>
    <br/>
  </xsl:template>
  <xsl:template match="message">
    <i><xsl:value-of select="."/></i>
    <br/>
  </xsl:template>
  <xsl:template match="exception">
    <br/>
    /!\ <xsl:value-of select="."/> /!\
    <br/>
  </xsl:template>

  <xsl:template match="display">
    <html style="margin: 0;padding: 0;height: 100%">
      <body style="margin: 0;padding: 0;height: 100%">
        <script type="text/javascript" src="static/scripts/lib/jquery/js/jquery.js"/>
	<script type="text/javascript" src="static/scripts/tools.js"/>
        <script type="text/javascript" src="static/scripts/Empty.js"/>
        <script type="text/javascript" src="static/scripts/lib/jwerty.js"/>
        <script type="text/javascript" src="static/scripts/Visio.js"/>
	<div style="overflow:auto;">
	  <table id="visio_preview">
	    <tr>
              <xsl:apply-templates select="@previ"/>
              <xsl:apply-templates select="@first"/>
              <xsl:if test="@first"><span>...</span></xsl:if>
              <xsl:apply-templates select="prev"/>
              <span class="current"><xsl:value-of select="@current" /> </span>
              <xsl:apply-templates select="next"/>
              <xsl:if test="@last">... </xsl:if>
              <xsl:apply-templates select="@last"/>
              <xsl:apply-templates select="@nexti"/>

	      <xsl:apply-templates select="photoList/page/@previ"/>
	      <xsl:apply-templates select="photoList/page/@first"/>
              <xsl:if test="photoList/page/@first"><td>...</td></xsl:if>
	      <xsl:apply-templates select="photoList/page/prev"/>
	      <xsl:apply-templates select="photoList/photo"/>
	      <xsl:apply-templates select="photoList/page/next"/>
              <xsl:if test="photoList/page/@last"><td>...</td></xsl:if>
              <xsl:apply-templates select="photoList/page/@last"/>
	      <xsl:apply-templates select="photoList/page/@nexti"/>

	      <td>
		<a title="Retour à la normal">
                  <xsl:if test="/webAlbums/photos">
                    <xsl:attribute name="href">Photos__<xsl:value-of select="album/@id" />_p<xsl:value-of select="photoList/page/@current"/>__<xsl:value-of select="/webAlbums/photos/display/album/title"/></xsl:attribute>
                  </xsl:if>
                  <xsl:if test="/webAlbums/tags">
                    <xsl:attribute name="href">Tags?<xsl:for-each select="title/tagList/*">&amp;tagAsked=<xsl:value-of select="@id"/></xsl:for-each>&amp;page=<xsl:value-of select="photoList/page/@current"/></xsl:attribute>
                   </xsl:if>
		  <span>&#8629;</span>
		</a>
	      </td>
	    </tr>
	  </table>
	</div>
        <center style="background-color:#222; overflow:scroll">
          <img  id="largeImg" style="max-width:100%">
              <xsl:if test="/webAlbums/affichage/@directAccess">
                  <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/photo_folder" /><xsl:value-of select="photoList/photo[1]/details/photoId/text()" />.png</xsl:attribute>
              </xsl:if>
              <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                  <xsl:attribute name="src">Image__<xsl:value-of select="picture/@id" />.png</xsl:attribute>
              </xsl:if>
          </img>
          </center>
      </body>
    </html>
  </xsl:template>
  
  <xsl:include href="Common.xsl"/>
  <xsl:template match="photo">
    <td>
      <a class="visio_img">
	<xsl:attribute name="rel"><xsl:value-of select="details/photoId/@id"/></xsl:attribute>
        <xsl:if test="/webAlbums/affichage/@directAccess">
              <xsl:attribute name="href"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/photo_folder" /><xsl:value-of select="details/photoId/text()" /></xsl:attribute>
          </xsl:if>
          <xsl:if test="not(/webAlbums/affichage/@directAccess)">
              <xsl:attribute name="href">Image__<xsl:value-of select="details/photoId/@id" /></xsl:attribute>
          </xsl:if>
          <xsl:attribute name="rel"><xsl:value-of select="details/photoId/@id" /></xsl:attribute>
	<img height="200px">
          <xsl:if test="/webAlbums/affichage/@directAccess">
              <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="details/photoId/text()" />.png</xsl:attribute>
          </xsl:if>
          <xsl:if test="not(/webAlbums/affichage/@directAccess)">
              <xsl:attribute name="src">Miniature__<xsl:value-of select="details/photoId/text()" />.png</xsl:attribute>
          </xsl:if>
	</img>
      </a>
      <span style="display:none">
	<xsl:attribute name="id">tip<xsl:value-of select="details/photoId/@id" /></xsl:attribute>
	<xsl:apply-templates select="details/tagList">
	  <xsl:with-param name="style">none</xsl:with-param>
	  <xsl:with-param name="mode">TAG_USED</xsl:with-param>
	  <xsl:with-param name="box">NONE</xsl:with-param>
	</xsl:apply-templates>
	<xsl:value-of select="details/description" />
	<xsl:apply-templates select="exif" />
      </span>
    </td>
  </xsl:template>

  <xsl:template match="prev|next|@first|@last|@nexti|@previ">
    <td>
      <a>
          <xsl:attribute name="class">page_<xsl:value-of select="name(.)"/></xsl:attribute>
          <xsl:attribute name="rel"><xsl:value-of select="."/></xsl:attribute>
	  <xsl:if test="/webAlbums/photos">
              <xsl:attribute name="href">Visio__<xsl:value-of select="../url/album" />_p<xsl:value-of select="."/>__<xsl:value-of select="/webAlbums/photos/display/album/title"/></xsl:attribute>
          </xsl:if>
	  <xsl:if test="/webAlbums/tags">
              <xsl:attribute name="href">Tags?&amp;tagAsked=<xsl:value-of select="../url/tagAsked"/>&amp;page=<xsl:value-of select="."/>&amp;special=VISIONNEUSE</xsl:attribute>
	  </xsl:if>
          <xsl:if test="name(.) = 'nexti'">»</xsl:if>
          <xsl:if test="name(.) = 'previ'">«</xsl:if>
          <xsl:if test="not(name(.) = 'nexti') and not (name(.) = 'previ')"><xsl:value-of select="." /></xsl:if>
      </a>
    </td>
  </xsl:template>

</xsl:stylesheet>
