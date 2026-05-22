package com.calculatoristul.keyboard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.content.Intent;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.calculatoristul.keyboard.R;

public class SettingsActivity extends Activity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("CalculatoristulPrefs", Context.MODE_PRIVATE);
        RadioGroup themeGroup = findViewById(R.id.themeRadioGroup);

        String currentTheme = prefs.getString("theme", "light");
        if (currentTheme.equals("dark")) {
            themeGroup.check(R.id.radioDark);
        } else if (currentTheme.equals("terminal")) {
            themeGroup.check(R.id.radioTerminal);
        } else {
            themeGroup.check(R.id.radioLight);
        }

        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedTheme = "light";
            if (checkedId == R.id.radioDark) selectedTheme = "dark";
            if (checkedId == R.id.radioTerminal) selectedTheme = "terminal";

            prefs.edit().putString("theme", selectedTheme).apply();
        });

        Button btnEnable = findViewById(R.id.btnEnableKeyboard);
        Button btnSelect = findViewById(R.id.btnSelectKeyboard);

        btnEnable.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            startActivity(intent);
        });

        btnSelect.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showInputMethodPicker();
            }
        });
    }
}
