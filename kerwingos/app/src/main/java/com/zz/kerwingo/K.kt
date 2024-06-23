package com.zz.kerwingo

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zz.route_runtime.Router.go
import com.zz.router_annotation.Destination

@Destination(url = "route://kglidetest", description = "kglide测试页面")
class K : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val t = TextView(this)
        t.layoutParams = ViewGroup.LayoutParams(-1, -1)
        t.setBackgroundColor(Color.WHITE)
        t.setTextColor(Color.BLUE)
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
        t.gravity = Gravity.CENTER
        t.text = intent.getStringExtra("message") + " : " + intent.getStringExtra("count")
        setContentView(t)
    }
}