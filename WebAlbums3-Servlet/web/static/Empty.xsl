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
  <xsl:template match="/">
    <xsl:apply-templates select="/webAlbums/maint" />

    <xsl:apply-templates select="/webAlbums/tags/cloud" />
    <xsl:apply-templates select="/webAlbums/tags/personsPlaces" />
    <xsl:apply-templates select="/webAlbums/tags/display" />
    <xsl:apply-templates select="/webAlbums/tags/about" />

    <xsl:apply-templates select="/webAlbums/albums/top" />
    <xsl:apply-templates select="/webAlbums/albums/years" />
    <xsl:apply-templates select="/webAlbums/albums/graph" />
    <xsl:apply-templates select="/webAlbums/albums/select" />
    <xsl:apply-templates select="/webAlbums/albums/about" />

    <xsl:apply-templates select="/webAlbums/photos/random" />
    <xsl:apply-templates select="/webAlbums/photos/display" />
    <xsl:apply-templates select="/webAlbums/photos/about" />

    <xsl:apply-templates select="/webAlbums/carnets/top" />
  </xsl:template>

  <xsl:include href="PhotosAlbums.xsl" />
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

  <xsl:template match="albums/about">
      <xsl:apply-templates select="album"/>
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
                     q: '<xsl:value-of select="albmDate"/>',
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
  
  <xsl:template match="albums/select">
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
      <xsl:attribute name="src">Miniature__<xsl:value-of select="@picture"/></xsl:attribute>
    </img>
    <div>
    <a>
        <xsl:attribute name="href">Images?mode=RANDOM_TAG&amp;id=<xsl:value-of select="@id"/></xsl:attribute>
        <img src="static/images/random.png" width="30px"/>
    </a>
    </div>
  </xsl:template>

  <xsl:template match="/webAlbums/tags/cloud">
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
 
  <xsl:template match="personsPlaces">
    <table>
      <tr valign="top">
	<xsl:apply-templates select="tagList" />
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="personsPlaces/tagList">
    <td>
      <table>
	<tr>
	  <td>
	    <xsl:if test="@picture">
	      <a target="_top">
		<xsl:attribute name="href">Tag__<xsl:value-of select="@id"/>__<xsl:value-of select="name"/></xsl:attribute>
		<img class="choix_img">
		  <xsl:attribute name="src">Miniature__<xsl:value-of select="@picture"/>.png</xsl:attribute>
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
                    Miniature__<xsl:value-of select="@picture"/>.png
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

 <xsl:template match="top/album|top/carnet">
   <td>
      <table>
	<tr>
	  <td>
	    <center>
	      <xsl:if test="@picture">
		<a target="_top">
		  <xsl:attribute name="href">
                      <xsl:if test="/webAlbums/albums">
Photos__<xsl:value-of select="@id"/>_p0_pa0__<xsl:value-of select="name"/>
                      </xsl:if>
                      <xsl:if test="/webAlbums/carnets">
Carnet__<xsl:value-of select="@id"/>_pc0__<xsl:value-of select="name"/>
                      </xsl:if>
                  </xsl:attribute>
		  <img class="choix_img">
		    <xsl:attribute name="src">Miniature__<xsl:value-of select="@picture"/>.png</xsl:attribute>
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
		  <xsl:if test="/webAlbums/albums">
                        Photos__<xsl:value-of select="@id"/>_p0_pa0__<xsl:value-of select="name"/>
                  </xsl:if>
                  <xsl:if test="/webAlbums/carnets">
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
        <script type="text/javascript" src="static/scripts/lib/jquery/js/jquery.js"></script>
	<script type="text/javascript" src="static/scripts/tools.js"/>	  
        <script type="text/javascript" src="static/scripts/Empty.js"/>
	<div style="overflow:auto;">
	  <table>
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
		<a title="Retour � la normal">
		  <xsl:attribute name="href">
		    <xsl:if test="/webAlbums/photos">
Photos?
albmCount=<xsl:value-of select="album/@count" />
&amp;album=<xsl:value-of select="album/@id" />
		    </xsl:if>
		    <xsl:if test="/webAlbums/tags">
Tags?
<xsl:for-each select="title/tagList/*">
&amp;tagAsked=<xsl:value-of select="@id"/>
</xsl:for-each>
		    </xsl:if>
&amp;page=<xsl:value-of select="photoList/page/@current"/>
		  </xsl:attribute>
		  &#8629;
		</a>
	      </td>
	    </tr>
	  </table>
	</div>
	<div style="">
	  <img  id="largeImg" style="width:100%">
	    <xsl:attribute name="SRC">
	      Images?id=<xsl:value-of select="photoList/photo[1]/details/photoId"/>&amp;mode=GRAND
	    </xsl:attribute>
	  </img>
	</div>
      </body>
    </html>
  </xsl:template>
  
  <xsl:include href="Common.xsl"/>
  <xsl:template match="photo">
    <td>
      <a href="#" class="visio_img">
	<xsl:attribute name="rel"><xsl:value-of select="details/photoId"/></xsl:attribute>
	<img height="200px">
	  <xsl:attribute name="src">Images?id=<xsl:value-of select="details/photoId"/>&amp;mode=PETIT</xsl:attribute>
	  <xsl:attribute name="onmouseout">
	    UnTip()
	  </xsl:attribute>
	  <xsl:attribute name="onmouseover">
	    TagToTip('tip<xsl:value-of select="details/photoId" />')
	  </xsl:attribute>
	</img>
      </a>
      <span style="display:none">
	<xsl:attribute name="id">tip<xsl:value-of select="details/photoId" /></xsl:attribute>
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
	<xsl:attribute name="href">
	  <xsl:if test="/webAlbums/photos">
Photos?
&amp;albmCount=<xsl:value-of select="../url/albmCount" />
&amp;album=<xsl:value-of select="../url/album" />
	  </xsl:if>
	  <xsl:if test="/webAlbums/tags">
Tags?
&amp;tagAsked=<xsl:value-of select="../url/tagAsked"/>
	  </xsl:if>
          &amp;page=<xsl:value-of select="."/>
          &amp;special=VISIONNEUSE
          </xsl:attribute><xsl:if test="name(.) = 'nexti'">
              �
          </xsl:if>
          <xsl:if test="name(.) = 'previ'">
              �
          </xsl:if>
          <xsl:if test="not(name(.) = 'nexti') and not (name(.) = 'previ')">
              <xsl:value-of select="." />
          </xsl:if>
      </a>
    </td>
  </xsl:template>

</xsl:stylesheet>
