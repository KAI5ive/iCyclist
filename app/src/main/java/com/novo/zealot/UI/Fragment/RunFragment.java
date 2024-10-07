package com.novo.zealot.UI.Fragment;

/**
 * Created by Novo on 2019/5/27.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.novo.zealot.Bean.RunRecord;
import com.novo.zealot.R;
import com.novo.zealot.UI.Activity.CountDownActivity;
import com.novo.zealot.Utils.DateUtil;
import com.novo.zealot.Utils.GlobalUtil;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.List;

public class RunFragment extends Fragment {

    public static final String TAG = "RunFragment";

    TickerView tv_todayDistance;
    ImageButton btn_run;
    Context context;
    TextView tv_unit;


    public RunFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_run, container, false);

        //设置TickerView
        tv_todayDistance = view.findViewById(R.id.tv_todayDistance);
        tv_todayDistance.setCharacterLists(TickerUtils.provideNumberList());
        tv_todayDistance.setAnimationDuration(2000);

        //添加OnClickListener
        btn_run = view.findViewById(R.id.btn_run);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CountDownActivity.class);
                context.startActivity(intent);
            }
        });
        tv_unit = view.findViewById(R.id.tv_unit);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        String todayDate = DateUtil.getFormattedDate();
        List<RunRecord> results = GlobalUtil.getInstance().databaseHelper.queryRecord(todayDate);

        //今日运动总距离
        int todayTotalDistance = 0;
        for (RunRecord record :
                results) {
            todayTotalDistance += record.getDistance();
        }

        //若大于1公里，则显示公里数
        if (todayTotalDistance < 1000) {
            tv_todayDistance.setText(todayTotalDistance + "");
        }else{
            double disKM = todayTotalDistance/1000.0;
            tv_todayDistance.setText(disKM + "");
            tv_unit.setText("公里");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
