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
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
	<xsl:apply-templates select="/root/userLogin/valid"/>
	
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<title>WebAlbums 3 : <xsl:value-of select="/root/login/theme" />  (<xsl:value-of select="/root/login/user" />)</title>

	<meta name="keywords" content="" />
	<meta name="description" content="" />
        <link href="static/styles.css"     rel="stylesheet" type="text/css" media="screen" />
        <link href="static/pagination.css" rel="stylesheet" type="text/css" media="screen" />
        <style type="text/css">          body {
             <xsl:if test="count(/root/affichage/background) = 0">background:   #62993B url(static/images/back_all.jpg) fixed no-repeat;</xsl:if>
             <xsl:if test="count(/root/affichage/background) != 0" >background: #62993B url(Images?mode=BACKGROUND) fixed no-repeat;</xsl:if>
          }
        </style>s
        <script type="text/javascript" src="static/scripts/jquery/js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="static/scripts/jquery/js/jquery-ui-1.8.4.custom.min.js"></script>

        <script src="static/scripts/tools.js" type="text/javascript" />

        <link rel="stylesheet" type="text/css" href="static/scripts/shadowbox/shadowbox.css" />
        <script type="text/javascript"          src="static/scripts/shadowbox/shadowbox.js" />
        <script type="text/javascript">
            Shadowbox.init({
                handleUnsupported:  "remove",
                modal:     true
            });
        </script>
        
        <xsl:if test="count(/root/photos) != 0 or count(/root/tags) != 0 ">
            <script type="text/javascript" src="static/scripts/Photos.js"></script>
            <xsl:if test="/root/photos/photo/exif or /root/tags/photo/exif">
                <script type="text/javascript" src="static/scripts/wz_tooltip.js"></script>
            </xsl:if>
        </xsl:if>
        <xsl:if test="count(/root/tags) != 0">
            <script type="text/javascript" src="static/scripts/Tags.js"></script>
        </xsl:if>
	<xsl:if test="count(/root/config) != 0">
            <script type="text/javascript" src="static/scripts/Config.js"/>
        </xsl:if>
        
        <xsl:if test="count(/root/choix) != 0">
            <script type="text/javascript" src="static/scripts/wz_tooltip.js"></script>
            <link type="text/css" href="static/scripts/jquery/css/ui-lightness/jquery-ui-1.8.4.custom.css" rel="stylesheet" media="screen"/>
	    <script src="Choix?special=map.js" type='text/javascript'></script>
            <script src="static/scripts/Choix.js" type='text/javascript'></script>
        </xsl:if>
      </head>
      <body>
	<div id="header"> 
	  <div id="logo">
	    <h1>WebAlbums 3</h1>
	    <h2>by Kevin POUGET</h2>
	  </div>
	  
	  <div id="menu">
	    <ul>
	      <li><a href="Index" title="Retour aux thèmes">Thème</a></li>
	      <li><a href="Choix" title="Choix">Choix</a></li>
	    </ul>	
	  </div>
	</div>
	<div id="main">
	  <div id="top">
	    <div id="bottom">
	      <div id="right">
		<h3>Affichage</h3>
		<ul>
		  <li><a href="javascript:updateAffichage('maps')"  title=""><xsl:value-of select="/root/affichage/maps" /></a></li>
		  <li><a href="javascript:updateAffichage('details');" title=""><xsl:choose>
			<xsl:when test="/root/affichage/details = 'false'">Sans Détails</xsl:when>
			<xsl:when test="not(/root/affichage/details = 'false')">Avec Détails</xsl:when>
		      </xsl:choose> </a></li>
		  </ul>

		<h3>Connexion</h3>
		<ul>
		  <li><xsl:value-of select="/root/login/user" /></li>
                  <li><xsl:value-of select="/root/login/theme" /></li>
		  <li><a href="Index?logout=TRUE" title="logout">Log out</a></li>
		</ul>
                <xsl:if test="count(/root/login/admin)!=0">
                    <h3>Administration</h3>
                    <ul>
                        <li>
                            <a href="Config" rel="shadowbox" title="Configuration (box)">Config</a>
                            <a href="Config" title="Configuration (new page)">uration</a>
                        </li>
                        <li><a href="javascript:updateAffichage('edition');" title=""><xsl:value-of select="/root/affichage/edition" /></a></li>
                    </ul>
                </xsl:if>
		<h3>Nuage de tags <input id="cloudLoader" type="button" value="load cloud" onclick="loadCloud();"/></h3>
		<div id="cloud">
		  <!--<img src="static/images/loading.gif"/>-->
		</div>
	      </div>
	      <div id="left">
		<xsl:apply-templates select="/root/Exception"/>
		<xsl:apply-templates select="/root/message"/>

		<xsl:apply-templates select="/root/index"/>
		<xsl:apply-templates select="/root/userLogin"/>
		<xsl:apply-templates select="/root/choix"/>
		<xsl:apply-templates select="/root/albums"/>
		<xsl:apply-templates select="/root/photos"/>
		<xsl:apply-templates select="/root/tags"/>

		<xsl:apply-templates select="/root/albm_edit"/>
		<xsl:apply-templates select="/root/photo_edit"/>
		<xsl:apply-templates select="/root/config"/>

		<xsl:apply-templates select="/root/*/page"/>
		<xsl:call-template name="print_return_link" />
	      </div>
	      <div id="footer">
                 <p>Page générée en <xsl:value-of select="/root/time"/>s. Copyright 2009.</p>
                 <p>Design by <a href="http://www.metamorphozis.com/" title="Flash Templates">Flash Templates</a></p>
              </div>
	    </div>
	  </div>
	</div>
      </body>
    </html>
  </xsl:template>

  <xsl:include href="Index.xsl" />
  <xsl:include href="UserLogin.xsl" />
  <xsl:include href="Choix.xsl" />
  <xsl:include href="Albums.xsl" />
  <xsl:include href="Photos.xsl" /> 
  <xsl:include href="PhotosAlbums.xsl" />
  <xsl:include href="Tags.xsl" />
  <xsl:include href="Common.xsl" />
 
  <xsl:include href="ModifAlbum.xsl" /> 
  <xsl:include href="ModifPhoto.xsl" /> 
  <xsl:include href="Config.xsl" /> 
 </xsl:stylesheet>
