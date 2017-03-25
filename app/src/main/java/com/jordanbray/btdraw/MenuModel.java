package com.jordanbray.btdraw;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Brad on 3/25/2017.
 */

public class MenuModel {

    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;

    public MenuModel(List<ExpandedMenuModel> listDataHeader, HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild) {
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    public List<ExpandedMenuModel> getListDataHeader() {
        return listDataHeader;
    }

    public void setListDataHeader(List<ExpandedMenuModel> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }

    public HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> getListDataChild() {
        return listDataChild;
    }

    public void setListDataChild(HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild) {
        this.listDataChild = listDataChild;
    }
}
