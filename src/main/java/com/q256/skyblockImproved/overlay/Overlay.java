package com.q256.skyblockImproved.overlay;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.config.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public abstract class Overlay extends Gui {
    protected transient SkyblockImproved main = SkyblockImproved.getInstance();
    protected double x;
    protected double y;
    protected double size;
    protected AlignModeX alignModeX;
    protected AlignModeY alignModeY;

    /**
     * Used when previewing an overlay and when changing alignModeX
     */
    protected transient int width;
    /**
     * Used when previewing an overlay and when changing alignModeY
     */
    protected transient int height;

    public Overlay(double x, double y){
        this(x, y, 1, AlignModeX.LEFT, AlignModeY.TOP);
    }

    public Overlay(double x, double y, double size){
        this(x, y, size, AlignModeX.LEFT, AlignModeY.TOP);
    }

    public Overlay(double x, double y, double size, AlignModeX alignModeX, AlignModeY alignModeY){
        this.x = x;
        this.y = y;
        this.size = size;
        this.alignModeX = alignModeX;
        this.alignModeY = alignModeY;
    }

    public abstract void drawOverlay();

    public abstract void drawPreview();

    /**
     * Returns a boolean setting that determines whether this overlay is enabled
     */
    public abstract Setting<Boolean> getEnabled();

    public abstract void updateDimensions();

    public int getXCorner(){
        ScaledResolution scaledResolution = getScaledResolution();
        return (int)((x*scaledResolution.getScaledWidth() - alignModeX.ordinal()*width*size/2));
    }

    public int getYCorner(){
        ScaledResolution scaledResolution = getScaledResolution();
        return (int)((y*scaledResolution.getScaledHeight() - alignModeY.ordinal()*height*size/2));
    }

    public int getWidth(){ return (int)(width*size); }

    public int getHeight() { return (int)(height*size); }

    public int getUnscaledWidth(){return width;}

    public int getUnscaledHeight(){return height;}

    public void moveX(int dX){
        x += ((double)dX)/getScaledResolution().getScaledWidth();
    }

    public void moveY(int dY){
        y += ((double)dY)/getScaledResolution().getScaledHeight();
    }

    public void setAlignModeX(AlignModeX newAlignModeX){
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        x += width/2.0 * size/scaledResolution.getScaledWidth() * (newAlignModeX.ordinal() - alignModeX.ordinal());
        alignModeX = newAlignModeX;
    }

    public void setAlignModeY(AlignModeY newAlignModeY) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        y += height/2.0 * size/scaledResolution.getScaledHeight() * (newAlignModeY.ordinal() - alignModeY.ordinal());
        alignModeY = newAlignModeY;
    }

    public void setSize(double newSize) {
        size = newSize;

        if(size<0.1) size = 0.1;

        if(width==0 || height==0){
            if(size>10) size=10;
        } else {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            if(getXCorner() < 0) {
                size = x * scaledResolution.getScaledWidth() * 2 / width / alignModeX.ordinal();
            }
            if(getXCorner() + width*size > scaledResolution.getScaledWidth()){
                size = (x-1)*scaledResolution.getScaledWidth()/(width*(alignModeX.ordinal()/2.0 - 1));
            }

            if(getYCorner() < 0){
                size = y * scaledResolution.getScaledHeight() * 2 / height / alignModeY.ordinal();
            }
            if(getYCorner() + height*size > scaledResolution.getScaledHeight()){
                size = (y-1)*scaledResolution.getScaledHeight()/(height*(alignModeY.ordinal()/2.0 - 1));
            }
        }
    }

    public AlignModeX getAlignModeX() {
        return alignModeX;
    }

    public AlignModeY getAlignModeY() {
        return alignModeY;
    }

    protected ScaledResolution getScaledResolution(){
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    /**
     * Resets the position, dimensions and alignments of this overlay.
     */
    public abstract void resetPos();
}
