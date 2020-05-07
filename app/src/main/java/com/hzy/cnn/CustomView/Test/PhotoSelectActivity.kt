package com.hzy.cnn.CustomView.Test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hzy.cnn.CustomView.R
import com.sgxy.hzy.photoselector.build.PhotoSelect
import com.sgxy.hzy.photoselector.util.PhotoSelectorUtil
import kotlinx.android.synthetic.main.aty_select_photo.*

/**
 * User: hzy
 * Date: 2020/5/5
 * Time: 6:16 PM
 * Description:
 */
class PhotoSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_select_photo)

        btnSingle.setOnClickListener {
            PhotoSelect.with(this)
                    .showGif(true)
                    .maxSelect(1)
                    .columnsNum(3)
                    .isCrop(true)
                    .setCallBack(PhotoSelectorUtil.SelectedPhotoCallback { selectedList ->
                        for (info in selectedList) {
                            tvResult.text = info.path
                        }
                    }).build()

        }
        btnMutile.setOnClickListener {
            PhotoSelect.with(this)
                    .showGif(true)
                    .maxSelect(2)
                    .columnsNum(3)
                    .isCrop(true)
                    .setCallBack(PhotoSelectorUtil.SelectedPhotoCallback { selectedList ->
                        for (info in selectedList) {
                            tvResult.text = info.path
                        }
                    }).build()
        }
    }

}