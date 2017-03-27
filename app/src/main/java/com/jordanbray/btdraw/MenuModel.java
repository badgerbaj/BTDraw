package com.jordanbray.btdraw;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Brad on 3/25/2017.
 * Holds all objects ExpandableListAdapter needs so
 * they can be passed between classes
 */

public class MenuModel {

    // Declare variables
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;

    // Declare Constructor
    public MenuModel(List<ExpandedMenuModel> listDataHeader, HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild) {
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    // Declare Getters
    public List<ExpandedMenuModel> getListDataHeader() {
        return listDataHeader;
    }
    public HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> getListDataChild() {
        return listDataChild;
    }

    //Declare Setters
    public void setListDataHeader(List<ExpandedMenuModel> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }
    public void setListDataChild(HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild) {
        this.listDataChild = listDataChild;
    }
}
