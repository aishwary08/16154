package com.software.predictopia;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReportAcitvity extends AppCompatActivity {

    TextView plc_dist_da;
    TextView adm_plc_da;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_acitvity);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        String branch = intent.getStringExtra("branch");
        String loc = intent.getStringExtra("location");
        String coll = intent.getStringExtra("college");

        TableLayout scrollable_table = (TableLayout) findViewById(R.id.scrollable_part);
        int textBackground1 = ContextCompat.getColor(this, R.color.textBackground1);
        int textBackground2 = ContextCompat.getColor(this, R.color.textBackground2);
        int colors[] = new int[]{textBackground2, textBackground1};
        TableRow tr = null;
        List<Double> placementPercent = null;
        List<String> keySet = null;

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        plc_dist_da = (TextView) findViewById(R.id.plc_dist_da);
        plc_dist_da.setText("Placement Distribution in "+loc+": ");
        adm_plc_da = (TextView) findViewById(R.id.adm_plc_da);
        adm_plc_da.setText(coll+" admission and placement value in "+loc+": ");

        try {

            JSONArray jsonArray = new JSONArray(data);
            JSONObject location = (JSONObject) jsonArray.getJSONObject(0).get("location");
            //Iterator<?> keys1 = location.keys();
            //Toast.makeText(this,(String)keys1.next(),Toast.LENGTH_SHORT).show();
            JSONObject locationAdmission = location.getJSONObject("admission");
            JSONObject locationPlacement = location.getJSONObject("placement");
            Iterator<?> keys = locationAdmission.keys();
            keySet = new ArrayList<String>();
            placementPercent = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Log.d("dopost", key);
                keySet.add(key);
                placementPercent.add((double) Math.round(((double) locationPlacement.get(key) / (double) locationAdmission.get(key) * 100.0)) / 100.0);
            }

            for (int k = 0; k < 3; k++) {
                for (int i = 0, j = 1; i < keySet.size(); i++) {
                    if (k == 0)
                        tr = new TableRow(this);
                    else
                        tr = (TableRow) scrollable_table.getChildAt(i);
                    TextView label = new TextView(this);
                    if (i == 0)
                        label.setText(" "+keySet.get(k)+" ");
                    else if (i == 1)
                        label.setText(Math.round((double) locationAdmission.get(keySet.get(k))) + "");
                    else if (i == 2)
                        label.setText(Math.round((double) locationPlacement.get(keySet.get(k))) + "");
                    label.setId(View.generateViewId());
                    label.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    label.setPadding(pixelAsDp(10), pixelAsDp(10), pixelAsDp(10), pixelAsDp(10));
                    label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    label.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                    label.setBackgroundColor(colors[j % 2]);
                    label.setGravity(Gravity.CENTER);
                    label.setTextColor(Color.WHITE);
                    LinearLayout Ll = new LinearLayout(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 1, 0);
                    Ll.addView(label, params);
                    tr.addView((View) Ll);
                    if (k == 0)
                        scrollable_table.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                    j++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < keySet.size(); i++) {
            if (i == 0)
                tr = new TableRow(this);
            TextView label = new TextView(this);
            label.setText(Math.round((placementPercent.get(i) * 100.0)) + "%");
            label.setId(View.generateViewId());
            label.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            label.setPadding(pixelAsDp(10), pixelAsDp(10), pixelAsDp(10), pixelAsDp(10));
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            label.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            label.setBackgroundColor(colors[0]);
            label.setGravity(Gravity.CENTER);
            label.setTextColor(Color.WHITE);
            LinearLayout Ll = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 1, 0);
            Ll.addView(label, params);
            tr.addView((View) Ll);
            if (i == 0)
                scrollable_table.addView(tr, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
        }

        TextView resultBranch = (TextView) findViewById(R.id.result_branch);
        TextView resultAdm = (TextView) findViewById(R.id.result_adm);
        TextView resultPlc = (TextView) findViewById(R.id.result_placement);
        TextView resultPer = (TextView) findViewById(R.id.result_percent);

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(data);
            JSONObject college = (JSONObject) jsonArray.getJSONObject(1).get("college");
            JSONObject collegeAdmission = college.getJSONObject("admission");
            JSONObject collegePlacement = college.getJSONObject("placement");
            resultBranch.setText(branch);
            resultAdm.setText(Math.round((double)collegeAdmission.get(branch))+"");
            resultPlc.setText(Math.round((double)collegePlacement.get(branch))+"");
            resultPer.setText(Math.round(((double)collegePlacement.get(branch)/(double)collegeAdmission.get(branch))*100.0)+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Iterator<?> keys1 = location.keys();
        //Toast.makeText(this,(String)keys1.next(),Toast.LENGTH_SHORT).show();
    }


    int pixelAsDp(int value) {
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (value * scale + 0.5f);
        return dp;
    }
}
