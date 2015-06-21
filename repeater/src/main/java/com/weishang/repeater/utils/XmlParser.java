package com.weishang.repeater.utils;

import android.content.res.XmlResourceParser;
import android.util.Xml;

import com.weishang.repeater.listener.ITask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 解析xml内容体
 *
 * @author momo
 * @Date 2014/11/25
 */
public class XmlParser {
    /**
     * 开始解析xml
     *
     * @param inputStream
     * @param listener
     */
    public static void startParser(InputStream inputStream, ParserListener listener) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "utf-8");
            int eventType = parser.getEventType();
            while (XmlPullParser.END_DOCUMENT != eventType) {
                if (null != listener) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            listener.startParser(parser);
                            break;
                        case XmlPullParser.END_TAG:
                            listener.endParser(parser);
                            break;
                        default:
                            break;
                    }
                }
                eventType = parser.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(inputStream);
        }
    }

    /**
     * 开始解析xml
     *
     * @param parser
     * @param listener
     */
    public static void startParser(XmlResourceParser parser, ParserListener<XmlResourceParser> listener) {
        try {
            int eventType = parser.getEventType();
            while (XmlPullParser.END_DOCUMENT != eventType) {
                if (null != listener) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            listener.startParser(parser);
                            break;
                        case XmlPullParser.END_TAG:
                            listener.endParser(parser);
                            break;
                        default:
                            break;
                    }
                }
                eventType = parser.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void runParser(XmlPullParser parser, ITask<String> task) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (null != task) {
                task.run(parser.getAttributeName(i), parser.getAttributeValue(i));
            }
        }
    }

    public interface ParserListener<P extends XmlPullParser> {
        void startParser(P parser);

        void endParser(P parser);
    }

    public interface ParserResultListener<E> {
        void parserResult(E e);
    }
}
