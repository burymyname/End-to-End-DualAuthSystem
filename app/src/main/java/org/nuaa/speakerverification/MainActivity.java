package org.nuaa.speakerverification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.nuaa.speakerverification.dataTrans.DataTransferActivity;
import org.nuaa.speakerverification.dataTrans.Setting.SettingsActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        createPreferences();
    }

    private void initView() {
        ((Button) findViewById(R.id.dataTransfer)).setOnClickListener(this);
        ((Button) findViewById(R.id.speakerVerification)).setOnClickListener(this);
        ((Button) findViewById(R.id.setting)).setOnClickListener(this);

    }

    private void createPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains(SettingsActivity.KEY_START_FREQUENCY)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SettingsActivity.KEY_START_FREQUENCY,
                    SettingsActivity.DEF_START_FREQUENCY);
            editor.putString(SettingsActivity.KEY_END_FREQUENCY,
                    SettingsActivity.DEF_END_FREQUENCY);
            editor.putString(SettingsActivity.KEY_BIT_PER_TONE,
                    SettingsActivity.DEF_BIT_PER_TONE);
            editor.putBoolean(SettingsActivity.KEY_ERROR_DETECTION,
                    SettingsActivity.DEF_ERROR_DETECTION);
            editor.putString(SettingsActivity.KEY_ERROR_BYTE_NUM,
                    SettingsActivity.DEF_ERROR_BYTE_NUM);
            editor.commit();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dataTransfer:
                Intent dataIntent = new Intent(MainActivity.this, DataTransferActivity.class);
                startActivity(dataIntent);
                break;

            case R.id.speakerVerification:
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                break;

            case R.id.setting:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);

            default: break;
        }
    }
}
