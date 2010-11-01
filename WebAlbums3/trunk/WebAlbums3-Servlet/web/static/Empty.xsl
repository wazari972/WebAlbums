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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  <xsl:template match="/">
    <xsl:apply-templates select="/webAlbums/maint" />

    <xsl:apply-templates select="/webAlbums/tags/cloud" />
    <xsl:apply-templates select="/webAlbums/tags/personsPlaces" />
    <xsl:apply-templates select="/webAlbums/tags/display" />
    <xsl:apply-templates select="/webAlbums/tags/about" />

    <xsl:apply-templates select="/webAlbums/albums/top" />
    <xsl:apply-templates select="/webAlbums/albums/years" />
    <xsl:apply-templates select="/webAlbums/albums/select" />
    <xsl:apply-templates select="/webAlbums/albums/about" />

    <xsl:apply-templates select="/webAlbums/photos/random" />
    <xsl:apply-templates select="/webAlbums/photos/display" />
    <xsl:apply-templates select="/webAlbums/photos/about" />

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
            <xsl:attribute name="href">Photos?album=<xsl:value-of select="@id"/></xsl:attribute>
            <xsl:value-of select="title"/>
        </a>
    </h3>
    <xsl:apply-templates select="details"/>
  </xsl:template>
  
  <xsl:template match="albums/select">
    <p>
        <label for="fromDate">Albums datés entre le </label>
        <span id="fromDate" style="font-weight:bold;" />
        <label for="toDate"> et le </label>
        <span id="toDate" style="font-weight:bold;" />
    </p>
    <br/>
    <div id="slider-range"></div>
    <br/>
    <p><label for="albmName"> et ayant pour nom </label> <input type="text" id="albmName"/></p>
    <script>
     $("#slider-range").slider(sliderOption);
     $("#fromDate").text(printDate(<xsl:value-of select="album[last()]/@time"/>));
     $("#toDate").text(printDate(<xsl:value-of select="album/@time"/>));
     $("#slider-range").slider( "option", "max", <xsl:value-of select="album/@time"/>+$( "#slider-range" ).slider( "option", "step" ));
     $("#slider-range").slider( "option", "min", <xsl:value-of select="album[last()]/@time"/>);

     $("#slider-range").slider( "option", "values", [<xsl:value-of select="album[last()]/@time"/>, <xsl:value-of select="album/@time"/>]);
     $("#albmName").keyup(
        function(){
           trimAlbums($("#slider-range").slider( "option", "range", "min" ),
                      $("#slider-range").slider( "option", "range", "max" ),
                      $(this).val());

        }
     );
    </script>
    <div style="overflow: auto; height: 400px">
        <ul>
        <xsl:for-each select="album">
            <xsl:sort select="count(ancestor::*)" order="descending"/>
            <li class="selectAlbum">
                <xsl:attribute name="rel"><xsl:value-of select="@time"/></xsl:attribute>
                <label><xsl:value-of select="albmDate"/></label> &#160;
                <a class="albumTT">
                    <xsl:attribute name="id">album-target-<xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:attribute name="href">Photos?album=<xsl:value-of select="@id"/>&amp;albmCount=<xsl:value-of select="count"/></xsl:attribute>
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
    <script language="javascript">
        $(".albumTT").ezpz_tooltip({stayOnContent: true,beforeShow: prepareAlbumsTooltipsDiv});
    </script>
  </xsl:template>

  <xsl:template match="tags/about">
    <xsl:apply-templates select="tag"/>
  </xsl:template>

    <xsl:template match="about/tag">
      <h3>
        <a>
            <xsl:attribute name="href">Tags?tagsAsked=<xsl:value-of select="@id"/></xsl:attribute>
            <xsl:value-of select="name"/>
        </a>
    </h3>
    <img>
      <xsl:attribute name="src">Images?mode=PETIT&amp;id=<xsl:value-of select="@picture"/></xsl:attribute>
    </img>
    <div>
    <a>
        <xsl:attribute name="href">Images?mode=RANDOM_TAG&amp;id=<xsl:value-of select="@id"/></xsl:attribute>
        <img src="static/images/random.png" width="30px"/>
    </a>
    </div>
  </xsl:template>

  <xsl:template match="/webAlbums/tags/cloud">
      <script type="text/javascript" src="static/scripts/treemenu/simpletreemenu.js">
            /***********************************************
            * Simple Tree Menu- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
            * This notice MUST stay intact for legal use
            * Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
            * (http://www.dynamicdrive.com/dynamicindex1/navigate1.htm)
            ***********************************************/
      </script>
      <link rel="stylesheet" type="text/css" href="static/scripts/treemenu/simpletree.css" />
      <h3>Nuage de tags </h3>
      <ul>
          <li><a href="javascript:ddtreemenu.flatten('cloudTree', 'expand')">Expand All</a>
          | <a href="javascript:ddtreemenu.flatten('cloudTree', 'contract')">Contact All</a></li>

          <ul id="cloudTree" class="treeview">
            <xsl:apply-templates select="tag"/>
          </ul>
      </ul>
      <script type="text/javascript">
        ddtreemenu.createTree("cloudTree", false)
      </script>
  </xsl:template>

  <xsl:template match="cloud/tag|children/tag">
      <li>
        <a class="cloud-tag">
          <xsl:attribute name="id">cloud-target-<xsl:value-of select="@id"/></xsl:attribute>
          <xsl:attribute name="style">font-size: <xsl:value-of select="@size"/>%;</xsl:attribute>
          <xsl:attribute name="href">Tags?tagAsked=<xsl:value-of select="@id"/>&amp;wantTagChildren=true</xsl:attribute>
          <xsl:attribute name="nbElements"><xsl:value-of select="@name"/> : <xsl:value-of select="@nb"/></xsl:attribute>
          <xsl:value-of select="name" />
        </a>
        <span class="cloud_tooltip">
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
      <tr>
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
		<xsl:attribute name="HREF">Tags?tagAsked=<xsl:value-of select="@id"/></xsl:attribute>
		<img width="100px">
		  <xsl:attribute name="src">Images?mode=PETIT&amp;id=<xsl:value-of select="@picture"/></xsl:attribute>
		</img>
	      </a>
	    </xsl:if>
	  </td>
	</tr>
	<tr>
	  <td>
	    <center>
	      <a target="_top">
		<xsl:attribute name="HREF">Tags?tagAsked=<xsl:value-of select="@id"/></xsl:attribute>
		<xsl:value-of select="."/>
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
        <b><xsl:value-of select="@year"/></b>
        <table>
            <xsl:apply-templates select="album" />
        </table>
     </div>
 </xsl:template>

 <xsl:template match="/webAlbums/albums/top">
   <table>
     <tr>
       <xsl:apply-templates select="album" />
     </tr>
   </table>
 </xsl:template>

 <xsl:template match="top/album">
   <td>
      <table>
	<tr>
	  <td>
	    <center>
	      <xsl:if test="@picture">
		<a target="_top">
		  <xsl:attribute name="href">
		    Photos?albmCount=<xsl:value-of select="position()"/>&amp;album=<xsl:value-of select="@id"/>
		  </xsl:attribute>
		  <img width="100px">
		    <xsl:attribute name="src">Images?mode=PETIT&amp;id=<xsl:value-of select="@picture"/></xsl:attribute>
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
		  Photos?albumCount=<xsl:value-of select="position()"/>&amp;album=<xsl:value-of select="@id"/>
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
	<script type="text/javascript" src="static/scripts/wz_tooltip.js"></script>
	<script type="text/javascript" src="static/scripts/tools.js"/>	  
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
		<a title="Retour à la normal">
		  <xsl:attribute name="href">
		    <xsl:if test="/webAlbums/photos">
		      Photos?
albmCount=<xsl:value-of select="album/@count" />
&amp;album=<xsl:value-of select="album/@id" />
&amp;page=<xsl:value-of select="photoList/page/@current"/>
		    </xsl:if>
		    <xsl:if test="/webAlbums/tags">
		      Tags?
<xsl:for-each select="title/tagList/*">
&amp;tagAsked=<xsl:value-of select="@id"/>
</xsl:for-each>
&amp;page=<xsl:value-of select="photoList/page/@current"/>
		    </xsl:if>
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
      <a href="#">
	<xsl:attribute name="onClick">
	  javascript:updateFullImage('<xsl:value-of select="details/photoId"/>')
	</xsl:attribute>
	<img height="200px">
	  <xsl:attribute name="SRC">
	    Images?id=<xsl:value-of select="details/photoId"/>&amp;mode=PETIT
	  </xsl:attribute>
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
&amp;page=<xsl:value-of select="." />
&amp;special=VISIONNEUSE
	  </xsl:if>
	  <xsl:if test="/webAlbums/tags">
	    Tags?
&amp;tagAsked=<xsl:value-of select="../url/tagAsked"/>
&amp;page=<xsl:value-of select="."/>
&amp;special=VISIONNEUSE
	  </xsl:if>
          </xsl:attribute><xsl:if test="name(.) = 'nexti'">
              »
          </xsl:if>
          <xsl:if test="name(.) = 'previ'">
              «
          </xsl:if>
          <xsl:if test="not(name(.) = 'nexti') and not (name(.) = 'previ')">
              <xsl:value-of select="." />
          </xsl:if>
      </a>
    </td>
  </xsl:template>

</xsl:stylesheet>
