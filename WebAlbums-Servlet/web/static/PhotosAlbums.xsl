<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template name="for-stars-loop">
        <xsl:param name="count" select="1"/>
        <xsl:param name="stars"/>
        <xsl:param name="photoId"/>
        
        <xsl:if test="$count > 0">
            <xsl:call-template name="for-stars-loop">
                <xsl:with-param name="count" select="$count - 1"/>
                <xsl:with-param name="stars" select="$stars"/>
                <xsl:with-param name="photoId" select="$photoId"/>
            </xsl:call-template>
            <img src="static/images/void.png">
                <xsl:attribute name="rel">
                    <xsl:value-of select="$photoId" />/<xsl:value-of select="$count" />
                </xsl:attribute>
                <xsl:if test="$count > $stars">
                    <xsl:attribute name="class">fastedit_stars star_off</xsl:attribute>
                </xsl:if>
                <xsl:if test="not($count > $stars)">
                    <xsl:attribute name="class">fastedit_stars star_on</xsl:attribute>
                </xsl:if>
            </img>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="details">
        <xsl:if test="../exif">
            <span class="exif_container">
                <span class="exif_tooltip">
                    <xsl:attribute name="id">exif-content-<xsl:value-of select="photoId/@id" /></xsl:attribute>
                    <xsl:apply-templates select="../exif" />
                </span>
            </span>
        </xsl:if>
        <div class="details">
            <div class="pict">
                <a>
                    <xsl:if test="/webAlbums/photos or /webAlbums/tags">
                        <!-- placeholder for a box-related property -->
                    </xsl:if>
                    <xsl:if test="/webAlbums/photos or /webAlbums/tags">
                        <xsl:if test="/webAlbums/affichage/@directAccess">
                            <xsl:attribute name="href"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/photo_folder" /><xsl:value-of select="photoId/text()" /></xsl:attribute>
                        </xsl:if>
                        <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                            <xsl:attribute name="href">Image__<xsl:value-of select="photoId/@id" /></xsl:attribute>
                        </xsl:if>
                    </xsl:if>
                    
                    <xsl:if test="/webAlbums/tags or /webAlbums/photos/random">
                        <xsl:attribute name="title"><xsl:value-of select="date/@date" />&#160;<xsl:value-of select="albumName" /></xsl:attribute>
                    </xsl:if>
                    
                    <xsl:if test="/webAlbums/albums">
                        <xsl:attribute name="href">Photos__<xsl:value-of select="../@id" />_p0<xsl:if test="not(/webAlbums/affichage/@static)">_pa<xsl:value-of select="/webAlbums/albums/display/albumList/page/@current" /></xsl:if>__<xsl:value-of select="../name" /></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="/webAlbums/carnets">
                        <xsl:attribute name="href">Carnet__<xsl:value-of select="../@id" /><xsl:if test="not(/webAlbums/affichage/@static)">_pc<xsl:value-of select="/webAlbums/carnets/display/page/@current" /></xsl:if>__<xsl:value-of select="../name" /></xsl:attribute>
                    </xsl:if>
                    <img class="photo">
                        <xsl:attribute name="alt">
                            <xsl:value-of select="name" />
                        </xsl:attribute>
                        <xsl:if test="@isGpx = 'true'">
                            <xsl:attribute name="src">static/images/gps.png</xsl:attribute>
                        </xsl:if>
                        <xsl:if test="not(@isGpx = 'true')">
                            <xsl:if test="not(photoId)">
                                <xsl:attribute name="src">static/images/rien.jpg</xsl:attribute>
                            </xsl:if>
                            <xsl:if test="photoId">
                                <xsl:if test="/webAlbums/affichage/@directAccess">
                                    <xsl:attribute name="src"><xsl:value-of select="$RootPath" /><xsl:value-of select="/webAlbums/affichage/mini_folder" /><xsl:value-of select="photoId/text()" />.png</xsl:attribute>
                                </xsl:if>
                                <xsl:if test="not(/webAlbums/affichage/@directAccess)">
                                    <xsl:attribute name="src">Miniature__<xsl:value-of select="photoId/@id" />.png</xsl:attribute>
                                </xsl:if>
                            </xsl:if>
                        </xsl:if>
                    </img>
                </a>
            </div>
                <xsl:if test="not(/webAlbums/albums or /webAlbums/carnets)">
                    <div class="stars">
                        <xsl:attribute name="id">stars_<xsl:value-of select="photoId/@id" /></xsl:attribute>
                        <xsl:call-template name="for-stars-loop">
                            <xsl:with-param name="count" select="5"/>
                            <xsl:with-param name="stars" select="@stars"/>
                            <xsl:with-param name="photoId" select="photoId/@id" />
                        </xsl:call-template>
                        <span>
                            <xsl:attribute name="id">stars_<xsl:value-of select="photoId/@id" />_message</xsl:attribute>
                        </span>
                    </div>
                </xsl:if>
                <div class="options">
                    <xsl:if test="/webAlbums/tags or /webAlbums/photos/random">
                        <div>
                            <xsl:value-of select="albumDate"/>
                            <span>&#160;</span>
                            <a class="albumTT">
                                <xsl:attribute name="title">
                                    <xsl:value-of select="albumName"/>
                                </xsl:attribute>
                                <xsl:attribute name="id">album-target-<xsl:value-of select="photoId/@id"/></xsl:attribute>
                                <xsl:attribute name="href">Photos__<xsl:value-of select="@albumId" />_p0__<xsl:value-of select="albumName" /></xsl:attribute>
                                <xsl:value-of select="albumName"/>
                            </a>
                        </div>
                        <span class="album_tooltip">
                            <xsl:attribute name="id">album-content-<xsl:value-of select="photoId/@id"/></xsl:attribute>
                            <xsl:attribute name="rel"><xsl:value-of select="@albumId"/></xsl:attribute>
			    (doesn't work yet...)
                        </span>
                    </xsl:if>
                    <xsl:apply-templates select="tagList">
                        <xsl:with-param name="mode">TAG_USED</xsl:with-param>
                        <xsl:with-param name="style">
                            <xsl:if test="not(/webAlbums/albums or /webAlbums/carnets)">none</xsl:if>
                            <xsl:if test="/webAlbums/albums or /webAlbums/carnets">enhanced</xsl:if>
                        </xsl:with-param>
                        <xsl:with-param name="incMinor">true</xsl:with-param>
                        <xsl:with-param name="setRel">true</xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:if test="not(tagList/*)"><div class="tags"/></xsl:if>
                    <xsl:if test="/webAlbums/loginInfo/@admin">
                        <span class="edit">
                            <div class="fastedit">
                                <xsl:attribute name="id">fastedit_div_tag_<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                <p>
                                    <xsl:apply-templates select="../../massEdit/tagList">
                                        <xsl:with-param name="name">tagSet</xsl:with-param>
                                        <xsl:with-param name="style">multiple</xsl:with-param>
                                        <xsl:with-param name="id">fastedit_tag_<xsl:value-of select="photoId/@id" /></xsl:with-param>
                                        <xsl:with-param name="mode">TAG_USED</xsl:with-param>
                                        <xsl:with-param name="mode2">TAG_NEVER_EVER</xsl:with-param>
					<xsl:with-param name="class">fastedit_tag</xsl:with-param>
                                        <xsl:with-param name="incMinor">true</xsl:with-param>
                                    </xsl:apply-templates>
                                    <input value="set" type="button" class="fastedit_settags">
                                        <xsl:attribute name="rel"><xsl:value-of select="photoId/@id" /></xsl:attribute>
                                    </input>
                                </p>
                            </div>
                        </span>
                    </xsl:if>
                    <div class="description">
                        <xsl:attribute name="id">desc_<xsl:value-of select="photoId/@id" /></xsl:attribute>
                        <xsl:for-each select="description/line"><p><xsl:value-of select="." /></p></xsl:for-each>
                    </div>
                    <xsl:if test="/webAlbums/loginInfo/@admin">
                        <span class="edit">
                            <div class="fastedit">
                                <xsl:attribute name="id">fastedit_div_desc_<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                <p>
                                    <textarea cols="30" >
                                        <xsl:attribute name="id">fastedit_desc_<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                        <xsl:for-each select="description/line"><xsl:value-of select="." /><xsl:text>&#10;</xsl:text></xsl:for-each>
                                    </textarea>
                                    <input value="edit" type="button" class="fastedit_desc">
                                        <xsl:attribute name="rel"><xsl:value-of select="photoId/@id" /></xsl:attribute>
                                    </input>
                                </p>
                            </div>
                        </span>
                    </xsl:if>
                    <xsl:if test="../author">
                        <div class="author_opt">
                            <span>By:</span>
                            <a>
                                <xsl:attribute name="href">Tag__<xsl:value-of select="../author/@id"/>__<xsl:value-of select="../author/name"/></xsl:attribute>
                                <xsl:if test="../author/contact">
                                    <xsl:attribute name="title"><xsl:value-of select="../author/contact"/></xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="../author/name"/>
                            </a>
                        </div>
                    </xsl:if>
                    <xsl:if test="../carnet">
                        <div class="carnets_opt">
                            <xsl:apply-templates select="../carnet"/>
                        </div>
                    </xsl:if>
                    <xsl:if test="../../carnet">
                        <hr/>
                        <xsl:apply-templates select="tagTree"/>
                        <div id="carnet_map">
                            <xsl:attribute name="rel"><xsl:value-of select="../@id"/></xsl:attribute>
                        </div>
                    </xsl:if>
                    <div>
                        <span class="edit">
                            <xsl:if test="/webAlbums/loginInfo/@admin and not(/webAlbums/albums or /webAlbums/photos/random or /webAlbums/carnets)">
                                <input type="checkbox" class="massedit_chk massedit_chkbox" value="modif">
                                    <xsl:attribute name="name">chk<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                    <xsl:attribute name="id">chk<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                </input>
                            </xsl:if>
                            <label>
                                <xsl:attribute name="for">chk<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                <span>@</span>
                                <xsl:if test="/webAlbums/albums">
                                    <xsl:value-of select="../@id" />
                                </xsl:if>
                                <xsl:if test="not(/webAlbums/albums)">
                                    <xsl:value-of select="photoId/@id" />
                                </xsl:if>
                            </label>
                        </span>
                        <xsl:if test="/webAlbums/loginInfo/@admin">
                            <xsl:apply-templates select="user" />
                        </xsl:if>
                    </div>
                    <xsl:if test="../gpx">
                        <img src="static/images/map.png" height="30px" title="Trace GPX"/>
                    </xsl:if>
                    <span class="optional">
                        <xsl:if test="not(/webAlbums/carnets or /webAlbums/photos/random)">
                            <a rel="singlepage[no]" target="_blank" title="Visionneuse">
                                <xsl:if test="/webAlbums/albums">
                                    <xsl:attribute name="href">Visio__<xsl:value-of select="../@id" />_p0__<xsl:value-of select="../name" /></xsl:attribute>
                                </xsl:if>
                                <xsl:if test="/webAlbums/photos">
                                    <xsl:attribute name="href">Visio__<xsl:value-of select="../../../album/@id" />_p<xsl:value-of select="/webAlbums/photos/display/photoList/page/@current" /><xsl:if test="not(/webAlbums/affichage/@static)">_pa</xsl:if>__<xsl:value-of select="../../../album/name" />#<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                </xsl:if>
                                <xsl:if test="/webAlbums/tags">
                                    <xsl:attribute name="href">Tags?<xsl:for-each select="/webAlbums/tags/display/title/tagList/*">&amp;tagAsked=<xsl:value-of select="@id" /></xsl:for-each>&amp;page=<xsl:value-of select="/webAlbums/tags/display/photoList/page/@current"/>&amp;special=VISIONNEUSE#<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                </xsl:if>
                                <img src="static/images/slide.png" height="30px"/>
                            </a>
                        </xsl:if>
                        <xsl:if test="not(/webAlbums/albums or /webAlbums/carnets)">
                            <xsl:if test="not(/webAlbums/affichage/@static)">
                                <a title="Photo r�duite">
                                    <!-- no url rewritting -->
                                    <xsl:attribute name="href">Images?id=<xsl:value-of select="photoId/@id" />&amp;mode=SHRINK&amp;width=800&amp;borderWidth=10&amp;borderColor=white</xsl:attribute>
                                    <img src="static/images/reduire.gif" width="30px"/>
                                </a>
                            </xsl:if>
                            <xsl:if test="not(/webAlbums/albums or /webAlbums/carnets or /webAlbums/photos/random)">
                                <span class="exif"><xsl:attribute name="id">exif-target-<xsl:value-of select="photoId/@id" /></xsl:attribute>
                                    <img src="static/images/info.png" width="30px"/>
                                </span>
                            </xsl:if>
                        </xsl:if>
                        <xsl:if test="/webAlbums/loginInfo/@admin">
                            <a class="edit" title="Edition" rel="singlepage[no]">
                              <!-- no url rewritting -->
                                <xsl:if test="/webAlbums/photos">
                                    <xsl:attribute name="href">Photos?action=EDIT&amp;id=<xsl:value-of select="photoId/@id" />&amp;page=<xsl:value-of select="//*/page/@current"/>&amp;albmPage=<xsl:value-of select="/webAlbums/photos/display/photoList/page/url/albmPage" />&amp;album=<xsl:value-of select="../../../album/@id"	/></xsl:attribute>
                                </xsl:if>
                                <xsl:if test="/webAlbums/tags">
                                    <xsl:attribute name="href">Tags?action=EDIT&amp;id=<xsl:value-of select="photoId/@id" /><xsl:for-each select="/webAlbums/tags/display/title/tagList/*">&amp;tagAsked=<xsl:value-of select="@id" /></xsl:for-each>&amp;page=<xsl:value-of select="//*/page/@current" /></xsl:attribute>
                                </xsl:if>
                                <xsl:if test="/webAlbums/albums">
                                    <xsl:attribute name="href">Albums?action=EDIT&amp;id=<xsl:value-of select="../@id" />&amp;page=<xsl:value-of select="//*/page/@current" /></xsl:attribute>
                                </xsl:if>
                                <xsl:if test="/webAlbums/carnets">
                                    <xsl:attribute name="href">Carnets?action=EDIT&amp;carnet=<xsl:value-of select="../@id" />&amp;page=<xsl:value-of select="../@carnetsPage"/></xsl:attribute>
                                </xsl:if>
                                <img src="static/images/edit.png" height="30px"/>
                            </a>
                            <xsl:if test="/webAlbums/loginInfo/@admin and not(/webAlbums/albums or /webAlbums/photos/random or /webAlbums/carnets)">
                                <div class="fastedit_bt fastedit_desc_bt edit">
                                    <xsl:attribute name="rel"><xsl:value-of select="photoId/@id" /></xsl:attribute>
                                    <span>Descr</span>
                                </div>
                                <div class="fastedit_bt edit">&#160;||&#160;</div>
                                <div class="fastedit_bt fastedit_tag_bt edit">
                                    <xsl:attribute name="rel"><xsl:value-of select="photoId/@id" /></xsl:attribute>
                                    <span>Tags</span>
                                </div>
                            </xsl:if>
                        </xsl:if>
                    </span>
                </div>
            </div>
        
    </xsl:template>
  
    <xsl:template match="tagTree//tag">
        <br/>
        <b>Places: </b><span>&#160;<xsl:value-of select="name" /></span>
        <xsl:apply-templates select="children/tag"/>
    </xsl:template>
  
    <xsl:template match="tagTree">
        <p>
            <div id="carnetRegion">&#160;<xsl:value-of select="name" /></div>
            <!--<xsl:apply-templates select="children/tag"/>-->
        </p>
    </xsl:template>
  
    <xsl:template match="rights">
        <xsl:param name="id"></xsl:param>
        <xsl:param name="default">-1</xsl:param>
        <select name="user">
            <xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
            <xsl:apply-templates select="user">
                <xsl:with-param name="default">
                    <xsl:value-of select="$default" />
                </xsl:with-param>    
            </xsl:apply-templates>
        </select>
    </xsl:template>
    
    <xsl:template match="rights/user">
        <xsl:param name="default"></xsl:param>
        <option>
            <xsl:attribute name="value"><xsl:value-of select="@id" /></xsl:attribute>
            <xsl:if test="@selected"><xsl:attribute name="selected">true</xsl:attribute></xsl:if>
            <xsl:if test="not(@selected) and $default = @id">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="." />
        </option>
    </xsl:template>
  
    <xsl:template match="user">
        <div class="visibility edit">
            <xsl:if test="/webAlbums/albums">
                <xsl:value-of select="."/>
                <xsl:apply-templates select="../userInside"/>
            </xsl:if>
            <xsl:if test="/webAlbums/photos and not(@album)">
                <xsl:if test="@outside = 'true'">[</xsl:if>
                <xsl:value-of select="."/>
                <xsl:if test="@outside  = 'true'">]</xsl:if>
            </xsl:if>
            <xsl:if test="/webAlbums/tags and @album">
                [<xsl:value-of select="."/>]
            </xsl:if>
            <xsl:if test="/webAlbums/tags and not(@album)">
                <xsl:value-of select="."/>
            </xsl:if>
        </div>
    </xsl:template>

    <xsl:template match="userInside">(<xsl:value-of select="."/>)</xsl:template>
</xsl:stylesheet>
