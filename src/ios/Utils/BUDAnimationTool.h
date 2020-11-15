#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <BUAdSDK/BUSplashAdView.h>
#import <BUAdSDK/BUSplashZoomOutView.h>
typedef void (^BUDAnimationCompletion)(void);
NS_ASSUME_NONNULL_BEGIN

@interface BUDAnimationTool : NSObject
+ (instancetype)sharedInstance;
- (void)transitionFromView:(BUSplashAdView *)fromView toView:(BUSplashZoomOutView *)toView;
@end

NS_ASSUME_NONNULL_END
