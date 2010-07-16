<%-- 
    Document   : Other
    Created on : Jul 4, 2010, 10:24:12 AM
    Author     : kevinpouget
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Other/ information page</title>
    </head>
    <body>
        <h1>Configuration</h1>
        <ul>
            <li><a href="Config?action=SAVE">SAVE</a></li>
            <li><a href="Config?action=RELOAD">RELOAD</a></li>
            <li><a href="Config?action=LOGOUT">LOGOUT</a></li>
            <li><a href="Config">PRINT</a></li>
        </ul>
        <h1>Display</h1>
        <ul>
            <li><a href="Display?action=NEXT_EDITION">NEXT_EDITION</a></li>
            <li><a href="Display?action=SWAP_DETAILS">SWAP_DETAILS</a></li>
        </ul>
        <h1>Plugins</h1>
        <ul>
            <li><a href="Plugins?action=RELOAD_PLUGINS">RELOAD_PLUGINS</a></li>
            <li><a href="Plugins">LIST</a></li>
        </ul>
        <h1>Maintenance</h1>
        <ul>
            <li><a href="Config?action=CREATE_DIRS">CREATE_DIRS</a></li>
            <li><a href="../Maint?action=IMPORT_XML">IMPORT_XML</a></li>
            <li><a href="../Maint?action=EXPORT_XML">EXPORT_XML</a></li>
        </ul>
    </body>
</html>