package anaptuksh.skyvalos.gkountas.NmapAndroid;

/**
 * POJO for receiving jobs and using jackson to convert to this class
 */
public class jobb {
    private String hash,id,job;

    public String getjob(){
        return this.job;
    }
    public String gethash(){
        return this.hash;
    }
    public String getid(){
        return this.id;
    }
}
