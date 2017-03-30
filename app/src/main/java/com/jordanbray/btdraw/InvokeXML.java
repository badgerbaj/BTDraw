package com.jordanbray.btdraw;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static android.content.ContentValues.TAG;

/**
 * Created by Brad on 1/28/2017.
 * Reads menu data found in menu_items.xml
 */

public class InvokeXML extends  MainActivity {

    // Build menu objects
    private static List<ExpandedMenuModel> listDataHeader;
    private static HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;

    // Declare constants
    private enum XML_Ele {
        menu,
        header,
        item,
        string,
        drawable,
        value,
        image,
        action
    }

    // Read XML
    public static MenuModel readMenuItemsXML(Context ctx) {

        String tagName;
        int eventType;
        int headings = 0;
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<ExpandedMenuModel>>();
        List<ExpandedMenuModel> heading = new ArrayList<ExpandedMenuModel>();

        try {
            // Build XML parsable object
            XmlPullParser xpp = ctx.getResources().getXml(R.xml.menu_items);

            // SAX Parser style data getter
            eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                tagName = xpp.getName();

                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(tagName.equals(XML_Ele.header.toString())){

                            // Create new header object
                            ExpandedMenuModel item = new ExpandedMenuModel();
                            heading = new ArrayList<ExpandedMenuModel>();

                            // Add data header
                            item.setIconName(getStringResourceByName(ctx, xpp.getAttributeValue(null,
                                    XML_Ele.value.toString()), XML_Ele.string.toString()));
                            item.setIconImg(getIntResourceByName(ctx, xpp.getAttributeValue(null,
                                    XML_Ele.image.toString()), XML_Ele.drawable.toString()));
                            if(!item.getIconName().equals(ctx.getString(R.string.color))) {
                                item.setAvAction(Integer.parseInt(xpp.getAttributeValue(null,
                                        XML_Ele.action.toString())));
                            } else item.setAvAction(Long.parseLong(xpp.getAttributeValue(null,
                                    XML_Ele.action.toString()), 16));

                            listDataHeader.add(item);
                        }
                        if(tagName.equals(XML_Ele.item.toString())){

                            // Create new item object, add to header object
                            String headingName = listDataHeader.get(headings).getIconName();

                            // Adding child data
                            ExpandedMenuModel menuItem = new ExpandedMenuModel();
                            menuItem.setIconName(getStringResourceByName(ctx,
                                    xpp.getAttributeValue(null, XML_Ele.value.toString()),
                                    XML_Ele.string.toString()));
                            menuItem.setIconImg(getIntResourceByName(ctx,
                                    xpp.getAttributeValue(null, XML_Ele.image.toString()),
                                    XML_Ele.drawable.toString()));
                            if(headingName.equals(ctx.getString(R.string.color))) {
                                menuItem.setAvAction(Long.parseLong(xpp.getAttributeValue(null,
                                        XML_Ele.action.toString()), 16));
                            } else menuItem.setAvAction(Integer.parseInt(xpp.getAttributeValue(null,
                                        XML_Ele.action.toString())));

                            heading.add(menuItem);
                        }
                        break;

                    case  XmlPullParser.TEXT:
                        //curText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(tagName.equals(XML_Ele.header.toString())){

                            // Add this header object to object of header objects
                            listDataChild.put(listDataHeader.get(headings), heading);
                            headings++;
                        }
                        if(tagName.equals(XML_Ele.item.toString())){

                        }
                        break;
                    default:
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.e("XML ERROR", e.getMessage());
        }
        return new MenuModel(listDataHeader, listDataChild);
    }

    // Convert string data from xml to value in @string
    private static String getStringResourceByName(Context ctx, String aString, String resourceType) {
        int resId = ctx.getResources().getIdentifier(aString, resourceType, ctx.getPackageName());
        return ctx.getString(resId);
    }

    // Convert drawable name to a resourceID
    private static int getIntResourceByName(Context ctx, String aString, String resourceType) {
        return ctx.getResources().getIdentifier(aString, resourceType, ctx.getPackageName());
    }
}