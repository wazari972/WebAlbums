<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="tagList">
    <xsl:param name="name">tagAsked</xsl:param>
    <xsl:param name="style">none</xsl:param>
    <xsl:param name="size">7</xsl:param>
    <xsl:param name="mode">TAG_USED</xsl:param>
    <xsl:param name="mode2">NONE</xsl:param>
    <xsl:param name="type">all</xsl:param>
    <xsl:param name="incMinor">false</xsl:param>
    <xsl:param name="box">MULTIPLE</xsl:param>
    <xsl:param name="onChange"></xsl:param>
    <xsl:param name="id"></xsl:param>
    <xsl:if test="not(@box) or @box = $box">
    <xsl:if test="not(@mode) or @mode = $mode">
      <xsl:if test="who|what|where">            
	<xsl:if test="$style = 'list' or $style = 'multiple'">
	  <select>
	    <xsl:if test="$style = 'multiple'">
	      <xsl:attribute name="multiple">yes</xsl:attribute>
	      <xsl:attribute name="size"><xsl:value-of select="$size" /></xsl:attribute>
	    </xsl:if>
	    <xsl:if test="not($onChange = '')">
	      <xsl:attribute name="onChange"><xsl:value-of select="$onChange" /></xsl:attribute>
	    </xsl:if>
	    <xsl:if test="not($id = '')">
	      <xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
	    </xsl:if>
	    <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>

            <xsl:if test="$style = 'list'">
	      <option value="-1">==========</option>
	    </xsl:if>
            
	    <optgroup label="Who">
                <xsl:apply-templates select="who">
                  <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                  <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
                  <xsl:with-param name="incMinor"><xsl:value-of select="$incMinor" /></xsl:with-param>
                </xsl:apply-templates>
            </optgroup>
            <optgroup label="What">
                <xsl:apply-templates select="what">
                  <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                  <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
                  <xsl:with-param name="incMinor"><xsl:value-of select="$incMinor" /></xsl:with-param>
                </xsl:apply-templates>
            </optgroup>
            <optgroup label="Where">
                <xsl:apply-templates select="where">
                  <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                  <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
                  <xsl:with-param name="incMinor"><xsl:value-of select="$incMinor" /></xsl:with-param>
                </xsl:apply-templates>
            </optgroup>
                <xsl:if test="not($mode2 = 'NONE')">
                    <xsl:if test="not(../tags[@mode = $mode2]/*)">
                        <optgroup>
                            <xsl:attribute name="label">--- <xsl:value-of select="$mode2" /> ---</xsl:attribute>
                            <xsl:apply-templates select="../tagList[@mode = $mode2]/*">
                                <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
                                <xsl:with-param name="incMinor"><xsl:value-of select="$incMinor" /></xsl:with-param>
                            </xsl:apply-templates>
                        </optgroup>
                    </xsl:if>
                </xsl:if>
	  </select>
	</xsl:if>
	<xsl:if test="not($style = 'list' or $style = 'multiple')">
	  <div class="tags">
	    <xsl:apply-templates select="who">
                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
	        <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                <xsl:with-param name="incMinor"><xsl:value-of select="$incMinor" /></xsl:with-param>
	    </xsl:apply-templates>
            <xsl:apply-templates select="author">
                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
	        <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                <xsl:with-param name="incMinor"><xsl:value-of select="$incMinor" /></xsl:with-param>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="what">
                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
	        <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                <xsl:with-param name="incMinor"><xsl:value-of select="$incMinor" /></xsl:with-param>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="where">
                <xsl:with-param name="type"><xsl:value-of select="$type" /></xsl:with-param>
	        <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                <xsl:with-param name="incMinor"><xsl:value-of select="$incMinor" /></xsl:with-param>
	    </xsl:apply-templates>
	  </div>
	</xsl:if>     
      </xsl:if>     
    </xsl:if>
    </xsl:if>
  </xsl:template>
    
  <xsl:template match="where|what|who|author">
    <xsl:param name="type">all</xsl:param>
    <xsl:param name="style">none</xsl:param>
    <xsl:param name="incMinor">false</xsl:param>
    
    <xsl:if test="(@minor and $incMinor = 'true') or not(@minor)">
    <xsl:if test="$type = 'all' or $type = name(.)">
        <xsl:if test="$style = 'list' or $style = 'multiple'">
          <option>
            <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:if test="@checked = 'true'">
              <xsl:attribute name="selected">selected</xsl:attribute>	  
            </xsl:if>
[<xsl:value-of select="name(.)"/>]<xsl:if test="@minor">(</xsl:if><xsl:value-of select="name"/><xsl:if test="@minor">)</xsl:if>
          </option>    
        </xsl:if>      
        <xsl:if test="not($style = 'list' or $style = 'multiple')">
          <a>
            <xsl:attribute name="href">Tag__<xsl:value-of select="@id"/>__<xsl:value-of select="name"/></xsl:attribute>
            <xsl:if test="birthdate">
                <xsl:attribute name="title"><xsl:value-of select="birthdate"/> ans</xsl:attribute>
            </xsl:if>
            <xsl:if test="@minor">(</xsl:if>
            <xsl:value-of select="name"/>
            <xsl:if test="@minor">)</xsl:if>
          </a>
          <xsl:if test="position() != last()">, </xsl:if>
          <xsl:if test="position() = last() and name(.) = 'who' and (count(../what)!= 0 or count(../where) != 0)">, </xsl:if>
          <xsl:if test="position() = last() and name(.) = 'what' and count(../where) != 0">,  </xsl:if>      
        </xsl:if>
        </xsl:if>
    </xsl:if>
  </xsl:template>

  <xsl:template match="exception">
    <b><xsl:value-of select="."/></b><BR/>
  </xsl:template>
  <xsl:template match="message">
    <i><xsl:value-of select="."/></i><BR/>
  </xsl:template>
  
  <xsl:template name="get_validate_addr">
    <xsl:param name="to_add"></xsl:param>
    
    <xsl:if test="../return_to/name = 'Photos'">
<xsl:value-of select="../return_to/name"/>?<xsl:value-of select="$to_add" />
&amp;id=<xsl:value-of select="@id" />
&amp;count=<xsl:value-of select="../return_to/count" />
&amp;album=<xsl:value-of select="../return_to/album" />
&amp;albmCount=<xsl:value-of select="../return_to/albmCount" />
#<xsl:value-of select="@id" />
   </xsl:if>
   <xsl:if test="../return_to/name = 'Tags'">
<xsl:value-of select="../return_to/name"/>?<xsl:value-of select="$to_add" />
&amp;page=<xsl:value-of select="../return_to/page" />
<xsl:for-each select="../return_to/tagsAsked">
&amp;tagAsked=<xsl:value-of select="." />
</xsl:for-each>
#<xsl:value-of select="id" />
   </xsl:if>
 </xsl:template>

  <xsl:template match="page">
    <div class="item">
      <div class="body">
        <div class="pagination">
          <xsl:apply-templates select="@previ"/>
          <xsl:apply-templates select="@first"/>
          <xsl:if test="@first"><span>...</span></xsl:if>
          <xsl:apply-templates select="prev"/>
          <xsl:if test="@first or @last or prev or next"><span class="current"><xsl:value-of select="@current" /> </span></xsl:if>
          <xsl:apply-templates select="next"/>
          <xsl:if test="@last">... </xsl:if>
          <xsl:apply-templates select="@last"/>
          <xsl:apply-templates select="@nexti"/>
        </div>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="prev|next|@nexti|@previ|@first|@last">
    <a>
        <xsl:if test="/webAlbums/photos">
          <xsl:attribute name="href">Photos__<xsl:value-of select="../url/album" />_p<xsl:value-of select="." /><xsl:if test="not(/webAlbums/affichage/@static)">_pa<xsl:value-of select="/webAlbums/photos/display/photoList/page/url/albmPage" /></xsl:if>__<xsl:value-of select="/webAlbums/photos/display/album/title" /></xsl:attribute>
	</xsl:if>
	<xsl:if test="/webAlbums/albums">
            <xsl:attribute name="href">Albums__p<xsl:value-of select="."/></xsl:attribute>
        </xsl:if>
	<xsl:if test="/webAlbums/tags">
            <xsl:attribute name="href">Tag<xsl:for-each select="/webAlbums/tags/display/title/tagList/*">__<xsl:value-of select="@id" /></xsl:for-each><xsl:if test=". != '0'">_p<xsl:value-of select="." /></xsl:if><xsl:for-each select="/webAlbums/tags/display/title/tagList/*">__<xsl:value-of select="name" /></xsl:for-each></xsl:attribute>
        </xsl:if>
        <xsl:if test="name(.) = 'nexti'">Next »</xsl:if>
        <xsl:if test="name(.) = 'previ'">« Previous</xsl:if>
        <xsl:if test="not(name(.) = 'nexti') and not (name(.) = 'previ')"><xsl:value-of select="." /></xsl:if>
    </a>
  </xsl:template>

 <xsl:template name="print_return_link">
    <div class="item">
      <div class="content">   
	<div class="return">
	  <a>
	    <xsl:attribute name="href">
	      <xsl:if test="/webAlbums/photos/display">
		Albums__p<xsl:value-of select="/webAlbums/photos/display/photoList/page/url/albmPage" />#<xsl:value-of select="/webAlbums/photos/display/album/@id" />
	      </xsl:if>
	      <xsl:if test="/webAlbums/albums or /webAlbums/tags or /webAlbums/config">
		Choix<xsl:if test="/webAlbums/affichage/@static">__<xsl:value-of select="/webAlbums/loginInfo/themeid" />__<xsl:value-of select="/webAlbums/loginInfo/theme" /></xsl:if>
	      </xsl:if>
	    </xsl:attribute>
	    <xsl:if test="/webAlbums/photos/display">Retour aux Albums</xsl:if>
	    <xsl:if test="/webAlbums/albums or /webAlbums/tags or /webAlbums/config">Retour au Choix</xsl:if>
	  </a>
	</div>
      </div>
    </div>
 </xsl:template>

 <xsl:template match="exif">
   <table>    
     <xsl:for-each select="./*">
       <tr>
	 <th><xsl:value-of select="name"/></th>
	 <td><xsl:value-of select="value"/></td>
       </tr>
     </xsl:for-each>
   </table>
 </xsl:template>
</xsl:stylesheet>
