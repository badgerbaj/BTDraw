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

public class InvokeXML {

    private enum XML_Ele {
        menu,
        header,
        item,
        name,
        image
    }

    public static void readMenuItemsXML(Context ctx) {
        String tagname;
        int eventtype;

        try {
            XmlPullParser xpp = ctx.getResources().getXml(R.xml.menu_items);

            eventtype = xpp.getEventType();
            while (eventtype != XmlPullParser.END_DOCUMENT){
                tagname = xpp.getName();

                switch (eventtype){
                    case XmlPullParser.START_TAG:
                        if(tagname.equals(XML_Ele.header.toString())){
                            Log.d("XML TEST", "" + xpp.getName());
                            Log.d("XML TEST", "" + xpp.getAttributeValue(0));
                        }
                        if(tagname.equals(XML_Ele.name.toString())){
                            Log.d("XML TEST", "" + xpp.getName());
                            Log.d("XML TEST", "" + xpp.getAttributeValue(0));
                        }
                        if(tagname.equals(XML_Ele.image.toString())){
                            Log.d("XML TEST", "" + xpp.getName());
                            Log.d("XML TEST", "" + xpp.getAttributeValue(0));
                        }
                        break;

                    case  XmlPullParser.TEXT:
                        //curText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(tagname.equals(XML_Ele.header.toString())){
                            Log.d("XML TEST", "End " + xpp.getName());
                        }
                        if(tagname.equals(XML_Ele.item.toString())){
                            //Log.d("XML TEST", "" + xpp.getName());
                        }
                        if(tagname.equals(XML_Ele.name.toString())){
                            //Log.d("XML TEST", "" + xpp.getName());
                            //Log.d("XML TEST", "" + xpp.getAttributeValue(0));
                        }
                        if(tagname.equals(XML_Ele.image.toString())){
                            //Log.d("XML TEST", "" + xpp.getName());
                            //Log.d("XML TEST", "" + xpp.getAttributeValue(0));
                        }
                        break;
                    default:
                        break;
                }
                eventtype = xpp.next();
            }

        } catch (Exception e) {
            Log.e("XML ERROR", e.getMessage());
        }

    }
}