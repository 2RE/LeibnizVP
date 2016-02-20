package com.dandddeveloper.leibnizvp;


import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.epapyrus.plugpdf.core.PlugPDF;
import com.epapyrus.plugpdf.core.PlugPDFException;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.WeakHashMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


public class Frontpage extends ActionBarActivity {
    TextView NameView;
    public static final String Name = "example_text";
    public static final String MyPREFERENCES = "MyPrefs";
    private WeakHashMap<NameChangedListener, String> nameChangedListenerHashMap;
    private static final String TAG = "Frontpage";

    SharedPreferences sharedpreferences;
    SharedPreferences mPrefs;

    RequestParams params = new RequestParams();
    GoogleCloudMessaging gcmObj;
    Context applicationContext;
    String regId = "";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    AsyncTask<Void, Void, String> createRegIdTask;

    public static final String REG_ID = "regId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        applicationContext = getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean welcomeScreenShown = mPrefs.getBoolean("welcomeScreenShownPref", false);

        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name)) {
            NameView.setText(sharedpreferences.getString(Name, ""));
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        try {
            // Initialize PlugPDF with a license key.
            PlugPDF.init(getApplicationContext(),
                    "36F5BE648GA7BFFCED34D5F44C36F49D6A73G2DA2HEBEHC8EFF5FBF5");
        } catch (PlugPDFException.InvalidLicense ex) {
            Log.e("PlugPDF", "error ", ex);
            // Handle invalid license exceptions.
        }
        if (!welcomeScreenShown) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        if (checkPlayServices()) {

            // Register Device in GCM Server
            registerInBackground();
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(applicationContext);
                    }
                    regId = gcmObj
                            .register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    // Store RegId created by GCM Server in SharedPref
                    storeRegIdinSharedPref(applicationContext, regId);
                    /*Toast.makeText(
                           applicationContext,
                          "Registered with GCM Server successfully.\n\n"
                                   + msg, Toast.LENGTH_SHORT).show();*/
                } else {
                    /*Toast.makeText(
                            applicationContext,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();*/
                }
            }
        }.execute(null, null, null);
    }

    private void storeRegIdinSharedPref(Context context, String regId) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.commit();
        storeRegIdinServer();
    }

    private void storeRegIdinServer() {
        //prgDialog.show();
        params.put("regId", regId);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_SERVER_URL, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // Hide Progress Dialog
                        //prgDialog.hide();
                        //if (prgDialog != null) {
                          //  prgDialog.dismiss();
                        //}
                        /*Toast.makeText(applicationContext,
                                "Reg Id shared successfully with Web App ",
                                Toast.LENGTH_LONG).show();*/
                        /*Intent i = new Intent(applicationContext,
                                Frontpage.class);
                        i.putExtra("regId", regId);
                        startActivity(i);
                        finish();*/
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // Hide Progress Dialog
                        //prgDialog.hide();
                        /*if (prgDialog != null) {
                            prgDialog.dismiss();
                        }*/
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            /*Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();*/
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                           /*Toast.makeText(applicationContext,
                                   "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();*/
                        }
                        // When Http response code other than 404, 500
                        else {
                           /*Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        applicationContext,
                        "Dieses Gerät unterstützt nicht Push-Benachrichtigungen. Bitte lade dir Play Services runter.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            /*Toast.makeText(
                    applicationContext,
                    "This device supports Play services, App will work normally",
                   Toast.LENGTH_LONG).show();*/
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frontpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_aboutus) {
            Intent i = new Intent(this, AboutUsActivity.class);
            startActivity(i);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage1(View view) {
        Intent i = new Intent(this, Vertretungsplanheuteactvity.class);

        startActivity(i);
    }
    public void sendMessage2(View view) {
        Intent i = new Intent(this, Vertretungsplanmorgenactivity.class);
        startActivity(i);
    }
    public void sendMessage3(View view) {
        Intent i = new Intent(this, Stundenplanactivity.class);
        startActivity(i);
    }
    public void sendMessage4(View view) {
        Intent i = new Intent(this, LehrerplanActivity.class);
        startActivity(i);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements NameChangedListener{

        private TextView textView;
        private String previousName = null;
        private Boolean tutorialtrue = false;
        private Boolean lehrer = false;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            LayoutInflater lf = getActivity().getLayoutInflater();
            View rootView = lf.inflate(R.layout.fragment_frontpage, container,
                    false);
            final SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            previousName = pref.getString("example_text", "");
            tutorialtrue = pref.getBoolean("TutorialShownPref", false);
            lehrer = pref.getBoolean("LehrerPref", false);
            textView = (TextView) rootView.findViewById(R.id.NameView);
            String[] swearwords = new String[10];
            swearwords[0] = "fuc";
            swearwords[1] = "hure";
            swearwords[2] = "spast";
            swearwords[3] = "idio";
            swearwords[4] = "arsch";
            swearwords[5] = "dreck";
            swearwords[6] = "bitc";
            swearwords[7] = "shit";
            swearwords[8] = "bastar";
            swearwords[9] = "Mistmadige Masurensau";
            if(stringContains(previousName, swearwords)){
                textView.setText("Süßes Miezekätzchen");
            }
            else{
            textView.setText(previousName);
            }
            if (!tutorialtrue) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Tutorial")
                        .setMessage(R.string.notificationcont)
                        .setPositiveButton("Verstanden", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("TutorialShownPref", true);
                                editor.apply();
                            }
                        });
                AlertDialog tutdia = alertDialogBuilder.create();
                tutdia.show();
            }
            if (lehrer){
                FloatingActionButton lehrerbutton = (FloatingActionButton) rootView.findViewById(R.id.lehrerbutton);
                FloatingActionButton schuelerbutton = (FloatingActionButton) rootView.findViewById(R.id.schuelerbutton);
                lehrerbutton.setVisibility(View.VISIBLE);
                schuelerbutton.setVisibility(View.GONE);
                String i = "Log recieved";
                Log.v(TAG, "index=" + i);
            }
            if (!lehrer){
                FloatingActionButton lehrerbutton = (FloatingActionButton) rootView.findViewById(R.id.lehrerbutton);
                FloatingActionButton schuelerbutton = (FloatingActionButton) rootView.findViewById(R.id.schuelerbutton);
                lehrerbutton.setVisibility(View.GONE);
                schuelerbutton.setVisibility(View.VISIBLE);
            }
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
            Calendar calendar = Calendar.getInstance();
            String weekDay;
            weekDay = dayFormat.format(calendar.getTime());
            if (weekDay.equals("Fri")){
                FloatingActionButton morgenbutton = (FloatingActionButton) rootView.findViewById(R.id.vpmorgenbutton);
                morgenbutton.setTitle("Vertretungsplan für Montag");
            }
            if (weekDay.equals("Sat")){
                FloatingActionButton heutebutton = (FloatingActionButton) rootView.findViewById(R.id.vpheutebutton);
                heutebutton.setTitle("Vertretungsplan für Montag");
                FloatingActionButton morgenbutton = (FloatingActionButton) rootView.findViewById(R.id.vpmorgenbutton);
                morgenbutton.setTitle("Vertretungsplan für Dienstag");
            }
            if (weekDay.equals("Sun")){
                FloatingActionButton heutebutton = (FloatingActionButton) rootView.findViewById(R.id.vpheutebutton);
                heutebutton.setTitle("Vertretungsplan für Morgen");
                FloatingActionButton morgenbutton = (FloatingActionButton) rootView.findViewById(R.id.vpmorgenbutton);
                morgenbutton.setTitle("Vertretungsplan für Dienstag");
            }
            return rootView;
        }



        public static boolean stringContains(String inputString, String[] items){
            for ( int i =0; i <items.length; i++)
            {
                if (inputString.toLowerCase().contains(items[i].toLowerCase())){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Frontpage)activity).addNameChangedListener(this);
        }

        @Override
        public void nameChanged(String name) {
            // TODO Auto-generated method stub
            if (!name.equals(previousName)) {
                textView.setText(name);
                previousName = name;
            }
        }

    }

    public void addNameChangedListener(NameChangedListener nameChangedListener) {
        if (nameChangedListenerHashMap == null) {
            nameChangedListenerHashMap = new WeakHashMap<NameChangedListener, String>();
        }
        nameChangedListenerHashMap.put(nameChangedListener,
                "entry");
    }

    private void fireNameChange() {
        if (nameChangedListenerHashMap != null) {
            Iterator<NameChangedListener> iterator = nameChangedListenerHashMap
                    .keySet().iterator();
            while (iterator.hasNext()) {
                NameChangedListener nameChangedListener = iterator.next();
                if (nameChangedListener != null) {
                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(this);
                    String name = pref.getString("example_text", "");
                    nameChangedListener.nameChanged(name);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        fireNameChange();
        checkPlayServices();
    }


    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}
