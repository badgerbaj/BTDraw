package com.jordanbray.btdraw;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArtistView av;
    private DrawerLayout drawer;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private MenuModel navMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        av = (ArtistView)findViewById(R.id.drawing);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setItemIconTintList(null);

        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

                String currentItem = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getIconName();

                // Set Selected item to header icon
                listDataHeader.get(groupPosition).setIconImg(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getIconImg());
                //Toast.makeText(MainActivity.this, "clicked " + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getIconName(), Toast.LENGTH_SHORT).show();

                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                parent.setItemChecked(index, true);
                parent.collapseGroup(groupPosition);

                //drawer.closeDrawers();

                if (currentItem.equals(getString(R.string.brush))) {
                    // Handle the brush action
                } else if (currentItem.equals(getString(R.string.erase))) {
                    av.Erase();
                } else if (currentItem.equals(getString(R.string.object_small))) {
                    // Handle the small brush action
                } else if (currentItem.equals(getString(R.string.object_medium))) {
                    // Handle the medium brush action
                } else if (currentItem.equals(getString(R.string.object_large))) {
                    // Handle the large brush action
                } else if (currentItem.equals(getString(R.string.color_custom))) {
                    // show color picker
                } else if (currentItem.equals(getString(R.string.color_red))) {
                    av.setPaintColor(Color.RED);
                } else if (currentItem.equals(getString(R.string.color_orange))) {
                    av.setPaintColor(0xFFFFA500);
                } else if (currentItem.equals(getString(R.string.color_yellow))) {
                    av.setPaintColor(Color.YELLOW);
                } else if (currentItem.equals(getString(R.string.color_green))) {
                    av.setPaintColor(Color.GREEN);
                } else if (currentItem.equals(getString(R.string.color_blue))) {
                    av.setPaintColor(Color.BLUE);
                } else if (currentItem.equals(getString(R.string.color_purple))) {
                    av.setPaintColor(0xFF551A8B);
                } else if (currentItem.equals(getString(R.string.color_pink))) {
                    av.setPaintColor(0xFFFF69B4);
                } else if (currentItem.equals(getString(R.string.color_white))) {
                    av.setPaintColor(Color.WHITE);
                } else if (currentItem.equals(getString(R.string.color_grey))) {
                    av.setPaintColor(Color.LTGRAY);
                } else if (currentItem.equals(getString(R.string.color_black))) {
                    av.setPaintColor(Color.BLACK);
                }

                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {
                //Log.d("DEBUG", "heading clicked: " + i);
                return false;
            }
        });
    }

    private void prepareListData() {

        navMenu = InvokeXML.readMenuItemsXML(getApplicationContext());
        listDataHeader = navMenu.getListDataHeader();
        listDataChild = navMenu.getListDataChild();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_load) {

        } else if (id == R.id.action_save) {

        } else if (id == R.id.action_start_new) {

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here
        // NOTE: This method is no longer used

        return true;
    }
}
