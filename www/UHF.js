cordova.define("cordova-plugin-uhf.UHF", function(require, exports, module) {
    var exec = require("cordova/exec");
  
    exports.test = function(arg0, success, error) {
      exec(success, error, "UHF", "test", [arg0]);
    };
  
    exports.getInstance = function(success, error) {
      exec(success, error, "UHF", "getInstance", []);
    };
  
    exports.getFirmware = function(id, success, error) {
      exec(success, error, "UHF", "getFirmware", [id]);
    };
  
    exports.setOutputPower = function(id, value, success, error) {
      exec(success, error, "UHF", "setOutputPower", [id, value]);
    };
  
    exports.inventoryRealTime = function(id, success, error) {
      exec(success, error, "UHF", "inventoryRealTime", [id]);
    };
  
    exports.selectEPC = function(id, epc, success, error) {
      exec(success, error, "UHF", "selectEPC", [id, epc]);
    };
  
    exports.readFrom6C = function(id, memBank, startAddress, length, accessPassword, success, error) {
      exec(success, error, "UHF", "readFrom6C", [id, memBank, startAddress, length, accessPassword]);
    };
  
    exports.writeTo6C = function(id, password, memBank, startAddress, data, success, error) {
      exec(success, error, "UHF", "writeTo6C", [id, password, memBank, startAddress, data]);
    };
  
    exports.setWorkArea = function(id, area, success, error) {
      exec(success, error, "UHF", "setWorkArea", [id, area]);
    };
  
    exports.getFrequency = function(id, startFrequency, freqSpace, freqQuality, success, error) {
      exec(success, error, "UHF", "getFrequency", [id, startFrequency, freqSpace, freqQuality]);
    };
  
    exports.setFrequency = function(id, startFrequency, freqSpace, freqQuality, success, error) {
      exec(success, error, "UHF", "setFrequency", [id, startFrequency, freqSpace, freqQuality]);
    };
  
    exports.close = function(id, success, error) {
      exec(success, error, "UHF", "close", [id]);
    };
    
  });
  