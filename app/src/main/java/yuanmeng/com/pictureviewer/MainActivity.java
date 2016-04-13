package yuanmeng.com.pictureviewer;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Integer[] imgs = new Integer[]{R.mipmap.test_pic, R.mipmap.test_pic1, R.mipmap.test_pic2, R.mipmap.test_pic3, R.mipmap.test_pic4, R.mipmap.test_pic5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((GridView) findViewById(R.id.gv)).setAdapter(new ImageAdapter());
    }

    class ImageAdapter extends BaseAdapter {

        Map<Integer, View> views = new ArrayMap<>();

        @Override
        public int getCount() {
            return imgs.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, (int) (100 * getResources().getDisplayMetrics().density));
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(params);
            imageView.setImageResource(imgs[position]);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    views.put(position, v);
                    ImageDialog.Builder builder = new ImageDialog.Builder(MainActivity.this);
                    builder.setViews(views);
                    builder.setImages(Arrays.asList(imgs));
                    builder.setCurrentItem(position);
                    builder.create().show();
                }
            });
            views.put(position, imageView);
            return imageView;
        }
    }
}
