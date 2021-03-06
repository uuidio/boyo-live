package com.kol.jumhz.common.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.kol.jumhz.R

/**
 * Created on 2020/12/8.
 *
 * @author Simon
 */
class RemindDialog : Dialog {
    var mContext: Context
    var mTitle: String = ""
    private var title: TextView? = null
    private var cancle: Button? = null
    private var ok: Button? = null

    private var tv_title_up: TextView? = null

    private var tv_up_process: TextView? = null
    private var progress: ProgressBar? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, title: String) : super(context) {
        this.mContext = context
        this.mTitle = title
    }


    override fun show() {
        var view = LayoutInflater.from(mContext).inflate(R.layout.dialog_redmin, null)
        setContentView(view)
        title = view.findViewById<TextView>(R.id.tv_title)
        title?.setText(mTitle)
        cancle = view.findViewById<Button>(R.id.btn_cancle)
        cancle?.setOnClickListener { clickLister?.cancel() }
        ok = view.findViewById<Button>(R.id.btn_ok)
        ok?.setOnClickListener { clickLister?.ok() }

        tv_title_up = view.findViewById(R.id.tv_title_up)
        tv_up_process = view.findViewById(R.id.tv_up_process)
        progress = view.findViewById(R.id.progress)

        window?.setBackgroundDrawable(mContext.resources.getDrawable(android.R.color.transparent))
        window?.setGravity(Gravity.TOP)
        super.show()
    }


    var clickLister: OnClickLister? = null


    fun startUp() {
        setCanceledOnTouchOutside(false)
        ok?.visibility = View.GONE
        cancle?.visibility = View.GONE
        title?.visibility = View.GONE
        tv_up_process?.visibility = View.VISIBLE
        tv_title_up?.visibility = View.VISIBLE
        progress?.visibility = View.VISIBLE
        tv_title_up?.text = "正在下载更新"
    }

    fun setProgress(p: Int) {
        tv_up_process?.text = "$p%"
        progress?.setProgress(p, true)
    }

    interface OnClickLister {
        fun cancel()

        fun ok()
    }
}