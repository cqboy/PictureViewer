package yuanmeng.com.pictureviewer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wsx - heikepianzi@qq.com
 * @Description: TODO(-viewpager配置器和滑动监听-)
 * @date 2016/04/09.
 */
public class ImagePager extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private List<Integer> imgs;
    private Context mContext;
    private List<View> marks;
    private View mCurrentView;
    private View.OnClickListener listener;

    public ImagePager(Context mContext, List<Integer> imgs, ViewGroup markGroup, int item) {
        this.mContext = mContext;
        this.imgs = imgs;
        addMarkimages(markGroup, item);
    }

    /**
     * 设置viewpager子项点击事件
     *
     * @param listener
     */
    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    /**
     * 获取viewpager当前展示控件
     *
     * @return
     */
    public View getPrimaryItem() {
        return mCurrentView;
    }

    /**
     * 设置下部图片个数，状态标识
     *
     * @param markGroup
     * @param item
     */
    private void addMarkimages(ViewGroup markGroup, int item) {

        if (imgs == null || imgs.size() == 1) {
            return;
        }
        marks = new ArrayList<>();
        markGroup.removeAllViews();
        DisplayMetrics display = mContext.getResources().getDisplayMetrics();
        int size = (int) (4 * display.density);
        for (int j = 0; j < imgs.size(); j++) {
            View mark = new View(mContext);
            mark.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            mark.setId(j);
            if (j == item) {
                mark.setBackgroundResource(R.drawable.mark_white);
            } else {
                mark.setBackgroundResource(R.drawable.mark_gray);
            }
            markGroup.addView(mark);
            marks.add(mark);
            // 设置间距
            mark = new View(mContext);
            mark.setLayoutParams(new ViewGroup.LayoutParams(size * 3, size));
            markGroup.addView(mark);
        }
    }


    /**
     * ---------------------配置器接口回掉-----------------------
     *
     * @return
     */

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_pager_item, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageResource(imgs.get(position));
        container.addView(view);
        view.setOnClickListener(listener);
        return view;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (View) object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
//            return super.getItemPosition(object);
        return POSITION_NONE;
    }


    /**
     * -------------------滑动监听回掉接口-----------------------
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    int oldPosition = 0;

    @Override
    public void onPageSelected(int position) {

        marks.get(position).setBackgroundResource(R.drawable.mark_white);
        marks.get(oldPosition).setBackgroundResource(R.drawable.mark_gray);
        oldPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}