package com.posturedetection.android.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.posturedetection.android.R;

public class TitleLayout extends LinearLayout {
    private ImageView iv_backward,iv_save;
    private TextView tv_title;

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LinearLayout bar_title = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_title, this);
        iv_backward = (ImageView) bar_title.findViewById(R.id.iv_backward);
        iv_save = (ImageView) bar_title.findViewById(R.id.iv_save);
        tv_title = (TextView) bar_title.findViewById(R.id.tv_title);

        iv_backward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });

        //设置监听器
        //如果点击back则结束活动
//        iv_backward.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((Activity)getContext()).finish();
//            }
//        });
//
//        //如果点击save则保存数据
//        iv_save.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //保存数据
//
//            }
//        });
    }


    public TextView getTextView_title(){
        return tv_title;
    }

    public void setTextView_title(String title){
        tv_title.setText(title);
    }

    public ImageView getIv_save(){
        return iv_save;
    }

    public ImageView getIv_backward(){
        return iv_backward;
    }
}
