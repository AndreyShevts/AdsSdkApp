package com.example.ads_sdk_app.ads

import androidx.appcompat.app.AppCompatActivity
import com.appodeal.ads.Appodeal
import com.appodeal.ads.BannerCallbacks
import com.appodeal.ads.InterstitialCallbacks
import com.appodeal.ads.NativeAd
import com.appodeal.ads.RewardedVideoCallbacks
import com.appodeal.ads.initializing.ApdInitializationCallback
import com.appodeal.ads.initializing.ApdInitializationError

class AppodealAdsManager(
    private val activity: AppCompatActivity,
    private val logger: AdEventLogger,
    private val listener: Listener
) {

    interface Listener {
        fun onNativeAdsReady(nativeAds: List<NativeAd>)
        fun onNativeAdsHidden()
        fun onButtonsStateChanged()
    }

    private var isInitialized = false
    private var bannerCount = 0
    private var rewardedShown = 0
    private var lastInterstitialTime = 0L

    companion object {
        private const val APPODEAL_APP_KEY = "83924fa670436be853a45d53b7a43a1eb5f5f6c0d319f695"

        private const val MAX_BANNERS = 5
        private const val MAX_REWARDED = 3
        private const val INTERSTITIAL_COOLDOWN_MS = 60_000L
        private const val NATIVE_REQUIRED_COUNT = 3
    }

    fun initialize() {
        setupCallbacks()

        val adTypes = Appodeal.INTERSTITIAL or
                Appodeal.REWARDED_VIDEO or
                Appodeal.BANNER or
                Appodeal.NATIVE

        logger.log("Appodeal initialization started")

        Appodeal.setTesting(true)
        logger.log("Appodeal test mode enabled")

        Appodeal.initialize(
            context = activity,
            appKey = APPODEAL_APP_KEY,
            adTypes = adTypes,
            callback = object : ApdInitializationCallback {
                override fun onInitializationFinished(errors: List<ApdInitializationError>?) {
                    isInitialized = true

                    if (errors.isNullOrEmpty()) {
                        logger.log("Appodeal initialized successfully")
                    } else {
                        logger.log("Appodeal initialized with errors: $errors")
                    }

                    cacheNativeAds()
                    listener.onButtonsStateChanged()
                }
            }
        )
    }

    private fun setupCallbacks() {
        Appodeal.setBannerCallbacks(object : BannerCallbacks {
            override fun onBannerLoaded(height: Int, isPrecache: Boolean) {
                logger.log("Banner callback: onBannerLoaded. Height: $height, isPrecache: $isPrecache")
                listener.onButtonsStateChanged()
            }

            override fun onBannerFailedToLoad() {
                logger.log("Banner callback: onBannerFailedToLoad")
                listener.onButtonsStateChanged()
            }

            override fun onBannerShown() {
                bannerCount++
                logger.log("Banner callback: onBannerShown. Session count: $bannerCount")

                if (bannerCount >= MAX_BANNERS) {
                    Appodeal.hide(activity, Appodeal.BANNER)
                    logger.log("Banner limit reached. Banners disabled")
                }

                listener.onButtonsStateChanged()
            }

            override fun onBannerShowFailed() {
                logger.log("Banner callback: onBannerShowFailed")
                listener.onButtonsStateChanged()
            }

            override fun onBannerClicked() {
                logger.log("Banner callback: onBannerClicked")
            }

            override fun onBannerExpired() {
                logger.log("Banner callback: onBannerExpired")
                listener.onButtonsStateChanged()
            }
        })

        Appodeal.setInterstitialCallbacks(object : InterstitialCallbacks {
            override fun onInterstitialLoaded(isPrecache: Boolean) {
                logger.log("Interstitial callback: onInterstitialLoaded. isPrecache: $isPrecache")
                listener.onButtonsStateChanged()
            }

            override fun onInterstitialFailedToLoad() {
                logger.log("Interstitial callback: onInterstitialFailedToLoad")
                listener.onButtonsStateChanged()
            }

            override fun onInterstitialShown() {
                logger.log("Interstitial callback: onInterstitialShown")
                listener.onButtonsStateChanged()
            }

            override fun onInterstitialShowFailed() {
                logger.log("Interstitial callback: onInterstitialShowFailed")
                listener.onButtonsStateChanged()
            }

            override fun onInterstitialClicked() {
                logger.log("Interstitial callback: onInterstitialClicked")
            }

            override fun onInterstitialClosed() {
                lastInterstitialTime = System.currentTimeMillis()
                logger.log("Interstitial callback: onInterstitialClosed. Cooldown started")
                listener.onButtonsStateChanged()
            }

            override fun onInterstitialExpired() {
                logger.log("Interstitial callback: onInterstitialExpired")
                listener.onButtonsStateChanged()
            }
        })

        Appodeal.setRewardedVideoCallbacks(object : RewardedVideoCallbacks {
            override fun onRewardedVideoLoaded(isPrecache: Boolean) {
                logger.log("Rewarded callback: onRewardedVideoLoaded. isPrecache: $isPrecache")
                listener.onButtonsStateChanged()
            }

            override fun onRewardedVideoFailedToLoad() {
                logger.log("Rewarded callback: onRewardedVideoFailedToLoad")
                listener.onButtonsStateChanged()
            }

            override fun onRewardedVideoShown() {
                logger.log("Rewarded callback: onRewardedVideoShown")
                listener.onButtonsStateChanged()
            }

            override fun onRewardedVideoShowFailed() {
                logger.log("Rewarded callback: onRewardedVideoShowFailed")
                listener.onButtonsStateChanged()
            }

            override fun onRewardedVideoClicked() {
                logger.log("Rewarded callback: onRewardedVideoClicked")
            }

            override fun onRewardedVideoFinished(amount: Double, currency: String) {
                rewardedShown++
                logger.log("Rewarded callback: onRewardedVideoFinished. Reward: $amount $currency. Session count: $rewardedShown")
                listener.onButtonsStateChanged()
            }

            override fun onRewardedVideoClosed(finished: Boolean) {
                logger.log("Rewarded callback: onRewardedVideoClosed. Finished: $finished")
                listener.onButtonsStateChanged()
            }

            override fun onRewardedVideoExpired() {
                logger.log("Rewarded callback: onRewardedVideoExpired")
                listener.onButtonsStateChanged()
            }
        })
    }

    fun showBanner() {
        if (!canShowBanner()) {
            logger.log("Banner unavailable or session limit reached")
            listener.onButtonsStateChanged()
            return
        }

        listener.onNativeAdsHidden()

        val shown = Appodeal.show(activity, Appodeal.BANNER_TOP)
        logger.log("Banner show requested. Result: $shown")

        listener.onButtonsStateChanged()
    }

    fun showInterstitial() {
        if (!isInitialized) {
            logger.log("Interstitial unavailable: Appodeal not initialized")
            listener.onButtonsStateChanged()
            return
        }

        if (!isInterstitialCooldownReady()) {
            logger.log("Interstitial cooldown active")
            listener.onButtonsStateChanged()
            return
        }

        if (!Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            logger.log("Interstitial not loaded")
            listener.onButtonsStateChanged()
            return
        }

        Appodeal.hide(activity, Appodeal.BANNER)
        listener.onNativeAdsHidden()

        val shown = Appodeal.show(activity, Appodeal.INTERSTITIAL)
        logger.log("Interstitial show requested. Result: $shown")

        listener.onButtonsStateChanged()
    }

    fun showRewarded() {
        if (!isInitialized) {
            logger.log("Rewarded unavailable: Appodeal not initialized")
            listener.onButtonsStateChanged()
            return
        }

        if (rewardedShown >= MAX_REWARDED) {
            logger.log("Rewarded session limit reached")
            listener.onButtonsStateChanged()
            return
        }

        if (!Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
            logger.log("Rewarded not loaded")
            listener.onButtonsStateChanged()
            return
        }

        Appodeal.hide(activity, Appodeal.BANNER)
        listener.onNativeAdsHidden()

        val shown = Appodeal.show(activity, Appodeal.REWARDED_VIDEO)
        logger.log("Rewarded show requested. Result: $shown")

        listener.onButtonsStateChanged()
    }

    fun showNativeAds() {
        if (!isInitialized) {
            logger.log("Native unavailable: Appodeal not initialized")
            listener.onButtonsStateChanged()
            return
        }

        Appodeal.hide(activity, Appodeal.BANNER)

        val availableCount = Appodeal.getAvailableNativeAdsCount()
        logger.log("Native available count: $availableCount")

        if (availableCount < NATIVE_REQUIRED_COUNT) {
            logger.log("Not enough native ads. Required: $NATIVE_REQUIRED_COUNT, available: $availableCount")
            cacheNativeAds()
            listener.onButtonsStateChanged()
            return
        }

        val nativeAds = Appodeal.getNativeAds(NATIVE_REQUIRED_COUNT)
        logger.log("Native ads received. Count: ${nativeAds.size}")

        listener.onNativeAdsReady(nativeAds)
        cacheNativeAds()
        listener.onButtonsStateChanged()
    }

    fun canShowBanner(): Boolean =
        isInitialized && bannerCount < MAX_BANNERS

    fun canShowInterstitial(): Boolean =
        isInitialized &&
                isInterstitialCooldownReady() &&
                Appodeal.isLoaded(Appodeal.INTERSTITIAL)

    fun canShowRewarded(): Boolean =
        isInitialized &&
                rewardedShown < MAX_REWARDED &&
                Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)

    fun canShowNative(): Boolean = isInitialized

    fun destroy() {
        Appodeal.hide(activity, Appodeal.BANNER)
    }

    private fun cacheNativeAds() {
        logger.log("Native cache requested: $NATIVE_REQUIRED_COUNT")
        Appodeal.cache(activity, Appodeal.NATIVE, NATIVE_REQUIRED_COUNT)
    }

    private fun isInterstitialCooldownReady(): Boolean =
        System.currentTimeMillis() - lastInterstitialTime >= INTERSTITIAL_COOLDOWN_MS
}