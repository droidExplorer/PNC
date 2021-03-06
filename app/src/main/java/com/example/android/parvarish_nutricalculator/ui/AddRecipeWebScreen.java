package com.example.android.parvarish_nutricalculator.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.parvarish_nutricalculator.R;
import com.example.android.parvarish_nutricalculator.custom.ComplexPreferences;
import com.example.android.parvarish_nutricalculator.helpers.API;
import com.example.android.parvarish_nutricalculator.helpers.EnumType;
import com.example.android.parvarish_nutricalculator.helpers.GetPostClass;
import com.example.android.parvarish_nutricalculator.helpers.PrefUtils;
import com.example.android.parvarish_nutricalculator.model.regionalmainModel;
import com.example.android.parvarish_nutricalculator.model.regionalsubModel;
import com.example.android.parvarish_nutricalculator.model.sanjeevmainModel;
import com.example.android.parvarish_nutricalculator.model.userModel;
import com.facebook.login.LoginManager;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class AddRecipeWebScreen extends ActionBarActivity implements View.OnClickListener {
    private ProgressDialog progressDialog,progressDialog2;
    private EditText edURL;
    ListPopupWindow popupWindow1,popupWindow2;
    private Spinner SpRegionalRecipie;
    private Button btnImport;
    private Toolbar toolbar;
    ImageView img1, img2, img3, img4;
    userModel currentUser;
    sanjeevmainModel sajneevObj;
    regionalmainModel regionalObj;
    boolean importFromRegional;
    TextView addTitle, enterURLTitle, calculateTitle, calculateTitle2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe_web_screen);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("PARVARISH NUTRI CALCULATOR");
//            toolbar.setLogo(R.drawable.logo);
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        SpRegionalRecipie = (Spinner) findViewById(R.id.SpRegionalRecipie);
        addTitle = (TextView)findViewById(R.id.addTitle);
        enterURLTitle= (TextView)findViewById(R.id.enterURLTitle);
        calculateTitle = (TextView)findViewById(R.id.calculateTitle);
        calculateTitle2= (TextView)findViewById(R.id.calculateTitle2);

        calculateTitle2.setTypeface(PrefUtils.getTypeFace(AddRecipeWebScreen.this));
        calculateTitle.setTypeface(PrefUtils.getTypeFace(AddRecipeWebScreen.this));
        enterURLTitle.setTypeface(PrefUtils.getTypeFace(AddRecipeWebScreen.this));
        addTitle.setTypeface(PrefUtils.getTypeFace(AddRecipeWebScreen.this));


        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(AddRecipeWebScreen.this, "user_pref", 0);
        currentUser = complexPreferences.getObject("current-user", userModel.class);

        callWebServices();

        edURL = (EditText)findViewById(R.id.edURL);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);

        edURL.setTypeface(PrefUtils.getTypeFace(AddRecipeWebScreen.this));

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        btnImport = (Button) findViewById(R.id.btnImport);
        btnImport.setTypeface(PrefUtils.getTypeFace(AddRecipeWebScreen.this));

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(importFromRegional){
                    Intent i = new Intent(AddRecipeWebScreen.this, AddRegionalManualScreen.class);
                    i.putExtra("pos",SpRegionalRecipie.getSelectedItemPosition());
                    startActivity(i);
                }else{
                    Intent i = new Intent(AddRecipeWebScreen.this, ImportRecipeFromWebScreen.class);
                    i.putExtra("url",edURL.getText().toString().trim());
                    startActivity(i);
                }


            }
        });
    }

    void callWebServices(){

        fetchSanjeevKapoorRecpiesDetails();
        fetchRegionalWebService();

    }



    void fetchRegionalWebService(){

        progressDialog2 =new ProgressDialog(AddRecipeWebScreen.this);
        progressDialog2.setMessage("Loading ...");
        progressDialog2.show();

        new GetPostClass(API.REGIONAL_RECIPE, EnumType.GET) {
            @Override
            public void response(String response) {
                progressDialog2.dismiss();
                Log.e("regional response", response);

                try {

                    regionalObj = new GsonBuilder().create().fromJson(response, regionalmainModel.class);

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(AddRecipeWebScreen.this, "user_pref", 0);
                    complexPreferences.putObject("regional-recipe", regionalObj);
                    complexPreferences.commit();

                   ArrayList<String> spinnerList =  new ArrayList<String>();
                    spinnerList.add(0,"Select recipe");
                    for(int i=0;i<regionalObj.data.size();i++){
                        spinnerList.add(regionalObj.data.get(i).Recipe.name);
                    }


                    CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(AddRecipeWebScreen.this, spinnerList);
                    SpRegionalRecipie.setAdapter(customSpinnerAdapter);

                    SpRegionalRecipie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                           if(position==0)
                               importFromRegional = false;
                            else
                               importFromRegional = true;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                }catch(Exception e){
                    Log.e("exc",e.toString());
                }

            }

            @Override
            public void error(String error) {
                progressDialog2.dismiss();
                Toast.makeText(AddRecipeWebScreen.this, error, Toast.LENGTH_SHORT).show();
            }
        }.call();
    }



    void fetchSanjeevKapoorRecpiesDetails(){

        progressDialog =new ProgressDialog(AddRecipeWebScreen.this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        new GetPostClass(API.SANJEEV_KAPOOR_RECIPE, EnumType.GET) {
            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e("sanjeev kapoor response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response.toString().trim());
                    sajneevObj = new GsonBuilder().create().fromJson(response, sanjeevmainModel.class);

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(AddRecipeWebScreen.this, "user_pref", 0);
                    complexPreferences.putObject("sanjeev-recipe", sajneevObj);
                    complexPreferences.commit();



                }catch(Exception e){
                    Log.e("exc",e.toString());
                }

            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
                Toast.makeText(AddRecipeWebScreen.this, error, Toast.LENGTH_SHORT).show();
            }
        }.call();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img1:
                Intent i1 = new Intent(AddRecipeWebScreen.this, SanjeevKapoorScreen.class);
                i1.putExtra("Title", "8-10 MONTHS");
                startActivity(i1);
                break;
            case R.id.img2:
                Intent i2 = new Intent(AddRecipeWebScreen.this, SanjeevKapoorScreen.class);
                i2.putExtra("Title","6-8 MONTHS");
                startActivity(i2);
                break;
            case R.id.img3:
                Intent i3 = new Intent(AddRecipeWebScreen.this, SanjeevKapoorScreen.class);
                i3.putExtra("Title","10-12 MONTHS");
                startActivity(i3);
                break;
            case R.id.img4:
                Intent i4 = new Intent(AddRecipeWebScreen.this, SanjeevKapoorScreen.class);
                i4.putExtra("Title","12-24 MONTHS");
                startActivity(i4);
                break;

        }
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context, ArrayList<String> asr) {
            this.asr = asr;
            activity = context;
        }

        public int getCount() {
            return asr.size();
        }

        public Object getItem(int i) {
            return asr.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(AddRecipeWebScreen.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(getResources().getDimension(R.dimen.spinner_text));
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position).trim());
            txt.setTextColor(Color.parseColor("#000000"));
            txt.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
            return txt;

        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(AddRecipeWebScreen.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(getResources().getDimension(R.dimen.spinner_text));
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_drop_down, 0);
            txt.setText(asr.get(i).trim());
            txt.setTextColor(Color.parseColor("#000000"));
            txt.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
            return txt;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.actionMore:
                openMore();
                break;
            case R.id.actionSettings:
                openSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void openSettings() {

        View menuSettings = findViewById(R.id.actionSettings); // SAME ID AS MENU ID
        String[] names = {"Settings", "Rate Us on Play Store", "Join Us on Facebook", "Share this App with Friends", "Disclaimers", "About Us", "Feedback", "Logout"};
        int[] drawableImage = {R.drawable.icon_home, R.drawable.drawable_profile, R.drawable.drawable_myrecipes, R.drawable.drawable_diary, R.drawable.drawable_friends, R.drawable.icon_nutritional, R.drawable.icon_gloassary, R.drawable.drawable_tour};
        popupWindow1 = new ListPopupWindow(AddRecipeWebScreen.this);
        popupWindow1.setAnchorView(menuSettings);
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(names));

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        popupWindow1.setWidth((int) (width / 1.5));
        popupWindow1.setHeight((int) (height / 1.5));
        popupWindow1.setModal(true);
        popupWindow1.setAdapter(new SettingsAdapter(AddRecipeWebScreen.this, arrayList, drawableImage,true));
        popupWindow1.show();
    }

    private void openMore() {

        View menuItemView = findViewById(R.id.actionMore); // SAME ID AS MENU ID
        String[] names = {"Home", "Profile", "My Recipes", "Diary", "Friends", "Nutritional Guidelines", "Glossary of Ingredients", "Welcome Tour"};
        int[] drawableImage = {R.drawable.icon_home, R.drawable.drawable_profile, R.drawable.drawable_myrecipes, R.drawable.drawable_diary, R.drawable.drawable_friends, R.drawable.icon_nutritional, R.drawable.icon_gloassary, R.drawable.drawable_tour};
        popupWindow2 = new ListPopupWindow(AddRecipeWebScreen.this);

        popupWindow2.setListSelector(new ColorDrawable());
        popupWindow2.setAnchorView(menuItemView);
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(names));

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        popupWindow2.setWidth((int) (width / 1.5));
        popupWindow2.setHeight((int) (height / 1.5));
        popupWindow2.setModal(true);
        popupWindow2.setAdapter(new MoreAdapter(AddRecipeWebScreen.this, arrayList, drawableImage, false));
        popupWindow2.show();


    }

    private void logoutFromApp() {

        Log.e("click", "logout");
        PrefUtils.clearCurrentUser(AddRecipeWebScreen.this);
        LoginManager.getInstance().logOut();


        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isUserLogin", false);
        editor.commit();

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(AddRecipeWebScreen.this, "user_pref", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
        Intent i = new Intent(AddRecipeWebScreen.this, StartScreen.class);
        startActivity(i);
        finish();
    }

    public class SettingsAdapter extends ArrayAdapter<String> {

        // View lookup cache
        private ArrayList<String> users;
        private int[] imgIcons;
        private boolean isSettings;
        Context ctx;

        private class ViewHolder {
            TextView name;
            TextView home;
        }

        public SettingsAdapter(Context context, ArrayList<String> users, int[] img, boolean value) {
            super(context, R.layout.item_popup, users);
            this.users = users;
            this.ctx = context;
            this.imgIcons = img;
            this.isSettings = value;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position

            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_popup, parent, false);

                TextView itemNames = (TextView) convertView.findViewById(R.id.txtItemName);
                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);

                if (isSettings) {
                    itemNames.setText(users.get(position));
                    int col = Color.parseColor("#D13B3D");
                    imgIcon.setColorFilter(col, PorterDuff.Mode.SRC_ATOP);
                    imgIcon.setImageResource(R.drawable.iconsettings);
                    if (position != 0) {
                        imgIcon.setVisibility(View.INVISIBLE);
                    }
                } else {
                    itemNames.setText(users.get(position));
                    imgIcon.setImageResource(imgIcons[position]);
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (position) {
                        case 4:
                            popupWindow1.dismiss();
                            Intent i = new Intent(AddRecipeWebScreen.this, DisclaimerScreen.class);
                            startActivity(i);
                            break;
                        case 5:
                            popupWindow1.dismiss();
                            Intent i2 = new Intent(AddRecipeWebScreen.this, AboutusScreen.class);
                            startActivity(i2);
                            break;
                        case 7:
                            popupWindow1.dismiss();
                            logoutFromApp();
                            break;
                    }
                }
            });

            // Populate the data into the template view using the data object
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public class MoreAdapter extends ArrayAdapter<String> {

        // View lookup cache
        private ArrayList<String> users;
        private int[] imgIcons;
        private boolean isSettings;
        Context ctx;

        private class ViewHolder {
            TextView name;
            TextView home;
        }

        public MoreAdapter(Context context, ArrayList<String> users, int[] img, boolean value) {
            super(context, R.layout.item_popup, users);
            this.users = users;
            this.ctx = context;
            this.imgIcons = img;
            this.isSettings = value;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position

            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_popup, parent, false);

                TextView itemNames = (TextView) convertView.findViewById(R.id.txtItemName);
                ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);

                if (isSettings) {

                    itemNames.setText(users.get(position));
                    int col = Color.parseColor("#D13B3D");
                    imgIcon.setColorFilter(col, PorterDuff.Mode.SRC_ATOP);
                    imgIcon.setImageResource(R.drawable.iconsettings);
                    if (position != 0) {
                        imgIcon.setVisibility(View.INVISIBLE);
                    }
                } else {

                    itemNames.setText(users.get(position));
                    imgIcon.setImageResource(imgIcons[position]);
                }

                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (position) {
                        case 0:
                            popupWindow2.dismiss();
                            Intent pro1 = new Intent(AddRecipeWebScreen.this, HomeScreen.class);
                            startActivity(pro1);
                            break;
                        case 1:
                            popupWindow2.dismiss();
                            Intent pro = new Intent(AddRecipeWebScreen.this, ProfileScreen.class);
                            startActivity(pro);
                            break;
                        case 2:
                            popupWindow2.dismiss();
                            Intent myrecipe = new Intent(AddRecipeWebScreen.this, MyRecipeListScreen.class);
                            startActivity(myrecipe);
                            break;
                        case 3:
                            popupWindow2.dismiss();
                            Intent diary = new Intent(AddRecipeWebScreen.this, DiaryScreen.class);
                            startActivity(diary);
                            break;

                        case 4:
                            popupWindow2.dismiss();
                            Intent iGuide1 = new Intent(AddRecipeWebScreen.this, FriendsScreen.class);
                            startActivity(iGuide1);
                            break;
                        case 5:
                            popupWindow2.dismiss();
                            Intent iGuide = new Intent(AddRecipeWebScreen.this, GuideLinesMainScreen.class);
                            startActivity(iGuide);
                            break;
                        case 6:
                            popupWindow2.dismiss();
                            Intent i = new Intent(AddRecipeWebScreen.this, GlossaryScreenTemp.class);
                            startActivity(i);
                            break;
                        case 7:
                            Intent intent = new Intent(AddRecipeWebScreen.this, WalkThorugh.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            break;
                    }
                }
            });

            return convertView;
        }
    }


}
