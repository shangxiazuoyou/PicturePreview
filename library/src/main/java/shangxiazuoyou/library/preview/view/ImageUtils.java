package shangxiazuoyou.library.preview.view;

import android.content.Context;

import shangxiazuoyou.library.preview.R;

/**
 * 图片工具类
 */
public class ImageUtils {
    /**
     * 取状态栏高度
     *
     * @param mApplicationContent 应用上下文
     * @return int
     */
    public static int getStatusBarHeight(Context mApplicationContent) {
        int result = mApplicationContent.getResources().getDimensionPixelSize(R.dimen.yms_dimens_50_0_px);
        int resourceId = mApplicationContent.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mApplicationContent.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isLongPressed(float lastX, float lastY, float thisX, float thisY, long lastDownTime, long thisEventTime, long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        return offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime;
    }
}
