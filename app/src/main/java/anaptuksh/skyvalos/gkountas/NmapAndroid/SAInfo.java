package anaptuksh.skyvalos.gkountas.NmapAndroid;

/**
 * POJO for jackson converting received SA JSON data to this structure
 */
public class SAInfo {
    private String hash,device,ip,mac,os,nmap,active;

    public String getHash(){
        return this.hash;
    }
    public String getDevice(){
        return this.device;
    }
    public String getIP(){
        return this.ip;
    }
    public String getMAC(){
        return this.mac;
    }
    public String getOS(){
        return this.os;
    }
    public String getNmap(){
        return this.nmap;
    }
    public String getActive(){
        return this.active;
    }
}

