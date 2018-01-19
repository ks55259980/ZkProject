package com.wemarklinks.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ZKConfig {
    {
        Properties prop = new Properties(); 
        InputStream in = ZKConfig.class.getResourceAsStream("/zksdk.properties"); 
        try { 
            prop.load(in); 
            ip = prop.getProperty("ip").trim().split(","); 
            machineNumber = prop.getProperty("machineNumber").trim().split(","); 
            port = Integer.valueOf(prop.getProperty("port").trim());
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    }
    private String[] ip;
    private String[] machineNumber;
    private int port;
    
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String[] getIp() {
        return ip;
    }
    public void setIp(String[] ip) {
        this.ip = ip;
    }
    public String[] getMachineNumber() {
        return machineNumber;
    }
    public void setMachineNumber(String[] machineNumber) {
        this.machineNumber = machineNumber;
    }
    
}
