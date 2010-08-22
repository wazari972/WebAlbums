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
  <xsl:template match="/root/choix">
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1><a href="Albums">Tous les albums</a> <input id="albumsLoader" type="button" value="load top5" onclick="loadAlbums();"/></h1>
	<div class="body">
	  <div id="albums" style="overflow:auto;">
	    <center>
	      <img src="static/images/loading.gif"/>
	    </center>
	  </div>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Hall of fame <input id="personsLoader" type="button" value="load persons" onclick="loadPersons();"/></h1>
	<div class="body">
	  <div id="persons" style="overflow:auto">
	    <center>
	      <img src="static/images/loading.gif"/>
	    </center>
	  </div>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Places of the world <input id="placesLoader" type="button" value="load places" onclick="loadPlaces();"/></h1>
	<div class="body">
	  <div id="places" style="overflow:auto">
	    <center>
	      <img src="static/images/loading.gif"/>
	    </center>
	  </div>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<form action="Tags">
	  <h1>Choix par Tags <input type="submit" value="OK"/></h1>
	  <div class="body">
	    <center>
	      <xsl:apply-templates select="tags">
		<xsl:with-param name="mode">TAG_USED</xsl:with-param>
		<xsl:with-param name="style">multiple</xsl:with-param>
	      </xsl:apply-templates>
	    </center>
	  </div>
	</form>
      </div>
    </div>
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Géolocalisations</h1>
	<div class="body">
	  <center>
	    <div style="width: 400px; height: 400px" id="mapChoix">
             <!--<img src="static/images/loading.gif"/>-->
	    </div>
	  </center>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Annees <input id="yearsLoader" type="button" value="load years" onclick="loadYears();"/></h1>
	<div class="body">
	  <div id="years" style="overflow:auto">
	    <center>
	      <img src="static/images/loading.gif"/>
	    </center>
	  </div>
	</div>
      </div>
    </div>

  </xsl:template>

</xsl:stylesheet>
