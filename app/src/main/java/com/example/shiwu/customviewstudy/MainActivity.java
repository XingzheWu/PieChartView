package com.example.shiwu.customviewstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.shiwu.customviewstudy.prectise.PieChartView;
import com.example.shiwu.customviewstudy.prectise.DrawData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PieChartView mCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleView = (PieChartView)findViewById(R.id.circle_view);
        initData();
    }

    private void initData() {
        List<DrawData> list = new ArrayList<>();

        DrawData item0 = new DrawData(10,R.color.C1,"第一项第一项第一项");
        DrawData item1 = new DrawData(30,R.color.C2,"第二项");
        DrawData item2 = new DrawData(40,R.color.C3,"第三项");
        DrawData item3 = new DrawData(10,R.color.C4,"第四项");
        DrawData item4 = new DrawData(20,R.color.C5,"第五项");
        list.add(item0);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);

        mCircleView.setData(list);
    }
}
