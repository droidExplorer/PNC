package com.example.android.parvarish_nutricalculator.ui.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.parvarish_nutricalculator.R;
import com.example.android.parvarish_nutricalculator.helpers.AdvancedSpannableString;
import com.example.android.parvarish_nutricalculator.model.POJOTableRow;

import java.util.ArrayList;

/**
 * Created by Android on 23-04-2015.
 */
public class MyTableView extends TableLayout {

    private Context ctx;
    private String[] colors = {"#BCC221", "#FDBF03", "#F48222","#FDBF03"};
    private ArrayList<Float> weights;

    public MyTableView(Context context) {
        super(context);
        this.ctx = context;
    }

    public void setWeights(ArrayList<Float> weights ){
        this.weights = weights;
    }

    public void addRow(ArrayList<POJOTableRow> values) {

        TableRow row = new TableRow(ctx);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        for (int i = 0; i < values.size(); i++) {

            TableRow.LayoutParams tvPar = new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, weights.get(i));
            TextView txt = new TextView(ctx);
            txt.setLayoutParams(tvPar);
            txt.setPadding(8, 8, 8, 8);
            txt.setMaxLines(2);
            txt.setTextColor(values.get(i).color);
            txt.setText(values.get(i).value);
            txt.setGravity(Gravity.LEFT);
           // txt.setSingleLine(true);
            txt.setTextSize(ctx.getResources().getDimension(R.dimen.small_text2));
            txt.setBackgroundColor(Color.parseColor(colors[i]));
            row.addView(txt);

        }
        this.addView(row);
        requestLayout();

    }
    public void addDivider() {

        TableRow row = new TableRow(ctx);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TableRow.LayoutParams tvPar = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,3, 1f);
        tvPar.leftMargin = 16;
        tvPar.rightMargin = 16;

        View v = new View(ctx);
        v.setLayoutParams(tvPar);
        v.setBackgroundColor(Color.WHITE);
        row.addView(v);

        this.addView(row);
    }




}
