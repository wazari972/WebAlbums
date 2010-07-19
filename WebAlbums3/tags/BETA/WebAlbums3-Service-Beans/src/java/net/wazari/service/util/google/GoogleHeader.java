package net.wazari.service.util.google;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoogleHeader {

    private static final Map<String, String> googleKeys = new HashMap<String, String>();

    static {
        googleKeys.put("192.168.1.9", "ABQIAAAAFkDQEQAV0T9D_hG6NbawIhQEpPcd-ZFOiQgzblrnGR4NjGjTWBQ64M5_5gYa_GNCRsAEcIvPzGi_8A");
        googleKeys.put("192.168.2.1", "ABQIAAAAFkDQEQAV0T9D_hG6NbawIhTK_nMORFpGnrPZNOiBU8rqVz7MsBQhrMI2D0dZRhu72MzNFO09e-0lag");
        googleKeys.put("127.0.0.1",   "ABQIAAAAFkDQEQAV0T9D_hG6NbawIhRi_j0U6kJrkFvY4-OX2XYmEAa76BQg7GDO1xjGmeDrxVVqn_kEo8MJ4A");
    }
    
    private List<GoogleMap> maps = null;

    public void addMap(GoogleMap map) {
        if (maps == null) {
            maps = new ArrayList<GoogleMap>();
        }
        maps.add(map);
    }

    public static String getKey(String server) {
        return "http://maps.google.com/maps?file=api&amp;v=2&amp;"
                + "key=" + googleKeys.get(server);
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
