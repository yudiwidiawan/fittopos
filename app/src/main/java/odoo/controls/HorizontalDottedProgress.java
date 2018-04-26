package odoo.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Created by makan on 06/07/2017.
 */

public class HorizontalDottedProgress extends View {

    private int circleRadius = 5;
    private int circleBounceRadius = 8;
    private int circlePos;
    private int circleAmount = 3;

    public HorizontalDottedProgress(Context context) {
        super(context);
    }

    public HorizontalDottedProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalDottedProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#444545"));

        createDot(canvas,paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    private void createDot(Canvas canvas, Paint paint) {
        for(int i = 0;i<circleAmount;i++) {
            if (i == circlePos) {
                canvas.drawCircle(10+(i*20), circleBounceRadius, circleBounceRadius, paint);
            } else {
                canvas.drawCircle(10+(i*20), circleBounceRadius, circleRadius, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width, height;

        int calculateWidth = (20*9);
        width = calculateWidth;
        height = (circleBounceRadius*2);

        setMeasuredDimension(width, height);
    }

    public void startAnimation() {
        BounceAnimation bounceAnimation = new BounceAnimation();
        bounceAnimation.setDuration(100);
        bounceAnimation.setRepeatCount(Animation.INFINITE);
        bounceAnimation.setInterpolator(new LinearInterpolator());
        bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                circlePos++;
                if(circlePos == circleAmount) {
                    circlePos = 0;
                }
            }
        });
        startAnimation(bounceAnimation);
    }

    private class BounceAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //call invalidate to redraw your view againg.
            invalidate();
        }
    }
}
