package com.dandddeveloper.leibnizvp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import com.dandddeveloper.leibnizvp.util.IabHelper;
import com.dandddeveloper.leibnizvp.util.IabResult;
import com.dandddeveloper.leibnizvp.util.Purchase;


public class AboutUsActivity extends ActionBarActivity {

    private static final String TAG = "com.dandd.leibnizvp";
    IabHelper mHelper;
    static final String ITEM_SKU = "donation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        String base64EncodedPublicKey = "<MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuSxY19w0YAAq8Ljt3BtWHdENbGD/iTLHMJRkW8fXnlPPXKCXD7JF9MeWkwSOt6asMjrveTNqOe/geF46d9aItyStAB8dObtJu3GeB+xAGuhECwlTc6VJq7PEghsg2uUMiN4aYpgvMdPTIZ6d7iMxKnAHyiryyYsTezuhK4oYV3yE+fSXOWj3kG8n5+ihxCCEIWEQKgCFBBgp4J/nuTw0e2cr6Z8x700IIxM69rfy0GkmnM5ZA4x5tFoKioLLOe26p3rW1BKP9npiLts2ZsPZGfIwxcWx6V/WXHnONE/Feq5iw4oXtvmOzfORLfRuhdJ4bY60WJ6DnTIxCQbwQmzdDQIDAQAB>";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener(){
            public void onIabSetupFinished(IabResult result){
                if(!result.isSuccess()){
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                }
                else {
                    Log.d(TAG, "In-app Billing set up OK");
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about_us, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void openWebsite(View view) {
        Uri uri = Uri.parse("http://dandd-developer.com/home/leibnizvp/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void pay(View view) {
        //Uri uri = Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=dandd%2edeveloper%40gmail%2ecom&lc=DE&item_name=D%26D%20Developer&no_note=0&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest");
        //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //startActivity(intent);
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");
    }

    public void rate(View view) {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.dandddeveloper.leibnizvp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                //consumeItem();
                //buyButton.setEnabled(false);
                Toast toast = Toast.makeText(getApplicationContext(), "Danke für die Spende! :)", Toast.LENGTH_LONG);
                toast.show();
            }

        }
    };



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
            TextView tv = (TextView) rootView.findViewById(R.id.textView3);
            tv.setMovementMethod(new ScrollingMovementMethod());
            return rootView;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
