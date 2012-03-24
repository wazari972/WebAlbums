<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet  [
  <!ENTITY % xhtml-lat1 SYSTEM
     "http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent">
  <!ENTITY % xhtml-special SYSTEM
     "http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent">
  <!ENTITY % xhtml-symbol SYSTEM
     "http://www.w3.org/TR/xhtml1/DTD/xhtmlroot-symbol.ent">
  %xhtml-lat1;
  %xhtml-special;
  %xhtml-symbol;
  ]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  <xsl:template match="albums/select|choix/select">
    <p>
        <label for="fromDate">Albums dat�s entre le </label>
        <span id="fromDate" style="font-weight:bold;" />

        <label for="toDate"> et le </label>
        <span id="toDate" style="font-weight:bold;">
            <xsl:attribute name="rel"></xsl:attribute>
        </span>
    </p>
    <br/>
    <div id="slider-range"></div>
    <br/>
    <p><label for="albmName"> et ayant pour nom </label> <input type="text" id="albmName"/></p>
    <div style="overflow: auto; height: 400px">
        <ul>
        <xsl:for-each select="album">
            <xsl:sort select="count(ancestor::*)" order="descending"/>
            <li class="selectAlbum">
                <xsl:attribute name="rel"><xsl:value-of select="@time"/></xsl:attribute>
                <label><xsl:value-of select="albmDate"/></label> &#160;
                <a class="albumTT">
                    <xsl:attribute name="id">album-target-<xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:attribute name="href">Photos__<xsl:value-of select="@id"/>_p0_pa__<xsl:value-of select="name"/></xsl:attribute>
                    <xsl:value-of select="name"/>
                </a>
                <span class="album_tooltip">
                    <xsl:attribute name="id">album-content-<xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:attribute name="rel"><xsl:value-of select="@id"/></xsl:attribute>
                </span>
            </li>
        </xsl:for-each>
        </ul>
    </div>
    <script>
       data = {fromDate:<xsl:value-of select="album[last()]/@time"/>,
               toDate  :<xsl:value-of select="album/@time"/>}
             
       function init_slider() {
           do_init_slider(data)
       }
       $(init_slider)
    </script>
  </xsl:template>
  
  <xsl:template match="persons|places">
    <table>
      <tr valign="top">
	<xsl:apply-templates select="tagList" />
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
		  <xsl:attribute name="src">
                    <xsl:if test="/webAlbums/affichage/@directAccess">
                          <xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picture/text()" />.png
                    </xsl:if>
                    <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                          Miniature__<xsl:value-of select="picture/@id" />.png
                    </xsl:if>
                  </xsl:attribute>
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

<xsl:template match="/webAlbums/albums/years">
   <xsl:apply-templates select="year" />
 </xsl:template>

 <xsl:template match="year">
     <div style="overflow: auto;">
        <b><xsl:value-of select="year"/></b>
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
            <xsl:attribute name="href">
                Photos__<xsl:value-of select="@id"/>_p0_pa__<xsl:value-of select="name"/>
             </xsl:attribute>
            <img class="choix_img">
                <xsl:attribute name="src">
                    <xsl:if test="/webAlbums/affichage/@directAccess">
                          <xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picture/text()" />.png
                    </xsl:if>
                    <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                          Miniature__<xsl:value-of select="picture/@id" />.png
                    </xsl:if>
                </xsl:attribute>
                <xsl:attribute name="title">
                    <xsl:value-of select="name"/>
                </xsl:attribute>
            </img>
        </a>
    </td>
 </xsl:template>

 <xsl:template match="/webAlbums/albums/top|/webAlbums/carnets/top">
   <table>
     <tr>
       <xsl:apply-templates select="album" />
       <xsl:apply-templates select="carnet" />
     </tr>
   </table>
 </xsl:template>

 <xsl:template match="topAlbums/album|topCarnets/carnet">
   <td>
      <table>
	<tr>
	  <td>
	    <center>
	      <xsl:if test="picture">
		<a target="_top">
		  <xsl:attribute name="href">
                      <xsl:if test="name(..) = 'topAlbums'">
Photos__<xsl:value-of select="@id"/>_p0_pa0__<xsl:value-of select="name"/>
                      </xsl:if>
                      <xsl:if test="name(..) = 'topCarnets'">
Carnet__<xsl:value-of select="@id"/>_pc0__<xsl:value-of select="name"/>
                      </xsl:if>
                  </xsl:attribute>
		  <img class="choix_img">
		    <xsl:attribute name="src">
                        <xsl:if test="/webAlbums/affichage/@directAccess">
                          <xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="picture/text()" />.png
                        </xsl:if>
                        <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                              Miniature__<xsl:value-of select="picture/@id" />.png
                        </xsl:if>
                    </xsl:attribute>
		  </img>
		</a>
	      </xsl:if>
	    </center>
	  </td>
	</tr>
	<tr>
	  <td>
	    <center>
	      <a target="_top">
		<xsl:attribute name="href">
		  <xsl:if test="name(..) = 'topAlbums'">
                        Photos__<xsl:value-of select="@id"/>_p0_pa0__<xsl:value-of select="name"/>
                  </xsl:if>
                  <xsl:if test="name(..) = 'topCarnets'">
                        Carnet__<xsl:value-of select="@id"/>_pc0__<xsl:value-of select="name"/>
                  </xsl:if>
		</xsl:attribute>
		<xsl:value-of select="name"/>
	      </a>
	    </center>
	  </td>
	</tr>
      </table>
    </td>
  </xsl:template>
  
  
  <xsl:template match="cloud">
      <link rel="stylesheet" type="text/css" href="static/scripts/lib/treemenu/simpletree.css" />
      <h3>Nuage de tags </h3>
      <ul>
          <li><a id="tree_expand">Expand All</a> | <a id="tree_contract">Contract All</a></li>
          <li><hl/></li>
          <ul id="cloudTree" class="treeview">
            <xsl:apply-templates select="tag"/>
          </ul>
      </ul>
      <script type="text/javascript" src="static/scripts/Empty.js"/>
      <script type="text/javascript" src="static/scripts/lib/treemenu/simpletreemenu.js">
            /***********************************************
            * Simple Tree Menu- � Dynamic Drive DHTML code library (www.dynamicdrive.com)
            * This notice MUST stay intact for legal use
            * Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
            * (http://www.dynamicdrive.com/dynamicindex1/navigate1.htm)
            ***********************************************/
      </script>
  </xsl:template>

  <xsl:template match="cloud/tag|children/tag">
      <li>
        <a class="cloud-tag_used">
          <xsl:attribute name="id">cloud-target-<xsl:value-of select="@id"/></xsl:attribute>
          <xsl:attribute name="style">font-size: <xsl:value-of select="@size"/>%;</xsl:attribute>
          <xsl:attribute name="href">Tag__<xsl:value-of select="@id"/>x__<xsl:value-of select="name"/></xsl:attribute>
          <xsl:attribute name="title"><xsl:value-of select="@nb"/> photos</xsl:attribute>
          <xsl:value-of select="name" />
        </a>
        <span class="cloud_tooltip_not_used">
            <xsl:attribute name="id">cloud-content-<xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="rel"><xsl:value-of select="@id"/></xsl:attribute>
        </span>
        <xsl:if test="children/tag">
	    <ul rel="open">
              <xsl:apply-templates select="children/tag"/>
            </ul>
        </xsl:if>
    </li>
  </xsl:template>
</xsl:stylesheet>