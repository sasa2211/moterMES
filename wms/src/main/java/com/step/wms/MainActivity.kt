package com.step.wms

import com.step.wms.activity.BaseKtActivity
import com.step.wms.databinding.ActivityMainBinding

class MainActivity : BaseKtActivity<ActivityMainBinding>() {
    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        binding.tvName.text = "this is test project"
    }

    suspend fun login(x: String): String {
        return ""
    }
}