<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  <xsl:template match="albums/select|choix/select">
    <p><label for="albmName">Nom : </label> <input type="text" id="albmName"/></p>
    <br/>
    <div style="overflow: auto; height: 400px">
        <ul>
        <xsl:for-each select="album">
            <xsl:sort select="count(ancestor::*)" order="descending"/>
            <li class="selectAlbum">
                <xsl:attribute name="rel"><xsl:value-of select="@time"/></xsl:attribute>
                <label><xsl:value-of select="date/@date"/></label> &#160;
                <a>
                    <xsl:if test="not(/webAlbums/affichage/@static)"><xsl:attribute name="class">albumTT</xsl:attribute></xsl:if>
                    <xsl:attribute name="id">album-target-<xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:attribute name="href">Photos__<xsl:value-of select="@id"/>_p0<xsl:if test="not(/webAlbums/affichage/@static)">_pa</xsl:if>__<xsl:value-of select="name"/></xsl:attribute>
                    <xsl:value-of select="name"/><xsl:if test="gpx"><small> (GPX)</small></xsl:if>
                </a>
                <xsl:if test="not(/webAlbums/affichage/@static)">
                    <span class="album_tooltip">
                        <xsl:attribute name="id">album-content-<xsl:value-of select="@id"/></xsl:attribute>
                        <xsl:attribute name="rel"><xsl:value-of select="@id"/></xsl:attribute>
                    </span>
                </xsl:if>
            </li>
        </xsl:for-each>
        </ul>
    </div>
    <script>
       $(init_selecter)
    </script>
  </xsl:template>
  
  <xsl:template match="persons|places|topAlbums|topCarnets">
    <table>
      <tr valign="top">
	<xsl:apply-templates select="tagList" />
        <xsl:apply-templates select="album" />
        <xsl:apply-templates select="carnet" />
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="persons/tagList|places/tagList">
    <td>
      <table>
	<tr>
	  <td>
	    <xsl:if test="picture">
	      <a target="_top">
		<xsl:attribute name="href">Tag__<xsl:value-of select="@id"/>__<xsl:value-of select="name"/></xsl:attribute>
		<img class="choix_img">
                    <xsl:if test="/webAlbums/affichage/@directAccess">
                          <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picture/text()" />.png</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                          <xsl:attribute name="src">Miniature__<xsl:value-of select="picture/@id" />.png</xsl:attribute>
                    </xsl:if>
		</img>
	      </a>
	    </xsl:if>
	  </td>
	</tr>
	<tr>
	  <td>
	    <center>
	      <a>
		<xsl:attribute name="href">Tag__<xsl:value-of select="@id"/>__<xsl:value-of select="name"/></xsl:attribute>
		<xsl:value-of select="name"/>
	      </a>
	    </center>
	  </td>
	</tr>
      </table>
    </td>
  </xsl:template>

<xsl:template match="/webAlbums/albums/times_ago">
   <xsl:apply-templates select="album" />
 </xsl:template>

<xsl:template match="/webAlbums/albums/years">
   <xsl:apply-templates select="year" />
 </xsl:template>

 <xsl:template match="year">
     <div style="overflow: auto;">
        <b><xsl:value-of select="@year"/></b>
        <table>
            <tr>
                <xsl:apply-templates select="album" />
            </tr>
        </table>
     </div>
 </xsl:template>

<xsl:template match="year/album">
    <td>
        <a>
            <xsl:attribute name="href">Photos__<xsl:value-of select="@id"/>_p0<xsl:if test="not(/webAlbums/affichage/@static)">_pa</xsl:if>__<xsl:value-of select="name"/></xsl:attribute>
            <img class="choix_img">
                <xsl:if test="/webAlbums/affichage/@directAccess">
                      <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="details/photoId/text()" />.png</xsl:attribute>
                </xsl:if>
                <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                      <xsl:attribute name="src">Miniature__<xsl:value-of select="details/photoId/@id" />.png</xsl:attribute>
                </xsl:if>
                <xsl:attribute name="title">
                    <xsl:value-of select="date/@date"/>&#160;<xsl:value-of select="name/text()"/>
                </xsl:attribute>
            </img>
        </a>
    </td>
 </xsl:template>

 <xsl:template match="topAlbums/album|topCarnets/carnet|times_ago/album">
   <td>
      <table>
          <xsl:if test="name(..) = 'times_ago'">
              <tr>
                <td>
                  <center>
                    <a target="_top">
                        <xsl:attribute name="href">Photos__<xsl:value-of select="@id"/>_p0<xsl:if test="not(/webAlbums/affichage/@static)">_pa0</xsl:if>__<xsl:value-of select="name"/></xsl:attribute>
                        <xsl:value-of select="date/@date"/>
                    </a>
                  </center>
                </td>
              </tr>
          </xsl:if>
	<tr>
	  <td>
	    <center>
		<a target="_top">
                    <xsl:if test="name(..) = 'topAlbums' or name(..) = 'times_ago'">
                        <xsl:attribute name="href">Photos__<xsl:value-of select="@id"/>_p0<xsl:if test="not(/webAlbums/affichage/@static)">_pa0</xsl:if>__<xsl:value-of select="name"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="name(..) = 'topCarnets'">
                        <xsl:attribute name="href">Carnet__<xsl:value-of select="@id"/><xsl:if test="not(/webAlbums/affichage/@static)">_pc0</xsl:if>__<xsl:value-of select="name"/></xsl:attribute>
                    </xsl:if>
                    <img class="choix_img">
                        <xsl:if test="/webAlbums/affichage/@directAccess">
                          <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="details/photoId/text()" />.png</xsl:attribute>
                        </xsl:if>
                        <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                            <xsl:if test="name(..) = 'topCarnets'">
                                <xsl:attribute name="src">Miniature__<xsl:value-of select="picture/@id" />.png</xsl:attribute>
                            </xsl:if>
                            <xsl:if test="name(..) != 'topCarnets'">
                                <xsl:attribute name="src">Miniature__<xsl:value-of select="details/photoId/@id" />.png</xsl:attribute>
                            </xsl:if>
                        </xsl:if>
                    </img>
		</a>
	    </center>
	  </td>
	</tr>
	<tr>
	  <td>
	    <center>
	      <a target="_top">
                <xsl:if test="name(..) = 'topAlbums' or name(..) = 'times_ago'">
        		<xsl:attribute name="href">Photos__<xsl:value-of select="@id"/>_p0<xsl:if test="not(/webAlbums/affichage/@static)">_pa0</xsl:if>__<xsl:value-of select="name"/></xsl:attribute>
                  </xsl:if>
                  <xsl:if test="name(..) = 'topCarnets'">
                        <xsl:attribute name="href">Carnet__<xsl:value-of select="@id"/><xsl:if test="not(/webAlbums/affichage/@static)">_pc0</xsl:if>__<xsl:value-of select="name"/></xsl:attribute>
                  </xsl:if>		
                  <xsl:value-of select="name"/>
	      </a>
	    </center>
	  </td>
	</tr>
      </table>
    </td>
  </xsl:template>
  
  <xsl:template match="cloud">
      <h3>Nuage de tags</h3>
      <span class="tree-opt"><a id="tree_expand">Expand All</a> | <a id="tree_contract">Contract All</a></span> | <a href="tags.html" target="_blank" rel="singlepage[no]" title="Ouvrir dans une nouvelle page">Tree</a>
      <hr/>
      <ul id="cloudTree" class="treeview">
        <xsl:if test="tagInfo[@type = 'who']">
            <li class="not-real-tag" rel="open">
                <a>Who<hr/></a>
                <ul>
                    <xsl:apply-templates select="tagInfo[@type = 'who']"/>
                </ul>
            </li>
        </xsl:if>
        
        <xsl:if test="tagInfo[@type = 'what']">
            <li class="not-real-tag" rel="open">
                <a><hr/>What<hr/></a>
                <ul>
                    <xsl:apply-templates select="tagInfo[@type = 'what']"/>
                </ul>
            </li>
        </xsl:if>
        
        <xsl:if test="tagInfo[@type = 'where']">
            <li class="not-real-tag" rel="open">
                <a><hr/>Where<hr/></a>
                <ul>
                    <xsl:apply-templates select="tagInfo[@type = 'where']"/>
                </ul>
            </li>
        </xsl:if>
        
        <xsl:if test="../tag_never">
            <li class="not-real-tag" rel="open">
                <a><hr/>Tags never ever used</a>
                <ul>
                    <xsl:apply-templates select="../tag_never"/>
                </ul>
            </li>
        </xsl:if>
      </ul>
      
      <script type="text/javascript" src="static/scripts/Empty.js"/>
      <link rel="stylesheet" type="text/css" href="static/scripts/lib/treemenu/simpletree.css" />
      <script type="text/javascript" src="static/scripts/lib/treemenu/simpletreemenu.js">
            /***********************************************
            * Simple Tree Menu- � Dynamic Drive DHTML code library (www.dynamicdrive.com)
            * This notice MUST stay intact for legal use
            * Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
            * (http://www.dynamicdrive.com/dynamicindex1/navigate1.htm)
            ***********************************************/
      </script>
  </xsl:template>

  <xsl:template match="cloud/tagInfo|children/tagInfo|tag_never/where|tag_never/what|tag_never/who">
      <li>
        <a class="cloud-tag_used">
          <xsl:attribute name="id">cloud-target-<xsl:value-of select="tag/@id"/></xsl:attribute>
          <xsl:attribute name="style">font-size: <xsl:value-of select="@size"/>%;</xsl:attribute>
          <xsl:attribute name="href">Tag__<xsl:value-of select="tag/@id"/>x__<xsl:value-of select="tag/name"/></xsl:attribute>
          <xsl:attribute name="title"><xsl:value-of select="@nb"/> photos</xsl:attribute>
          <xsl:value-of select="tag/name" />
        </a>
        <span class="cloud_tooltip_not_used">
            <xsl:attribute name="id">cloud-content-<xsl:value-of select="tag/@id"/></xsl:attribute>
            <xsl:attribute name="rel"><xsl:value-of select="tag/@id"/></xsl:attribute>
        </span>
        <xsl:if test="children/tagInfo">
	    <ul rel="open">
              <xsl:apply-templates select="children/tagInfo"/>
            </ul>
        </xsl:if>
    </li>
  </xsl:template>
  <xsl:key name="product" match="/webAlbums/albums/gpxes/gpx/tagList/*/name/text()" use="." />
  <xsl:template match="/webAlbums/albums/gpxes">
      <xsl:if test="/webAlbums/albums/gpxes/gpx">
        <div id="gpxSelection">
            <select multiple="true" data-placeholder="Filtrer par tag..." >
                <xsl:for-each select="/webAlbums/albums/gpxes/gpx/tagList/*/name/text()[generate-id() = generate-id(key('product',.)[1])]">
                    <xsl:sort select="." data-type="text" order="ascending"/>
                    <option>
                        <xsl:attribute name="value">gpxTrack__<xsl:value-of select="translate(., ' ', '_')"/></xsl:attribute>
                        <xsl:value-of select="."/>
                    </option>
                </xsl:for-each>      
            </select>
            <xsl:apply-templates select="/webAlbums/albums/gpxes/gpx"/>
            <div id="gpxChoix"></div>
        </div>
      </xsl:if>
      <xsl:if test="not(/webAlbums/albums/gpxes/gpx)">
          <div>pas de trace disponnible</div>
      </xsl:if>
  </xsl:template>
  
  <xsl:template match="/webAlbums/albums/gpxes/gpx">
    <p>
        <xsl:attribute name="class">
                <xsl:for-each select="tagList/*">gpxTrack__<xsl:value-of select="translate(./name/text(), ' ', '_')"/>
                <xsl:if test="position() != last()">
                    <xsl:text> </xsl:text>
                </xsl:if>
                </xsl:for-each>
        </xsl:attribute>
        <a class="gpxTrack">
            <xsl:attribute name="rel"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:value-of select="description/line[1]/text()"/>
        </a>
        <xsl:text> (</xsl:text>
        <a target="_blank"><xsl:attribute name="href">Photos__<xsl:value-of select="@albumId"/>_p0_pa__</xsl:attribute>@ <xsl:value-of select="albumName"/></a>
        <xsl:if test="tagList/*">
            <xsl:text> -- </xsl:text>
          <xsl:for-each select="tagList/*">
              <xsl:value-of select="./name/text()"/>
              <xsl:if test="position() != last()">
                <xsl:text>, </xsl:text>
              </xsl:if>
          </xsl:for-each>
        </xsl:if>
        <xsl:text>)</xsl:text>
    </p>
  </xsl:template>
  
  <xsl:template match="albums/about">
      <xsl:apply-templates select="album"/>
  </xsl:template>
</xsl:stylesheet>