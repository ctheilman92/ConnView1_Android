package com.example.milkymac.connview_main;

/**
 * Created by milkymac on 3/7/17.
 */

public class devices {

    private String devName;
    private String ip;
//    private String mac;


    public devices(int num) {
        //dummycontent
        devName = "LOCAL DEVICE"+num;
        ip = "127.0.0.1";
//        mac = "00:00:00:00:00:00";
    }

    //region SETS
    public void setDevName(String name) { devName = name; }
    public void setIp(String getip) { getip = ip; }
//    public void setMac(String getmac) { getmac = mac; }
    //endregion

    //region GETS
    public String getDevName() { return devName; }
    public String getIp() { return ip; }
//    public String getMac() { return mac; }

}
