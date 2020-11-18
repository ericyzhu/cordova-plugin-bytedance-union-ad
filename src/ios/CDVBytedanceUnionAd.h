#import <Cordova/CDVPlugin.h>
#import "BUAdSDK/BUSplashAdView.h"
#import <BUAdSDK/BURewardedVideoAd.h>
#import <BUAdSDK/BURewardedVideoModel.h>
#import <BUAdSDK/BUNativeExpressInterstitialAd.h>
#import <BUAdSDK/BUNativeExpressBannerView.h>

@interface CDVBytedanceUnionAd : CDVPlugin <BUSplashAdDelegate, BUSplashZoomOutViewDelegate, BURewardedVideoAdDelegate, BUNativeExpresInterstitialAdDelegate, BUNativeExpressBannerViewDelegate>

@property(nonatomic, strong)CDVInvokedUrlCommand *splashCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *rewardedVideoCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *interstitialCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *bannerCommand;

- (void)showSplashAd:(CDVInvokedUrlCommand*)command;
- (void)showRewardedVideoAd:(CDVInvokedUrlCommand*)command;
- (void)showInterstitialAd:(CDVInvokedUrlCommand*)command;
- (void)showBannerAd:(CDVInvokedUrlCommand*)command;

@end
