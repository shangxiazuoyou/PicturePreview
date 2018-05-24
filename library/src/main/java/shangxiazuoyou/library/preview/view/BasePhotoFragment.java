package shangxiazuoyou.library.preview.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import shangxiazuoyou.library.preview.GPreviewActivity;
import shangxiazuoyou.library.preview.R;
import shangxiazuoyou.library.preview.ZoomMediaLoader;
import shangxiazuoyou.library.preview.enitity.IThumbViewInfo;
import shangxiazuoyou.library.preview.loader.MySimpleTarget;
import shangxiazuoyou.library.preview.wight.SmoothImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片预览单个图片的fragment
 */
public class BasePhotoFragment extends Fragment {
    /**
     * 预览图片 类型
     */
    private static final String KEY_TRANS_PHOTO = "is_trans_photo";
    private static final String KEY_SING_FILING = "isSingleFling";
    private static final String KEY_PATH = "key_item";
    private static final String KEY_DRAG = "isDrag";
    private IThumbViewInfo beanViewInfo;
    private boolean isTransPhoto = false;
    protected SmoothImageView imageView;
    protected View rootView;
    protected ProgressBar loading;
    protected MySimpleTarget<Bitmap> mySimpleTarget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_photo_layout, container, false);
    }

    public static BasePhotoFragment getInstance(Class<? extends BasePhotoFragment> fragmentClass, IThumbViewInfo item, boolean currentIndex, boolean isSingleFling, boolean isDrag) {
        BasePhotoFragment fragment;
        try {
            fragment = fragmentClass.newInstance();
        } catch (Exception e) {
            fragment = new BasePhotoFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(BasePhotoFragment.KEY_PATH, item);
        bundle.putBoolean(BasePhotoFragment.KEY_TRANS_PHOTO, currentIndex);
        bundle.putBoolean(BasePhotoFragment.KEY_SING_FILING, isSingleFling);
        bundle.putBoolean(BasePhotoFragment.KEY_DRAG, isDrag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initDate();
    }

    @CallSuper
    @Override
    public void onStop() {
        ZoomMediaLoader.getInstance().getLoader().onStop(this);
        super.onStop();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        release();
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null == getActivity()) {
            return;
        }
        ZoomMediaLoader.getInstance().getLoader().clearMemory(getActivity());
    }

    public void release() {
        mySimpleTarget = null;
        if (imageView != null) {
            imageView.setImageBitmap(null);
            imageView.setOnViewTapListener(null);
            imageView.setOnPhotoTapListener(null);
            imageView.setAlphaChangeListener(null);
            imageView.setTransformOutListener(null);
            imageView.transformIn(null);
            imageView.transformOut(null);
            imageView.setOnLongClickListener(null);
            imageView = null;
            rootView = null;
            isTransPhoto = false;
        }
    }

    /**
     * 初始化控件
     */
    private void initView(View view) {
        loading = view.findViewById(R.id.loading);
        imageView = view.findViewById(R.id.photoView);
        rootView = view.findViewById(R.id.rootView);
        rootView.setDrawingCacheEnabled(false);
        imageView.setDrawingCacheEnabled(false);
        mySimpleTarget = new MySimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap) {
                if (imageView.getTag().toString().equals(beanViewInfo.getUrl())) {
                    imageView.setImageBitmap(bitmap);
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadFailed(Drawable errorDrawable) {
                loading.setVisibility(View.GONE);
                imageView.setImageResource(R.mipmap.bga_pp_ic_holder_light);
            }

            @Override
            public void onLoadStarted() {

            }
        };
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        Bundle bundle = getArguments();
        boolean isSingleFling = true;
        if (bundle != null) {
            isSingleFling = bundle.getBoolean(KEY_SING_FILING);
            //地址
            beanViewInfo = bundle.getParcelable(KEY_PATH);
            //位置
            assert beanViewInfo != null;
            imageView.setThumbRect(beanViewInfo.getBounds());
            imageView.setDrag(bundle.getBoolean(KEY_DRAG));
            imageView.setTag(beanViewInfo.getUrl());
            //是否展示动画
            isTransPhoto = bundle.getBoolean(KEY_TRANS_PHOTO, false);
            //加载原图
            ZoomMediaLoader.getInstance().getLoader().displayImage(this, beanViewInfo.getUrl(), mySimpleTarget);
        }
        // 非动画进入的Fragment，默认背景为黑色
        if (!isTransPhoto) {
            rootView.setBackgroundColor(Color.BLACK);
        } else {
            imageView.setMinimumScale(0.7f);
        }
        if (isSingleFling) {
            imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    if (imageView.checkMinScale()) {
                        if (null == getActivity()) {
                            return;
                        }
                        ((GPreviewActivity) getActivity()).transformOut();
                    }
                }
            });
        } else {
            imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if (imageView.checkMinScale()) {
                        if (null == getActivity()) {
                            return;
                        }
                        ((GPreviewActivity) getActivity()).transformOut();
                    }
                }
            });
        }
        imageView.setAlphaChangeListener(new SmoothImageView.OnAlphaChangeListener() {
            @Override
            public void onAlphaChange(int alpha) {
                Log.d("onAlphaChange", "alpha:" + alpha);
                rootView.setBackgroundColor(getColorWithAlpha(alpha / 255f, Color.BLACK));
            }
        });
        imageView.setTransformOutListener(new SmoothImageView.OnTransformOutListener() {
            @Override
            public void onTransformOut() {
                if (imageView.checkMinScale()) {
                    if (null == getActivity()) {
                        return;
                    }
                    ((GPreviewActivity) getActivity()).transformOut();
                }
            }
        });
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }

    public void transformIn() {
        imageView.transformIn(new SmoothImageView.onTransformListener() {
            @Override
            public void onTransformCompleted(SmoothImageView.Status status) {
                rootView.setBackgroundColor(Color.BLACK);
            }
        });
    }

    public void transformOut(SmoothImageView.onTransformListener listener) {
        imageView.transformOut(listener);
    }

    public void changeBg(int color) {
        rootView.setBackgroundColor(color);
    }

}
