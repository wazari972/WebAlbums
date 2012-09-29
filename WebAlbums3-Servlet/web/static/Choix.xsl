<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/webAlbums/choix">
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1><a href="Carnets">Tous les Carnets</a>&#160;
            <xsl:if test="not(/webAlbums/choix/topCarnets)">
                <input id="carnetsLoader" type="button" value="5 derniers"/>
            </xsl:if>
        </h1>
	<div class="body">
	  <div id="carnets" style="overflow:auto">
              <xsl:apply-templates select="/webAlbums/choix/topCarnets"/>    
          </div>
	</div>
      </div>
    </div>
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
        <h1><a href="Albums">Tous les Albums</a>&#160;
            <xsl:if test="not(/webAlbums/choix/topAlbums)">
                <input id="albumsLoader" type="button" value="5 derniers"/>
            </xsl:if>
        </h1>
        <div class="body">
          <div id="albums" style="overflow:auto">
            <xsl:apply-templates select="/webAlbums/choix/topAlbums"/>
          </div>
        </div>
      </div>
    </div>
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Hall of Fame 
            <xsl:if test="not(/webAlbums/choix/persons)">
                <input id="personsLoader" type="button" value="+"/>
            </xsl:if>
        </h1>
	<div class="body">
	  <div id="persons" style="overflow:auto">
            <xsl:apply-templates select="/webAlbums/choix/persons" />    
          </div>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>
           <span>Places of the World</span>
        <xsl:if test="not(/webAlbums/choix/places)">
            <input id="placesLoader" type="button" value="+"/>
        </xsl:if>
        </h1>
	<div class="body">
	  <div id="places" style="overflow:auto">
              <xsl:apply-templates select="/webAlbums/choix/places" />
          </div>
	</div>
      </div>
    </div>
<xsl:if test="not(/webAlbums/choix/@complete)">
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Photo Aléatoire <input id="randPictLoader" type="button" value="+"/>
            <span>&#160;</span>
            <!-- no url rewritting -->
            <a href="Photos?special=RANDOM" target="_blank" rel="singlepage[no]" title="Ouvrir dans une nouvelle page">^</a>
        </h1>
	<div class="body">
	  <div id="randPict"/>
	</div>
      </div>
    </div>
</xsl:if>
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Aléatoire par Années 
            <xsl:if test="not(/webAlbums/choix/years)">
                <input id="yearsLoader" type="button" value="+"/>
                <span>&#160;</span>
                <!-- no url rewritting -->
                <a href="Albums?special=YEARS&amp;nbPerYear=5" target="_blank" rel="singlepage[no]" title="Ouvrir dans une nouvelle page">^</a>
            </xsl:if>
        </h1>
	<div class="body">
	  <div id="years" style="overflow:auto">
              <xsl:apply-templates select="/webAlbums/choix/years" />
          </div>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Recherche d'Albums 
            <xsl:if test="not(/webAlbums/choix/select)">
                <input id="selectLoader" type="button" value="+"/>
            </xsl:if>
        </h1>
	<div class="body">
	  <div id="select" style="overflow:none">
            <xsl:apply-templates select="/webAlbums/choix/select" />
          </div>
	</div>
      </div>
    </div>
    
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Géolocalisations <input id="mapLoader" type="button" value="+"/></h1>
	<div class="body">
	  <center>
	    <div id="theMapChoix"></div>
	  </center>
	</div>
      </div>
    </div>
    
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Traces Gpx <input id="gpsLoader" type="button" value="+"/></h1>
	<div class="body">
            <div id="gpsChoix"/>
	</div>
      </div>
    </div>
<xsl:if test="not(/webAlbums/choix/@complete)">
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<form action="Tags">
	  <h1>Choix par Tags <input id="tagGraphLoader" type="button" value="Graph it!"/> <input type="submit" value="Search"/></h1>
	  <div class="body">
              <div id="tags">
                <center>
                  <xsl:apply-templates select="tagList">
                      <xsl:with-param name="id">tagChoix</xsl:with-param>
                    <xsl:with-param name="mode">TAG_USED</xsl:with-param>
                    <xsl:with-param name="style">multiple</xsl:with-param>
                  </xsl:apply-templates><br/>
                </center>
              </div>
              <div id="tagGraph" />
	  </div>
	</form>
      </div>
    </div>
</xsl:if>
  </xsl:template>
</xsl:stylesheet>
