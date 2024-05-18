//package com.hxjt.dqyt.fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.GridView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.hxjt.dqyt.R;
//import com.hxjt.dqyt.adapter.DeviceAdapter;
//import com.hxjt.dqyt.bean.DeviceInfoBean;
//
//import java.util.List;
//
//public class DeviceListFragment extends Fragment {
//
//    private GridView gridView;
//    private DeviceAdapter adapter;
//    private List<DeviceInfoBean> deviceList;
//
//    public DeviceListFragment(List<DeviceInfoBean> deviceList){
//        this.deviceList = deviceList;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.device_list_layout, container, false);
//
//        // 初始化 RecyclerView
//        gridView = view.findViewById(R.id.grid_view);
//
//        adapter = new DeviceAdapter(getContext(),deviceList);
//        gridView.setAdapter(adapter);
//
//        return view;
//    }
//}
