<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="database">
        <p>
          <b><xsl:value-of select="./message"/></b>
        </p>
        <p>
          <b><xsl:value-of select="./exception"/></b>
        </p>
        <div class="item">
          <a name="export"/>
          <a name="import"/>
          <a name="trunk"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Import-Export</h1>
              <h2>Exportation des données</h2>
              <form action="Database?action=EXPORT#export" method="POST"><input type="submit" value="Go!"/></form>
              <xsl:apply-templates select="export"/>
              
              <h2>Importation des données</h2>
              <form action="Database?action=IMPORT#import" method="POST"><input type="submit" value="Go!"/></form>
              <xsl:apply-templates select="import"/>
              
              <h2>Vidage de la base</h2>
              <form action="Database?action=TRUNK" method="POST"><input type="submit" value="Go!"/></form>
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
              <form action="Database?action=CHECK_DB" method="POST"><input type="submit" value="Check Database"/></form>
              <form action="Database?action=CHECK_FS" method="POST"><input type="submit" value="Check Filesystem"/></form>
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
              <form action="Database?action=STATS" method="POST"><input type="submit" value="Go!"/></form>
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
              <p><a rel="singlepage[no]" href="Database?action=SAVE_CONFIG">Sauvegarder</a></p>
              <p><a rel="singlepage[no]" href="Database?action=RELOAD_CONFIG">Recharger</a></p>
              <p><a rel="singlepage[no]" href="Database?action=PRINT_CONFIG">Afficher</a></p>
              <p><xsl:value-of select="./config"/></p>
          </div>
        </div>
        <div class="item">
          <a name="plugins"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Plugins</h1>
              <p><a rel="singlepage[no]" href="Database?action=RELOAD_PLUGINS">Recharger</a></p>
              <p><a rel="singlepage[no]" href="Database?action=PLUGINS">Lister</a></p>
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
              <p><a rel="singlepage[no]" href="Database?action=CREATE_DIRS">Créer les dossiers</a></p>
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
    <xsl:template match="files/entry">
        <p>
            <xsl:value-of select="key/text()" /><b>&#126;<xsl:value-of select="value/text()" /></b>
            <xsl:if test="string(number(key/text())) != 'NaN'">
                &#128;<a><xsl:attribute name="href">Photos?action=EDIT&amp;id=<xsl:value-of select="key/text()" /></xsl:attribute>EDIT</a>
                &#128;<a title="Pas de confirmation !"><xsl:attribute name="href">Photos?special=FASTEDIT&amp;id=<xsl:value-of select="key/text()" />&amp;suppr=Oui je veux supprimer cette photo</xsl:attribute>/!\ DELETE</a>
            </xsl:if>
        </p>
    </xsl:template>
    
    <xsl:template match="stats">
        <xsl:apply-templates select="theme"/>
    </xsl:template>   
    <xsl:template match="stats/theme">
        <div class="stat_theme">
            <h3><xsl:value-of select="name"/></h3>
            <ul>
            <li><xsl:value-of select="albums"/> albums</li>
            <li><xsl:value-of select="photos"/> photos</li>
            <li><xsl:value-of select="tags"/> tags</li>
            </ul>
            <xsl:if test="tagCloud">
                <h3>Tags:</h3>
                <ul>
                <xsl:for-each select="tagCloud/tag">
                    <xsl:sort select="@nb" data-type="number" order="descending"/>
                    <li><xsl:value-of select="@nb" /> &#8594; <xsl:value-of select="name" /></li>
                </xsl:for-each>
                </ul>
            </xsl:if>
        </div>
    </xsl:template>  
    <xsl:template match="theme/tagCloud">
        <div class="stat_theme">
            <h3><xsl:value-of select="name"/></h3>
            <ul>
            <li><xsl:value-of select="albums"/> albums</li>
            <li><xsl:value-of select="photos"/> photos</li>
            <li><xsl:value-of select="tags"/> tags</li>
            </ul>
        </div>
    </xsl:template>  
</xsl:stylesheet>