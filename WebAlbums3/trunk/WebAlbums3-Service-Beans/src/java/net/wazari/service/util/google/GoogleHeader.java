package net.wazari.service.util.google;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoogleHeader {

    private List<GoogleMap> maps = null;

    public void addMap(GoogleMap map) {
        if (maps == null) {
            maps = new ArrayList<GoogleMap>();
        }
        maps.add(map);
    }

    public String script() {
        if (maps == null) 
            return "";


        StringBuilder str = new StringBuilder();
        str.append(
                "function initialize() {\n"
                + "  if (GBrowserIsCompatible()) {\n"
                + "     // Creates a marker at the given point\n"
                + "     // Clicking the marker will hide it\n");

        for (GoogleMap m : maps) {
            str.append(m.getInitCode());
        }
        str.append(
                "     \n"
                + "  }\n"
                + "}\n");
        str.append("\n\n");
        for (GoogleMap m : maps) {
            str.append(m.getFunctions());
        }

        maps = new ArrayList<GoogleMap>();
        return str.toString();
    }

    public String bodyAttributes() {
        if (maps == null) {
            return "";
        }

        return "onload='initialize()' onunload='GUnload()'";
    }
}
