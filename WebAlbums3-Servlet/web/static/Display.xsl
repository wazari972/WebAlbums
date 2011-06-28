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
	<xsl:apply-templates select="/webAlbums/login/valid"/>
	<link rel="shortcut icon" href="static/favicon.png" type="image/png" /> 
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<title>WebAlbums 3 : <xsl:value-of select="/webAlbums/loginInfo/theme" /> - <xsl:value-of select="/webAlbums/loginInfo/user"/></title>

	<meta name="keywords" content="" />
	<meta name="description" content="" />
        <link href="static/design.css"     rel="stylesheet" type="text/css" media="screen" />
        <link href="static/style.css"     rel="stylesheet" type="text/css" media="screen" />
        <link href="static/pagination.css" rel="stylesheet" type="text/css" media="screen" />
        <style type="text/css">          body {
             <xsl:if test="not(/webAlbums/affichage/@background)">background: #62993B url(static/images/back_all.jpg) fixed no-repeat;</xsl:if>
             <xsl:if test="/webAlbums/affichage/@background"     >background: #62993B url(Images?mode=BACKGROUND)     fixed no-repeat;</xsl:if>
          }
        </style>
        
      </head>
      <body>
        <script type="text/javascript" src="static/scripts/jquery/js/jquery-1.5.1.min.js"></script>
        <script type="text/javascript" src="static/scripts/jquery/js/jquery-ui-1.8.13.custom.min.js"></script>

        <script type="text/javascript" src="static/scripts/ezpz/jquery.ezpz_tooltip.min.js"></script>

        <link rel="stylesheet" type="text/css" href="static/scripts/shadowbox/shadowbox.css" />
        <script type="text/javascript"          src="static/scripts/shadowbox/shadowbox.js" />
        <script type="text/javascript">
            Shadowbox.init({
                handleUnsupported:  "remove",
                modal:     true
            });
        </script>
        <script type="text/javascript" src="static/scripts/tools.js"/>
        
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
		  <li><a href="javascript:updateAffichage('maps')"  title=""><xsl:value-of select="/webAlbums/affichage/maps" /></a></li>
		  <li><a href="javascript:updateAffichage('details');" title=""><xsl:choose>
			<xsl:when test="/webAlbums/affichage/@details = 'false'">Sans Détails</xsl:when>
			<xsl:when test="not(/webAlbums/affichage/@details = 'false')">Avec Détails</xsl:when>
		      </xsl:choose> </a></li>
		  </ul>

		<h3>Connexion</h3>
		<ul>
		  <li><xsl:value-of select="/webAlbums/loginInfo/user" /></li>
                  <li><xsl:value-of select="/webAlbums/loginInfo/theme" /></li>
		  <li><a href="Index?logout=TRUE" title="logout" rel="singlepage[no]">Log out</a></li>
		</ul>

                <xsl:if test="/webAlbums/loginInfo/@admin">
                    <h3>Administration</h3>
                    <ul>
                        <li>
                            <a href="Config" rel="shadowbox" title="Configuration (box)">Config</a>
                            <a href="Config" rel="singlepage[no]" title="Configuration (new page)">uration</a>
                        </li>
                        <li>
                            <a href="Database" rel="singlepage[no]" title="Database">Database</a>
                        </li>
                        <li class="massedit_toggle">Massedit</li>
                        <li>Mode: <a href="javascript:updateAffichage('edition');" title=""><xsl:value-of select="/webAlbums/affichage/@edition" /></a></li>
                    </ul>
                </xsl:if>
		<div id="cloud" />
	      </div>
              <div id="loader" style="display: none">
                <center><img src="static/images/ajax-loader.gif" /></center>
              </div>
	      <div id="left">
		<xsl:apply-templates select="/webAlbums/Exception"/>
		<xsl:apply-templates select="/webAlbums/message"/>

		<xsl:apply-templates select="/webAlbums/themes"/>
		<xsl:apply-templates select="/webAlbums/login"/>
		<xsl:apply-templates select="/webAlbums/choix"/>
		<xsl:apply-templates select="/webAlbums/albums"/>
		<xsl:apply-templates select="/webAlbums/photos"/>
                <xsl:apply-templates select="/webAlbums/carnets"/>
		<xsl:apply-templates select="/webAlbums/tags"/>
		<xsl:apply-templates select="/webAlbums/config"/>
                <xsl:apply-templates select="/webAlbums/database"/>

		<xsl:apply-templates select="/webAlbums/*/page"/>
		<xsl:call-template name="print_return_link" />
                
                <xsl:if test="/webAlbums/photos or /webAlbums/tags">
                <script type="text/javascript" src="static/scripts/Photos.js"></script>
                </xsl:if>
                <xsl:if test="/webAlbums/tags">
                    <script type="text/javascript" src="static/scripts/Tags.js"></script>
                </xsl:if>
                <xsl:if test="/webAlbums/config">
                    <script type="text/javascript" src="static/scripts/Config.js"/>
                </xsl:if>
                <xsl:if test="/webAlbums/choix">
                    <link type="text/css" href="static/scripts/jquery/css/ui-lightness/jquery-ui-1.8.13.custom.css" rel="stylesheet" media="screen"/>
                    <script src="Choix?special=map.js" type='text/javascript'></script>
                    <script src="static/scripts/Choix.js" type='text/javascript'></script>
                </xsl:if>
                <xsl:if test="/webAlbums/carnets">
                    <script type="text/javascript" src="static/scripts/Carnets.js"></script>
                </xsl:if>
	      </div>
	      <div id="footer">
                 <p>Page générée en <xsl:value-of select="/webAlbums/time"/>s. Copyright 2009.</p>
                 <p>Design by <a href="http://www.metamorphozis.com/" title="Metamorphozis Design">Flash Templates</a></p>
              </div>
	    </div>
	  </div>
	</div>
        <script type="text/javascript" src="static/scripts/SinglePageInterface.js" />
        <script type="text/javascript" src="static/scripts/Common.js"></script>
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
  <xsl:include href="Carnet.xsl" />
 
  <xsl:include href="ModifAlbum.xsl" /> 
  <xsl:include href="ModifPhoto.xsl" /> 
  <xsl:include href="ModifCarnet.xsl" /> 
  <xsl:include href="Config.xsl" /> 
  <xsl:include href="Database.xsl" /> 
 </xsl:stylesheet>
