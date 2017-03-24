package com.jordanbray.btdraw;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;

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

                String currentItem = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).toString();

                Toast.makeText(MainActivity.this, "clicked " + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();

                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                parent.setItemChecked(index, true);


                //drawer.closeDrawers();

                if (currentItem.equals(getString(R.string.brush))) {
                    // Handle the brush action
                } else if (currentItem.equals(getString(R.string.erase))) {
                    av.Erase();
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
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        // Adding data header
        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setIconName(getString(R.string.tools));
        item1.setIconImg(android.R.drawable.ic_delete);
        listDataHeader.add(item1);

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        heading1.add(getString(R.string.brush));
        heading1.add(getString(R.string.erase));

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), heading1);

        // Adding data header
        ExpandedMenuModel item2 = new ExpandedMenuModel();
        item2.setIconName(getString(R.string.object_size));
        item2.setIconImg(android.R.drawable.ic_delete);
        listDataHeader.add(item2);

        // Adding child data
        List<String> heading2 = new ArrayList<String>();
        heading2.add(getString(R.string.object_small));
        heading2.add(getString(R.string.object_medium));
        heading2.add(getString(R.string.object_large));

        // Header, Child data
        listDataChild.put(listDataHeader.get(1), heading2);

        // Adding data header
        ExpandedMenuModel item3 = new ExpandedMenuModel();
        item3.setIconName(getString(R.string.color));
        item3.setIconImg(android.R.drawable.ic_delete);
        listDataHeader.add(item3);

        // Adding child data
        List<String> heading3 = new ArrayList<String>();
        heading3.add(getString(R.string.color_red));
        heading3.add(getString(R.string.color_orange));
        heading3.add(getString(R.string.color_yellow));
        heading3.add(getString(R.string.color_green));
        heading3.add(getString(R.string.color_blue));
        heading3.add(getString(R.string.color_purple));
        heading3.add(getString(R.string.color_pink));
        heading3.add(getString(R.string.color_white));
        heading3.add(getString(R.string.color_grey));
        heading3.add(getString(R.string.color_black));

        // Header, Child data
        listDataChild.put(listDataHeader.get(2), heading3);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here
        // NOTE: This method is no longer used

        /*
        int id = item.getItemId();

        if (id == R.id.nav_brush) {
            // Handle the camera action
        } else if (id == R.id.nav_erase) {
            av.Erase();
        } else if (id == R.id.color_red) {
            av.setPaintColor(Color.RED);
        } else if (id == R.id.color_orange) {
            av.setPaintColor(0xFFFFA500);
        } else if (id == R.id.color_yellow) {
            av.setPaintColor(Color.YELLOW);
        } else if (id == R.id.color_green) {
            av.setPaintColor(Color.GREEN);
        } else if (id == R.id.color_blue) {
            av.setPaintColor(Color.BLUE);
        } else if (id == R.id.color_purple) {
            av.setPaintColor(0xFF551A8B);
        } else if (id == R.id.color_pink) {
            av.setPaintColor(0xFFFF69B4);
        } else if (id == R.id.color_white) {
            av.setPaintColor(Color.WHITE);
        } else if (id == R.id.color_grey) {
            av.setPaintColor(Color.LTGRAY);
        } else if (id == R.id.color_black) {
            av.setPaintColor(Color.BLACK);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        */
        return true;
    }
}
