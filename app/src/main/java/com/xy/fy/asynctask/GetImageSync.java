package com.xy.fy.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetImageSync extends AsyncTask<String, String, Bitmap> {

    private String url;
    private syncLintener lintener;

    public GetImageSync(String url, syncLintener lintener) {
        this.url = url;
        this.lintener = lintener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // TODO Auto-generated method stub

        try {
            return InputStream2Bitmap(getImageStream(url));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        lintener.sync(result);
    }

    public InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

    public Bitmap InputStream2Bitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    public interface syncLintener {
        void sync(Bitmap bitmap);
    }
}
