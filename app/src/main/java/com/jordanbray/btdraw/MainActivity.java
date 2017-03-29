package com.jordanbray.btdraw;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    final Context context = this;
    private ArtistView av;
    private DrawerLayout drawer;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private MenuModel navMenu;

    private final int MODE_COLOR_PICKER = 6;

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
                boolean found = false;
                ExpandedMenuModel custom = new ExpandedMenuModel();
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if(av.getMode() == MODE_COLOR_PICKER) {
                        for (ExpandedMenuModel p : listDataHeader) {
                            if (p.getIconName().equals(getString(R.string.color))) {
                                for (ExpandedMenuModel m : listDataChild.get(p)) {
                                    if(m.getIconName().equals(getString(R.string.color_custom)))
                                        custom = m;
                                    if( (int) m.getAvAction() == av.getPaintColor() ) {
                                        p.setIconImg(m.getIconImg());
                                        invalidateOptionsMenu();
                                        mMenuAdapter.notifyDataSetInvalidated();
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found) {
                                    p.setIconImg(custom.getIconImg());
                                    invalidateOptionsMenu();
                                    mMenuAdapter.notifyDataSetInvalidated();
                                }
                                break;
                            }
                        }
                    }
                }
            }
        };
        drawer.setDrawerListener(toggle);
        setDrawerLeftEdgeSize(this, drawer);
        toggle.syncState();

        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);


        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

                // Get the name of the current item
                String currentItem = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getIconName();


                // Set Selected item to header icon
                if(!listDataHeader.get(groupPosition).getIconName().equals(getString(R.string.options))) {
                    listDataHeader.get(groupPosition).setIconImg(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getIconImg());
                }
                
                // Refresh the display
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                parent.setItemChecked(index, true);
                // Collapse heading
                parent.collapseGroup(groupPosition);

                // Determine the heading of the selected item, tell ArtistView what to do next
                if(listDataHeader.get(groupPosition).getIconName().equals(getString(R.string.options))) {

                    if (currentItem.equals(getString(R.string.start_new))) {
                        av.newCanvas();
                    } else if (currentItem.equals(getString(R.string.save))) {
                        saveImage();
                    } else if (currentItem.equals(getString(R.string.undo))) {
                        av.sendBitmapToCanvas();
                    }

                    drawer.closeDrawer(GravityCompat.START);

                } else if(listDataHeader.get(groupPosition).getIconName().equals(getString(R.string.tools))) {

                    if (currentItem.equals(getString(R.string.erase))) {
                        av.setMode(0);
                        av.Erase();
                    } else av.setMode((int) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAvAction());

                } else if (listDataHeader.get(groupPosition).getIconName().equals(getString(R.string.object_size))) {
                    av.setBrushSize((int) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAvAction());
                } else if (listDataHeader.get(groupPosition).getIconName().equals(getString(R.string.color))) {

                    if (currentItem.equals(getString(R.string.color_custom))) {
                        int testing = showDialog();
                    }
                    else av.setPaintColor((int) listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAvAction());

                }

                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {

                return false;
            }
        });
    }

    private void prepareListData() {
        // Build the entries in the drawer menu
        navMenu = InvokeXML.readMenuItemsXML(getApplicationContext());
        listDataHeader = navMenu.getListDataHeader();
        listDataChild = navMenu.getListDataChild();
    }

    @Override
    public void onBackPressed() {
        // If the drawer is open on back pressed, close it
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                av.setPaintColor(Color.rgb(Integer.parseInt(redtvValue.getText().toString()), Integer.parseInt(greentvValue.getText().toString()) , Integer.parseInt(bluetvValue.getText().toString())));
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
        return Color.rgb(Integer.parseInt(redtvValue.getText().toString()), Integer.parseInt(greentvValue.getText().toString()) , Integer.parseInt(bluetvValue.getText().toString()));
    }

    public void saveImage () {
        av.saveToBitmap();
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "//BTDraw//";
            File fpath = new File(path);
            if (!fpath.isDirectory()) {
                fpath.mkdir();
            }
            File f = new File(path, "myImage.png");
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            av.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {

        }
        av.destroyDrawingCache();
    }

}
