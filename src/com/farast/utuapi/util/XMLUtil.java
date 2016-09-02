package com.farast.utuapi.util;

import com.farast.utuapi.util.functional_interfaces.Action;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public final class XMLUtil {

    private XMLUtil() {

    }

    public static void forEachElement(NodeList nodeList, Action<Element> execute) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                execute.accept((Element) node);
            }
        }
    }

    public static NodeList getNodeList(Element element, String parent, String children) {
        return XMLUtil.getElement(element, parent).getElementsByTagName(children);
    }

    public static boolean getAndParseBooleanValueOfChild(Element element, String elementName) {

        return Boolean.parseBoolean(getValueOfChild(element, elementName));
    }

    public static int getAndParseIntValueOfChild(Element element, String elementName) throws NumberFormatException {
        String childValue = getValueOfChild(element, elementName);
        if (!childValue.isEmpty())
            return Integer.parseInt(childValue);
        else
            return -1;
    }

    public static Date getAndParseDateValueOfChild(Element element, String elementName) throws ParseException {

        return DateUtil.DATE_FORMAT.parse(getValueOfChild(element, elementName));
    }

    public static String getValueOfChild(Element element, String elementName) {
        return element.getElementsByTagName(elementName).item(0).getTextContent();
    }

    public static Element getElement(Element element, String elementName) {
        return (Element) element.getElementsByTagName(elementName).item(0);
    }

    public static boolean exists(Element element, String childElementName) {
        return element.getElementsByTagName(childElementName).getLength() > 0;
    }

    public static boolean existsAndNotEmpty(Element element, String childElementName) {
        NodeList kidi = element.getElementsByTagName(childElementName);
        String text = kidi.item(0).getTextContent();
        return kidi.getLength() > 0 && text != null && !text.isEmpty();
    }

    public static Element parseXml(InputStream xmlStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(xmlStream);
        return document.getDocumentElement();
    }

    public static Date getAndParseDateTimeValueOfChild(Element element, String elementName) throws ParseException {
        return DateUtil.DATE_TIME_FORMAT.parse(getValueOfChild(element, elementName));
    }
}
