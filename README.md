# 穿山甲广告 Cordova 插件

## 平台

- iOS
- Android

## 安装

```shell script
cordova plugin add cordova-plugin-bytedance-union-ad
```

## 配置 App ID

在 `config.xml` 中配置以下内容。

```xml
<!-- iOS -->
<platform name="ios">
    <preference name="CDVBytedanceUnionAdAppId" value="YOUR_BUAD_IOS_APP_ID"/>
</platform>

<!-- Android -->
<platform name="android">
    <preference name="CDVBytedanceUnionAdAppId" value="YOUR_BUAD_ANDROID_APP_ID"/>
</platform>
```

## 使用

当成功安装和配置完成后，您可以通过全局变量 `BytedanceUnionAd` 来调用支持的广告类型。

### 类型定义

```javascript
/**
 * 常规事件对象。
 *
 * @typedef {Object} Event
 * @property {String} type - 事件类型
 */

/**
 * 错误事件对象。
 *
 * @typedef {Object} ErrorEvent
 * @property {String} type - 事件类型
 * @property {Number} code - 错误代码
 * @property {String} message - 错误信息
 */

/**
 * 常规事件回调。
 *
 * @callback EventCallback
 * @param {Event} event
 * @return {void}
 */

/**
 * 错误事件回调。
 *
 * @callback ErrorEventCallback
 * @param {ErrorEvent} event
 * @return {void}
 */
```

### 开屏广告 

```javascript
/**
 * 展示广告。
 * 
 * @function BytedanceUnionAd.SplashAd.show
 * @param {String} slotId - 广告位 ID
 * @return {SplashAd} - 返回 SplashAd 实例
 */
BytedanceUnionAd.SplashAd.show(slotId)

/**
 * 监听事件。
 * 
 * @function SplashAd.on
 * @param {String} eventType - 事件类型
 * @param {EventCallback | ErrorEventCallback} callback - 事件回调
 * @return {SplashAd} - 返回 SplashAd 实例
 */
SplashAd.on(eventType, callback)
 ```

#### 事件类型

- `show` - 广告物料载入成功
- `close` - 广告关闭
- `click` - 广告被点击
- `skip` - 广告跳过按钮被点击
- `error` - 广告物料载入失败

### 激励视频广告 

```javascript
/**
 * 展示广告。
 * 
 * @function BytedanceUnionAd.RewardedVideoAd.show
 * @param {String} slotId - 广告位 ID
 * @param {String} [userId] - 可选。用户 ID
 * @return {RewardedVideoAd} - 返回 RewardedVideoAd 实例
 */
BytedanceUnionAd.RewardedVideoAd.show(slotId, userId)

/**
 * 监听事件。
 * 
 * @function RewardedVideoAd.on
 * @param {String} eventType - 事件类型
 * @param {EventCallback | ErrorEventCallback} callback - 事件回调
 * @return {RewardedVideoAd} - 返回 RewardedVideoAd 实例
 */
RewardedVideoAd.on(eventType, callback)
 ```

#### 事件类型

- `show` - 广告物料载入成功
- `close` - 广告关闭
- `click` - 广告被点击
- `skip` - 广告跳过按钮被点击
- `play:finish` - 视频广告播放完成
- `play:error` - 视频广告播放遇到错误
- `verify:error` - 服务端验证异步请求失败
- `verify:valid` - 服务端验证异步请求成功并验证有效
- `verify:invalid` - 服务端验证异步请求成功并验证无效
- `error` - 广告物料载入失败

### 插屏广告 

```javascript
/**
 * 展示广告。
 * 
 * 广告的宽度和高度需要根据设备屏幕尺寸动态计算，如设置不当可能会错位。
 * 
 * @function BytedanceUnionAd.InterstitialAd.show
 * @param {String} slotId - 广告位 ID
 * @param {Number} width - 广告宽度
 * @param {Number} height - 广告高度
 * @return {InterstitialAd} - 返回 InterstitialAd 实例
 */
BytedanceUnionAd.InterstitialAd.show(slotId, width, height)

/**
 * 监听事件。
 * 
 * @function InterstitialAd.on
 * @param {String} eventType - 事件类型
 * @param {EventCallback | ErrorEventCallback} callback - 事件回调
 * @return {InterstitialAd} - 返回 InterstitialAd 实例
 */
InterstitialAd.on(eventType, callback)

 ```
#### 事件类型

- `show` - 广告物料载入成功
- `close` - 广告关闭
- `click` - 广告被点击
- `error` - 广告物料载入失败或渲染失败

### 横幅广告 

```javascript
/**
 * 展示广告。
 * 
 * 广告的宽度和高度需要根据设备屏幕尺寸动态计算，如设置不当可能会错位。
 * 
 * @function BytedanceUnionAd.BannerAd.show
 * @param {String} slotId - 广告位 ID
 * @param {Number} width - 广告宽度
 * @param {Number} height - 广告高度
 * @param {String} align - 广告位置。可选值，`top`、`bottom`（默认）
 * @param {Number} interval - 广告轮播间隔时长。单位，秒。默认 30 秒，如设置为 0 则不轮播
 * @return {BannerAd} - 返回 BannerAd 实例
 */
BytedanceUnionAd.BannerAd.show(slotId, width, height, align, interval)

/**
 * 关闭广告。
 * 
 * @function BytedanceUnionAd.BannerAd.show
 * @param {String} slotId - 广告位 ID
 * @return {BannerAd} - 返回 BannerAd 实例
 */
BytedanceUnionAd.BannerAd.hide(slotId)

/**
 * 监听事件。
 * 
 * @function BannerAd.on
 * @param {String} eventType - 事件类型
 * @param {EventCallback | ErrorEventCallback} callback - 事件回调
 * @return {BannerAd} - 返回 BannerAd 实例
 */
BannerAd.on(eventType, callback)

 ```
#### 事件类型

- `show` - 广告物料载入成功。`show' 方法没有此事件
- `close` - 广告关闭
- `click` - 广告被点击。`show' 方法没有此事件
- `error` - 广告物料载入失败或渲染失败。`show' 方法没有此事件



