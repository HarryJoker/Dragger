package com.example.wanghua.dragger;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


public class MainActivity extends AppCompatActivity {

    DraggerPageLayout layout;

    TextView tv_sign;

    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setCustomTitle();

        Logger.addLogAdapter(new AndroidLogAdapter(){
            @Override
            public boolean isLoggable(int priority, String tag) {
                return true;
            }
        });

        layout = findViewById(R.id.layout_drag);

        layout.setBackgroundColor(Color.GRAY);

        tv_sign = findViewById(R.id.tv_sign);

    }



    int count = 1;

    public void addClick(View v) {

        TextView textView = new TextView(this);
        textView.setText("王华" + count++);
        textView.setBackgroundColor(Color.CYAN);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tv_sign.getWidth(), tv_sign.getHeight());
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.leftMargin = tv_sign.getLeft();
        textView.setLayoutParams(layoutParams);

        layout.addDragView(textView);

    }

    private void setCustomTitle() {
        String ctitle = "当前第 " + (page + 1) + "页";
        setTitle("Dragger  " + ctitle);
        Toast.makeText(this, ctitle, Toast.LENGTH_SHORT).show();
    }

    public void lastClick(View v) {
        if (page == 0) {
            Toast.makeText(this, "没有上一页了", Toast.LENGTH_SHORT).show();
        } else {
            layout.selectCurrentPage(--page);
        }
        setCustomTitle();
    }

    public void nextClick(View v) {
        layout.selectCurrentPage(++page);
        setCustomTitle();
    }
}
