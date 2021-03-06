package com.example.android.parvarish_nutricalculator.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.parvarish_nutricalculator.R;
import com.example.android.parvarish_nutricalculator.custom.ComplexPreferences;
import com.example.android.parvarish_nutricalculator.helpers.API;
import com.example.android.parvarish_nutricalculator.helpers.EnumType;
import com.example.android.parvarish_nutricalculator.helpers.GetPostClass;
import com.example.android.parvarish_nutricalculator.helpers.NutritionCalculationSingleMyRecipe;
import com.example.android.parvarish_nutricalculator.helpers.PrefUtils;
import com.example.android.parvarish_nutricalculator.model.POJOTableRow;
import com.example.android.parvarish_nutricalculator.model.freindFeedsMainModel;
import com.example.android.parvarish_nutricalculator.model.glossaryDescription;
import com.example.android.parvarish_nutricalculator.model.glossaryIngredient;
import com.example.android.parvarish_nutricalculator.model.icmrMainModel;
import com.example.android.parvarish_nutricalculator.model.myrecipeModel;
import com.example.android.parvarish_nutricalculator.model.userModel;
import com.example.android.parvarish_nutricalculator.ui.widgets.HUD;
import com.example.android.parvarish_nutricalculator.ui.widgets.MyTableView;
import com.facebook.login.LoginManager;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class ViewFriendsRecipe extends ActionBarActivity {
    userModel currentUser;
    private HUD progressDialog;
    ListPopupWindow popupWindow1,popupWindow2;
    int listPos;
    private LinearLayout linearTableDetails;
    private Toolbar toolbar;
    private TextView txtTitle,txtING,txtMethod,btnSaveRecipe,btnEditRecipe,txtServing,txtAgeGroup;
    freindFeedsMainModel frndfeedObj;
   // myrecipeModel myrecipe;
    glossaryDescription ingdredient;
    icmrMainModel icmrOBJ;
    ImageView photoImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_freinds_recipie);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("PARVARISH NUTRI CALCULATOR");
//            toolbar.setLogo(R.drawable.logo);
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        init();

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ViewFriendsRecipe.this, "user_pref", 0);
        currentUser = complexPreferences.getObject("current-user", userModel.class);


        linearTableDetails = (LinearLayout)findViewById(R.id.linearTableFriendRecipeDetail);

        fetchIngredientsdetails();



        btnSaveRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSaveRecipeTOMyRecipe();
            }
        });



    }

    private void init(){

        listPos = getIntent().getIntExtra("pos",0);
        listPos -=1; // bcz position must be with zero


        txtServing = (TextView)findViewById(R.id.txtServing);
        txtAgeGroup= (TextView)findViewById(R.id.txtAgeGroup);

        txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtING  = (TextView)findViewById(R.id.txtING);
        txtMethod = (TextView)findViewById(R.id.txtMethod);
        btnSaveRecipe = (TextView)findViewById(R.id.btnSaveRecipe);
        btnEditRecipe = (TextView)findViewById(R.id.btnEditRecipe);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ViewFriendsRecipe.this, "user_pref", 0);
        frndfeedObj = complexPreferences.getObject("current-frnd-recipe", freindFeedsMainModel.class);


        ComplexPreferences complexPreferences2 = ComplexPreferences.getComplexPreferences(ViewFriendsRecipe.this, "user_pref", 0);
        complexPreferences2.putObject("current-myrecipe", frndfeedObj);
        complexPreferences2.commit();



        txtTitle.setText(frndfeedObj.data.Recipe.get(listPos).name);


      //  txtTitle.setText(myrecipe.data.Recipe.get(listPos).name);
        txtServing.setText("Servings : " + frndfeedObj.data.Recipe.get(listPos).no_of_servings);
        txtAgeGroup.setText("Age of Baby : " + frndfeedObj.data.Recipe.get(listPos).age_group);


        Bitmap recipeimg = PrefUtils.returnBitmapImage(frndfeedObj.data.Recipe.get(listPos).photo_url);

        try {
            Log.e("### base 64", frndfeedObj.data.Recipe.get(listPos).photo_url);
            photoImg.setImageBitmap(recipeimg);
        }catch (Exception e){
            Log.e("### EXCinIMG",e.toString());
        }

       /* Glide.with(MyRecipeViewScreen.this).load(recipeimg)
                .into(photoImg);
*/

        txtMethod.setText(Html.fromHtml(frndfeedObj.data.Recipe.get(listPos).method));


        btnEditRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(ViewFriendsRecipe.this, MyRecipeEditScreen.class);
                i.putExtra("listPos", listPos);
                startActivity(i);
            }
        });

    }


    private void fetchIngredientsdetails(){

        progressDialog = new HUD(ViewFriendsRecipe.this,android.R.style.Theme_Translucent_NoTitleBar);
        progressDialog.show();


        new GetPostClass(API.GLOSSARY_INGREDIENTS, EnumType.GET) {
            @Override
            public void response(String response) {
                // Log.e("ingridents response", response);
                progressDialog.dismiss();
                try {
                    //  JSONObject jsonObject = new JSONObject(response.toString().trim());/*
                    ingdredient = new GsonBuilder().create().fromJson(response, glossaryDescription.class);

                    String ing="";

                    ArrayList<glossaryIngredient> arr = ingdredient.returnAllIngredients();
                    for(int i = 0; i < frndfeedObj.data.Recipe.get(listPos).RecipeIngredientList.size(); i++) {

                        for (int j = 0; j < arr.size(); j++) {
                            if (arr.get(j).id.equalsIgnoreCase(frndfeedObj.data.Recipe.get(listPos).RecipeIngredientList.get(i).RecipeIngredient.ingredient_id)) {
                                ing +=frndfeedObj.data.Recipe.get(listPos).RecipeIngredientList.get(i).RecipeIngredient.quantity+" "+arr.get(j).name+" "+frndfeedObj.data.Recipe.get(listPos).RecipeIngredientList.get(i).RecipeIngredient.unit+"\n";

                                Log.e("#### my recipe ing id", frndfeedObj.data.Recipe.get(listPos).RecipeIngredientList.get(i).RecipeIngredient.ingredient_id);
                                Log.e("#### org ing id", arr.get(j).id);
                                Log.e("#### ing", arr.get(j).name);
                            }
                        }
                    }


                    txtING.setText(ing);

                }catch(Exception e){
                    Log.e("exc",e.toString());
                }

                fetchICMRdetails();


            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
                Toast.makeText(ViewFriendsRecipe.this, error, Toast.LENGTH_SHORT).show();
            }
        }.call();
    }


    private void fetchICMRdetails(){

        progressDialog = new HUD(ViewFriendsRecipe.this,android.R.style.Theme_Translucent_NoTitleBar);
        progressDialog.show();


        new GetPostClass(API.FETCH_IMCR, EnumType.GET) {
            @Override
            public void response(String response) {
                Log.e("icmr response", response);
                progressDialog.dismiss();
                try {
                    //  JSONObject jsonObject = new JSONObject(response.toString().trim());/*
                    icmrOBJ = new GsonBuilder().create().fromJson(response, icmrMainModel.class);


                }catch(Exception e){
                    Log.e("exc",e.toString());
                }



                NutritionCalculationSingleMyRecipe executer = new NutritionCalculationSingleMyRecipe(ViewFriendsRecipe.this,frndfeedObj.data.Recipe.get(listPos).RecipeIngredientList,ingdredient,frndfeedObj.data.Recipe.get(listPos).no_of_servings);
                executer.startCalculation();

                executer.setOnCalculationResult(new NutritionCalculationSingleMyRecipe.OnCalculationResult() {
                    @Override
                    public void onResult(float energy, float protien, float fat, float calcium, float iron) {
                        Log.e("Result Energy ", "" + energy);
                        Log.e("Result protien ", "" + protien);
                        Log.e("Result fat ", "" + fat);
                        Log.e("Result calcium ", "" + calcium);
                        Log.e("Result iron ", "" + iron);


                        addTableView(energy, protien, fat, calcium, iron);
                    }
                });




            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
                Toast.makeText(ViewFriendsRecipe.this, error, Toast.LENGTH_SHORT).show();
            }
        }.call();
    }


    private void addTableView(float energy, float protien, float fat, float calcium, float iron){

        int ICMR_GET_POS = 0;
        for(int i=0;i<icmrOBJ.data.size();i++){
            if(icmrOBJ.data.get(i).IcmrRecommended.age_group.trim().equalsIgnoreCase(frndfeedObj.data.Recipe.get(listPos).age_group)){
                ICMR_GET_POS = i;
                break;
            }
        }



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        MyTableView tableView = new MyTableView(ViewFriendsRecipe.this);
        tableView.setPadding(8,8,8,8);

        // setting weights recommanded
        ArrayList<Float> weights = new ArrayList<>();
        weights.add(1f);
        weights.add(1f);
        weights.add(0.5f);
        tableView.setWeights(weights);

        linearTableDetails.addView(tableView, params);

        ArrayList<POJOTableRow> values = new ArrayList<>();
        values.add(new POJOTableRow("Nutrients",Color.BLACK));
        values.add(new POJOTableRow("ICMR Recommandation", Color.BLACK));
        values.add(new POJOTableRow("Recipe Values", Color.BLACK));

        tableView.addRow(values);

        tableView.addDivider();

        ArrayList<POJOTableRow> values2 = new ArrayList<>();
        values2.add(new POJOTableRow("Energy (kcal)",Color.WHITE));
        values2.add(new POJOTableRow(icmrOBJ.data.get(0).IcmrRecommended.energy.equalsIgnoreCase("")?"0":icmrOBJ.data.get(ICMR_GET_POS).IcmrRecommended.energy,Color.WHITE));
        values2.add(new POJOTableRow(String.format("%.2f",energy),Color.WHITE));


        ArrayList<POJOTableRow> values3 = new ArrayList<>();
        values3.add(new POJOTableRow("Protein (g)",Color.WHITE));
        values3.add(new POJOTableRow((icmrOBJ.data.get(0).IcmrRecommended.protein.equalsIgnoreCase("")?"0":icmrOBJ.data.get(ICMR_GET_POS).IcmrRecommended.protein),Color.WHITE));
        values3.add(new POJOTableRow(String.format("%.2f",protien),Color.WHITE));


        ArrayList<POJOTableRow> values4 = new ArrayList<>();
        values4.add(new POJOTableRow("Calcium (mg)",Color.WHITE));
        values4.add(new POJOTableRow((icmrOBJ.data.get(0).IcmrRecommended.calcium.equalsIgnoreCase("")?"0":icmrOBJ.data.get(ICMR_GET_POS).IcmrRecommended.calcium),Color.WHITE));
        values4.add(new POJOTableRow(String.format("%.2f",calcium),Color.WHITE));


        ArrayList<POJOTableRow> values5 = new ArrayList<>();
        values5.add(new POJOTableRow("Iron (mg)",Color.WHITE));
        values5.add(new POJOTableRow((icmrOBJ.data.get(0).IcmrRecommended.iron.equalsIgnoreCase("") ? "0" : icmrOBJ.data.get(ICMR_GET_POS).IcmrRecommended.iron),Color.WHITE));
        values5.add(new POJOTableRow(String.format("%.2f", iron),Color.WHITE));

        ArrayList<POJOTableRow> values6 = new ArrayList<>();
        values6.add(new POJOTableRow("Zinc (mg)",Color.WHITE));
        values6.add(new POJOTableRow((icmrOBJ.data.get(0).IcmrRecommended.fat.equalsIgnoreCase("")?"0":icmrOBJ.data.get(ICMR_GET_POS).IcmrRecommended.fat),Color.WHITE));
        values6.add(new POJOTableRow(String.format("%.2f",fat),Color.WHITE));


        tableView.addRow(values2);
        tableView.addRow(values3);
        tableView.addRow(values4);
        tableView.addRow(values5);
        tableView.addRow(values6);

    }


    private void processSaveRecipeTOMyRecipe(){
        progressDialog =new HUD(ViewFriendsRecipe.this,android.R.style.Theme_Translucent_NoTitleBar);
        progressDialog.show();

        Log.e("Copy recipe link",API.COPY_RECIPE+currentUser.data.id);
        new GetPostClass(API.COPY_RECIPE+currentUser.data.id, EnumType.GET) {
            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e("my recipe response", response);

                try {

                Toast.makeText(ViewFriendsRecipe.this,"Webservice Rsponse - "+response,Toast.LENGTH_SHORT).show();

                }catch(Exception e){
                    Log.e("exc",e.toString());
                }

            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
                Toast.makeText(ViewFriendsRecipe.this, error, Toast.LENGTH_SHORT).show();
            }
        }.call();
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
        popupWindow1 = new ListPopupWindow(ViewFriendsRecipe.this);
        popupWindow1.setAnchorView(menuSettings);
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(names));

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        popupWindow1.setWidth((int) (width / 1.5));
        popupWindow1.setHeight((int) (height / 1.5));
        popupWindow1.setModal(true);
        popupWindow1.setAdapter(new SettingsAdapter(ViewFriendsRecipe.this, arrayList, drawableImage,true));
        popupWindow1.show();
    }

    private void openMore() {

        View menuItemView = findViewById(R.id.actionMore); // SAME ID AS MENU ID
        String[] names = {"Home", "Profile", "My Recipes", "Diary", "Friends", "Nutritional Guidelines", "Glossary of Ingredients", "Welcome Tour"};
        int[] drawableImage = {R.drawable.icon_home, R.drawable.drawable_profile, R.drawable.drawable_myrecipes, R.drawable.drawable_diary, R.drawable.drawable_friends, R.drawable.icon_nutritional, R.drawable.icon_gloassary, R.drawable.drawable_tour};
        popupWindow2 = new ListPopupWindow(ViewFriendsRecipe.this);

        popupWindow2.setListSelector(new ColorDrawable());
        popupWindow2.setAnchorView(menuItemView);
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(names));

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        popupWindow2.setWidth((int) (width / 1.5));
        popupWindow2.setHeight((int) (height / 1.5));
        popupWindow2.setModal(true);
        popupWindow2.setAdapter(new MoreAdapter(ViewFriendsRecipe.this, arrayList, drawableImage, false));
        popupWindow2.show();


    }

    private void logoutFromApp() {

        Log.e("click", "logout");
        PrefUtils.clearCurrentUser(ViewFriendsRecipe.this);
        LoginManager.getInstance().logOut();


        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isUserLogin", false);
        editor.commit();

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ViewFriendsRecipe.this, "user_pref", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
        Intent i = new Intent(ViewFriendsRecipe.this, StartScreen.class);
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
                            Intent i = new Intent(ViewFriendsRecipe.this, DisclaimerScreen.class);
                            startActivity(i);
                            break;
                        case 5:
                            popupWindow1.dismiss();
                            Intent i2 = new Intent(ViewFriendsRecipe.this, AboutusScreen.class);
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
                            Intent pro1 = new Intent(ViewFriendsRecipe.this, HomeScreen.class);
                            startActivity(pro1);
                            break;
                        case 1:
                            popupWindow2.dismiss();
                            Intent pro = new Intent(ViewFriendsRecipe.this, ProfileScreen.class);
                            startActivity(pro);
                            break;
                        case 2:
                            popupWindow2.dismiss();
                            Intent myrecipe = new Intent(ViewFriendsRecipe.this, MyRecipeListScreen.class);
                            startActivity(myrecipe);
                            break;
                        case 3:
                            popupWindow2.dismiss();
                            Intent diary = new Intent(ViewFriendsRecipe.this, DiaryScreen.class);
                            startActivity(diary);
                            break;

                        case 4:
                            popupWindow2.dismiss();
                            Intent iGuide1 = new Intent(ViewFriendsRecipe.this, FriendsScreen.class);
                            startActivity(iGuide1);
                            break;
                        case 5:
                            popupWindow2.dismiss();
                            Intent iGuide = new Intent(ViewFriendsRecipe.this, GuideLinesMainScreen.class);
                            startActivity(iGuide);
                            break;
                        case 6:
                            popupWindow2.dismiss();
                            Intent i = new Intent(ViewFriendsRecipe.this, GlossaryScreenTemp.class);
                            startActivity(i);
                            break;
                        case 7:
                            Intent intent = new Intent(ViewFriendsRecipe.this, WalkThorugh.class);
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



//end of main classs
}
