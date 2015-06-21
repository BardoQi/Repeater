package com.weishang.repeater.translation;

import android.text.TextUtils;
import android.util.Pair;

/**
 * Created by momo on 2015/3/13.
 * 动画移动属性
 */
public class TranslationValue implements Cloneable {
    public int id;//执行动画id;
    public boolean isRestoreRvs;//动画还原时,反置
    public int duration;//动画时间
    public int gravity;//动画执行方向
    public int mode;//执行动画模式
    public int anim;//执行动画体
    public int weight;//动画执行权重
    //动画取值范围
    public Pair<Float, Float> alphaValue;
    public Pair<Float, Float> scaleXValue;
    public Pair<Float, Float> scaleYValue;
    public Pair<Float, Float> rotationValue;
    public Pair<Float, Float> rotationXValue;
    public Pair<Float, Float> rotationYValue;

    public TranslationValue() {
    }

    public TranslationValue(boolean isRestoreRvs, int duration, int gravity, int mode, int anim) {
        this(isRestoreRvs, duration, gravity, mode, anim, 0, 0f, 0f, 0f, 360f, 360f, 360f);
    }

    public TranslationValue(boolean isRestoreRvs, int duration, int gravity, int mode, int anim, int weight, float alphaValue, float scaleXValue, float scaleYValue, float rotationValue, float rotationXValue, float rotationYValue) {
        this.isRestoreRvs = isRestoreRvs;
        this.duration = duration;
        this.gravity = gravity;
        this.mode = mode;
        this.anim = anim;
        this.weight = weight;
        this.alphaValue = new Pair<Float, Float>(alphaValue, 1f);
        this.scaleXValue = new Pair<Float, Float>(scaleXValue, 1f);
        this.scaleYValue = new Pair<Float, Float>(scaleYValue, 1f);
        this.rotationValue = new Pair<Float, Float>(rotationValue, 0f);
        this.rotationXValue = new Pair<Float, Float>(rotationXValue, 0f);
        this.rotationYValue = new Pair<Float, Float>(rotationYValue, 0f);
    }

    /**
     * 获得对应属性的动画值对
     *
     * @param propertyName 动画属性名称
     * @return 动画取值范围
     */
    public Pair<Float, Float> getPropertyValue(String propertyName) {
        Pair<Float, Float> value = null;
        if (TextUtils.isEmpty(propertyName)) return value;
        if (ViewTranslation.ALPHA.equals(propertyName)) {
            value = alphaValue;
        } else if (ViewTranslation.SCALE_X.equals(propertyName)) {
            value = scaleXValue;
        } else if (ViewTranslation.SCALE_Y.equals(propertyName)) {
            value = scaleYValue;
        } else if (ViewTranslation.ROTATION.equals(propertyName)) {
            value = rotationValue;
        } else if (ViewTranslation.ROTATION_X.equals(propertyName)) {
            value = rotationXValue;
        } else if (ViewTranslation.ROTATION_Y.equals(propertyName)) {
            value = rotationYValue;
        }
        return value;
    }

    @Override
    public TranslationValue clone() {
        TranslationValue translationValue = new TranslationValue(isRestoreRvs,
                duration,
                gravity,
                mode,
                anim,
                weight,
                alphaValue.first,
                scaleXValue.first,
                scaleYValue.first,
                rotationValue.first,
                rotationXValue.first,
                rotationYValue.first
        );
        translationValue.id = id;
        return translationValue;
    }

    public TranslationValue ensure() {
        if (0 == duration) {
            duration = 300;
        }
        if (null == this.alphaValue) {
            this.alphaValue = new Pair<Float, Float>(0f, 1f);
        }
        if (null == this.scaleXValue) {
            this.scaleXValue = new Pair<Float, Float>(0f, 1f);
        }
        if (null == this.scaleYValue) {
            this.scaleYValue = new Pair<Float, Float>(0f, 1f);
        }
        if (null == this.rotationValue) {
            this.rotationValue = new Pair<Float, Float>(360f, 0f);
        }
        if (null == this.rotationXValue) {
            this.rotationXValue = new Pair<Float, Float>(360f, 0f);
        }
        if (null == this.rotationYValue) {
            this.rotationYValue = new Pair<Float, Float>(360f, 0f);
        }
        return this;
    }

}
