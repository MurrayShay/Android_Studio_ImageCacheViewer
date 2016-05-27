package shay.murray.imagecacheviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by murray.shay on 2016/5/26.
 */
public class ImageLoader {
    //圖片快取
    ImageCache mImageCache = new MemoryCache();

    public ImageLoader(){
    }

    //注入快取實作
    public void setImageCache(ImageCache cache){
        mImageCache = cache;
    }

    public void displayImage(String imageUrl, ImageView imageView){
        Bitmap bitmap = mImageCache.get(imageUrl);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            return;
        }
        //圖片沒有快取,提交到執行緒池中下載圖片
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

         ImageView mImageView;
         String mImageUrl;

         public DownloadImageAsyncTask(ImageView imageView) {
             super();
             this.mImageView = imageView;
         }

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
         }

         @Override
         protected void onPostExecute(Bitmap bitmap) {
             if(bitmap == null){
                return;
             }
             mImageView.setImageBitmap(bitmap);
             mImageCache.put(mImageUrl,bitmap);
         }

         @Override
         protected Bitmap doInBackground(String... params) {
             if (params == null || params[0] == null) {
                 return null;
             }
             this.mImageUrl = params[0];
             return downloadImage(params[0],150);
         }

         @Override
         protected void onProgressUpdate(Integer... values) {
             super.onProgressUpdate(values);
         }
     }
}
