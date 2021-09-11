package com.relevantAds.splashcall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class AddCustomNumber extends AppCompatActivity {

    public EditText numberEditText;
    public Button saveNumberToList;
    int count = 0;



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
        String number = numberEditText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("added_number", number);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home){
            onBackPressed();
            return true;
        }
        return false;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}