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
 *
 */

public class InvokeXML extends  MainActivity {

    private static List<ExpandedMenuModel> listDataHeader;
    private static HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;

    private enum XML_Ele {
        menu,
        header,
        item,
        string,
        drawable
    }

    public static MenuModel readMenuItemsXML(Context ctx) {
        String tagName;
        int eventType;
        int headings = 0;
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<ExpandedMenuModel>>();
        List<ExpandedMenuModel> heading = new ArrayList<ExpandedMenuModel>();

        try {
            XmlPullParser xpp = ctx.getResources().getXml(R.xml.menu_items);

            eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                tagName = xpp.getName();

                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(tagName.equals(XML_Ele.header.toString())){
                            // Create new header object
                            // Adding data header
                            ExpandedMenuModel item = new ExpandedMenuModel();
                            heading = new ArrayList<ExpandedMenuModel>();

                            item.setIconName(getStringResourceByName(ctx, xpp.getAttributeValue(0), XML_Ele.string.toString()));
                            item.setIconImg(getIntResourceByName(ctx, xpp.getAttributeValue(1), XML_Ele.drawable.toString()));
                            listDataHeader.add(item);
                        }
                        if(tagName.equals(XML_Ele.item.toString())){
                            // Create new item header object, add to header object
                            // Adding child data
                            ExpandedMenuModel menuItem = new ExpandedMenuModel();
                            menuItem.setIconName(getStringResourceByName(ctx, xpp.getAttributeValue(0), XML_Ele.string.toString()));
                            menuItem.setIconImg(getIntResourceByName(ctx, xpp.getAttributeValue(1), XML_Ele.drawable.toString()));
                            heading.add(menuItem);
                        }
                        break;

                    case  XmlPullParser.TEXT:
                        //curText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(tagName.equals(XML_Ele.header.toString())){
                            // Add this header object to object of header objects
                            // Header, Child data
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

    private static String getStringResourceByName(Context ctx, String aString, String resourceType) {
        int resId = ctx.getResources().getIdentifier(aString, resourceType, ctx.getPackageName());
        return ctx.getString(resId);
    }
    private static int getIntResourceByName(Context ctx, String aString, String resourceType) {
        int resId;
        return resId = ctx.getResources().getIdentifier(aString, resourceType, ctx.getPackageName());
    }
}