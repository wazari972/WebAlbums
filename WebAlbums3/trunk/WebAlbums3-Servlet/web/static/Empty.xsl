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
  <xsl:output method="html"/>
  <xsl:template match="/">
    <xsl:apply-templates select="/root/maint" />

    <xsl:apply-templates select="/root/tags/cloud" />
    <xsl:apply-templates select="/root/tags/persons" />
    <xsl:apply-templates select="/root/tags/places" />
    <xsl:if test="not(/root/tags/cloud or /root/tags/persons or
    /root/tags/places)">
      <xsl:apply-templates select="/root/tags" />
    </xsl:if>
    <xsl:apply-templates select="/root/albums" />

    <xsl:apply-templates select="/root/photos" />
  </xsl:template>

  <xsl:template match="/root/tags/cloud">
    <center>
      <xsl:apply-templates select="tag"/>
    </center>
  </xsl:template>

  <xsl:template match="root/albums/select">
    <p>
        <label for="fromDate">Albums datés entre le </label>
        <span id="fromDate" style="font-weight:bold;" />
        <label for="toDate"> et le </label>
        <span id="toDate" style="font-weight:bold;" />
    </p>
    <br/>
    <div id="slider-range"></div>
    <br/>
    <script>
     $("#slider-range").slider(sliderOption);
     $("#fromDate").text(printDate(<xsl:value-of select="album[last()]/time"/>));
     $("#toDate").text(printDate(<xsl:value-of select="album/time"/>));
     $("#slider-range").slider( "option", "max", <xsl:value-of select="album/time"/>+$( "#slider-range" ).slider( "option", "step" ));
     $("#slider-range").slider( "option", "min", <xsl:value-of select="album[last()]/time"/>);

     $("#slider-range").slider( "option", "values", [<xsl:value-of select="album[last()]/time"/>, <xsl:value-of select="album/time"/>]);
    </script>
    <div style="overflow: auto; height: 400px">
        <ul>
        <xsl:for-each select="album">
            <xsl:sort select="count(ancestor::*)" order="descending"/>
            <li class="selectAlbum">
                <xsl:attribute name="rel"><xsl:value-of select="time"/></xsl:attribute>
                <label><xsl:value-of select="date"/></label> &#160;
                <a>
                    <xsl:attribute name="href">Photos?album=<xsl:value-of select="id"/>&amp;albmCount=<xsl:value-of select="count"/></xsl:attribute>
                    <xsl:value-of select="nom"/>
                </a>
            </li>
        </xsl:for-each>
        </ul>
    </div>
  </xsl:template>

  <xsl:template match="cloud/tag">
    <a> 
      <xsl:attribute name="style">font-size: <xsl:value-of select="@size"/>%;</xsl:attribute>
      <xsl:attribute name="href">Tags?tagAsked=<xsl:value-of select="@id"/></xsl:attribute>
      <xsl:attribute name="title"><xsl:value-of select="."/> : <xsl:value-of select="@nb"/></xsl:attribute>
      <xsl:value-of select="." />
    </a>
    <a>
      <xsl:attribute name="href">Images?id=<xsl:value-of select="@id"/>&amp;mode=RANDOM_TAG</xsl:attribute>
      <xsl:attribute name="title">Random imh</xsl:attribute>
      &#9827;
    </a><br/>
  </xsl:template>

  <xsl:template match="persons|places">
    <table>
      <tr>
	<xsl:apply-templates select="tag" />
      </tr>
    </table>
  </xsl:template>
  <xsl:template match="persons/tag|places/tag">
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

<xsl:template match="/root/albums/years">
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

 <xsl:template match="/root/albums/top5">
   <table>
     <tr>
       <xsl:apply-templates select="album" />
     </tr>
   </table>
 </xsl:template>

 <xsl:template match="album">
   <td>
      <table>
	<tr>
	  <td>
	    <center>
	      <xsl:if test="photo">
		<a target="_top">
		  <xsl:attribute name="href">
		    Photos?albmCount=<xsl:value-of select="position()"/>&amp;album=<xsl:value-of select="id"/>
		  </xsl:attribute>
		  <img width="100px">
		    <xsl:attribute name="src">Images?mode=PETIT&amp;id=<xsl:value-of select="photo"/></xsl:attribute>
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
		  Photos?albumCount=<xsl:value-of select="position()"/>&amp;album=<xsl:value-of select="id"/>
		</xsl:attribute>
		<xsl:value-of select="nom"/>
	      </a>
	    </center>
	  </td>
	</tr>
      </table>
    </td>
  </xsl:template>

  <xsl:template match="/root/maint">
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

  <xsl:template match="/root/photos|/root/tags">
    <html style="margin: 0;padding: 0;height: 100%">
      <body style="margin: 0;padding: 0;height: 100%">
	<script type="text/javascript" src="static/scripts/wz_tooltip.js"></script>
	<script type="text/javascript" src="static/scripts/tools.js"/>	  
	<div style="overflow:auto;">
	  <table>
	    <tr>
	      <xsl:apply-templates select="page/prev"/>
	      <xsl:apply-templates select="photo"/>
	      <xsl:apply-templates select="page/next"/>
	      <td>
		<a title="Retour à la normal">
		  <xsl:attribute name="href">
		    <xsl:if test="/root/photos">
		      Photos?
albmCount=<xsl:value-of select="/root/photos/album/count" />
&amp;album=<xsl:value-of select="/root/photos/album/id" />
&amp;page=<xsl:value-of select="page/current"/>
		    </xsl:if>
		    <xsl:if test="/root/tags">
		      Tags?
<xsl:for-each select="title/tags/*">
&amp;tagAsked=<xsl:value-of select="@id"/>
</xsl:for-each>
&amp;page=<xsl:value-of select="page/current"/>
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
	      Images?id=<xsl:value-of select="photo[1]/details/photoID"/>&amp;mode=GRAND
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
	  javascript:updateFullImage('<xsl:value-of select="details/photoID"/>')
	</xsl:attribute>
	<img height="200px">
	  <xsl:attribute name="SRC">
	    Images?id=<xsl:value-of select="details/photoID"/>&amp;mode=PETIT
	  </xsl:attribute>
	  <xsl:attribute name="onmouseout">
	    UnTip()
	  </xsl:attribute>
	  <xsl:attribute name="onmouseover">
	    TagToTip('tip<xsl:value-of select="details/photoID" />')
	  </xsl:attribute>
	</img>
      </a>
      <span style="display:none">
	<xsl:attribute name="id">tip<xsl:value-of select="details/photoID" /></xsl:attribute>
	<xsl:apply-templates select="details/tags">
	  <xsl:with-param name="style">none</xsl:with-param>
	  <xsl:with-param name="mode">TAG_USED</xsl:with-param>
	  <xsl:with-param name="box">NONE</xsl:with-param>
	</xsl:apply-templates>
	<xsl:value-of select="details/description" />
	<xsl:apply-templates select="exif" />
      </span>
    </td>
  </xsl:template>

  <xsl:template match="prev|next">
    <td>
      <a>
	<xsl:attribute name="href">
	  <xsl:if test="/root/photos">
	    Photos?
&amp;albmCount=<xsl:value-of select="../url/albmCount" />
&amp;album=<xsl:value-of select="../url/album" />
&amp;page=<xsl:value-of select="." />
&amp;special=VISIONNEUSE
	  </xsl:if>
	  <xsl:if test="/root/tags">
	    Tags?
&amp;tagAsked=<xsl:value-of select="../url/tagAsked"/>
&amp;page=<xsl:value-of select="."/>
&amp;special=VISIONNEUSE
	  </xsl:if>
	</xsl:attribute>
	<xsl:value-of select="." />
      </a>
    </td>
  </xsl:template>

</xsl:stylesheet>
