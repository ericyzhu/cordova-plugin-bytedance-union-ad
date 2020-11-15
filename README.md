# 穿山甲广告 Cordova 插件

## 平台

- iOS
- Android（暂未实现）

## 安装

```shell script
cordova plugin add cordova-plugin-bytedance-union-ad
```

## 配置 App ID

在 `config.xml` 中配置以下内容。

```xml
<platform name="ios">
    <config-file target="*-Info.plist" parent="CDVBytedanceUnionAdAppId">
        <string>YOUR_BUAD_APP_ID</string>
    </config-file>
</platform>
```

## 使用

当成功安装和配置完成后，您可以通过全局变量 `BytedanceUnionAd` 来调用支持的广告类型。目前插件仅实现了开屏广告和激励视频广告。

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
 * @return {RewardedVideoAd} - 返回 SplashAd 实例
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



