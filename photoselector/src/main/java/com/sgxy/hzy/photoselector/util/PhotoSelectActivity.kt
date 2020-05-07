package com.sgxy.hzy.photoselector.util

import android.app.Activity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.sgxy.hzy.photoselector.R
import com.sgxy.hzy.photoselector.build.PhotoSelect
import com.sgxy.hzy.photoselector.config.PSRouteConfig
import kotlinx.android.synthetic.main.aty_select_photo.*

/**
 * User: hzy
 * Date: 2020/5/5
 * Time: 6:16 PM
 * Description:
 */
@Route(path = PSRouteConfig.PhotoSelect)
class PhotoSelectActivity : Activity() {
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