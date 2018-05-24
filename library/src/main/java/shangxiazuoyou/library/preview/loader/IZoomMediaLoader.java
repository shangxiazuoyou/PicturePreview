package shangxiazuoyou.library.preview.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * 加载器
 */
public interface IZoomMediaLoader {

    /**
     * 展示图片
     *
     * @param context      上下文
     * @param path         图片路径
     * @param simpleTarget 图片加载状态回调
     */
    void displayImage(@NonNull Fragment context, @NonNull String path, @NonNull MySimpleTarget<Bitmap> simpleTarget);

    /**
     * 停止
     *
     * @param context 上下文
     */
    void onStop(@NonNull Fragment context);

    /**
     * 清理内存
     *
     * @param context 上下文
     */
    void clearMemory(@NonNull Context context);
}
