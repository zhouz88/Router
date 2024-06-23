package com.zz.kerwingo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zz.route_runtime.Router;
import com.zz.router_annotation.Destination;
@Destination(url = "route://glidetest", description = "glide测试页面")
public class H extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView t = new TextView(this);
        t.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        t.setBackgroundColor(Color.BLACK);
        t.setText("start!");
        setContentView(t);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router.INSTANCE.go(H.this, "route://kglidetest?message=happyness&count=100 %25");
            }
        });
    }
}

/**
 * url出现了有+，空格，/，?，%，#，&，=等特殊符号的时候，可能在服务器端无法获得正确的参数值，如何是好？
 *
 * 解决办法
 *
 * 将这些字符转化成服务器可以识别的字符，对应关系如下：
 *
 * URL字符转义
 *
 * 用其它字符替代吧，或用全角的。
 *
 * +    URL 中+号表示空格                                 %2B
 *
 * 空格 URL中的空格可以用+号或者编码           %20
 *
 * /   分隔目录和子目录                                     %2F
 *
 * ?    分隔实际的URL和参数                             %3F
 *
 * %    指定特殊字符                                          %25
 *
 * #    表示书签                                                  %23
 *
 * &    URL 中指定的参数间的分隔符                  %26
 *
 * =    URL 中指定参数的值                                %3D
 */
