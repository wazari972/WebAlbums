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
  <xsl:template match="/webAlbums/choix">
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1><a href="Carnets">Tous les Carnets</a>&#160;<input id="carnetsLoader" type="button" value="5 derniers"/></h1>
	<div class="body">
	  <div id="carnets" style="overflow:auto;"/>
	</div>
      </div>
    </div>
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
        <h1><a href="Albums">Tous les Albums</a>&#160;<input id="albumsLoader" type="button" value="5 derniers"/></h1>
        <div class="body">
          <div id="albums" style="overflow:auto;"/>
        </div>
      </div>
    </div>
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Hall of Fame <input id="personsLoader" type="button" value="+"/></h1>
	<div class="body">
	  <div id="persons" style="overflow:auto"/>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Places of the World <input id="placesLoader" type="button" value="+"/></h1>
	<div class="body">
	  <div id="places" style="overflow:auto"/>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<form action="Tags">
	  <h1>Choix par Tags <input id="tagShower" type="button" value="+"/></h1>
	  <div class="body">
              <div id="tags" style="display:none">
                <center>
                  <xsl:apply-templates select="tagList">
                    <xsl:with-param name="mode">TAG_USED</xsl:with-param>
                    <xsl:with-param name="style">multiple</xsl:with-param>
                  </xsl:apply-templates><br/>
                  <input type="submit" value="Go"/>
                </center>
              </div>
	  </div>
	</form>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Photo Aléatoire <input id="randPictLoader" type="button" value="+"/>
            &#160;
            <a href="Photos?special=RANDOM" target="_blank" rel="singlepage[no]">^</a>
        </h1>
	<div class="body">
	  <div id="randPict"/>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Aléatoire par Années <input id="yearsLoader" type="button" value="+"/>
        &#160;
            <a href="Albums?special=YEARS&amp;nbPerYear=5" target="_blank" rel="singlepage[no]">^</a>
        </h1>
	<div class="body">
	  <div id="years" style="overflow:auto"/>
	</div>
      </div>
    </div>

    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Recherche d'Albums <input id="selectLoader" type="button" value="+"/></h1>
	<div class="body">
	  <div id="select" style="overflow:none" />
	</div>
      </div>
    </div>
    <div class="item">
      <div class="date">
	<span></span>
      </div>
      <div class="content">
	<h1>Géolocalisations <input id="googleMapLoader" type="button" value="+"/></h1>
	<div class="body">
	  <center>
	    <div id="mapChoix">
	    </div>
	  </center>
	</div>
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>
