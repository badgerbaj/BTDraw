package com.jordanbray.btdraw;

/**
 * Created by bjordan on 3/24/2017.
 */

public class ExpandedMenuModel {

    private String iconName = "";
    private int iconImg = -1; // menu icon resource id
    private long avAction = -1;

    public String getIconName() {
        return iconName;
    }
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
    public int getIconImg() {
        return iconImg;
    }
    public void setIconImg(int iconImg) {
        this.iconImg = iconImg;
    }
    public long getAvAction() {
        return avAction;
    }
    public void setAvAction(long avAction) {
        this.avAction = avAction;
    }
}
