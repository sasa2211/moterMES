package com.step.wms.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseKtActivity<T : ViewDataBinding> : AppCompatActivity() {

    protected val binding : T by lazy{
        DataBindingUtil.setContentView(this@BaseKtActivity, layoutId())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }
    abstract fun layoutId(): Int
    abstract fun initView()
}