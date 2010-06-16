package net.wazari.util;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class XmlBuilder {

    public enum Type {

        NORMAL, TEXT, COMMENT
    }
    public static final Type TEXT = Type.TEXT;
    public static final Type COMMENT = Type.COMMENT;
    private Type type = Type.NORMAL;
    private StringBuilder text = new StringBuilder();
    private List<String> header = new LinkedList<String>();
    private List<XmlBuilder> content = new LinkedList<XmlBuilder>();
    private List<XmlBuilder> contentTemp = new LinkedList<XmlBuilder>();
    private String name;
    private String value;
    private Map<String, String> attrib = new HashMap<String, String>();
    private boolean printed = false;
    private XmlBuilder exception = null;

    private XmlBuilder() {
    }

    public static XmlBuilder newText() {
        XmlBuilder xml = new XmlBuilder();
        xml.type = TEXT;

        return xml;
    }

    public static XmlBuilder newComment(String comment) {
        XmlBuilder xml = new XmlBuilder();
        xml.type = COMMENT;
        xml.text.append(comment);

        return xml;
    }

    public XmlBuilder addText(String str) {
        text.append(str);

        return this;
    }

    public Type getType() {
        return this.type;
    }

    private XmlBuilder(String name, List<XmlBuilder> content) {
        this.name = name;
        this.content = content;
    }

    public XmlBuilder(String name, Object value) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        if (value instanceof XmlBuilder) {
            this.contentTemp.add((XmlBuilder) value);
        } else if (value != null) {
            this.value = value.toString();
        }
    }

    public XmlBuilder(String name) {
        this.name = name;
        this.value = null;
    }

    public XmlBuilder addHeader(String head) {
        header.add(head);

        return this;
    }

    public XmlBuilder validate() {
        content.addAll(contentTemp);
        contentTemp = new LinkedList<XmlBuilder>();

        return this;
    }

    public XmlBuilder cancel() {
        content.add(new XmlBuilder("canceled", contentTemp));
        contentTemp = new LinkedList<XmlBuilder>();

        return this;
    }

    public XmlBuilder addException(Object message) {
        return this.addException(null, message);
    }

    public XmlBuilder addException(String type, Object message) {
        if (this.exception == null) {
            this.exception = new XmlBuilder("exception");
        }
        //if (message == null) throw new NullPointerException("message cannot be null");
        if (type == null) {
            type = "Exception";
        }
        this.exception.add(type, message);
        return this;
    }

    public XmlBuilder addAttribut(String name, Object value) {
        if (name == null) {
            throw new NullPointerException();
        }
        attrib.put(name, value.toString());

        return this;
    }

    public XmlBuilder addComment(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.add(XmlBuilder.newComment(name));
        return this;
    }

    public XmlBuilder add(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.add(new XmlBuilder(name));
        return this;
    }

    public XmlBuilder add(String name, Object value) {
        if (name == null) {
            throw new NullPointerException();
        }

        XmlBuilder xml;
        if (value == null || "".equals(value)) {
            xml = new XmlBuilder(name);
        } else {
            xml = new XmlBuilder(name, value);
        }

        return this.add(xml);
    }

    public XmlBuilder add(XmlBuilder element) {
        if (element != null) {
            contentTemp.add(element);
        }

        return this;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int indent) {
        if (printed) {
            throw new RuntimeException("Already printed");
        } else {
            printed = true;
        }

        String space = indent(indent);

        if (type == TEXT) {
            return this.text.toString() + "\n";
        } else if (type == COMMENT) {
            return space + "<!-- " + this.text.toString() + " -->\n";
        }
        this.validate();

        if (this.exception != null) {
            this.content.add(0, this.exception);
        }

        String str = "";
        for (String head : header) {
            str += space + head + "\n";
        }

        if (name != null) {
            str += space + "<" + name;
            for (String key : attrib.keySet()) {
                String attribute = attrib.get(key);
                if (attribute == null || "".equals(attribute)) {
                    continue;
                }
                str += " " + key + "=\"" + attrib.get(key) + "\"";
            }

            if (value == null && content.isEmpty()) {
                str += "/>\n";
                return str;
            } else {
                str += ">";
            }

            if (value != null) {
                str += StringUtil.escapeXML(value);
            }
        }

        if (!content.isEmpty()) {
            if (name != null) {
                str += "\n";
            }

            for (XmlBuilder balise : content) {
                str += balise.toString(indent + 1);
            }
        }

        if (name != null) {
            if (!content.isEmpty()) {
                str += space;
            }
            str += "</" + name + ">\n";
        }
        return str;
    }

    private String indent(int nb) {
        if (nb == 0) {
            return "";
        }

        String indentation = "";
        for (int i = 0; i < nb; i++) {
            indentation += "  ";
        }
        return indentation;
    }
}
