package com.haomee.view;

/**
 * 自己定义的Gallery
 * @author zengxiaotao
 */
import java.lang.reflect.Method;

import com.haomee.liulian.R;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class GalleryFlow extends Gallery {

    /**
     * 控件可循环展示图片的最小图片数，小于该数时控件不能循环展示图片
     */
    public static final int MIN_CYCLE_NUMS = 5;

    /**
     * mCamera是用来做类3D效果处理，比如Z轴方向上的平移，绕Y轴的旋转等
     */
    private Camera mCamera;

    /**
     * mMaxRotationAngle是图片绕Y轴最大旋转角度，也用作图片平移的基数，使图片产生层次感
     */
    private int mMaxRotationAngle = 30;

    /**
     * mMaxZoom是图片在Z轴平移的距离，视觉上看起来就是放大缩小的效果
     */
    private int mMaxZoom = -180;

    /**
     * 整个Gallery视图的中心点
     */
    private int mCoveflowCenter;

    /**
     * 键盘监听器
     */
    private OnKeyListener mOnKeyListener;

    public GalleryFlow(Context context) {
        super(context);
        this.initGallery();
    }

    public GalleryFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initGallery();
    }

    public GalleryFlow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initGallery();
    }

    private void initGallery() {
        mCamera = new Camera();
        this.setStaticTransformationsEnabled(true);
    }

    /**
     * 获取整个Gallery视图的中心点
     * @return
     */
    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    /**
     * 获取单个子试图的中心点
     * @param view
     * @return
     */
    private static int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }

    /**
     * 重写Garray方法 ，产生层叠和放大效果
     */
    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();
        float rotationAngle = 0f;
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        rotationAngle = ((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle;
        transformImageBitmap((BorderImageView) child, t, rotationAngle);
        return true;
    }

    /**
     * This is called during layout when the size of this view has changed. If you were just added
     * to the view hierarchy, you're called with the old values of 0.
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCoveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Transform the Image Bitmap by the Angle passed
     * @param imageView ImageView the ImageView whose bitmap we want to rotate
     * @param t transformation
     * @param rotationAngle the Angle by which to rotate the Bitmap
     */
    private void transformImageBitmap(BorderImageView child, Transformation t, float rotationAngle) {
        mCamera.save();
        final Matrix imageMatrix = t.getMatrix();
        final int imageHeight = child.getLayoutParams().height;
        final int imageWidth = child.getLayoutParams().width;
        final float rotation = Math.abs(rotationAngle);
        float zoomAmount = mMaxZoom + 2 * rotation;

        //缩放比例，720P为1，1080P为1.5
        float scale = this.getContext().getResources().getInteger(R.integer.gallery_scale) / 10.0f;

        mCamera.translate((float) (rotationAngle * 2.8 * scale), (float) (rotation * 0.1),
                zoomAmount);

        //设置透明度
        child.setAlpha((int) (255 - 255 * 0.1 * Math.pow(rotation / mMaxRotationAngle, 2)));

        //设置背景

        int padding = (int) Math.abs(mMaxRotationAngle * 0.1f * scale);
        child.setBorderwidth(padding);
        child.setBorderRadius(padding);
        if (rotation / mMaxRotationAngle == 1) {
            child.setBorderColor(this.getContext().getResources()
                    .getColor(R.color.layer_middle_border));
        } else if (rotation / mMaxRotationAngle == 2) {
            child.setBorderColor(this.getContext().getResources()
                    .getColor(R.color.layer_bottom_border));
        } else {
            child.setBorderColor(Color.TRANSPARENT);
        }

        mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
        mCamera.restore();

    }

    /**
     * 重载视图显示顺序让左到中间显示，再到右到中间显示
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedPos = this.getSelectedItemPosition();
        if (childCount <= 2) {
            return super.getChildDrawingOrder(childCount, i);
        } else if (childCount == 3) {
            if (selectedPos < (childCount / 2)) {
                return childCount - i - 1;
            } else {
                return i;
            }

        } else if (childCount == 4) {
            if (selectedPos < (childCount / 2)) {
                if (i < 1) {
                    return i;
                }
                return childCount - i;
            } else {
                if (i < (childCount / 2)) {
                    return i;
                }
                return (childCount - i) + 1;
            }

        } else {
            if (i < (childCount / 2)) {
                return i;
            }
            return (childCount - i - 1) + (childCount / 2);
        }
    }

    /**
     * 重写dispatchKeyEvent方法，解决Gallery不触发键盘事件的问题
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mOnKeyListener != null && mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 重写setOnKeyListener方法，为本类中申明的键盘监听器赋值
     */
    @Override
    public void setOnKeyListener(OnKeyListener onKeyListener) {
        mOnKeyListener = onKeyListener;
    }

    /**
     * 滑动到下一个子项，用于首页自动滚动
     * @param position
     */
    public void moveNext() {
        try {
            @SuppressWarnings("unchecked")
            Class<Gallery> c = (Class<Gallery>) Class.forName("android.widget.Gallery");
            Method[] flds = c.getDeclaredMethods();
            for (Method f: flds) {
                if ("moveNext".equals(f.getName())) {
                    f.setAccessible(true);
                    f.invoke(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
