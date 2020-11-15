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

module.exports = {
  SplashAd: SplashAd,
  RewardedVideoAd: RewardedVideoAd,
};
