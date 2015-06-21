package com.weishang.repeater.translation;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Pair;

import com.weishang.repeater.App;
import com.weishang.repeater.utils.Loger;
import com.weishang.repeater.utils.XmlParser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by momo on 2015/5/9.
 * xml anim layout 布局读取器
 */
public class LayoutReader {
    private static final String ANDROID_NAME_SPACE = "http://schemas.android.com/apk/res/android";
    private static final ArrayList<String> FILTER_NAME_SPACE;
    private static final HashMap<String, Integer> mAnimValues;

    static {
        FILTER_NAME_SPACE = new ArrayList<String>();
        FILTER_NAME_SPACE.add(ANDROID_NAME_SPACE);
        mAnimValues = new HashMap<String, Integer>();
        mAnimValues.put("alpha", 0x01);
        mAnimValues.put("translation_x", 0x02);
        mAnimValues.put("translation_y", 0x04);
        mAnimValues.put("scale_x", 0x08);
        mAnimValues.put("scale_y", 0x10);
        mAnimValues.put("rotation", 0x20);
        mAnimValues.put("rotation_x", 0x40);
        mAnimValues.put("rotation_y", 0x80);
    }

    private Context mContext;
    private final int mLayout;
    private int id;

    public LayoutReader(int mLayout) {
        this.mContext = App.getAppContext();
        this.mLayout = mLayout;
    }

    public ArrayList<TranslationValue> read() {
        ArrayList<TranslationValue> configs = null;
        try {
            configs = new ArrayList<TranslationValue>();
            XmlResourceParser resourceParser = mContext.getResources().getLayout(mLayout);
            XmlParser.ParserListener listener = getParserListener(configs);
            if (null != listener) {
                XmlParser.startParser(resourceParser, listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configs;
    }

    public XmlParser.ParserListener getParserListener(final ArrayList<TranslationValue> configs) {
        return new XmlParser.ParserListener<XmlResourceParser>() {
            private ArrayList<String> tags = new ArrayList<String>();
            private TranslationValue value;

            @Override
            public void startParser(final XmlResourceParser parser) {
                parserAttrs(parser, new XmlLayoutParserListener() {
                    @Override
                    public void parser(String nameSpace, String attrName, String attrValue) {
                        if (null == value) {
                            value = new TranslationValue();
                        }
                        if ("anim_duration".equals(attrName)) {
                            value.duration = Integer.valueOf(attrValue);
                        } else if ("anim_restore_rvs".equals(attrName)) {
                            value.isRestoreRvs = Boolean.valueOf(attrValue);
                        } else if ("anim_weight".equals(attrName)) {
                            value.weight = Integer.valueOf(attrValue);
                        } else if ("anim_gravity".equals(attrName)) {
                            value.gravity = getHexValue(attrValue);
                        } else if ("anim_mode".equals(attrName)) {
                            value.mode = getHexValue(attrValue);
                        } else if ("anim".equals(attrName)) {
                            value.id = id;
                            value.anim = getHexValue(attrValue);
                            String name = parser.getName();
                            if (!tags.contains(name)) {
                                tags.add(name);
                            }
                        } else if ("alpha_value".equals(attrName)) {
                            value.alphaValue = new Pair<Float, Float>(Float.valueOf(attrValue), 0f);
                        } else if ("scalex_value".equals(attrName)) {
                            value.scaleXValue = new Pair<Float, Float>(Float.valueOf(attrValue), 0f);
                        } else if ("scaley_value".equals(attrName)) {
                            value.scaleYValue = new Pair<Float, Float>(Float.valueOf(attrValue), 0f);
                        } else if ("rotation_value".equals(attrName)) {
                            value.rotationValue = new Pair<Float, Float>(360f, Float.valueOf(attrValue));
                        } else if ("rotationx_value".equals(attrName)) {
                            value.rotationXValue = new Pair<Float, Float>(360f, Float.valueOf(attrValue));
                        } else if ("rotationy_value".equals(attrName)) {
                            value.rotationYValue = new Pair<Float, Float>(360f, Float.valueOf(attrValue));
                        }
                    }
                });
            }

            @Override
            public void endParser(XmlResourceParser parser) {
                String name = parser.getName();
                if (tags.contains(name)) {
                    tags.remove(name);
                    configs.add(value.ensure());
                    value = null;
                }
            }
        };
    }

    private int getHexValue(String attrValue) {
        int value = 0;
        String hex = "0x";
        int index = attrValue.indexOf(hex);
        if (-1 != index) {
            value = Integer.valueOf(attrValue.substring(index + hex.length()), 16);
        }
        return value;
    }

    public void parserAttrs(XmlResourceParser parser, XmlLayoutParserListener listener) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String namespace = parser.getAttributeNamespace(i);
            String attributeName = parser.getAttributeName(i);
            if ("id".equals(attributeName)) {
                id = parser.getAttributeResourceValue(ANDROID_NAME_SPACE, "id", i);
                Loger.i(this, "name:" + parser.getName() + " id:" + id);
            }
            if (!FILTER_NAME_SPACE.contains(namespace) && null != listener) {
                listener.parser(namespace, attributeName, parser.getAttributeValue(i));
            }
        }
    }


    public interface XmlLayoutParserListener {
        void parser(String nameSpace, String attrName, String attrValue);
    }

}
