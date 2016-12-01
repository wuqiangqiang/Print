package com.zrodo.demo.utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;


public class NetUtils {

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * 判断是否有网络可用
     */
    public static boolean isOpenNetwork(Context context) {
        if (context != null) {
            ConnectivityManager connManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
//			if(connManager.getActiveNetworkInfo()!=null){
//				return true;
//			}
            if (connManager == null) {
                return false;
            }

            NetworkInfo[] networkInfo = connManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     */
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断外网IP是否有效
     *
     * @param aIP
     * @return
     */
    public static boolean IsValidateIP(String aIP, int aPort, int aTimeOut) {
        boolean nIsValidate = false;
        Socket socket = null;
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(aIP, aPort);
            socket = new Socket();
            socket.setReuseAddress(true);
            socket.connect(socketAddress, aTimeOut);
            nIsValidate = true;
        } catch (Exception e) {
            nIsValidate = false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nIsValidate;
    }


    /**
     * 判断是啥网络且出现上线速度
     */
    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    // private static final int NETWORK_TYPE_MOBILE = -100;  
    private static final int NETWORK_TYPE_WIFI = -101;

    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /**
     * Unknown network class.
     */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks.
     */
    private static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks.
     */
    private static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks.
     */
    private static final int NETWORK_CLASS_4_G = 3;


    // 适配低版本手机  
    /**
     * Network type is unknown
     */
    private static final int NETWORK_TYPE_UNKNOWN = 0;
//    /** Current network is GPRS */  
//    private static final int NETWORK_TYPE_GPRS = 1;  
//    /** Current network is EDGE */  
//    private static final int NETWORK_TYPE_EDGE = 2;  
//    /** Current network is UMTS */  
//    private static final int NETWORK_TYPE_UMTS = 3;  
//    /** Current network is CDMA: Either IS95A or IS95B */  
//    private static final int NETWORK_TYPE_CDMA = 4;  
//    /** Current network is EVDO revision 0 */  
//    private static final int NETWORK_TYPE_EVDO_0 = 5;  
//    /** Current network is EVDO revision A */  
//    private static final int NETWORK_TYPE_EVDO_A = 6;  
//    /** Current network is 1xRTT */  
//    private static final int NETWORK_TYPE_1xRTT = 7;  
//    /** Current network is HSDPA */  
//    private static final int NETWORK_TYPE_HSDPA = 8;  
//    /** Current network is HSUPA */  
//    private static final int NETWORK_TYPE_HSUPA = 9;  
//    /** Current network is HSPA */  
//    private static final int NETWORK_TYPE_HSPA = 10;  
//    /** Current network is iDen */  
//    private static final int NETWORK_TYPE_IDEN = 11;  
//    /** Current network is EVDO revision B */  
//    private static final int NETWORK_TYPE_EVDO_B = 12;  
//    /** Current network is LTE */  
//    private static final int NETWORK_TYPE_LTE = 13;  
//    /** Current network is eHRPD */  
//    private static final int NETWORK_TYPE_EHRPD = 14;  
//    /** Current network is HSPA+ */  
//    private static final int NETWORK_TYPE_HSPAP = 15;  


    public static final String STR_NET_UNAVAILABLE = "无";
    public static final String STR_NET_WIFI = "Wi-Fi";
    public static final String STR_NET_2G = "2G";
    public static final String STR_NET_3G = "3G";
    public static final String STR_NET_4G = "4G";
    public static final String STR_NET_UNKNOW = "未知";

    /**
     * 获取网络类型
     *
     * @return
     */
    public static String getCurrentNetworkType(Context ctx) {
        int networkClass = getNetworkClass(ctx);
        String type = STR_NET_UNKNOW;
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = STR_NET_UNAVAILABLE;
                break;
            case NETWORK_CLASS_WIFI:
                type = STR_NET_WIFI;
                break;
            case NETWORK_CLASS_2_G:
                type = STR_NET_2G;
                break;
            case NETWORK_CLASS_3_G:
                type = STR_NET_3G;
                break;
            case NETWORK_CLASS_4_G:
                type = STR_NET_4G;
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = STR_NET_UNKNOW;
                break;
        }
        return type;
    }


    private static int getNetworkClass(Context ctx) {
        int networkType = NETWORK_TYPE_UNKNOWN;
        try {
            final NetworkInfo network = ((ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
                    && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(
                            Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getNetworkClassByType(networkType);

    }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case TelephonyManager.NETWORK_TYPE_GPRS:    // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:    // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:    // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_1xRTT:    // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_IDEN:    // ~25 kbps
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:    // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:  // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:  // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:    // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:   // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:    // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:  // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:    // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:    // ~ 10-20 Mbps
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:    // ~ 10+ Mbps
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }


    //===========================关于wifi的名字和ip及其如何设置静态ip等功能=======================

    /**
     * 获得有效wifi的名字
     * "360-wifi" --> 360-wifi
     */
    public static String getWifiName(WifiManager wifiMgr) {
//    	WifiManager wifiMgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID().replace("\"", "") : null;
        return wifiId;
    }

    /**
     * 获得有效wifi的ip
     */
    public static String getWifiIp(WifiManager wifiMgr) {
//    	WifiManager wifiMgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();//连接上的info
        if (info != null) {
            int ipAddress = info.getIpAddress();
            if (ipAddress != 0) {
                return intIp2StrIp(ipAddress);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * 转进制用的
     * ip->string
     */
    public static String intIp2StrIp(int ip) {
        String ipString = ((ip & 0xff) + "." + (ip >> 8 & 0xff) + "."
                + (ip >> 16 & 0xff) + "." + (ip >> 24 & 0xff));
        return ipString;
    }


    /***
     * Convert a IPv4 address from an InetAddress to an integer
     *
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr)
            throws IllegalArgumentException {
        byte[] addr = inetAddr.getAddress();
        if (addr.length != 4) {
            throw new IllegalArgumentException("Not an IPv4 address");
        }
        return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16) |
                ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
    }

    public static boolean editStaticWifiConfig(WifiManager wifiMgr, WifiConfiguration historyWifiConfig, String ssid, String psw,
                                               String ip, String gateway, int prefixLength, String dns) throws Exception {
//        WifiConfiguration historyWifiConfig = getHistoryWifiConfig(wifiMgr,ssid);  

        if (historyWifiConfig == null) {
//            historyWifiConfig = createComWifiConfig(sr.SSID,pwd);  
//            int netId = mWifiManager.addNetwork(historyWifiConfig);  
//            mWifiManager.enableNetwork(netId, true);  
            return false;
        }

        setIpAssignment("STATIC", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting  
        setIpAddress(InetAddress.getByName(ip), prefixLength, historyWifiConfig);
        setGateway(InetAddress.getByName(gateway), historyWifiConfig);
        setDNS(InetAddress.getByName(dns), historyWifiConfig);
        //密码
        historyWifiConfig.preSharedKey = "\"" + psw + "\"";

//        int networkId = wifiMgr.updateNetwork(historyWifiConfig);
//        Log.d("net", "Edit Static Ip ... Then networkId = "+networkId);

        wifiMgr.removeNetwork(historyWifiConfig.networkId);
        int netId = wifiMgr.addNetwork(historyWifiConfig);
        wifiMgr.enableNetwork(netId, true);
//        int networkId = wifiMgr.updateNetwork(historyWifiConfig);
//        Log.d("net", "Edit Static Ip ... Then networkId = "+networkId);
        wifiMgr.startScan();

        return (netId == -1 ? false : true);  //apply the setting 
    }

    public static boolean editDhcpWifiConfig(WifiManager wifiMgr, WifiConfiguration historyWifiConfig, String ssid, String psw) throws Exception {
//    	WifiConfiguration historyWifiConfig = getHistoryWifiConfig(wifiMgr,ssid);  

        if (historyWifiConfig == null) {
//            historyWifiConfig = createComWifiConfig(sr.SSID,pwd);  
//            int netId = mWifiManager.addNetwork(historyWifiConfig);  
//            mWifiManager.enableNetwork(netId, true);  
            return false;
        }

        setIpAssignment("DHCP", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting  
//        int networkId = wifiMgr.updateNetwork(historyWifiConfig);
//        Log.d("net", "Edit DHCP Ip ... Then networkId = "+networkId);
        //密码
        historyWifiConfig.preSharedKey = "\"" + psw + "\"";

        wifiMgr.removeNetwork(historyWifiConfig.networkId);
        int netId = wifiMgr.addNetwork(historyWifiConfig);
        wifiMgr.enableNetwork(netId, true);
//        int networkId = wifiMgr.updateNetwork(historyWifiConfig);
//        Log.d("net", "Edit Static Ip ... Then networkId = "+networkId);
        wifiMgr.startScan();

        return (netId == -1 ? false : true);  //apply the setting 
    }

    /**
     * 查找已经设置好的Wifi
     *
     * @param ssid
     * @return
     */
    public static WifiConfiguration getHistoryWifiConfig(WifiManager wifiMgr, String ssid) {
        List<WifiConfiguration> localList = wifiMgr.getConfiguredNetworks();
        for (WifiConfiguration wc : localList) {
            if (("\"" + ssid + "\"").equals(wc.SSID)) {
                Log.d("net", "SSID:" + wc.SSID);
                return wc;
            }  //ScanResult.ssid
//            if((ssid).equals(wc.SSID)){  
//                return wc;  
//            } 
            wifiMgr.disableNetwork(wc.networkId);
        }
        return null;
    }


    public static void setIpAssignment(String assign, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
        ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
        Object routeInfo = routeInfoConstructor.newInstance(gateway);
        ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // or add a new dns address , here I just want to replace DNS1  
        mDnses.add(dns);
    }


    public static String getNetworkPrefixLength(WifiConfiguration wifiConf) {
        String address = "";
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");
            if (linkProperties == null)
                return null;

            if (linkProperties != null) {
                ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
                if (mLinkAddresses != null && mLinkAddresses.size() > 0) {
                    Object linkAddressObj = mLinkAddresses.get(0);
                    address = linkAddressObj.getClass().getMethod("getNetworkPrefixLength", new Class[]{}).invoke(linkAddressObj, new Object[]{}) + "";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static InetAddress getIpAddress(WifiConfiguration wifiConf) {
        InetAddress address = null;
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");
            if (linkProperties == null)
                return null;

            if (linkProperties != null) {
                ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
                if (mLinkAddresses != null && mLinkAddresses.size() > 0) {
                    Object linkAddressObj = mLinkAddresses.get(0);
                    address = (InetAddress) linkAddressObj.getClass().getMethod("getAddress", new Class[]{}).invoke(linkAddressObj, null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static InetAddress getGateway(WifiConfiguration wifiConf) {
        InetAddress address = null;
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");

            if (linkProperties != null) {
                ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
                if (mRoutes != null && mRoutes.size() > 0) {
                    Object linkAddressObj = mRoutes.get(0);
                    address = (InetAddress) linkAddressObj.getClass().getMethod("getGateway", new Class[]{}).invoke(linkAddressObj, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static InetAddress getDNS(WifiConfiguration wifiConf) {
        InetAddress address = null;
        try {
            Object linkProperties = getField(wifiConf, "linkProperties");

            if (linkProperties != null) {
                ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
                if (mDnses != null && mDnses.size() > 0) {
                    address = (InetAddress) mDnses.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return address;
    }

    public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setEnumField(Object obj, String value, String name) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }
}
