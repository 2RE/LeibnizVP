package com.dandddeveloper.leibnizvp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Vibrator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.rey.material.widget.SnackBar;


public class LoginActivity extends ActionBarActivity {
    private EditText username;
    private EditText pass;
    private Button bt_SignIn;
    SharedPreferences myPrefs;
    SnackBar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setWelcomeScreenPref(false, getApplicationContext());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    public void login(View view){
        username = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.pass);
        bt_SignIn = (Button) findViewById(R.id.bt_SignIn);
        myPrefs = getSharedPreferences("LehrerPref", MODE_PRIVATE);
        String theusername = String.valueOf(username.getText());
        String thepass = String.valueOf(pass.getText());
            if (theusername.equals("schueler") && thepass.equals("123456")) {
                setWelcomeScreenPref(true, getApplicationContext());
                setLehrerPref(false, getApplicationContext());
                Toast toast = Toast.makeText(getApplicationContext(), "Login erfolgreich! :D", Toast.LENGTH_SHORT);
                toast.show();
                /*mSnackbar = getApplication().getSnackbar();
                mSnackbar.applyStyle(R.style.SnackBarSingleLine)
                        .show();*/
                Intent i = new Intent(getApplicationContext(), Frontpage.class);
                startActivity(i);
            } else if (theusername.equals("lehrer") && thepass.equals("14869")) {
                setWelcomeScreenPref(true, getApplicationContext());
                setLehrerPref(true, getApplicationContext());
                Toast toast = Toast.makeText(getApplicationContext(), "Login erfolgreich! :D", Toast.LENGTH_SHORT);
                toast.show();
                /*mSnackbar.applyStyle(R.style.SnackBarSingleLine)
                        .show();*/

                Intent i = new Intent(getApplicationContext(), Frontpage.class);
                startActivity(i);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Login fehlgeschlagen :(", Toast.LENGTH_SHORT);
                toast.show();
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(findViewById(R.id.textView5));
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(findViewById(R.id.textView6));
                Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);

            }
    }


    private void setLehrerPref(boolean b, Context context){
        SharedPreferences mprefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mprefs.edit();
        editor.putBoolean("LehrerPref", b);
        editor.apply();
    }

    private void setWelcomeScreenPref(boolean bool, Context context){
        SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sprefs.edit();
        editor.putBoolean("welcomeScreenShownPref", bool);
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_forgotten) {
            Toast toast = Toast.makeText(getApplicationContext(), "Bitte wende dich an deinen Lehrer/Tutor, wenn du das Passwort vergessen hast. Tipp: Es ist das selbe Passwort wie auf der Website :)", Toast.LENGTH_LONG);
            toast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
