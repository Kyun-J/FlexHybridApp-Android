package app.dvkyun.flexhybrid

import android.R.color.black
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

class FlexWebChromeClient(activity: Activity) : WebChromeClient() {

    private var mActivity: Activity = activity

    private var mCustomView: View? = null
    private var mCustomViewCallback: CustomViewCallback? = null
    private var mOriginalOrientation = 0

    private var mFullscreenContainer: FrameLayout? = null

    private val params = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    )

    override fun onShowCustomView(view: View?, callback: CustomViewCallback) {
        if (mCustomView != null) {
            callback.onCustomViewHidden()
            return
        }
        mOriginalOrientation = mActivity.requestedOrientation
        val decor = mActivity.window.decorView as FrameLayout
        mFullscreenContainer = FullscreenHolder(mActivity)
        (mFullscreenContainer as FullscreenHolder).addView(view, params)
        decor.addView(mFullscreenContainer, params)
        mCustomView = view
        setFullscreen(true)
        mCustomViewCallback = callback
        super.onShowCustomView(view, callback)
    }


    override fun onShowCustomView(
        view: View?,
        requestedOrientation: Int,
        callback: CustomViewCallback
    ) {
        this.onShowCustomView(view, callback)
    }

    override fun onHideCustomView() {
        if (mCustomView == null) {
            return
        }
        setFullscreen(false)
        val decor = mActivity.window.decorView as FrameLayout
        decor.removeView(mFullscreenContainer)
        mFullscreenContainer = null
        mCustomView = null
        mCustomViewCallback!!.onCustomViewHidden()
        mActivity.requestedOrientation = mOriginalOrientation
    }

    private fun setFullscreen(enabled: Boolean) {
        val win = mActivity.window
        val winParams = win.attributes
        val bits = WindowManager.LayoutParams.FLAG_FULLSCREEN
        if (enabled) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
            if (mCustomView != null) {
                mCustomView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
        win.attributes = winParams
    }

    private class FullscreenHolder(ctx: Context?) : FrameLayout(ctx!!) {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(evt: MotionEvent): Boolean {
            return true
        }

        init {
            setBackgroundColor(ContextCompat.getColor(ctx!!, black))
        }
    }
}