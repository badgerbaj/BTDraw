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

        int foundAttributes;
        int expectedAttributes = 2;

        try {
            XmlPullParser xpp = ctx.getResources().getXml(R.xml.menu_items);

            //Log.d("XML TEST", "" + xpp.getEventType());

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals(XML_Ele.item.toString())) {
                        foundAttributes = 0;

                        Log.d("XML TEST", "" + xpp.getName());

                        while(foundAttributes < expectedAttributes) {
                            if(xpp.getAttributeCount() == 0) {
                                xpp.next();
                            } else {
                                foundAttributes++;
                                Log.d("XML TEST", "" + xpp.getName());
                                Log.d("XML TEST", "" + xpp.getAttributeValue(0));
                            }
                        }

                    }
                }

                xpp.next();
            }

        } catch (Exception e) {
            Log.d("XML ERROR", e.getMessage());
        }

    }
}