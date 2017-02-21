package shay.murray.imagecacheviewer;

import java.io.Closeable;
import java.io.IOException;

public final class CloseUtils {

    private CloseUtils() { }
    /**
     * 關閉 Closeable 物件
     * @param Closeable
     */
    public static void closeQuitly(Closeable closeable){
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
