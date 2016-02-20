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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class LehrerplanActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lehrerplan);
        SimpleDocumentReader v = SimpleReaderFactory.createSimpleViewer(this, null);
        v.openUrl("https://www.leibniz-gymnasium.de/upload/Lehrerplaene.pdf", "123456");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lehrerplan, menu);
        return true;
    }

    public void helpme(View view){
        TextView tv = (TextView) findViewById(R.id.lehrertitle);
        tv.setVisibility(View.VISIBLE);
        TextView tvn = (TextView) findViewById(R.id.lehrertext);
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
