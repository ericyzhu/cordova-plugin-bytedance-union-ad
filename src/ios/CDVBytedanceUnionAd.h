#import <Cordova/CDVPlugin.h>
#import "BUAdSDK/BUSplashAdView.h"
#import <BUAdSDK/BURewardedVideoAd.h>
#import <BUAdSDK/BURewardedVideoModel.h>

@interface CDVBytedanceUnionAd : CDVPlugin <BUSplashAdDelegate, BUSplashZoomOutViewDelegate, BURewardedVideoAdDelegate>

@property(nonatomic, strong)CDVInvokedUrlCommand *splashCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *rewardedVideoCommand;

- (void)showSplashAd:(CDVInvokedUrlCommand*)command;
- (void)showRewardedVideoAd:(CDVInvokedUrlCommand*)command;

@end
