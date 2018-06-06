package com.software.predictopia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ValidationResponse, TextWatcher {

    private Button myButton;
    private Spinner spinnerCollegeName, spinnerLocation, spinnerBranch, spinnerNba;
    private TextInputLayout inputRank;
    private TextInputLayout inputBranchCutOff;
    private TextInputLayout inputBranchPlacement;
    private TextInputLayout numberOfCompanies;
    private TextInputLayout avgSalaryPackage;
    private SessionManager session;
    private TextInputLayout partnerCompanies;
    private ArrayAdapter adapter;
    private PopupWindow mPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCollegeName = (Spinner) findViewById(R.id.input_college_name);
        spinnerLocation = (Spinner) findViewById(R.id.input_location);
        spinnerBranch = (Spinner) findViewById(R.id.input_branch_name);
        spinnerNba = (Spinner) findViewById(R.id.input_nba_accreditation);

        inputRank = ((TextInputLayout) findViewById(R.id.input_rank));
        inputBranchCutOff = ((TextInputLayout) findViewById(R.id.input_branch_cut_off));
        inputBranchPlacement = ((TextInputLayout) findViewById(R.id.input_branch_placement));
        numberOfCompanies = ((TextInputLayout) findViewById(R.id.input_number_of_companies));
        avgSalaryPackage = ((TextInputLayout) findViewById(R.id.input_avg_salary_package));
        partnerCompanies = (TextInputLayout) findViewById(R.id.input_partner_companies);

        session = new SessionManager(MainActivity.this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        adapter = ArrayAdapter.createFromResource(this, R.array.college_abd, android.R.layout.simple_spinner_dropdown_item);
        spinnerCollegeName.setAdapter(adapter);

        final String[] clgAbd = getApplicationContext().getResources().getStringArray(R.array.college_abd);
        final String[] clgVaro = getApplicationContext().getResources().getStringArray(R.array.college_vardora);
        final String[] clgGandhi = getApplicationContext().getResources().getStringArray(R.array.college_gandhi);
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                if (value.equals("AHMEDABAD")) {
                    ArrayAdapter new_adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.college_abd, android.R.layout.simple_spinner_dropdown_item);
                    spinnerCollegeName.setAdapter(new_adapter);
                } else if (value.equals("GANDHINAGAR")) {
                    ArrayAdapter new_adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.college_gandhi, android.R.layout.simple_spinner_dropdown_item);
                    spinnerCollegeName.setAdapter(new_adapter);
                } else if (value.equals("VADODARA")) {
                    ArrayAdapter new_adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.college_vardora, android.R.layout.simple_spinner_dropdown_item);
                    spinnerCollegeName.setAdapter(new_adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        inputRank.getEditText().addTextChangedListener(this);
        inputBranchCutOff.getEditText().addTextChangedListener(this);
        inputBranchPlacement.getEditText().addTextChangedListener(this);
        numberOfCompanies.getEditText().addTextChangedListener(this);
        avgSalaryPackage.getEditText().addTextChangedListener(this);
        partnerCompanies.getEditText().addTextChangedListener(this);

        final int year = Calendar.getInstance().get(Calendar.YEAR);
        myButton = (Button) findViewById(R.id.btn_signup);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valid()) {
                    JSONObject admissionData = new JSONObject();
                    String[] admissionKeys = MainActivity.this.getResources().getStringArray(R.array.admission);
                    try {
                        admissionData.put(admissionKeys[0], spinnerCollegeName.getSelectedItem());
                        admissionData.put(admissionKeys[1], year);
                        admissionData.put(admissionKeys[2], spinnerLocation.getSelectedItem());
                        admissionData.put(admissionKeys[3], inputRank.getEditText().getText());
                        admissionData.put(admissionKeys[4], spinnerBranch.getSelectedItem());
                        admissionData.put(admissionKeys[5], inputBranchCutOff.getEditText().getText());
                        admissionData.put(admissionKeys[6], inputBranchPlacement.getEditText().getText());
                        admissionData.put(admissionKeys[7], spinnerNba.getSelectedItem());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject placementData = new JSONObject();
                    String[] placementKeys = MainActivity.this.getResources().getStringArray(R.array.placement);
                    try {
                        placementData.put(placementKeys[0], spinnerCollegeName.getSelectedItem());
                        placementData.put(placementKeys[1], year);
                        placementData.put(placementKeys[2], spinnerLocation.getSelectedItem());
                        placementData.put(placementKeys[3], numberOfCompanies.getEditText().getText());
                        placementData.put(placementKeys[4], partnerCompanies.getEditText().getText());
                        placementData.put(placementKeys[5], spinnerBranch.getSelectedItem());
                        placementData.put(placementKeys[7], avgSalaryPackage.getEditText().getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONArray data = new JSONArray();
                    try {
                        data.put(new JSONObject().put("admission", admissionData));
                        data.put(new JSONObject().put("placement", placementData));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("data", data.toString());
                    GetResult conn = new GetResult(MainActivity.this);
                    showProgress(true);
                    conn.delegate = MainActivity.this;
                    conn.execute("11.24.26.146", "8888", data.toString());

                }
            }
        });

    }

    boolean valid() {
        boolean valid = true;
        String rank = inputRank.getEditText().getText().toString();
        String branchCutOff = inputBranchCutOff.getEditText().getText().toString();
        String branchPlacement = inputBranchPlacement.getEditText().getText().toString();
        String numberOfCompanies = this.numberOfCompanies.getEditText().getText().toString();
        String avgSalaryPkg = this.avgSalaryPackage.getEditText().getText().toString();
        String noPartnerComp = partnerCompanies.getEditText().getText().toString();

        if (branchPlacement.isEmpty()) {
            inputBranchPlacement.setError("Rank can't be blank");
            if (valid) inputBranchPlacement.getEditText().requestFocus();
            valid = false;
        } else {
            inputBranchPlacement.setError(null);
        }
        if (!branchPlacement.isEmpty()) {
            int bP = Integer.parseInt(branchPlacement);
            if (bP < 0 || bP > 100) {
                inputBranchPlacement.setError("Please enter value between 0-100");
                if (valid) inputBranchPlacement.requestFocus();
                valid = false;
            } else
                inputBranchPlacement.setError(null);
        }

        if (rank.isEmpty()) {
            inputRank.setError("Rank can't be blank");
            if (valid) inputRank.requestFocus();
            valid = false;
        } else {
            inputRank.setError(null);
        }

        if (branchCutOff.isEmpty()) {
            inputBranchCutOff.setError("This field can't be blank");
            if (valid) inputBranchCutOff.requestFocus();
            valid = false;
        } else {
            inputBranchCutOff.setError(null);
        }

        if (numberOfCompanies.isEmpty()) {
            this.numberOfCompanies.setError("This field can't be blank");
            if (valid) this.numberOfCompanies.requestFocus();
            valid = false;
        } else {
            this.numberOfCompanies.setError(null);
        }

        if (avgSalaryPkg.isEmpty()) {
            avgSalaryPackage.setError("Average Salary Package can't be blank");
            if (valid) avgSalaryPackage.requestFocus();
            valid = false;
        } else {
            avgSalaryPackage.setError(null);
        }

        if (noPartnerComp.isEmpty()) {
            partnerCompanies.setError("Partner can't be blank");
            if (valid) partnerCompanies.requestFocus();
            valid = false;
        } else {
            partnerCompanies.setError(null);
        }

        if (spinnerLocation.getSelectedItem().toString().equals("Select Location")) {
            TextView errorText = (TextView) spinnerLocation.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Select Location");
            valid = false;
        }

        if (spinnerCollegeName.getSelectedItem().toString().equals("Select College")) {
            TextView errorText = (TextView) spinnerCollegeName.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Select College");
            valid = false;
        }

        if (spinnerBranch.getSelectedItem().toString().equals("Select Branch")) {
            TextView errorText = (TextView) spinnerBranch.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Select Branch");
            valid = false;
        }
        return valid;
    }

    @Override
    public void response(boolean result, String s) {
        //showProgress(false);
        if (result) {
            Intent i = new Intent(MainActivity.this, ReportAcitvity.class);
            i.putExtra("data", s);
            i.putExtra("branch", String.valueOf(spinnerBranch.getSelectedItem()));
            i.putExtra("location", String.valueOf(spinnerLocation.getSelectedItem()));
            i.putExtra("college", String.valueOf(spinnerCollegeName.getSelectedItem()));
            startActivity(i);
        } else
            Snackbar.make(findViewById(R.id.input_form), Html.fromHtml("<b> " + s + " </b>"), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String branchPlacement = inputBranchPlacement.getEditText().getText().toString();

        if (editable == inputBranchPlacement.getEditText().getEditableText()) {
            if (branchPlacement.isEmpty()) {
                inputBranchPlacement.setError("Rank can't be blank");
                inputBranchPlacement.requestFocus();
            } else {
                inputBranchPlacement.setError(null);
            }
            if (!branchPlacement.isEmpty()) {
                int bP = Integer.parseInt(branchPlacement);
                if (bP < 0 || bP > 100) {
                    inputBranchPlacement.setError("Please enter value between 0-100");
                    inputBranchPlacement.requestFocus();
                } else
                    inputBranchPlacement.setError(null);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_out:
                session.logoutUser();
                finish();
                break;
            case R.id.menu_ip: {
                final String[] m_Text = {"192.168.1.8"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("IP Address");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(URLS.Script_URL);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text[0] = input.getText().toString();
                        URLS.Script_URL = m_Text[0];
                        Log.e("url", URLS.Script_URL);
                    }
                });
                builder.show();
                break;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return true;
    }

    public void showProgress(final boolean show) {
        View popupView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.my_progress, null);
        if (show) {
            Point size = new Point();
            this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = size.y;
            mPopup = new PopupWindow(popupView, width, height);
            mPopup.showAtLocation(findViewById(R.id.input_form), Gravity.CENTER, 0, 0);
        } else
            mPopup.dismiss();
    }
}
