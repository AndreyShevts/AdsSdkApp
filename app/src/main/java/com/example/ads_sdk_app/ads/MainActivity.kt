package com.example.ads_sdk_app.ads

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.NativeAd
import com.example.ads_sdk_app.R

class MainActivity : AppCompatActivity() {

    private lateinit var btnBanner: Button
    private lateinit var btnInterstitial: Button
    private lateinit var btnRewarded: Button
    private lateinit var btnNative: Button
    private lateinit var nativeList: ListView

    private lateinit var adLogger: AdEventLogger
    private lateinit var adsManager: AppodealAdsManager

    private val handler = Handler(Looper.getMainLooper())

    private val buttonUpdater = object : Runnable {
        override fun run() {
            updateButtons()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBanner = findViewById(R.id.btnBanner)
        btnInterstitial = findViewById(R.id.btnInterstitial)
        btnRewarded = findViewById(R.id.btnRewarded)
        btnNative = findViewById(R.id.btnNative)
        nativeList = findViewById(R.id.nativeList)

        adLogger = AdEventLogger(this)
        adLogger.clear()
        adLogger.log("MainActivity created. Local Appodeal log cleared")

        adsManager = AppodealAdsManager(
            activity = this,
            logger = adLogger,
            listener = object : AppodealAdsManager.Listener {
                override fun onNativeAdsReady(nativeAds: List<NativeAd>) {
                    nativeList.adapter = NativeAdsAdapter(
                        activity = this@MainActivity,
                        logger = adLogger,
                        ads = nativeAds
                    )
                    nativeList.visibility = View.VISIBLE
                    adLogger.log("Native ListView adapter attached")
                }

                override fun onNativeAdsHidden() {
                    nativeList.visibility = View.GONE
                }

                override fun onButtonsStateChanged() {
                    updateButtons()
                }
            }
        )

        btnBanner.setOnClickListener {
            adLogger.log("Banner button pressed")
            adsManager.showBanner()
        }

        btnInterstitial.setOnClickListener {
            adLogger.log("Interstitial button pressed")
            adsManager.showInterstitial()
        }

        btnRewarded.setOnClickListener {
            adLogger.log("Rewarded button pressed")
            adsManager.showRewarded()
        }

        btnNative.setOnClickListener {
            adLogger.log("Native button pressed")
            adsManager.showNativeAds()
        }

        adsManager.initialize()

        updateButtons()
        handler.post(buttonUpdater)
    }

    private fun updateButtons() {
        btnBanner.isEnabled = adsManager.canShowBanner()
        btnInterstitial.isEnabled = adsManager.canShowInterstitial()
        btnRewarded.isEnabled = adsManager.canShowRewarded()
        btnNative.isEnabled = adsManager.canShowNative()
    }

    override fun onDestroy() {
        adLogger.log("MainActivity onDestroy. Hide banner and remove handlers")
        handler.removeCallbacks(buttonUpdater)
        adsManager.destroy()
        super.onDestroy()
    }
}