<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="carnets">
      <xsl:if test="/webAlbums/carnets">
          <link rel="stylesheet" type="text/css" href="static/scripts/lib/pagedown/demo.css" />
          <script type="text/javascript" src="static/scripts/lib/pagedown/Markdown.Converter.js"/>
          <script type="text/javascript" src="static/scripts/lib/pagedown/Markdown.Sanitizer.js"/>
          <script type="text/javascript" src="static/scripts/lib/pagedown/Markdown.Editor.js"/>
      </xsl:if>
      <xsl:apply-templates select="edit"/>
      <xsl:apply-templates select="display"/>
  </xsl:template>

  <xsl:template match="carnets/display">
    <xsl:apply-templates select="exception"/>
    <xsl:apply-templates select="message"/>
    <xsl:if test="not(/webAlbums/carnets/display/carnet/text)">
        <center><a href="Carnets?action=EDIT" rel="singlepage[no]">Nouveau carnet</a></center>
    </xsl:if>
    <xsl:apply-templates select="carnet"/>
    <xsl:apply-templates select="page"/>
    <script type="text/javascript" src="static/scripts/Carnet.js"/>
  </xsl:template>

  <xsl:template match="carnet">
    <div class="item">
      <div class="date">
	<a><xsl:attribute name="id">anchor_<xsl:value-of select="@id" /></xsl:attribute></a>
	<div><xsl:value-of select="date/month" /></div>
	<span><xsl:value-of select="date/day" /></span>
      </div>
      <div class="content">
	<h1>
	  <a>
            <xsl:if test="not(/webAlbums/carnets/display/carnet/text)">
                <xsl:attribute name="href">Carnet__<xsl:value-of select="@id" /><xsl:if test="not(/webAlbums/affichage/@static)">_pc<xsl:value-of select="/webAlbums/carnets/display/page/@current" /></xsl:if>__<xsl:value-of select="name" /></xsl:attribute>
            </xsl:if>
            <xsl:if test="/webAlbums/carnets/display/carnet/text">
                <xsl:attribute name="href">Carnets__p<xsl:value-of select="/webAlbums/carnets/display/carnet/@carnetsPage" />#<xsl:value-of select="@id" /></xsl:attribute>
            </xsl:if>
	    <xsl:value-of select="name" />
	  </a>
	</h1>
	<div class="body">
            <xsl:apply-templates select="message"/>
            <xsl:apply-templates select="details"/>
            <br/>
            <xsl:apply-templates select="text"/>
	</div>
      </div>
    </div>
  </xsl:template>
  
  <xsl:template match="message">
    <i><xsl:value-of select="."/></i><br/>
  </xsl:template>
  
  <xsl:template match="text">
       <br/><span>&#160;</span>
       
       <hr id="carnet_head"/>
       <xsl:if test="/webAlbums/affichage/@directAccess">
        <script type="text/javascript">
            carnet_static_lookup = { <xsl:for-each select="../photo"><xsl:value-of select="@id"/>:"<xsl:value-of select="."/>",</xsl:for-each>}
        </script>
       </xsl:if>
       <div class="carnet_toc"/>
       <br/>
        <div id="carnet_text" class="carnet_text"><xsl:value-of select="."/></div>
        <div class="carnet_toc"/>
  </xsl:template>
</xsl:stylesheet>
