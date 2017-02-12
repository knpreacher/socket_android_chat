package com.example.knp.socketchat;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * Created by knp on 2/8/17.
 */

public class PrefActivity extends PreferenceActivity {
    EditTextPreference etpAddress;
    EditTextPreference etpPort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        etpAddress = (EditTextPreference) findPreference("address");
        etpPort = (EditTextPreference) findPreference("port");
        //etpAddress.setSummary(getPreferences(MODE_PRIVATE).getString("address",""));
        //etpPort.setSummary(Integer.toString(getPreferences(MODE_PRIVATE).getInt("port",0)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        //etpAddress.setSummary(getPreferences(MODE_PRIVATE).getString("address",""));
        //etpPort.setSummary(Integer.toString(getPreferences(MODE_PRIVATE).getInt("port",0)));
    }
}
