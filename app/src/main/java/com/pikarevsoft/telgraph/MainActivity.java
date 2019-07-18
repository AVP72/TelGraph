package com.pikarevsoft.telgraph;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import static com.pikarevsoft.telgraph.Public.checkGraph;
import static com.pikarevsoft.telgraph.Public.nameY0;
import static com.pikarevsoft.telgraph.Public.nameY1;
import static com.pikarevsoft.telgraph.Public.showY0;
import static com.pikarevsoft.telgraph.Public.showY1;

public class MainActivity extends AppCompatActivity {

    int width, height;
    com.pikarevsoft.telgraph.StartGraph startGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startGraph = findViewById(R.id.startView);

        CheckBox checkBoxY0 = findViewById(R.id.checkBoxY0);
        CheckBox checkBoxY1 = findViewById(R.id.checkBoxY1);

        checkBoxY0.setText(nameY0);
        checkBoxY1.setText(nameY1);

        checkBoxY0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showY0 = isChecked;
                startGraph.invalidate();
            }
        });
        checkBoxY1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showY1 = isChecked;
                startGraph.invalidate();
            }
        });

        RadioButton radioButton1 = findViewById(R.id.radioButton1);
        RadioButton radioButton2 = findViewById(R.id.radioButton2);
        RadioButton radioButton3 = findViewById(R.id.radioButton3);
        RadioButton radioButton4 = findViewById(R.id.radioButton4);

        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGraph = 0;
                startGraph.readFile();
                startGraph.setPaint();
                startGraph.invalidate();
            }
        });
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGraph = 1;
                startGraph.readFile();
                startGraph.setPaint();
                startGraph.invalidate();
            }
        });
        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGraph = 2;
                startGraph.readFile();
                startGraph.setPaint();
                startGraph.invalidate();
            }
        });
        radioButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGraph = 3;
                startGraph.readFile();
                startGraph.setPaint();
                startGraph.invalidate();
            }
        });
    }

/*
    void toLog(String s){
        if (s == null) s = "null";
        Log.i("avp", ""+s);
    }

*/
}
