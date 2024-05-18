    package com.hxjt.dqyt.adapter;

    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.GridView;

    import androidx.annotation.NonNull;
    import androidx.viewpager.widget.PagerAdapter;

    import com.hxjt.dqyt.R;
    import com.hxjt.dqyt.bean.DeviceInfoBean;

    import java.util.List;

    public class DeviceListAdapter extends PagerAdapter {
        private Context mContext;
        private List<DeviceInfoBean[]> mDeviceList;
        private String mDeviceNo;

        public DeviceListAdapter(Context context, List<DeviceInfoBean[]> deviceList,String deviceNo) {
            mContext = context;
            mDeviceList = deviceList;
            mDeviceNo = deviceNo;
        }

        @Override
        public int getCount() {
            return Math.max(1, mDeviceList.size());        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view;

            if (mDeviceList.isEmpty()) {
                view = inflater.inflate(R.layout.empty_layout, container, false);
            } else {
                view = inflater.inflate(R.layout.device_list_layout, container, false);

                GridView gridView = view.findViewById(R.id.grid_view);
                DeviceInfoBean[] deviceArray = mDeviceList.get(position);
                gridView.setAdapter(new DeviceAdapter(mContext, deviceArray));
            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        public void update(List<DeviceInfoBean[]> deviceList) {
            mDeviceList = deviceList;
            notifyDataSetChanged();
        }
    }


