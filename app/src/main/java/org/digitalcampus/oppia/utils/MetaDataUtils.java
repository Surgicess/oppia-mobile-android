/*
 * This file is part of OppiaMobile - https://digital-campus.org/
 *
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */
package org.digitalcampus.oppia.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.application.MobileLearning;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MetaDataUtils {

    public static final String TAG = MetaDataUtils.class.getSimpleName();
    private String networkProvider;
    private String deviceId;
    private String simSerial;
    private Context ctx;

    public MetaDataUtils(Context ctx) {
        this.ctx = ctx;
        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            networkProvider = manager.getNetworkOperatorName();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ctx.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                    deviceId = "not-granted";
                    simSerial = "not-granted";
                    return;
                }
            }

            deviceId = manager.getDeviceId();
            simSerial = manager.getSimSerialNumber();
        }

    }

    private String getNetworkProvider() {
        return networkProvider;
    }

    private String getDeviceId() {
        return deviceId;
    }

    private String getSimSerial() {
        return simSerial;
    }

    private float getBatteryLevel() {
        Intent batteryIntent = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    public void saveMetaData(JSONObject metadata, SharedPreferences prefs) throws JSONException {
        Editor editor = prefs.edit();
        Iterator<?> keys = metadata.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            editor.putBoolean(getMetadataPref(key), metadata.getBoolean(key));
        }
        editor.apply();
    }

    private String getMetadataPref(String metadataKey){
        return PrefsActivity.PREF_METADATA + "_" + metadataKey;
    }

    public JSONObject getMetaData(JSONObject json) throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (prefs.getBoolean(getMetadataPref(PrefsActivity.PREF_METADATA_NETWORK), MobileLearning.INCLUDE_METADATA_NETWORK)) {
            json.put("network", this.getNetworkProvider());
        }
        if (prefs.getBoolean(getMetadataPref(PrefsActivity.PREF_METADATA_DEVICE_ID), MobileLearning.INCLUDE_METADATA_DEVICE_ID)) {
            json.put("deviceid", this.getDeviceId());
        }
        if (prefs.getBoolean(getMetadataPref(PrefsActivity.PREF_METADATA_SIM_SERIAL), MobileLearning.INCLUDE_METADATA_SIM_SERIAL)) {
            json.put("simserial", this.getSimSerial());
        }
        if (prefs.getBoolean(getMetadataPref(PrefsActivity.PREF_METADATA_WIFI_ON), MobileLearning.INCLUDE_METADATA_WIFI_ON)) {
            json.put("wifion", ConnectionUtils.isOnWifi(ctx));
        }
        if (prefs.getBoolean(getMetadataPref(PrefsActivity.PREF_METADATA_NETWORK_CONNECTED), MobileLearning.INCLUDE_METADATA_NETWORK_CONNECTED)) {
            json.put("netconnected", ConnectionUtils.isNetworkConnected(ctx));
        }
        if (prefs.getBoolean(getMetadataPref(PrefsActivity.PREF_METADATA_BATTERY_LEVEL), MobileLearning.INCLUDE_METADATA_BATTERY_LEVEL)) {
            json.put("battery", this.getBatteryLevel());
        }
        if (prefs.getBoolean(getMetadataPref(PrefsActivity.PREF_METADATA_GPS), MobileLearning.INCLUDE_METADATA_GPS)) {
            json.put("gps", "0,0");
        }
        return json;
    }
}
