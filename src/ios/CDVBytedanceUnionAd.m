#import "CDVBytedanceUnionAd.h"
#import <BUAdSDK/BUAdSDKManager.h>
#import "BUAdSDK/BUSplashAdView.h"
#import <BUAdSDK/BURewardedVideoAd.h>
#import <BUAdSDK/BURewardedVideoModel.h>
#import <BUAdSDK/BUNativeExpressInterstitialAd.h>
#import <BUAdSDK/BUNativeExpressBannerView.h>
#import <BUAdSDK/BUAdSDK.h>
#import "BUDAnimationTool.h"

@interface CDVBytedanceUnionAd () <BUSplashAdDelegate, BUSplashZoomOutViewDelegate, BURewardedVideoAdDelegate, BUNativeExpresInterstitialAdDelegate, BUNativeExpressBannerViewDelegate>
@property (nonatomic, assign) CFTimeInterval startTime;
@property (nonatomic, strong) BUSplashAdView *splashAdView;
@property (nonatomic, strong) BURewardedVideoAd *rewardedVideoAd;
@property (nonatomic, strong) BUNativeExpressInterstitialAd *interstitialAd;
@property(nonatomic, strong) BUNativeExpressBannerView *bannerView;
@end

@implementation CDVBytedanceUnionAd

- (void)pluginInitialize
{
    NSString *appId = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CDVBytedanceUnionAdAppId"];

    //optional
    //GDPR 0 close privacy protection, 1 open privacy protection
    [BUAdSDKManager setGDPR:0];
    //optional
    //Coppa 0 adult, 1 child
    [BUAdSDKManager setCoppa:0];

#if DEBUG
    // Whether to open log. default is none.
    [BUAdSDKManager setLoglevel:BUAdSDKLogLevelDebug];
    //    [BUAdSDKManager setDisableSKAdNetwork:YES];
#endif
    //BUAdSDK requires iOS 9 and up
    [BUAdSDKManager setAppID:appId];

    [BUAdSDKManager setIsPaidApp:NO];
}

- (void)sendPluginResult:(CDVInvokedUrlCommand *)command
                withType:(NSString*)type
            keepCallback:(BOOL)keepCallback {
    if (command != nil) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsDictionary:@{@"type":type}];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:keepCallback]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void)sendPluginResult:(CDVInvokedUrlCommand *)command
                withType:(NSString*)type
                    code:(NSNumber*)code
                 message:(NSString*)message
            keepCallback:(BOOL)keepCallback {
    if (command != nil) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsDictionary:@{@"type":type,@"code":code,@"message":message}];
        [pluginResult setKeepCallback:[NSNumber numberWithBool:keepCallback]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (UIViewController *)getRootViewControler {
    //获取根控制器
    UIViewController *rootVC = [[UIApplication sharedApplication].delegate window].rootViewController;

    UIViewController *parent = rootVC;
    //遍历 如果是presentViewController
    while ((parent = rootVC.presentedViewController) != nil ) {
        rootVC = parent;
    }

    while ([rootVC isKindOfClass:[UINavigationController class]]) {
        rootVC = [(UINavigationController *)rootVC topViewController];
    }

    return rootVC;
}

#pragma mark - Splash Ad

- (void)showSplashAd:(CDVInvokedUrlCommand*)command
{
    self.splashCommand = command;

    NSDictionary* options = [command.arguments objectAtIndex:0];
    NSString *slotId = options[@"slotId"];

    if (slotId) {
        CGRect frame = [UIScreen mainScreen].bounds;
        self.splashAdView = [[BUSplashAdView alloc] initWithSlotID:slotId frame:frame];
        // tolerateTimeout = CGFLOAT_MAX , The conversion time to milliseconds will be equal to 0
        self.splashAdView.tolerateTimeout = 3;
        self.splashAdView.delegate = self;
        //optional
        self.splashAdView.needSplashZoomOutAd = YES;

        UIWindow *keyWindow = [[[UIApplication sharedApplication] delegate] window];
        self.startTime = CACurrentMediaTime();
        [self.splashAdView loadAdData];
        [keyWindow.rootViewController.view addSubview:self.splashAdView];
        self.splashAdView.rootViewController = keyWindow.rootViewController;
    }
}

- (void)removeSplashAdView {
    if (self.splashAdView) {
        [self.splashAdView removeFromSuperview];
        self.splashAdView = nil;
    }
}

#pragma mark BUSplashAdDelegate

/**
 闪屏广告物料载入成功时调用。
 */
- (void)splashAdDidLoad:(BUSplashAdView *)splashAd {
    if (splashAd.zoomOutView) {
        UIViewController *parentVC = [UIApplication sharedApplication].keyWindow.rootViewController;
        [parentVC.view addSubview:splashAd.zoomOutView];
        [parentVC.view bringSubviewToFront:splashAd];
        //Add this view to your container
        [parentVC.view insertSubview:splashAd.zoomOutView belowSubview:splashAd];
        splashAd.zoomOutView.rootViewController = parentVC;
        splashAd.zoomOutView.delegate = self;
    }

    [self sendPluginResult:self.splashCommand withType:@"show" keepCallback:YES];
}

/**
 闪屏广告被关闭时调用。
 */
- (void)splashAdDidClose:(BUSplashAdView *)splashAd {
    if (splashAd.zoomOutView) {
        [[BUDAnimationTool sharedInstance] transitionFromView:splashAd toView:splashAd.zoomOutView];
    } else{
        // Be careful not to say 'self.splashadview = nil' here.
        // Subsequent agent callbacks will not be triggered after the 'splashAdView' is released early.
        [splashAd removeFromSuperview];
    }

    [self sendPluginResult:self.splashCommand withType:@"close" keepCallback:NO];
    self.splashCommand = nil;
}

/**
 闪屏广告被点击时调用。
 */
- (void)splashAdDidClick:(BUSplashAdView *)splashAd {
    if (splashAd.zoomOutView) {
        [splashAd.zoomOutView removeFromSuperview];
    }
    // Be careful not to say 'self.splashadview = nil' here.
    // Subsequent agent callbacks will not be triggered after the 'splashAdView' is released early.
    [splashAd removeFromSuperview];

    [self sendPluginResult:self.splashCommand withType:@"click" keepCallback:YES];
}

/**
 闪屏广告跳过按钮被点击时调用。
 */
- (void)splashAdDidClickSkip:(BUSplashAdView *)splashAd {
    if (splashAd.zoomOutView) {
        [[BUDAnimationTool sharedInstance] transitionFromView:splashAd toView:splashAd.zoomOutView];
    } else{
        // Click Skip, there is no subsequent operation, completely remove 'splashAdView', avoid memory leak
        [self removeSplashAdView];
    }

    [self sendPluginResult:self.splashCommand withType:@"skip" keepCallback:YES];
}

/**
 闪屏广告物料载入失败时调用。
 @param error : 错误原因
 */
- (void)splashAd:(BUSplashAdView *)splashAd didFailWithError:(NSError *)error {
    [self removeSplashAdView];

    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.splashCommand withType:@"error" code:code message:message keepCallback:NO];
    self.splashCommand = nil;
}

/**
 闪屏广告倒计时归零时调用。
 */
- (void)splashAdCountdownToZero:(BUSplashAdView *)splashAd {
    if (!splashAd.zoomOutView) {
        [self removeSplashAdView];
    }
}

- (void)splashAdDidCloseOtherController:(BUSplashAdView *)splashAd interactionType:(BUInteractionType)interactionType {
    [self removeSplashAdView];
}

#pragma mark - BUSplashZoomOutViewDelegate

- (void)splashZoomOutViewAdDidClick:(BUSplashZoomOutView *)splashAd {
}

- (void)splashZoomOutViewAdDidClose:(BUSplashZoomOutView *)splashAd {
    // Click close, completely remove 'splashAdView', avoid memory leak
    [self removeSplashAdView];
}

- (void)splashZoomOutViewAdDidAutoDimiss:(BUSplashZoomOutView *)splashAd {
    // Back down at the end of the countdown to completely remove the 'splashAdView' to avoid memory leaks
    [self removeSplashAdView];
}

- (void)splashZoomOutViewAdDidCloseOtherController:(BUSplashZoomOutView *)splashAd interactionType:(BUInteractionType)interactionType {
    // No further action after closing the other Controllers, completely remove the 'splashAdView' and avoid memory leaks
    [self removeSplashAdView];
}

#pragma mark - Rewarded Video Ad

- (void)showRewardedVideoAd:(CDVInvokedUrlCommand *)command {
    self.rewardedVideoCommand = command;

    NSDictionary* options = [command.arguments objectAtIndex:0];
    NSString *slotId = options[@"slotId"];
    NSString *userId = options[@"userId"];

    BURewardedVideoModel *model = [[BURewardedVideoModel alloc] init];
    model.userId = userId;

    self.rewardedVideoAd = [[BURewardedVideoAd alloc] initWithSlotID:slotId rewardedVideoModel:model];
    self.rewardedVideoAd.delegate = self;
    [self.rewardedVideoAd loadAdData];
}

- (void)removeRewardedVideoAd {
    if (self.rewardedVideoAd) {
        self.rewardedVideoAd = nil;
    }
}

#pragma mark - BURewardedVideoAdDelegate

/**
 视频广告物料载入成功时调用。
 */
- (void)rewardedVideoAdDidLoad:(BURewardedVideoAd *)rewardedVideoAd {
    if(self.rewardedVideoAd.isAdValid) {
        [self.rewardedVideoAd showAdFromRootViewController:[self getRootViewControler]];
    }

    [self sendPluginResult:self.rewardedVideoCommand withType:@"show" keepCallback:YES];
}

/**
 视频广告物料载入失败时调用。
 @param error : 失败原因
 */
- (void)rewardedVideoAd:(BURewardedVideoAd *)rewardedVideoAd didFailWithError:(NSError *)error {
    [self removeRewardedVideoAd];

    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.rewardedVideoCommand withType:@"error" code:code message:message keepCallback:NO];
    self.rewardedVideoCommand = nil;
}

/**
 视频广告关闭时调用。
 */
- (void)rewardedVideoAdDidClose:(BURewardedVideoAd *)rewardedVideoAd {
    [self removeRewardedVideoAd];

    [self sendPluginResult:self.rewardedVideoCommand withType:@"close" keepCallback:NO];
    self.rewardedVideoCommand = nil;
}

/**
 视频广告被点击时调用。
 */
- (void)rewardedVideoAdDidClick:(BURewardedVideoAd *)rewardedVideoAd {
    [self sendPluginResult:self.rewardedVideoCommand withType:@"click" keepCallback:YES];
}

/**
 视频广告播放完成*或*遇到错误时调用。
 @param error : 错误原因
 */
- (void)rewardedVideoAdDidPlayFinish:(BURewardedVideoAd *)rewardedVideoAd didFailWithError:(NSError *_Nullable)error {
    if (error == nil) {
        [self sendPluginResult:self.rewardedVideoCommand withType:@"play:finish" keepCallback:YES];
    } else {
        NSNumber *code = [NSNumber numberWithLong:error.code];
        NSString *message = error.description;

        [self sendPluginResult:self.rewardedVideoCommand withType:@"play:error" code:code message:message keepCallback:YES];
    }
}

/**
 视频广告跳过按钮被点击时调用。
 */
- (void)rewardedVideoAdDidClickSkip:(BURewardedVideoAd *)rewardedVideoAd{
    [self sendPluginResult:self.rewardedVideoCommand withType:@"skip" keepCallback:YES];
}

/**
 服务端验证异步请求失败时调用。
 @param rewardedVideoAd 激励视频广告
 @param error : 请求错误信息
 */
- (void)rewardedVideoAdServerRewardDidFail:(BURewardedVideoAd *)rewardedVideoAd error:(nonnull NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.rewardedVideoCommand withType:@"verify:error" code:code message:message keepCallback:YES];
}

/**
 服务端验证异步请求成功时调用。
 @param verify : 请求返回值为 2000 时返回 YES
 */
- (void)rewardedVideoAdServerRewardDidSucceed:(BURewardedVideoAd *)rewardedVideoAd verify:(BOOL)verify{
    if (verify == YES) {
        [self sendPluginResult:self.rewardedVideoCommand withType:@"verify:valid" keepCallback:YES];
    } else {
        [self sendPluginResult:self.rewardedVideoCommand withType:@"verify:invalid" keepCallback:YES];
    }
}


#pragma mark - Interstitial Ad

- (void)showInterstitialAd:(CDVInvokedUrlCommand *)command {
    self.interstitialCommand = command;

    NSDictionary* options = [command.arguments objectAtIndex:0];
    NSString *slotId = options[@"slotId"];
    NSNumber *width = options[@"width"];
    NSNumber *height = options[@"height"];

    NSValue *sizeValue = [NSValue valueWithCGSize:CGSizeMake([width doubleValue], [height doubleValue])];
    CGSize size = [sizeValue CGSizeValue];
    CGFloat adWidth = CGRectGetWidth([UIScreen mainScreen].bounds)-40;
    CGFloat adHeight = adWidth/size.width*size.height;
    self.interstitialAd = [[BUNativeExpressInterstitialAd alloc] initWithSlotID:slotId adSize:CGSizeMake(adWidth, adHeight)];
    self.interstitialAd.delegate = self;
    [self.interstitialAd loadAdData];
}

- (void)removeInterstitialAd {
    if (self.interstitialAd) {
        self.interstitialAd = nil;
    }
    [self sendPluginResult:self.interstitialCommand withType:@"close" keepCallback:NO];
    self.interstitialCommand = nil;
}

#pragma mark - BUNativeExpresInterstitialAdDelegate

- (void)nativeExpresInterstitialAdDidLoad:(BUNativeExpressInterstitialAd *)interstitialAd {
}

- (void)nativeExpresInterstitialAd:(BUNativeExpressInterstitialAd *)interstitialAd didFailWithError:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.interstitialCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeInterstitialAd];
}

- (void)nativeExpresInterstitialAdRenderSuccess:(BUNativeExpressInterstitialAd *)interstitialAd {
    [self.interstitialAd showAdFromRootViewController:[self getRootViewControler]];
}

- (void)nativeExpresInterstitialAdRenderFail:(BUNativeExpressInterstitialAd *)interstitialAd error:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.interstitialCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeInterstitialAd];
}

- (void)nativeExpresInterstitialAdWillVisible:(BUNativeExpressInterstitialAd *)interstitialAd {
    [self sendPluginResult:self.interstitialCommand withType:@"show" keepCallback:YES];
}

- (void)nativeExpresInterstitialAdDidClick:(BUNativeExpressInterstitialAd *)interstitialAd {
    [self sendPluginResult:self.interstitialCommand withType:@"click" keepCallback:YES];
}

- (void)nativeExpresInterstitialAdDidClose:(BUNativeExpressInterstitialAd *)interstitialAd {
    [self removeInterstitialAd];
}

- (void)nativeExpresInterstitialAdDidCloseOtherController:(BUNativeExpressInterstitialAd *)interstitialAd interactionType:(BUInteractionType)interactionType {
    [self removeInterstitialAd];
}

#pragma mark - Banner Ad

- (void)showBannerAd:(CDVInvokedUrlCommand *)command {
    self.interstitialCommand = command;

    NSDictionary* options = [command.arguments objectAtIndex:0];
    NSString *slotId = options[@"slotId"];
    NSNumber *width = options[@"width"];
    NSNumber *height = options[@"height"];
    NSNumber *interval = options[@"interval"];
    NSString *align = options[@"align"];

    [self.bannerView removeFromSuperview];

    UIWindow *window = nil;
    if ([[UIApplication sharedApplication].delegate respondsToSelector:@selector(window)]) {
        window = [[UIApplication sharedApplication].delegate window];
    }
    if (![window isKindOfClass:[UIView class]]) {
        window = [UIApplication sharedApplication].keyWindow;
    }
    if (!window) {
        window = [[UIApplication sharedApplication].windows objectAtIndex:0];
    }

    CGFloat top = 0.0;
    CGFloat bottom = 0.0;
    if (@available(iOS 11.0, *)) {
        top = window.safeAreaInsets.top;
        bottom = window.safeAreaInsets.bottom;
    }

    NSValue *sizeValue = [NSValue valueWithCGSize:CGSizeMake([width doubleValue], [height doubleValue])];
    CGSize size = [sizeValue CGSizeValue];
    if (interval > 0) {
        self.bannerView = [[BUNativeExpressBannerView alloc] initWithSlotID:slotId rootViewController:[self getRootViewControler] adSize:size interval:[interval integerValue]];
    } else {
        self.bannerView = [[BUNativeExpressBannerView alloc] initWithSlotID:slotId rootViewController:[self getRootViewControler] adSize:size];
    }

    CGFloat screenWidth = CGRectGetWidth([UIScreen mainScreen].bounds);
    CGFloat screenHeight = CGRectGetHeight([UIScreen mainScreen].bounds);

    if ([@"top" isEqualToString:align]) {
        self.bannerView.frame = CGRectMake((screenWidth-size.width)/2.0, top, size.width, size.height);
    } else {
        self.bannerView.frame = CGRectMake((screenWidth-size.width)/2.0, screenHeight-size.height-bottom, size.width, size.height);
    }

    self.bannerView.delegate = self;
    [self.bannerView loadAdData];
}

- (void)removeBannerAd {
    if (self.bannerView) {
        [self.bannerView removeFromSuperview];
        self.bannerView = nil;
    }
    [self sendPluginResult:self.bannerCommand withType:@"close" keepCallback:NO];
    self.bannerCommand = nil;
}


#pragma mark - BUNativeExpressBannerViewDelegate

- (void)nativeExpressBannerAdViewDidLoad:(BUNativeExpressBannerView *)bannerAdView {
}

- (void)nativeExpressBannerAdView:(BUNativeExpressBannerView *)bannerAdView didLoadFailWithError:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.bannerCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeBannerAd];
}

- (void)nativeExpressBannerAdViewRenderSuccess:(BUNativeExpressBannerView *)bannerAdView {
    UIViewController *parentVC = [UIApplication sharedApplication].keyWindow.rootViewController;
    [parentVC.view addSubview:self.bannerView];
}

- (void)nativeExpressBannerAdViewRenderFail:(BUNativeExpressBannerView *)bannerAdView error:(NSError *)error {
    NSNumber *code = [NSNumber numberWithLong:error.code];
    NSString *message = error.description;

    [self sendPluginResult:self.bannerCommand withType:@"error" code:code message:message keepCallback:YES];
    [self removeBannerAd];
}

- (void)nativeExpressBannerAdViewWillBecomVisible:(BUNativeExpressBannerView *)bannerAdView {
    [self sendPluginResult:self.interstitialCommand withType:@"show" keepCallback:YES];
}

- (void)nativeExpressBannerAdViewDidClick:(BUNativeExpressBannerView *)bannerAdView {
    [self sendPluginResult:self.interstitialCommand withType:@"click" keepCallback:YES];
}

- (void)nativeExpressBannerAdView:(BUNativeExpressBannerView *)bannerAdView dislikeWithReason:(NSArray<BUDislikeWords *> *)filterwords {
    [UIView animateWithDuration:0.25 animations:^{
        bannerAdView.alpha = 0;
    } completion:^(BOOL finished) {
        [self removeBannerAd];
    }];
}

- (void)nativeExpressBannerAdViewDidCloseOtherController:(BUNativeExpressBannerView *)bannerAdView interactionType:(BUInteractionType)interactionType {
    [self removeBannerAd];
}

@end
