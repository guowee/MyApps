package com.example.wifireader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class WifiUtils {

    public static List<WifiConfig> readWifiConfig() {
        List<WifiConfig> wifiInfos = new ArrayList<WifiConfig>();

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;

        StringBuffer wifiConfs = new StringBuffer();

        try {
            
            /**
             * 执行该命令的前提是手机已经被root;否则会抛异常
             */
            Log.i("TAG", ">>>>>>>>>>>>>>>>>>>exec 'su'<<<<<<<<<<<<<<<<<<<<");
            process = Runtime.getRuntime().exec("su");

            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());

            dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");
            Log.i("TAG", ">>>>>>>>>>>>>>>>>>>exec 'cat'<<<<<<<<<<<<<<<<<<<<");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();

            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConfs.append(line);
            }

            Log.i("TAG", "WifiConf:" +wifiConfs.toString());
            
            
            Log.i("TAG", ">>>>>>>>>>>>>>>>>>>Read config file success !<<<<<<<<<<<<<<<<<<<<");

            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", ">>>>>>>>>>>>>>>>>>>throw exception !<<<<<<<<<<<<<<<<<<<<");

        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConfs.toString());
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find()) {
                WifiConfig wifiConf = new WifiConfig();
                wifiConf.setSsid(ssidMatcher.group(1));
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (pskMatcher.find()) {
                    wifiConf.setPassword(pskMatcher.group(1));
                } else {
                    wifiConf.setPassword("no password");
                }
                wifiInfos.add(wifiConf);
            }

        }

        Log.i("TAG", ">>>>>>>>>>>>>>>>>>>WIFI INFOS :<<<<<<<<<<<<<<<<<<<<" + wifiInfos.size());

        return wifiInfos;
    }

}
