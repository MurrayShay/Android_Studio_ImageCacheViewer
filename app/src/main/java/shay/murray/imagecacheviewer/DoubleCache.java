package shay.murray.imagecacheviewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Murray.shay on 2016/5/26.
 * 混合快取機制 DoubleCache 類別.
 */
public class DoubleCache implements ImageCache {

    private Activity appContext;
    MemoryCache mMemoryCache;
    DiskCache mDiskCache;
    String mUseCacheName;

    public DoubleCache(Activity context){
        this.appContext = context;
        this.mMemoryCache = new MemoryCache();
        this.mDiskCache = new DiskCache(appContext);
    }

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = mMemoryCache.get(url);
        mUseCacheName = mMemoryCache.getCacheMechanismName();
        if(bitmap == null){
            bitmap = mDiskCache.get(url);
            mUseCacheName = mDiskCache.getCacheMechanismName();
        }
        return bitmap;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        mMemoryCache.put(url,bitmap);
        mDiskCache.put(url,bitmap);
    }

    @Override
    public String getCacheMechanismName() {
        return "雙緩存機制,來自"+mUseCacheName;
    }
}
