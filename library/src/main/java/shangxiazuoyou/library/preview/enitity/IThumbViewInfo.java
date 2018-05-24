package shangxiazuoyou.library.preview.enitity;

import android.graphics.Rect;
import android.os.Parcelable;

/**
 * 图片预览接口
 */
public interface IThumbViewInfo extends Parcelable {

    /**
     * 图片地址
     *
     * @return 图片地址
     */
    String getUrl();

    /**
     * 图片坐标位置
     *
     * @return 图片坐标位置
     */
    Rect getBounds();

}