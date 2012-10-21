<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="carnets/edit">
    <div class="item">
      <div class="date">
	<span>*</span>
      </div>
      <div class="content">
          <xsl:if test="not(/webAlbums/carnets/edit/@id)">
              <h1>Création d'un carnet</h1>
          </xsl:if>
          <xsl:if test="/webAlbums/carnets/edit/@id">
              <h1>Modification d'un carnet</h1>
          </xsl:if>
	<div class="body">
          <xsl:apply-templates select="submit/exception"/>
          <xsl:apply-templates select="submit/message"/>
	  <center>
	    <img id="carnetReprImg">
	      <xsl:attribute name="src">Miniature__<xsl:value-of select="@picture" />.png</xsl:attribute>
	    </img>
	  </center>
	</div>
      </div>
      <div class="content">
	<div class="body">
	  <form method='post' id="formModifCarnet">
              <!-- &carnet=..# must be last, see ModifCarnet.js -->
	    <xsl:attribute name="action">Carnets?&amp;page=<xsl:value-of select="@page" />&amp;carnet=<xsl:value-of select="@id" />#<xsl:value-of select="@id" /></xsl:attribute>
	    <input type='hidden' name='action' value='SUBMIT' />
	    <label for="nom">Nom:</label>
	    <input id="carnetNom" type='text' size='40' maxlength='60' name='nom'
                   required="true" placeholder="Titre ...">
	      <xsl:attribute name="value"><xsl:value-of select="name" /></xsl:attribute>
              <xsl:attribute name="rel"><xsl:value-of select="@id" /></xsl:attribute>
	    </input>
	    <br/>
            <label for="date">Date:</label> 
	    <input type='date' placeholder="YYYY-MM-DD" name='date' id="carnetDate" 
                required="true" >
	      <xsl:attribute name="value">
                  <xsl:if test="date"><xsl:value-of select="date" /></xsl:if>
              </xsl:attribute>
	    </input>
            Photo pour représentation: 
            <input type='text' name='carnetRepr' id="carnetRepr" maxlength="4" size="5"
                   placeholder="ID ...">
                <xsl:attribute name="value"><xsl:value-of select="@picture" /></xsl:attribute>
            </input>
            <input type='hidden' name='carnetPhoto' id='carnetPhoto' value = ''/>
            <input type='hidden' name='carnetAlbum' id='carnetAlbum' value = ''/>
	    <br/>
	    Droits de visibilité : 
            <xsl:apply-templates select="rights">
                <xsl:with-param name="id">carnetUser</xsl:with-param>
                <xsl:with-param name="default">3</xsl:with-param>
            </xsl:apply-templates>
            <br/>
            <label for="desc">Description:</label>
	    <textarea id="carnetDesc" name='desc' rows='2' cols='65' placeholder="Description ...">
	      <xsl:value-of select="description" />
	    </textarea>
            <br/><br/>
            <input type='submit' value='Valider' class="carnetSubmit"/> &#160; 
            <input type='button' value='Enregistrer' class="carnetSave"/> &#160; 
            <input type='checkbox' class="carnetAutoSave" id="carnetAutoSave1"/> <label for="carnetAutoSave1"> AutoSave</label>&#160;
            <input type='button' value='Side-by-side' class="btSide"/> &#160;<input type='button' value='+' class="btSizeUp" style="display:none"/> &#160;<input type='button' value='-' class="btSizeDown" style="display:none"/> &#160;
            <div class="localsave_info">Saved locally at: <span class="localsave_ts">not saved</span></div>
            <div id="wmd-button-bar"></div>
            <div class="wmd-panel carnet_panel">
                <textarea class="wmd-input" id="wmd-input" name="carnetText">
                    <xsl:value-of select="text" />
                </textarea>
            </div>
            <div id="wmd-preview" class="wmd-panel wmd-preview carnet_text"></div>
                    <br/>
	    <input type='submit' value='Valider' class="carnetSubmit"/> &#160; 
            <input type='button' value='Enregistrer' class="carnetSave"/> &#160; 
            <input type='checkbox' class="carnetAutoSave" id="carnetAutoSave2"/> <label for="carnetAutoSave2"> AutoSave</label>&#160; 
            <div class="localsave_info">Saved locally at: <span class="localsave_ts">not saved</span></div>
            <br/>
            <br/>
            <label for="sure">"Oui je veux supprimer ce carnet" (définitif!)</label>
	    <input id="sure" type='text' autocomplete='off' name='suppr' 
                   size='31' maxlength='31'
                   placeholder="Oui je veux supprimer ce carnet"/>
	  </form>
	  <br/>
	  <br/>
          <center>
              <a>
                <xsl:attribute name="href">Carnets?page=<xsl:value-of select="@page"/>&amp;carnet=<xsl:value-of select="@id" /></xsl:attribute>
                Retour au carnet
              </a>
          </center>
	</div>
      </div>
    </div>
    <script type="text/javascript" src="static/scripts/ModifCarnet.js"/>
  </xsl:template>
</xsl:stylesheet>
