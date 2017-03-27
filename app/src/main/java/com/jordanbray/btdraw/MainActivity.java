package com.jordanbray.btdraw;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBar;
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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;
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
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_SETTLING) {

                    if(av.getMode() == 4) {
                        for (ExpandedMenuModel m : listDataChild.get(listDataHeader.get(2))) {
                            if( (int) m.getAvAction() == av.getPaintColor() ) {
                                listDataHeader.get(2).setIconImg(m.getIconImg());
                                invalidateOptionsMenu();
                                mMenuAdapter.notifyDataSetInvalidated();
                                break;
                            }
                        }
                        //listDataHeader.get(2).set
                    }

                    //int test = av.getPaintColor();
                    //int test2 = av.getMode();
                    /*
                    if (!isDrawerOpen()) {
                        // starts opening
                        getActionBar()
                                .setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    } else {
                        // closing drawer
                        getActionBar()
                                .setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    }
                    invalidateOptionsMenu();
                    */
                }
            }
        };
        drawer.setDrawerListener(toggle);
        setDrawerLeftEdgeSize(this, drawer);
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

                if(listDataHeader.get(groupPosition).getIconName().equals(getString(R.string.tools))) {
                    if (currentItem.equals(getString(R.string.erase))) {
                        av.Erase();
                    } else if (currentItem.equals(getString(R.string.color_picker))) {
                        av.setMode((int) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAvAction());
                    } else av.setMode((int) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAvAction());
                    Log.i("POSITION",Integer.toString((int) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAvAction()));
                } else if (listDataHeader.get(groupPosition).getIconName().equals(getString(R.string.object_size))) {
                    av.setBrushSize((int) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAvAction());
                } else if (listDataHeader.get(groupPosition).getIconName().equals(getString(R.string.color))) {
                    av.setPaintColor((int) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAvAction());
                }
                //drawer.closeDrawers();

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
            av.setDrawingCacheEnabled(true);
            String imageSave = MediaStore.Images.Media.insertImage(getContentResolver(), av.getDrawingCache(), UUID.randomUUID().toString()+".png", "Custom Drawing");
            av.destroyDrawingCache();
         } else if (id == R.id.action_start_new) {
            av.newCanvas();
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

    // Prevent the Navigation Drawer from sliding out while drawing
    public static void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout) {
        if (activity == null || drawerLayout == null)
            return;

        try {
            // find ViewDragHelper and set it accessible
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            // set new edgesize
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(leftDragger, 0); //Math.max(edgeSize, (int) (displaySize.x * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            // ignore
        } catch (IllegalArgumentException e) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
        }
    }

    public int showDialog() {
        int colorValue = 0;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_color);
        dialog.setTitle("SELECT CUSTOM COLOR");

        // set the custom dialog components - text, image and button
        final TextView colortv = (TextView) dialog.findViewById(R.id.color_textView);
        final TextView redtvValue = (TextView)dialog.findViewById(R.id.red_textView_value);
        final TextView greentvValue = (TextView)dialog.findViewById(R.id.green_textView_value);
        final TextView bluetvValue = (TextView)dialog.findViewById(R.id.blue_textView_value);
        SeekBar red = (SeekBar)dialog.findViewById(R.id.red_seekBar);
        SeekBar green = (SeekBar)dialog.findViewById(R.id.green_seekBar);
        SeekBar blue = (SeekBar)dialog.findViewById(R.id.blue_seekBar);

        Button ok_button = (Button) dialog.findViewById(R.id.ok_button);
        // if button is clicked, close the custom dialog
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button cancel_button = (Button) dialog.findViewById(R.id.cancel_button);
        // if button is clicked, close the custom dialog
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        red.setMax(255);
        green.setMax(255);
        blue.setMax(255);

        red.setKeyProgressIncrement(1);
        green.setKeyProgressIncrement(1);
        blue.setKeyProgressIncrement(1);

        red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                redtvValue.setText(Integer.toString(progress));
                colortv.setBackgroundColor(Color.rgb(Integer.parseInt(redtvValue.getText().toString()), Integer.parseInt(greentvValue.getText().toString()) , Integer.parseInt(bluetvValue.getText().toString())));

            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                greentvValue.setText(Integer.toString(progress));
                colortv.setBackgroundColor(Color.rgb(Integer.parseInt(redtvValue.getText().toString()), Integer.parseInt(greentvValue.getText().toString()) , Integer.parseInt(bluetvValue.getText().toString())));
            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                bluetvValue.setText(Integer.toString(progress));
                colortv.setBackgroundColor(Color.rgb(Integer.parseInt(redtvValue.getText().toString()), Integer.parseInt(greentvValue.getText().toString()) , Integer.parseInt(bluetvValue.getText().toString())));
            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



        dialog.show();
        return colorValue;
    }
}
