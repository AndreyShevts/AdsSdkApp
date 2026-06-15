package com.example.ads_sdk_app.ads

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.NativeAd
import com.appodeal.ads.nativead.NativeAdViewNewsFeed

class NativeAdsAdapter(
    private val activity: AppCompatActivity,
    private val logger: AdEventLogger,
    private val ads: List<NativeAd>
) : BaseAdapter() {

    override fun getCount(): Int = ads.size

    override fun getItem(position: Int): NativeAd = ads[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val nativeView = convertView as? NativeAdViewNewsFeed
            ?: NativeAdViewNewsFeed(activity)

        val nativeAd = getItem(position)
        val registered = nativeView.registerView(nativeAd)

        logger.log("Native Ad ${position + 1}: registerView result: $registered")

        return nativeView
    }
}