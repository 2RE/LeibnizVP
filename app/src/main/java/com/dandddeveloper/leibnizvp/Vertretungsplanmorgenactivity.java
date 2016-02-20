package com.dandddeveloper.leibnizvp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epapyrus.plugpdf.SimpleDocumentReader;
import com.epapyrus.plugpdf.SimpleReaderFactory;
import com.epapyrus.plugpdf.core.PlugPDF;
import com.epapyrus.plugpdf.core.PlugPDFException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class Vertretungsplanmorgenactivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertretungsplanmorgenactivity);
        HashMap<String, String> linkMap = new HashMap<String, String>();
        linkMap.put("Fri", "https://www.leibniz-gymnasium.de/upload/_1_Montag.pdf");
        linkMap.put("Mon", "https://www.leibniz-gymnasium.de/upload/_2_Dienstag.pdf");
        linkMap.put("Tue", "https://www.leibniz-gymnasium.de/upload/_3_Mittwoch.pdf");
        linkMap.put("Wed", "https://www.leibniz-gymnasium.de/upload/_4_Donnerstag.pdf");
        linkMap.put("Thu", "https://www.leibniz-gymnasium.de/upload/_5_Freitag.pdf");
        linkMap.put("Sat", "https://www.leibniz-gymnasium.de/upload/_2_Dienstag.pdf");
        linkMap.put("Sun", "https://www.leibniz-gymnasium.de/upload/_2_Dienstag.pdf");
        String weekDay; String link;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());
        link = linkMap.get(weekDay);
        if (weekDay.equals("Fri")){
            setTitle("Vertretungsplan f" + "\u00fc" + "r Montag");
        }
        if (weekDay.equals("Sat")||weekDay.equals("Sun")){
            setTitle("Vertretungsplan f" + "\u00fc" + "r Dienstag");
        }
        try{
            // Initialize PlugPDF with a license key.
            PlugPDF.init(getApplicationContext(),
                    "36F5BE648GA7BFFCED34D5F44C36F49D6A73G2DA2HEBEHC8EFF5FBF5");
        } catch (PlugPDFException.InvalidLicense ex) {
            Log.e("PlugPDF", "error ", ex);
            // Handle invalid license exceptions.
        }

        SimpleDocumentReader v = SimpleReaderFactory.createSimpleViewer(this, null);
        v.openUrl(link, "123456");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vertretungsplanmorgenactivity, menu);
        return true;
    }

    public void helpme(View view){
        TextView tv = (TextView) findViewById(R.id.vpmorgentitle);
        tv.setVisibility(View.VISIBLE);
        TextView tvn = (TextView) findViewById(R.id.vpmorgentext);
        tvn.setVisibility(View.VISIBLE);
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
        if (id == R.id.action_share){
            Bitmap bitmap = takeScreenshot();
            saveBitmap(bitmap);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/png");
            share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String filePath = getApplicationContext().getFilesDir().getPath() + "/screenshot.png";
            String path = "";
            try {
                path = MediaStore.Images.Media.insertImage(getContentResolver(), filePath, "title", null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Uri screenshotUri = Uri.parse(path);
            share.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            startActivity(Intent.createChooser(share, "Teilen via"));
        }

        return super.onOptionsItemSelected(item);
    }

    public Bitmap takeScreenshot() {
        Log.v("TAKE", "screenshot");
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        viewGroup.setDrawingCacheEnabled(true);
        return viewGroup.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        FileOutputStream fos;
        String filePath = getApplicationContext().getFilesDir().getPath() + "/screenshot.png";
        File f = new File(filePath);
        Log.v("SAVE", "file");
        try {
            fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }
}
