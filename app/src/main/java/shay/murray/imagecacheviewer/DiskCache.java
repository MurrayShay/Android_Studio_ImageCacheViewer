package shay.murray.imagecacheviewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by murray.shay on 2016/5/26.
 * 儲存裝置快取 DiskCahe 類別.
 */
public class DiskCache implements ImageCache{

    private static final String LT = "DiskCache";
    private Activity appContext;
    private static String cacheFileDir;

    public DiskCache(Activity context){
        this.appContext = context;
        this.cacheFileDir = appContext.getFilesDir().getAbsolutePath();
    }

    @Override
    public Bitmap get(String url) {
        Log.e(LT,"get(String url)---url : " + url);
        Log.e(LT,"get(String url)---sha1 : " + sha1(url));
        Log.e(LT,"get(String url)---cacheFileDir : " + cacheFileDir);
        FileInputStream fInputStream = null;
        try {
            File file = new File(cacheFileDir,sha1(url)+".png");
            fInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(LT,"get(String url)---FileNotFoundException have happened！");
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fInputStream);
        Log.e(LT,"get(String url)---bitmap : " + bitmap);

        return  bitmap;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        Log.e(LT,"put(String url, Bitmap bitmap)---url : " + url +
                        "\n---sha1 : " + sha1(url) +
                        "\n---bitmap : " + bitmap);
        OutputStream fileOutputStream = null;
        try{
            File file = new File(cacheFileDir,sha1(url)+".png");
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,10,fileOutputStream);
        }catch (FileNotFoundException ex){
            Log.e(LT,"public void put(String url, Bitmap bitmap)---FileNotFoundException have happened！");
            ex.printStackTrace();
        }finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }catch (IOException ex){
                    Log.e(LT,"public void put(String url, Bitmap bitmap)---IOException have happened！");
                    ex.printStackTrace();
                }
            }
        }
    }

    public static String sha1(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString((0xFF & messageDigest[i]) | 0x100).substring(1));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("murray","sha1 throw NoSuchAlgorithmException",e);
            return "";
        }
        catch (NullPointerException e) {
            Log.e("murray","sha1 throw NullPointerException",e);
            return "";
        }
    }
}
