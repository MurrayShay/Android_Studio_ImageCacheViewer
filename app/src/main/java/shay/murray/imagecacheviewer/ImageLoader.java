package shay.murray.imagecacheviewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by murray.shay on 2016/5/26.
 */
public class ImageLoader{
    private static final String LT = ImageLoader.class.getSimpleName();
    private final Context mContext;
    //圖片快取
    ImageCache mImageCache = new MemoryCache();

    public ImageLoader(Context context){
        mContext = context;
    }

    //注入快取實作
    public void setImageCache(ImageCache cache){
        mImageCache = cache;
    }

    public void displayImage(String imageUrl, ImageView imageView){
        Bitmap bitmap = mImageCache.get(imageUrl);
        if(bitmap != null){
            Log.e(LT,"mImageCache.get(imageUrl) have returned bitmap, 圖片已經在快取了");
            Toast.makeText(mContext,"圖片來快取,快取方法為"+mImageCache.getCacheMechanismName(),Toast.LENGTH_SHORT).show();
            imageView.setImageBitmap(bitmap);
            return;
        }
        //圖片沒有快取,提交到執行緒池中下載圖片
        Log.e(LT,"圖片沒有快取,提交到執行緒池中下載圖片");
        new DownloadImageAsyncTask(imageView).execute(imageUrl);
    }

    private Bitmap downloadImage(String imageUrl){
        Bitmap bitmap = null;
        try{
            URL url = new URL(imageUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
            httpURLConnection.disconnect();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Loading Large Bitmaps Efficiently from google android api
     * https://developer.android.com/training/displaying-bitmaps/load-bitmap.html#read-bitmap
     * http://blog.csdn.net/kesenhoo/article/details/7489243
     * http://givemepass.blogspot.tw/2015/11/lrucache.html
     * @param imageUrl
     * @param maxWidth
     * @return
     */
    public static Bitmap downloadImage(String imageUrl, int maxWidth){

        Bitmap bitmap = null;
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds =  true ;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxWidth);

            URL url = new URL(imageUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream is = (InputStream) httpURLConnection.getContent();
            options.inJustDecodeBounds =  false ;
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (MalformedInputException e){
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

     class DownloadImageAsyncTask extends AsyncTask<String,Integer,Bitmap>{

         String mImageUrl;
         ImageView mImageView;
         ProgressDialog progressDialog;

         public DownloadImageAsyncTask(ImageView imageView) {
             super();
             this.mImageView = imageView;
         }

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             progressDialog = new ProgressDialog(mContext);
             progressDialog.setMessage(mContext.getString(R.string.Loading));
             progressDialog.setCancelable(false);
             progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
             progressDialog.show();
         }

         @Override
         protected void onPostExecute(Bitmap bitmap) {
             if(bitmap == null){
                return;
             }
             progressDialog.dismiss();
             mImageView.setImageBitmap(bitmap);
             mImageCache.put(mImageUrl,bitmap);
         }

         @Override
         protected Bitmap doInBackground(String... params) {
             if (params == null || params[0] == null) {
                 return null;
             }
             this.mImageUrl = params[0];

             Bitmap bitmap = downloadImage(params[0], 150);

             for (int i = 0; i < 50; i++) {
                 SystemClock.sleep(1);
                 publishProgress((int) ((i / (float) 50) * 100));
             }

             publishProgress(100);

             return bitmap;
         }

         @Override
         protected void onProgressUpdate(Integer... values) {
             super.onProgressUpdate(values);
             progressDialog.setProgress(values[0]);
         }
     }



}