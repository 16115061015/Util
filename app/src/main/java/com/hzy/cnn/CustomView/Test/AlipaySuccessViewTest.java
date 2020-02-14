package com.hzy.cnn.CustomView.Test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hzy.cnn.CustomView.Ui.view.AlipaySuccessView;
import com.hzy.cnn.CustomView.R;

/**
 * Created by joel.
 * Date: 2019/5/17
 * Time: 19:53
 * Description:
 */
public class AlipaySuccessViewTest extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alpipaysuccessview_layout);
        //alipaysuccessview 使用实例
        AlipaySuccessView alipaySuccessView=findViewById(R.id.alpv);
        if(alipaySuccessView!=null) {
            alipaySuccessView.startAnimator();
        }
    }
}
