package shay.murray.imagecacheviewer;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by Murray.shay on 2016/5/26.
 * 記憶體快取 MemoryCache 類別
 */
public class MemoryCache implements ImageCache{

    private static final String LT = "MemoryCache";
    private LruCache<String,Bitmap> mMemoryCache;

    /**
     * 初始化 LRU 快去
     */
    public MemoryCache(){
        initMemeryCache();
    }

    private void initMemeryCache(){
        //計算可使用最大記憶體
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
        //取四分之一的可用記憶體作為快取
        final int cacheSize = maxMemory/4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap){
                return bitmap.getRowBytes()*bitmap.getHeight()/1024;
            }
        };
    }

    @Override
    public Bitmap get(String url) {
        Log.e(LT,"get(String url)---url : " + url);
        Bitmap bitmap = mMemoryCache.get(DiskCache.sha1(url)+".png");
        Log.e(LT,"get(String url)---bitmap : " + bitmap);
        return bitmap;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        Log.e(LT,"put(String url, Bitmap bitmap)---url : " + url +" ---bitmap : " + bitmap);
        mMemoryCache.put(DiskCache.sha1(url)+".png",bitmap);
    }
}
