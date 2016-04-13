package yuanmeng.com.pictureviewer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;

/**
 * @author wsx - heikepianzi@qq.com
 * @Description: TODO(-自定义图片查看dialog-)
 * @date 2016/04/09.
 */

public class ImageDialog extends Dialog {

    Builder mBuilder;

    private View animaView;
    // 动画时间
    private int duration = 400;
    // 屏幕宽高
    private float width, height;
    // 状态栏高度
    private float statusBarheight;


    public ImageDialog(Context context, Builder builder) {
        super(context, R.style.dialog_viewer);

        mBuilder = builder;

        // 初始化公用数据
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        statusBarheight = 25 * metrics.density;
        width = metrics.widthPixels;
        height = metrics.heightPixels - statusBarheight;
    }

    public static class Builder {
        private Context mContext;
        private View currentView;
        // image实际绘制的宽高
        private int bWidth, bHeight;
        //
        private List<Integer> images;
        // 当前选中项
        private int currentItem;
        ImageDialog imagDialog;
        // 传入的view列表
        private Map<Integer, View> views;


        public Builder(Context context) {
            this.mContext = context;
        }

        public void setImages(List<Integer> images) {
            this.images = images;
        }

        public void setCurrentItem(int item) {
            this.currentItem = item;
        }

        public Map<Integer, View> getViews() {
            return views;
        }

        public void setViews(Map<Integer, View> views) {
            this.views = views;
        }

        public ImageDialog create() {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imagDialog = new ImageDialog(mContext, this);
            View layout = inflater.inflate(R.layout.dialog_image, null);
            imagDialog.setContentView(layout);
            // 设置img位置，与传入view位置重合
            imagDialog.animaView = layout.findViewById(R.id.img);

            ImagePager imagePager = new ImagePager(mContext, images, (ViewGroup) layout.findViewById(R.id.ll_tag), currentItem);
            ViewPager vp = (ViewPager) layout.findViewById(R.id.vp);
            vp.setAdapter(imagePager);
            vp.setOnPageChangeListener(imagePager);
            vp.setCurrentItem(currentItem);

            // 设置动画参数
            initAnimaData();

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imagDialog.animaView.getLayoutParams();
            layoutParams.width = currentView.getWidth();
            layoutParams.height = currentView.getHeight();
            imagDialog.animaView.setLayoutParams(layoutParams);
            int[] location = new int[2];
            currentView.getLocationInWindow(location);
            imagDialog.animaView.setTranslationX(location[0]);
            imagDialog.animaView.setTranslationY(location[1] - imagDialog.statusBarheight);

            // 设置dialog窗体大小
            Window window = imagDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) imagDialog.width;
            params.height = (int) imagDialog.height;
//            params.x = location[0] - (metrics.widthPixels - currentView.getWidth()) / 2;
//            params.y = location[1] - (metrics.heightPixels - currentView.getHeight() + (int) (20 * metrics.density)) / 2;
            window.setAttributes(params);

            // 修改系统自带弹出，退出动画
            imagDialog.getWindow().setWindowAnimations(0);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagDialog.dismiss();
                }
            };
            imagePager.setListener(listener);
            return imagDialog;
        }

        private void initAnimaData() {

            ViewPager vp = (ViewPager) imagDialog.findViewById(R.id.vp);
            currentView = views.get(vp.getCurrentItem());
            if (currentView instanceof ImageView) {
                ImageView mImg = (ImageView) currentView;
                ((ImageView) imagDialog.animaView).setImageDrawable(((ImageView) currentView).getDrawable());
                ((ImageView) imagDialog.animaView).setImageDrawable(mImg.getDrawable());
                //获得ImageView中Image的真实宽高，
                int dw = mImg.getDrawable().getBounds().width();
                int dh = mImg.getDrawable().getBounds().height();
                //获得ImageView中Image的变换矩阵
                Matrix m = mImg.getImageMatrix();
                float[] values = new float[10];
                m.getValues(values);
                //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数
                float sx = values[0];
                float sy = values[4];
                //计算Image在屏幕上实际绘制的宽高
                bWidth = (int) (dw * sx);
                bHeight = (int) (dh * sy);
            }

//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imagDialog.animaView.getLayoutParams();
//            layoutParams.width = currentView.getWidth();
//            layoutParams.height = currentView.getHeight();
//            imagDialog.animaView.setLayoutParams(layoutParams);
//            int[] location = new int[2];
//            currentView.getLocationInWindow(location);
//            imagDialog.animaView.setTranslationX(location[0]);
//            imagDialog.animaView.setTranslationY(location[1] - imagDialog.statusBarheight);
//
//            // 设置dialog窗体大小
//            Window window = imagDialog.getWindow();
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = (int) imagDialog.width;
//            params.height = (int) imagDialog.height;
////            params.x = location[0] - (metrics.widthPixels - currentView.getWidth()) / 2;
////            params.y = location[1] - (metrics.heightPixels - currentView.getHeight() + (int) (20 * metrics.density)) / 2;
//            window.setAttributes(params);
        }
    }


    @Override
    public void show() {
        super.show();

        // X轴缩放大小
        float scaleX = width / (float) mBuilder.bWidth;
        // Y轴缩放大小
        float scaleY = height / (float) mBuilder.bHeight;
        // 对比高宽比率
        boolean isVertical = mBuilder.bHeight / (float) mBuilder.bWidth < height / width;
        // 放大或缩小后位移的坐标点
        float translationX, translationY;
        if (isVertical) {
            translationX = mBuilder.bWidth * (scaleX - 1) / 2f;
            translationY = (height - mBuilder.bHeight) / 2f;
        } else {
            translationX = (width - mBuilder.bWidth) / 2f;
            translationY = mBuilder.bHeight * (scaleY - 1) / 2f;
        }
        playAnima(true, scaleX, scaleY, isVertical, translationX, translationY,
                mBuilder.currentView.getWidth(), mBuilder.bWidth, mBuilder.currentView.getHeight(), mBuilder.bHeight);
    }

    @Override
    public void dismiss() {


        mBuilder.initAnimaData();

        // X轴缩放大小
        float scaleX = mBuilder.bWidth / (float) animaView.getWidth();
        // Y轴缩放大小
        float scaleY = mBuilder.bHeight / (float) animaView.getHeight();
        // 对比高宽比率
        boolean isVertical = mBuilder.bHeight / (float) mBuilder.bWidth < height / width;
        // 放大或缩小后位移的坐标点
        float translationX, translationY;
        int[] location = new int[2];
        mBuilder.currentView.getLocationInWindow(location);
        translationX = location[0];
        translationY = location[1] - statusBarheight;
        playAnima(false, scaleX, scaleY, isVertical, translationX, translationY,
                animaView.getWidth(), mBuilder.currentView.getWidth(), animaView.getHeight(), mBuilder.currentView.getHeight());

        // 执行
        ValueAnimator alphaAnima = ValueAnimator.ofInt(255, 0);
        alphaAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                findViewById(R.id.bg).getBackground().setAlpha((int) animation.getAnimatedValue());
            }
        });
    }

    /**
     * 统一执行动画
     *
     * @param isShow       是否打开dialog
     * @param scaleX       x轴缩放比例
     * @param scaleY       y轴缩放比例
     * @param isVertical   高宽比例（如果img实际绘画比例笑小于*幕比例，则x轴全*）
     * @param translationX 终点x位置
     * @param translationY 终点y位置
     * @param startWidth   宽度动画开始点
     * @param endWidth     宽度动画结束点
     * @param startHeight  高度动画开始点
     * @param endHeight    高度动画结束点
     */
    private void playAnima(final boolean isShow, float scaleX, float scaleY, boolean isVertical, float translationX, float translationY
            , float startWidth, float endWidth, float startHeight, float endHeight) {

        float scale;
        ViewPropertyAnimator animator = animaView.animate();
        ValueAnimator wAnim, hAnim;
        AnimatorSet animatorSet = new AnimatorSet();
        if (isVertical) {
            scale = scaleX;
        } else {
            scale = scaleY;
        }
        // 设置缩放大小终点坐标点
        animator.scaleX(scale).scaleY(scale).translationX(translationX).translationY(translationY);
        // 设置宽度调整动画
        wAnim = ValueAnimator.ofFloat(startWidth, endWidth);
        wAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = animaView.getLayoutParams();
                float value = (float) animation.getAnimatedValue();
                params.width = (int) value;
                animaView.requestLayout();
            }
        });
        // 设置高度调整动画
//        endHeight = width / scale;
        hAnim = ValueAnimator.ofFloat(startHeight, endHeight);
        hAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = animaView.getLayoutParams();
                float value = (float) animation.getAnimatedValue();
                params.height = (int) value;
                animaView.requestLayout();
            }
        });
        // 设置背景透明度动画
        ValueAnimator alphaAnima;
        if (isShow)
            alphaAnima = ValueAnimator.ofInt(0, 255);
        else {
            alphaAnima = ValueAnimator.ofInt(255, 0);
        }
        alphaAnima.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animaView.setVisibility(View.VISIBLE);
                findViewById(R.id.vp).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isShow) {
//                    ViewGroup.LayoutParams params = animaView.getLayoutParams();
//                    params.height = (int) height;
//                    params.width = (int) width;
//                    animaView.requestLayout();
                    findViewById(R.id.vp).setVisibility(View.VISIBLE);
                    animaView.setVisibility(View.GONE);
                } else {
                    animDismiss();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        alphaAnima.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                findViewById(R.id.bg).getBackground().setAlpha((int) animation.getAnimatedValue());
            }
        });

        animatorSet.playTogether(wAnim, hAnim, alphaAnima);
        animatorSet.setDuration(duration);
        animator.setDuration(duration);
        animator.start();
        animatorSet.start();
    }

    /**
     * 关闭dialog
     */
    private void animDismiss() {
        super.dismiss();
    }

}
