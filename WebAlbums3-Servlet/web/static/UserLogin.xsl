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
  <xsl:template match="login">
    <script type='text/javascript' src="static/scripts/tools.js" />
    <script type='text/javascript' src="static/scripts/Login.js" />
    <div class="item">
      <div class="date">
	<span>*</span>
      </div>
      <div class="content">
	<h1>Authentification</h1>
	<div class="body">
	  <xsl:if test="valid">
	    <b>Accès autorisé, <br/> please wait a second...</b><br/>
	  </xsl:if>
	  <xsl:if test="denied">
	    <b>Accès refusé !</b><br/>
	  </xsl:if>

	  <xsl:if test="not(valid)">
	    <form method="POST" action="Users">
                <table>
                    <tr><td><label for="userName">Nom d'utilisateur :</label></td>
                        <td><input id="userName" type="input" name="userName" value="kevin"/></td>
                    </tr>
                    <tr><td><label for="userPass">Mot de passe :</label></td>
                        <td><input id="userPass" type="password" name="userPass" /></td>
                    </tr>
                </table>
	      <input type='hidden' name='action' value='LOGIN'/>	
	      <input id="submit_login" type="submit" value="Valider" />
	    </form>
	  </xsl:if>
	</div>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="userLogin/valid">
    <meta http-equiv="refresh" content="0; URL=Index" />
  </xsl:template>
</xsl:stylesheet>
