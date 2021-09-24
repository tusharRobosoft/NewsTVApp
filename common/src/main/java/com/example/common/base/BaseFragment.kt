package com.example.common.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment {
    internal constructor() : super()
    constructor(@LayoutRes layoutResId: Int) : super(layoutResId)
}