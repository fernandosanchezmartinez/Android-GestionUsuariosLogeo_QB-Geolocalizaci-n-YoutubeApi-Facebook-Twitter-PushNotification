package damp_2.utad.pushnotification_lib;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBSubscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class PushNotificationAdmin{

    private static final String TAG = "PushNotificationAdmin";
    private static final String LOG_TAG="PushNotificationAdmin";

    private GoogleCloudMessaging googleCloudMessaging;
    private String regId;
    private Activity activity;
    private String sProjectNumber="";

    private ArrayList<PushNotificationsAdminListener> listeners=new ArrayList<PushNotificationsAdminListener>();

    public PushNotificationAdmin(Activity activity,String sProjectNumber){
        this.activity=activity;
        this.sProjectNumber=sProjectNumber;

    }

    public void registerToNotification(){
        checkPlayService();
    }


    public void addListener(PushNotificationsAdminListener listener){
        listeners.add(listener);
    }

    public void removeListener(PushNotificationsAdminListener listener){
        listeners.remove(listener);
    }

    private void checkPlayService() {
        Log.v(TAG, "checkPlayService ");
        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {
            googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
            regId = getRegistrationId();
            Log.v(TAG, "checkPlayService "+regId);
            if (regId.isEmpty()) {
                registerInBackground();
            }
            else{
                subscribeToPushNotifications(regId);
            }
        } else {
            Log.v(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, Consts.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.v(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(Consts.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.v(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Consts.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.v(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public int getAppVersion() {
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
                    }
                    regId = googleCloudMessaging.register(sProjectNumber);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    Handler h = new Handler(activity.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            subscribeToPushNotifications(regId);
                        }
                    });

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Subscribe to Push Notifications
     *
     * @param regId registration ID
     */
    private void subscribeToPushNotifications(String regId) {
        //Create push token with  Registration Id for Android
        //
        Log.v(TAG, "subscribing...");

        String deviceId;

        final TelephonyManager mTelephony = (TelephonyManager) activity.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
        } else {
            deviceId = Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }

        QBMessages.subscribeToPushNotificationsTask(regId, deviceId, QBEnvironment.DEVELOPMENT, new QBEntityCallbackImpl<ArrayList<QBSubscription>>() {
            @Override
            public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {
                Log.v(TAG, "subscribed");
                for(int i=0;i<listeners.size();i++){
                    listeners.get(i).pushNotificationsRegistered(true);
                }
            }

            @Override
            public void onError(List<String> strings) {
                for(int i=0;i<listeners.size();i++){
                    listeners.get(i).pushNotificationsRegistered(false);
                }
            }
        });
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion();
        Log.v(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Consts.PROPERTY_REG_ID, regId);
        editor.putInt(Consts.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
