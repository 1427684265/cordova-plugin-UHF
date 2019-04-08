package cordova.plugin.uhf;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import android.content.Context;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;
import com.android.hdhe.uhf.reader.UhfReader;

/**
 * This class echoes a string called from JavaScript.
 */
public class UHF extends CordovaPlugin {
    private List<UhfReader> managerList = new ArrayList();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("test")) {
            String message = args.getString(0);
            this.test(message, callbackContext);
            return true;
        }
        if (action.equals("getInstance")) {
            this.getInstance(callbackContext);
            return true;
        }
        if (action.equals("getFirmware")) {
            int id = args.getInt(0);
            this.getFirmware(id, callbackContext);
            return true;
        }
        if (action.equals("setOutputPower")) {
            int id = args.getInt(0);
            int value = args.getInt(1);
            this.setOutputPower(id, value, callbackContext);
            return true;
        }
        if (action.equals("inventoryRealTime")) {
            int id = args.getInt(0);
            this.inventoryRealTime(id, callbackContext);
            return true;
        }
        if (action.equals("selectEPC")) {
            int id = args.getInt(0);
            String ecp = args.getString(1);
            this.selectEPC(id, ecp, callbackContext);
            return true;
        }
        if (action.equals("readFrom6C")) {
            int id = args.getInt(0);
            String memBank = args.getString(1);
            int startAddress = args.getInt(2);
            int length = args.getInt(3);
            String accessPassword = args.getString(4);
            this.readFrom6C(id, memBank, startAddress, length, accessPassword, callbackContext);
            return true;
        }
        if (action.equals("writeTo6C")) {
            int id = args.getInt(0);
            String password = args.getString(1);
            int memBank = args.getInt(2);
            int startAddress = args.getInt(3);
            String data = args.getString(4);
            this.writeTo6C(id, password, memBank, startAddress, data, callbackContext);
            return true;
        }
        if (action.equals("setWorkArea")) {
            int id = args.getInt(0);
            int area = args.getInt(1);
            this.setWorkArea(id, area, callbackContext);
            return true;
        }
        if (action.equals("getFrequency")) {
            int id = args.getInt(0);
            this.getFrequency(id, callbackContext);
            return true;
        }
        if (action.equals("setFrequency")) {
            int id = args.getInt(0);
            int startFrequency = args.getInt(1);
            int freqSpace = args.getInt(2);
            int freqQuality = args.getInt(3);
            this.setFrequency(id, startFrequency, freqSpace, freqQuality, callbackContext);
            return true;
        }
        if (action.equals("close")) {
            int id = args.getInt(0);
            this.close(id);
            return true;
        }
        return false;
    }
    
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Context context=this.cordova.getActivity().getApplicationContext();
        Util.initSoundPool(context);
    }

    private void test(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void getInstance(CallbackContext callbackContext) {
        try{
            UhfReader manager = UhfReader.getInstance();
            if (manager !=  null) {
                managerList.add(manager);
                int index = managerList.indexOf(manager);
                callbackContext.success(index);
            } else {
                callbackContext.error("error： 无法获取设备");
            }
        }catch(Exception e){
            callbackContext.error("error： 无法获取设备");
        }
    }

    private void getFirmware(int id, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        byte[] data = manager.getFirmware();
        if (data != null && data.length > 1) {
            String version = Tools.Bytes2HexString(data, data.length);
            callbackContext.success(version);
        } else {
            callbackContext.error("error: 无法获取版本号");
        }
    }

    private void setOutputPower(int id, int value, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        Boolean success =  manager.setOutputPower(value);
        if (success) {
            callbackContext.success("success");
        } else {
            callbackContext.error("failed");
        }
    }

    private void inventoryRealTime(int id, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        try{
            List<byte[]> epcList = manager.inventoryRealTime();
            JSONArray epcArray = new JSONArray();
            JSONObject data = new JSONObject();

            if (epcList != null) {
                for(byte[] item : epcList){
                    String epc = Tools.Bytes2HexString(item, item.length);
                    epcArray.put(epc);
                }
                data.put("message", "扫描成功");
                data.put("list", epcArray);
                Util.play(1,0);
                callbackContext.success(data);
            } else {
                callbackContext.error("error: failed");
            }
        }catch(Exception e){
            callbackContext.error("error： 无法获取设备");
        }
    }

    private void  selectEPC (int id, String ecp, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        try{
            byte[] ecpByte = Tools.HexString2Bytes(ecp);
            manager.selectEPC(ecpByte);
            callbackContext.success();
        }catch(Exception e){
            callbackContext.error("error： 无法选择标签");
        }
    }

    private void readFrom6C(int id, String memBankString, int startAddress, int length, String accessPassword, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        int memBank = UhfReader.MEMBANK_RESEVER;

        if (memBankString.equalsIgnoreCase("RESEVER")) {
            memBank = UhfReader.MEMBANK_RESEVER ;
        }
        if (memBankString.equalsIgnoreCase("EPC")) {
            memBank = UhfReader.MEMBANK_EPC ;
        }
        if (memBankString.equalsIgnoreCase("TID")) {
            memBank = UhfReader.MEMBANK_TID ;
        }
        if (memBankString.equalsIgnoreCase("USER")) {
            memBank = UhfReader.MEMBANK_USER ;
        }

        byte[] accessPasswordByte = Tools.HexString2Bytes(accessPassword);
        byte[] tagByte = manager.readFrom6C(memBank, startAddress, length, accessPasswordByte);

        if(tagByte != null && tagByte.length > 1) {
            String tag = Tools.Bytes2HexString(tagByte, tagByte.length);
            callbackContext.success(tag);
        } else {
            callbackContext.error("failed");
            if (tagByte != null) {
                callbackContext.error("failed" + (tagByte[0] & 0xff));
                return;
            }
            callbackContext.error("failed: null");
        }
    }

    private void writeTo6C(int id, String password, int memBank, int startAddress, String data, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        byte[] passwordByte = Tools.HexString2Bytes(password);
        byte[] dataByte = Tools.HexString2Bytes(data);
        int dataLen = dataByte.length;
        boolean success = manager.writeTo6C(passwordByte, memBank, startAddress, dataLen, dataByte);
        if (success) {
            callbackContext.success("success");
        } else {
            callbackContext.error("failed");
        }
    }

    private void setWorkArea(int id, int area, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        int code = manager.setWorkArea(area);
        if (code == 0) {
            callbackContext.success("success");
        } else {
            callbackContext.error(code);
        }
    }

    private void getFrequency(int id, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        int value = manager.getFrequency();
        callbackContext.success(value);
    }

    private void setFrequency(int id, int startFrequency, int freqSpace, int freqQuality, CallbackContext callbackContext) {
        UhfReader manager = managerList.get(id);
        int code = manager.setFrequency(startFrequency, freqSpace, freqQuality);
        if (code == 0) {
            callbackContext.success("success");
        } else {
            callbackContext.error(code);
        }
    }

    private void close(int id) {
        UhfReader manager = managerList.get(id);
        manager.close();
        managerList.remove(id);
    }
}
