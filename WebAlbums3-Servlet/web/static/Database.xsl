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
    <xsl:template match="database">
        <div class="item">
          <a name="export"/>
          <a name="import"/>
          <a name="trunk"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Import-Export</h1>
              
              <h2>Exportation des donnée</h2>
              <form action="?action=EXPORT#export" method="POST"><input type="submit" value="Go!"/></form>
              <xsl:apply-templates select="export"/>
              
              <h2>Importation des donnée</h2>
              <form action="?action=IMPORT#import" method="POST"><input type="submit" value="Go!"/></form>
              <xsl:apply-templates select="import"/>
              
              <h2>Vidage de la base</h2>
              <form action="?action=TRUNK" method="POST"><input type="submit" value="Go!"/></form>
              <xsl:apply-templates select="trunk"/>
          </div>
        </div>
        <div class="item">
          <a name="check"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Verification des photos</h1>
              <form action="?action=CHECK" method="POST"><input type="submit" value="Go!"/></form>
              <xsl:apply-templates select="check"/>
          </div>
        </div>
        <div class="item">
          <a name="check"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Statistiques du thème</h1>
              <form action="?action=STATS" method="POST"><input type="submit" value="Go!"/></form>
              <xsl:apply-templates select="stats"/>
          </div>
        </div>
        <div class="item">
          <a name="config"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Fichier de Configuration</h1>
              <p><a rel="singlepage[no]" href="Other/Config?action=SAVE">Sauvegarder</a></p>
              <p><a rel="singlepage[no]" href="Other/Config?action=RELOAD">Recharger</a></p>
              <p><a rel="singlepage[no]" href="Other/Config">Afficher</a></p>
          </div>
        </div>
        <div class="item">
          <a name="plugins"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Plugins</h1>
              <p><a rel="singlepage[no]" href="?action=PLUGIN_RELOAD">Recharger</a></p>
              <p><a rel="singlepage[no]" href="?action=PLUGIN">Lister</a></p>
              <xsl:apply-templates select="plugins"/>
          </div>
        </div>
        <div class="item">
          <a name="maint"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Maintenance</h1>
              <p><a rel="singlepage[no]" href="?action=CREATE_DIRS">Créer les dossiers</a></p>
              <xsl:apply-templates select="create_dir/dirs"/>
          </div>
        </div>
        <div class="item">
          <a name="random"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Aleatoire</h1>
              <form action="Other/Random">
                  <input type="hidden" name="mode" value ="RANDOM_TAG" />
                  Afficher une image aléatoire pour le tag
                  <xsl:apply-templates select="default/tagList">
                    <xsl:with-param name="style">list</xsl:with-param>
                    <xsl:with-param name="mode">TAG_USED</xsl:with-param>
                    <xsl:with-param name="name">id</xsl:with-param>
                  </xsl:apply-templates>
                  <br/>
                    avec le compte 
                    <input type="text" name="login" size="5">
                        <xsl:attribute name="value"><xsl:value-of select="/webAlbums/loginInfo/user" /></xsl:attribute>
                    </input>
                    <br/>
                    dans le thème
                    <input type="text" name="themeId" size="3">
                        <xsl:attribute name="value"><xsl:value-of select="/webAlbums/loginInfo/themeid" /></xsl:attribute>
                    </input>
                    <br/>
                    <input type="submit" value="Afficher" />
                </form>
          </div>
        </div>
    </xsl:template>
    <xsl:template match="export | import_ | trunk">
        <p><xsl:value-of select="."/></p>
    </xsl:template>    
    
    <xsl:template match="create_dir/dirs">
        <p>
        <xsl:value-of select="."/>
        </p>
    </xsl:template>   
    <xsl:template match="Importers/WorkingImporters|Importers/FailingImporters|NotUsedSystems|System">
        <p><xsl:value-of select="name"/> (v. <xsl:value-of select="version"/>)</p>
    </xsl:template>
    <xsl:template match="plugins">
        <h3>Plugins pour importer:</h3>
        <xsl:apply-templates select="Importers/WorkingImporters"/>
        <p><b>not used:</b></p>
        <xsl:apply-templates select="Importers/FailingImporters"/>
        <h3>Plugins système:</h3>
        <xsl:apply-templates select="System"/>
        <p><b>not used:</b></p>
        <xsl:apply-templates select="NotUsedSystems"/>
        <p><xsl:value-of select="./message"/></p>
        <xsl:apply-templates select="files"/>
    </xsl:template>
    
    <xsl:template match="check">
        <p><xsl:value-of select="./message"/></p>
        <xsl:apply-templates select="files"/>
    </xsl:template>
    <xsl:template match="files">
        <p><xsl:value-of select="."/></p>
    </xsl:template>
    <xsl:template match="stats">
        <xsl:apply-templates select="theme"/>
    </xsl:template>   
    <xsl:template match="stats/theme">
        <div class="stat_theme">
            <h3><xsl:value-of select="name"/></h3>
            <p>Albums: <xsl:value-of select="albums"/></p>
            <p>Photos: <xsl:value-of select="photos"/></p>
            <p>Tags: <xsl:value-of select="tags"/></p>
            <br/>
        </div>
    </xsl:template>   
</xsl:stylesheet>