package com.jordanbray.btdraw;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.content.DialogInterface;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.R.attr.onClick;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/*
*   ------------------------------------- Additional Features --------------------------------------
*
*   Navigation Menu
*       Description: A custom ExpandableListView that is populated from xml\menu_items.xml
*       Classes: ExpandableListAdapter, ExpandedMenuModel, MenuModel, InvokeXML
*       XML: activity_main, xml\menuitems, list_menu, listheader
*       Methods: prepareListData, onChildClickListener, onBackPressed, setDrawerLeftEdgeSize
*       Variables: drawer, toggle, mMenuAdapter, expandableList, listDataHeader, listDataChild, navMenu
*
*       .Options
*           New
*               Description: Generates a blank canvas to be drawn on. A verification dialog prevents
*                   accidental selection.
*               Classes: ArtistView
*               Methods: createAndShowDialog, av.newCanvas
*               Variables: av, currentMode
*           Save
*               Description: Saves the current drawing to the phone's gallery A verification dialog prevents
*                   accidental selection.
*               Methods: createAndShowDialog, checkPermission, saveImage
*               Variables: av, currentMode, WRITE_EXTERNAL_STORAGE
*           Load
*               Description: Loads selected image
*               Classes:
*               Methods:
*               Variables:
*           Undo
*               Description: Restores the canvas to the state prior to the last onTouchEvent.MotionEvent.ACTION_DOWN.
*               Classes: ArtistView
*               Methods: av.sendBitmapToCanvas, saveToBitmap
*               Variables: av, currentCanvas
*
*       .Tools
*           Paint Bucket
*               Description: Fills a continuous colored area with the selected color.
*               Classes: ArtistView
*               Methods: av.setMode
*               Variables: av
*           Erase
*               Description: Restores the selected area to the original canvas color.  Tool is based on
*                   Object Size.
*               Classes: ArtistView
*               Methods: av.Erase, av.setMode
*               Variables:
*           Color Picker
*               Description: Allows the user to select a color displayed on screen.  The selected tool icon
*                   will update to reflect this.
*               Classes: ArtistView
*               Methods: av.setMode
*               Variables: av
*           Rectangle Fill
*               Description: The user can create a rectangle of variable size that will automatically fill
*                   the space within.
*               Classes: ArtistView
*               Methods: av.setMode
*               Variables: av
*           Oval
*               Description: The user can create an oval of variable size that is just an outline.  Object
*                   Size governs the thickness of its borders.
*               Classes: ArtistView
*               Methods: av.setMode
*               Variables: av
*           Oval Fill
*               Description: The user can create an oval of variable size that will automatically fill
*                   the space within.
*               Classes: ArtistView
*               Methods: av.setMode
*               Variables: av
*
*       .Color
*           Custom
*               Description: Brings up a dialog with 3 RGB sliders, which allows the user to choose
*                   a custom color to draw with.  Compatible with all tools including color picker.
*               Classes: ArtistView
*               XML: custom_color
*               Methods: setPaintColor, showDialog
*               Variables: av, MODE_COLOR_PICKER
*   ------------------------------------------------------------------------------------------------
 */

public class MainActivity extends AppCompatActivity implements AlertDialog.OnClickListener {

    final Context context = this;
    private ArtistView av;
    private DrawerLayout drawer;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private MenuModel navMenu;

    private enum DialogMode {
        SAVE,
        LOAD,
        NEW
    }
    private DialogMode currentMode;

    private final int MODE_COLOR_PICKER = 6;
    private final int WRITE_EXTERNAL_STORAGE = 5150;

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
                        currentMode = DialogMode.NEW;
                        createAndShowDialog(R.string.confirm_new, currentMode);
                    } else if (currentItem.equals(getString(R.string.save))) {
                        //saveFileDialog();
                        currentMode = DialogMode.SAVE;
                        createAndShowDialog(R.string.confirm_save, currentMode);
                    } else if (currentItem.equals(getString(R.string.load))) {
                        currentMode = DialogMode.LOAD;
                        createAndShowDialog(R.string.confirm_load, currentMode);
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
                        int testing = customColorDialog();
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_NEGATIVE:
                // int which = -2
                dialog.dismiss();
                break;
            case BUTTON_POSITIVE:
                // int which = -1
                // Take action
                try {
                    switch(currentMode) {
                        case SAVE:

                            Toast.makeText(this, "Saving... ", Toast.LENGTH_SHORT).show();

                            checkPermission();

                            break;
                        case LOAD:
                            //loadImage(files[i]);
                            break;
                        case NEW:
                            av.newCanvas();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                break;
        }
    }

    void createAndShowDialog(int message, DialogMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle(R.string.title);
        builder.setMessage(message);

        if(mode == DialogMode.LOAD) {
            final String files[] = loadFiles();

            builder.setItems(files, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    loadImage(files[i]);
                }
            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

        } else {
            builder.setPositiveButton(android.R.string.yes, this);
            builder.setNegativeButton(android.R.string.no, this);
        }

        builder.create().show();
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

    public int customColorDialog() {
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

    public void saveImage () { // String file_name) {

        try {
            /*
            av.setDrawingCacheEnabled(true);
            String imageSave = MediaStore.Images.Media.insertImage(getContentResolver(),
                    av.getDrawingCache(), UUID.randomUUID().toString()+getString(R.string.png),
                    getString(R.string.custom_drawing));
            //Log.i ("Save path: ", MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString());
            av.destroyDrawingCache();
            */
            av.saveToBitmap();
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "//BTDraw//";
            File fpath = new File(path);
            if (!fpath.isDirectory()) {
                fpath.mkdir();
            }
            File f = new File(path, "Testing.png"); //file_name + ".png");
            FileOutputStream out = new FileOutputStream(f);
            av.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 90, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //av.destroyDrawingCache();
    }

    private void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
        } else {
            saveImage();
        }
    }

    public void loadImage (String file_name) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "//BTDraw//" + file_name;
        File fpath = new File(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(fpath), null, options);
            av.loadBitmapToCanvas(b);
    }
    catch (Exception e) {
        e.printStackTrace();
    }

    }

    public String[] loadFiles () {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/BTDraw/";
        File fpath = new File(path);
        String[] files = fpath.list() ;
        return files;
    }

   /*
   public void saveFileDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.MATCH_PARENT,
               LinearLayout.LayoutParams.MATCH_PARENT
       );
       input.setLayoutParams(lp);
       builder.setView(input);
       builder.setTitle("Save File");

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                    saveImage(input.getText().toString());
             }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case WRITE_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveImage();
                }
                break;

            default:
                break;
        }
    }

}
