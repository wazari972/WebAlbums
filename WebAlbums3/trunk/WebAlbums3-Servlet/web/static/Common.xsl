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
  <xsl:template match="tagList">
    <xsl:param name="name">tagAsked</xsl:param>
    <xsl:param name="style">none</xsl:param>
    <xsl:param name="size">7</xsl:param>
    <xsl:param name="mode">TAG_USED</xsl:param>
    <xsl:param name="mode2">NONE</xsl:param>
    <xsl:param name="box">MULTIPLE</xsl:param>
    <xsl:param name="onChange"></xsl:param>
    <xsl:param name="id"></xsl:param>
    <xsl:if test="not(@box) or @box = $box">
    <xsl:if test="not(@mode) or @mode = $mode">
      <xsl:if test="who|what|where">
	<xsl:if test="$style = 'list' or $style = 'multiple'">
	  <SELECT>
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
                </xsl:apply-templates>
            </optgroup>
            <optgroup label="What">
                <xsl:apply-templates select="what">
                  <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                </xsl:apply-templates>
            </optgroup>
            <optgroup label="Where">
                <xsl:apply-templates select="where">
                  <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                </xsl:apply-templates>
            </optgroup>
                <xsl:if test="not($mode2 = 'NONE')">
                    <xsl:if test="not(../tags[@mode = $mode2]/*)">
                        <optgroup>
                            <xsl:attribute name="label">--- <xsl:value-of select="$mode2" /> ---</xsl:attribute>
                            <xsl:apply-templates select="../tagList[@mode = $mode2]/*">
                                <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
                            </xsl:apply-templates>
                        </optgroup>
                    </xsl:if>
                </xsl:if>
	  </SELECT>
	</xsl:if>
	
	<xsl:if test="not($style = 'list' or $style = 'multiple')">
	  <div class="tags">
	    <xsl:apply-templates select="who">
	      <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="what">
	      <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
	    </xsl:apply-templates>
	    <xsl:apply-templates select="where">
	      <xsl:with-param name="style"><xsl:value-of select="$style" /></xsl:with-param>
	    </xsl:apply-templates>
	  </div>
	</xsl:if>     
      </xsl:if>     
    </xsl:if>
    </xsl:if>
  </xsl:template>
    
  <xsl:template match="where|what|who">
    <xsl:param name="style">none</xsl:param>

    <xsl:if test="$style = 'list' or $style = 'multiple'">
      <OPTION>
	<xsl:attribute name="VALUE"><xsl:value-of select="@id"/></xsl:attribute>
	<xsl:if test="@checked = 'true'">
	  <xsl:attribute name="SELECTED">selected</xsl:attribute>	  
	</xsl:if>

	[<xsl:value-of select="name(.)"/>] <xsl:value-of select="."/>
      </OPTION>    
    </xsl:if>      
    
    <xsl:if test="not($style = 'list' or $style = 'multiple')">
      <A>
	<xsl:attribute name="HREF">Tags?tagAsked=<xsl:value-of select="@id"/></xsl:attribute>
	<xsl:value-of select="."/>
      </A>
      <xsl:if test="position() != last()">, </xsl:if>
      <xsl:if test="position() = last() and name(.) = 'who' and (count(../what)!=0 or count(../where) != 0)">, </xsl:if>
      <xsl:if test="position() = last() and name(.) = 'what' and count(../where) != 0">,  </xsl:if>      
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
   <xsl:if test="return_to/name = 'Tags'">
     <xsl:value-of select="../return_to/name"/>?<xsl:value-of select="$to_add" />
&amp;id=<xsl:value-of select="@id" />
&amp;page=<xsl:value-of select="../return_to/page" />
<xsl:for-each select="../return_to/tagAsked">
&amp;tagAsked=<xsl:value-of select="." />
</xsl:for-each>
#<xsl:value-of select="id" />
   </xsl:if>
 </xsl:template>

  <xsl:template match="page">
    <div class="item">
      <div class="content">
        <center>
          <div class="pagination">
              <xsl:apply-templates select="@previ"/>
              <xsl:apply-templates select="@first"/>
              <xsl:if test="@first"><span>...</span></xsl:if>
              <xsl:apply-templates select="prev"/>
              <span class="current"><xsl:value-of select="@current" /> </span>
              <xsl:apply-templates select="next"/>
              <xsl:if test="@last">... </xsl:if>
              <xsl:apply-templates select="@last"/>
              <xsl:apply-templates select="@nexti"/>
            </div>
        </center>
      </div>
    </div>
    <br/>
    <br/>
  </xsl:template>

  <xsl:template match="prev|next|@nexti|@previ|@first|@last">
    <A>
      <xsl:attribute name="HREF">
	<xsl:if test="/webAlbums/photos">
	  Photos?albmCount=<xsl:value-of select="../url/albmCount" />
&amp;album=<xsl:value-of select="../url/album" />
&amp;page=<xsl:value-of select="." />
	</xsl:if>
	<xsl:if test="/webAlbums/albums">
	  Albums?page=<xsl:value-of select="."/>
	</xsl:if>
	<xsl:if test="/webAlbums/tags">
	  Tags?
<xsl:for-each select="../url/tagAsked">
&amp;tagAsked=<xsl:value-of select="." />
</xsl:for-each>
&amp;page=<xsl:value-of select="." />
	</xsl:if>
      </xsl:attribute>
      <xsl:if test="name(.) = 'nexti'">
          Next »
      </xsl:if>
      <xsl:if test="name(.) = 'previ'">
          « Previous
      </xsl:if>
      <xsl:if test="not(name(.) = 'nexti') and not (name(.) = 'previ')">
          <xsl:value-of select="." />
      </xsl:if>
    </A>
  </xsl:template>

 <xsl:template name="print_return_link">
    <div class="item">
      <div class="content">   
	<CENTER>
	  <A>
	    <xsl:attribute name="HREF">
	      <xsl:if test="/webAlbums/photos/display">
		Albums?count=<xsl:value-of select="/webAlbums/photos/display/album/@count" />#<xsl:value-of select="/webAlbums/photos/display/album/@id" />
	      </xsl:if>
	      <xsl:if test="/webAlbums/albums or /webAlbums/tags or /webAlbums/config">
		Choix
	      </xsl:if>
	    </xsl:attribute>

	    <xsl:if test="/webAlbums/photos/display">
	      Retour aux Albums
	    </xsl:if>
	    <xsl:if test="/webAlbums/albums or /webAlbums/tags or /webAlbums/config">
	      Retour au Choix
	    </xsl:if>
	  </A>
	</CENTER>
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
