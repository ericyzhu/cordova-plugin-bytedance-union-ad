var exec = require('cordova/exec');
var channel = require('cordova/channel');

var BaseAd = function() {
  this.channels = {};
};

BaseAd.prototype = {
  _eventHandler: function(event) {
    if (event && (event.type in this.channels)) {
      this.channels[event.type].fire(event);
    }
  },
  on: function(type, func) {
    if (!(type in this.channels)) {
      this.channels[type] = channel.create(type);
    }
    this.channels[type].subscribe(func);
    return this;
  },
};

var SplashAd = function() { BaseAd.call(this); };
SplashAd.prototype = new BaseAd();

SplashAd.show = function(slotId) {
  var options = {
    slotId: slotId,
  };
  var ad = new SplashAd();
  var cb = function(eventname) {
    ad._eventHandler(eventname);
  };
  exec(cb, cb, 'BytedanceUnionAd', 'showSplashAd', [options]);
  return ad;
};

var RewardedVideoAd = function() { BaseAd.call(this); };
RewardedVideoAd.prototype = new BaseAd();

RewardedVideoAd.show = function(slotId, userId) {
  var options = {
    slotId: slotId,
    userId: userId || '',
  };
  var ad = new RewardedVideoAd();
  var cb = function(event) {
    ad._eventHandler(event);
  };
  exec(cb, cb, 'BytedanceUnionAd', 'showRewardedVideoAd', [options]);
  return ad;
};

var InterstitialAd = function() { BaseAd.call(this); };
InterstitialAd.prototype = new BaseAd();

InterstitialAd.show = function(slotId, width, height) {
  var options = {
    slotId: slotId,
    width: width,
    height: height,
  };
  var ad = new InterstitialAd();
  var cb = function(event) {
    ad._eventHandler(event);
  };
  exec(cb, cb, 'BytedanceUnionAd', 'showInterstitialAd', [options]);
  return ad;
};

var BannerAd = function() { BaseAd.call(this); };
BannerAd.prototype = new BaseAd();

BannerAd.show = function(slotId, width, height, align, interval) {
  var options = {
    slotId: slotId,
    width: width,
    height: height,
    interval: interval || 30,
    align: align || 'bottom',
  };
  var ad = new BannerAd();
  var cb = function(event) {
    ad._eventHandler(event);
  };
  exec(cb, cb, 'BytedanceUnionAd', 'showBannerAd', [options]);
  return ad;
};

BannerAd.hide = function(slotId) {
  var options = {
    slotId: slotId,
  };
  var ad = new BannerAd();
  var cb = function(event) {
    ad._eventHandler(event);
  };
  exec(cb, cb, 'BytedanceUnionAd', 'hideBannerAd', [options]);
  return ad;
};

module.exports = {
  SplashAd: SplashAd,
  RewardedVideoAd: RewardedVideoAd,
  InterstitialAd: InterstitialAd,
  BannerAd: BannerAd,
};
