<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>
  <xsl:include href="Include.xsl" />
  <xsl:template match="/">
    <html>
      <head>
	<xsl:apply-templates select="/webAlbums/login/valid"/>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<link rel="shortcut icon" href="static/favicon.png" type="image/png" /> 
	<title>WebAlbums 3 : <xsl:value-of select="/webAlbums/loginInfo/theme" /> - <xsl:value-of select="/webAlbums/loginInfo/user"/></title>

	<meta name="keywords" content="" />
	<meta name="description" content="" />
        <link href="static/design.css"     rel="stylesheet" type="text/css" media="screen" />
        <link href="static/style.css"     rel="stylesheet" type="text/css" media="screen" />
        <link href="static/pagination.css" rel="stylesheet" type="text/css" media="screen" />
        <style type="text/css">body {<xsl:if test="not(/webAlbums/affichage/@background)">background: #62993B url(static/images/back_all.jpg) fixed no-repeat;</xsl:if><xsl:if test="/webAlbums/affichage/@background"     >background: #62993B url(background<xsl:if test="/webAlbums/affichage/@static">__<xsl:value-of select="/webAlbums/loginInfo/themeid" />__<xsl:value-of select="/webAlbums/loginInfo/theme" /></xsl:if>.jpg) fixed no-repeat;</xsl:if>}</style>
      </head>
      <body>
        <script type="text/javascript"> var directAccess = false; <xsl:if test="/webAlbums/affichage/@directAccess"> directAccess = true; root_path = "<xsl:value-of select="$RootPath" />"; photo_folder = "<xsl:value-of select="/webAlbums/affichage/photo_folder" />"; mini_folder = "<xsl:value-of select="/webAlbums/affichage/mini_folder" />"; </xsl:if> var staticAccess = false; <xsl:if test="/webAlbums/affichage/@static"> staticAccess = true ;</xsl:if></script>
        <script type="text/javascript" src="static/scripts/lib/jquery/js/jquery.js"/>
        <script type="text/javascript" src="static/scripts/lib/jquery/js/jquery-ui.js"/>
        <script type="text/javascript" src="static/scripts/lib/jquery/js/jquery-cookie.js"/>
        <script type="text/javascript" src="static/scripts/lib/jquery/js/jquery.ezpz_tooltip.js"/>
        
	<div id="header"> 
	  <div id="logo">
	    <h1>WebAlbums 3</h1>
	    <h2>by Kevin POUGET</h2>
	  </div>
	  
	  <div id="menu">
	    <ul>
	      <li><a href="Index" title="Retour aux thèmes">Thème</a></li>
	      <li>
                <a title="Choix"><xsl:attribute name="href">Choix<xsl:if test="/webAlbums/affichage/@static">__<xsl:value-of select="/webAlbums/loginInfo/themeid" />__<xsl:value-of select="/webAlbums/loginInfo/theme" /></xsl:if></xsl:attribute>Choix</a>
              </li>
	    </ul>	
	  </div>
	</div>
	<div id="main">
	  <div id="top">
	    <div id="bottom">
	      <div id="right">
		<h3>Affichage</h3>
		<ul>
		  <li>Exif: <a id="mode_details" title="">not set</a></li>
                    <xsl:if test="/webAlbums/loginInfo/@admin">
                        <li>Mode: <a id="mode_edition" title="">not set</a></li>
                    </xsl:if>
                    <li>&#160;</li>
                  <li><h>Qualité: </h>
                  <ul>
                  <li><span id="qos_stars"/></li>
                  <li>Only ? <input id="qos_stars_only" value="1" type="checkbox"/></li>
                  </ul> </li>
                  <xsl:if test="not(/webAlbums/affichage/@static)">
                      <li><span>Items par page:</span>
                        <ul>
                          <li>
                            <select id="nbPhotoAlbum">
                                <option>
                                    <xsl:attribute name="src"><xsl:value-of select="/webAlbums/affichage/@photoAlbumSize" /></xsl:attribute>
                                    <xsl:value-of select="/webAlbums/affichage/@photoAlbumSize" />
                                </option>
                                <optgroup label="---">
                                  <option value="10">10</option>
                                  <option value="15">15</option>
                                  <option value="20">20</option>
                                  <option>25</option>
                                  <option value="30">30</option>
                                </optgroup>
                            </select>
                          </li>
                        </ul>
                      </li>
                  </xsl:if>
                </ul>
		  
		<h3>Connexion</h3>
		<ul>
		  <li><xsl:value-of select="/webAlbums/loginInfo/user" /></li>
                  <li><xsl:value-of select="/webAlbums/loginInfo/theme" /></li>
		  <li><a href="Index?logout=TRUE" title="logout" rel="singlepage[no]">Log out</a></li>
                  <xsl:if test="/webAlbums/affichage/@static"><li>Static mode</li></xsl:if>
                  <xsl:if test="/webAlbums/affichage/@directAccess"><li>Direct Access to images</li></xsl:if>
                  <li>Page générée en <br/><small id="gen_time"><xsl:value-of select="/webAlbums/time"/></small>.</li>
		</ul>

                <xsl:if test="/webAlbums/loginInfo/@admin">
                    <h3>Administration</h3>
                    <ul>
                        <li>
                            <a href="Config" rel="singlepage[no]" title="Configuration">Configuration</a>
                        </li>
                        <li>
                            <a href="Database" rel="singlepage[no]" title="Database">Database</a>
                        </li>
                    </ul>
                </xsl:if>
		<div id="cloud">
                    <xsl:apply-templates select="/webAlbums/choix/cloud"/>
                </div>
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
                
                <script type="text/javascript" src="static/scripts/tools.js"/>
                
                <xsl:if test="/webAlbums/photos and /webAlbums/photos/display/album/gpx or /webAlbums/choix or webAlbums/config">
                    <!--<script src="http://maps.google.com/maps/api/js?sensor=false"></script>-->
                    <script src="http://openlayers.org/api/OpenLayers.js"></script>
                    <!--<script src="static/scripts/lib/OpenLayers.js"/>-->
                    <script src="http://www.openstreetmap.org/openlayers/OpenStreetMap.js"></script>
                    <script src="static/scripts/OpenLayerFunctions.js"/>
                </xsl:if>
                
                <xsl:if test="/webAlbums/photos or /webAlbums/tags">
                    <script type="text/javascript" src="static/scripts/Photos.js"/>
                </xsl:if>
                <xsl:if test="/webAlbums/tags">
                    <script type="text/javascript" src="static/scripts/Tags.js"/>
                </xsl:if>
                <xsl:if test="/webAlbums/config">
                    <script type="text/javascript" src="static/scripts/Config.js"/>
                </xsl:if>
                <xsl:if test="/webAlbums/choix">
                    <link type="text/css" href="static/scripts/lib/jquery/css/smoothness/jquery-ui.css" rel="stylesheet" media="screen"/>
                    <script type="text/javascript" src="static/scripts/lib/raphael.js"/>
                    <script type="text/javascript" src="static/scripts/lib/morris.js"/>
                    <script src="static/scripts/Choix.js" type='text/javascript'/>
                </xsl:if>
	      </div>
	      <div id="footer">
                 <p>Copyright 2009, 2010, 2011, 2012</p>
                 <p>Design by <a href="http://www.metamorphozis.com/" title="Metamorphozis Design">Flash Templates</a></p>
              </div>
	    </div>
	  </div>
	</div>
        <xsl:if test="not(/webAlbums/affichage/@static)">
            <script type="text/javascript" src="static/scripts/SinglePageInterface.js" />
        </xsl:if>
        <script type="text/javascript" src="static/scripts/Common.js"/>
      </body>
    </html>
  </xsl:template>

  <xsl:include href="Index.xsl" />
  <xsl:include href="UserLogin.xsl" />
  <xsl:include href="Choix.xsl" />
  <xsl:include href="ChoixWidgets.xsl" />
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
