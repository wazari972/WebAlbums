package net.wazari.common.util;

import org.apache.commons.lang.StringEscapeUtils;

public class StringUtil {

    public static String escapeSpaces(String s) {
        return s.replace(" ", "\\ ");
    }

    public static String escapeSQL(String s) {
        return StringEscapeUtils.escapeSql(s);
    }

    public static String escapeHTML(String s) {
        s = StringEscapeUtils.unescapeHtml(s);
        return StringEscapeUtils.escapeHtml(s);
    }

    public static String escapeXML(String s) {
        s = StringEscapeUtils.unescapeHtml(s);
        s = StringEscapeUtils.escapeHtml(s);
        return s;
    }

    public static String toAscii(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s.length());
        int n = s.length();
        int i = 0;
        while (i < n) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z') {
                sb.append(c);
            }
            if (c >= 'A' && c <= 'Z') {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    public static String putbackAmp(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s.length());
        int n = s.length();
        int i = 0;
        while (i < n) {
            char c = s.charAt(i);
            if (c == '%') {
                i++;
                c = s.charAt(i);
                if (c == '2') {
                    i++;
                    c = s.charAt(i);
                    if (c == '6') {
                        sb.append('&');
                    } else {
                        sb.append(c);
                    }
                } else {
                    sb.append(c);
                }

            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    public static String escapeURL(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s.length());
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\'':
                    sb.append("%27");
                    break;
                case ' ':
                    sb.append("%20");
                    break;
                case 'é':
                    sb.append("%C3%A9");
                    break;
                case 'è':
                    sb.append("%C3%A8");
                    break;
                case 'à':
                    sb.append("%C3%A0");
                    break;
                case '&':
                    sb.append("%26");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public static String escapeJavaScript(String s) {
        return StringEscapeUtils.escapeJavaScript(s);
    }
    
    public static String unescapeHtml(String s) {
        return StringEscapeUtils.unescapeHtml(s);
    }

    public static String escapeSH(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s.length());
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\'':
                    sb.append("\\'");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case ' ':
                    sb.append("\\ ");
                    break;
                case '&':
                    sb.append("\\&");
                    break;
                case '!':
                    sb.append("\\!");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }
}
