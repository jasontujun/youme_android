package com.soulware.youme.ui.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.soulware.youme.R;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-25
 * Time: 下午2:37
 */
public class XRoundAngleImage extends ImageView {
    private float xRadius;
    private float yRadius;

    public XRoundAngleImage(Context context) {
        super(context);
        init(context, null);
    }

    public XRoundAngleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public XRoundAngleImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // 设置属性
        if (attrs != null) {
            TypedArray typeArray = context.obtainStyledAttributes(attrs,
                    R.styleable.XRoundAngleImageView);
            xRadius = typeArray.getDimension(
                    R.styleable.XRoundAngleImageView_xRadius, 0);
            yRadius = typeArray.getDimension(
                    R.styleable.XRoundAngleImageView_yRadius, 0);
            typeArray.recycle();
        } else {
            xRadius = 0;
            yRadius = 0;
        }
    }

    public float getxRadius() {
        return xRadius;
    }

    public void setxRadius(float xRadius) {
        this.xRadius = xRadius;
    }

    public float getyRadius() {
        return yRadius;
    }

    public void setyRadius(float yRadius) {
        this.yRadius = yRadius;
    }

    protected void onDraw(Canvas canvas) {
        BitmapShader shader;
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
        shader = new BitmapShader(bitmapDrawable.getBitmap(),
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //设置映射否则图片显示不全
        RectF rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
        int width = bitmapDrawable.getBitmap().getWidth();
        int height = bitmapDrawable.getBitmap().getHeight();
        RectF src = new RectF(0.0f, 0.0f, width, height);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(src, rect, Matrix.ScaleToFit.CENTER);
        shader.setLocalMatrix(matrix);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        canvas.drawRoundRect(rect, xRadius, yRadius, paint);
    }
}
