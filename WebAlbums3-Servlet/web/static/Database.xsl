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
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Export des données</h1>
              <xsl:apply-templates select="export"/>
              <form action="?action=EXPORT#export" method="POST"><input type="submit" value="Go!"/></form>
          </div>
        </div>
        <div class="item">
          <a name="import"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Importation des donnée</h1>
              <xsl:apply-templates select="import_"/>
              <form action="?action=IMPORT#import" method="POST"><input type="submit" value="Go!"/></form>
          </div>
        </div>
        <div class="item">
          <a name="trunk"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Vidage du contenus</h1>
              <xsl:apply-templates select="trunk"/>
              <form action="?action=TRUNK" method="POST"><input type="submit" value="Go!"/></form>
          </div>
        </div>
        <div class="item">
          <a name="check"/>
          <div class="date">
            <span>*</span>
          </div>
          <div class="content">
              <h1>Verification des photos</h1>
              <xsl:apply-templates select="check"/>
              <form action="?action=CHECK" method="POST"><input type="submit" value="Go!"/></form>
          </div>
        </div>
    </xsl:template>
    <xsl:template match="export | import_ | trunk">
        <p><xsl:value-of select="."/></p>
    </xsl:template>    
    
    <xsl:template match="check">
        <p><xsl:value-of select="./message"/></p>
        <xsl:apply-templates select="files"/>
    </xsl:template>    
    <xsl:template match="files">
        <p><xsl:value-of select="."/></p>
    </xsl:template>    
</xsl:stylesheet>