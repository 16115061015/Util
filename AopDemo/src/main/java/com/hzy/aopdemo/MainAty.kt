package com.hzy.aopdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hzy.aopdemo.aop.ClickTrack
import kotlinx.android.synthetic.main.activity_main.*

/**
 * User: hzy
 * Date: 2020/6/25
 * Time: 4:27 PM
 * Description:
 */
class MainAty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv.setOnClickListener {
            onClick()
        }
    }

    @ClickTrack
    private fun onClick() {
        Toast.makeText(this, "cilck", Toast.LENGTH_SHORT).show()
    }
}

