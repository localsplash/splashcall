package com.relevantAds.splashcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.pinball83.maskededittext.MaskedEditText;
import com.google.gson.Gson;
import com.relevantAds.splashcall.views.InfoDialogFragment;

import java.util.ArrayList;


public class AddNewNumber extends AppCompatActivity {

    public MaskedEditText numberEditText;
    public Button saveNumberToList;
    int count = 0;
    public ArrayList<String> addedNumbers = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_number_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_add_custom_number);
        setSupportActionBar(toolbar);
        getSupportActionBar()/* or getSupportActionBar() */.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Add Your Number" + "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        numberEditText = findViewById(R.id.add_custom_mobile_number_et);
//        numberEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                int inputLength = numberEditText.getText().toString().length();
//                if ((count <= inputLength)){
//                    if (inputLength == 3){
//                        numberEditText.setText(numberEditText.getText().toString() + " ");
//                        int pos = numberEditText.getText().length();
//                        numberEditText.setSelection(pos);
//                    }
//                    if (inputLength == 7){
//                        Log.d("inside","inside");
//                        numberEditText.setText(numberEditText.getText().toString() + "-");
//                        int pos = numberEditText.getText().length();
//                        numberEditText.setSelection(pos);
//                    }
//
//                }
//
//
//
//            }
//        });
        saveNumberToList = findViewById(R.id.add_custom_mobile_number_button);
        saveNumberToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateField();
            }
        });

    }

    public void validateField(){
        String enteredNumber = numberEditText.getUnmaskedText();
        if (enteredNumber.length() < 10){
            showInfo("Invalid Number Entered","Please enter a valid 10 digit number.");
            return;
        }

        String number = numberEditText.getUnmaskedText();
        Intent intent = new Intent();
        intent.putExtra("added_number", number);
        setResult(RESULT_OK, intent);
        finish();
        addedNumbers.clear();
        addedNumbers.add(number);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home){
            finish();
            return true;
        }
        return false;

    }

    @Override
    public void onBackPressed() {
        finish();

    }
    public void showInfo(String title,String message)
    {
        InfoDialogFragment infoDialogFragment = InfoDialogFragment.newInstance(title, message);
        infoDialogFragment.setOnInfoClickListener(new InfoDialogFragment.InfoDialogListener() {
            @Override
            public void onInfoDialogOkClick() {

            }
        });
        infoDialogFragment.show(getSupportFragmentManager(), "information");
    }
}
