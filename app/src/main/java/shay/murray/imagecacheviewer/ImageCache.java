package shay.murray.imagecacheviewer;

import android.graphics.Bitmap;

/**
 * Created by Murray.shay on 2016/5/26.
 */
public interface ImageCache {
    public Bitmap get(String url);
    public void put(String url, Bitmap bitmap);
}
