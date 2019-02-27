package anaptuksh.skyvalos.gkountas.NmapAndroid;

/**
 * POJO for Jackson mapping results from server to this structure.
 */
public class SAResult {
    private String hash,id,time,hostname,tasks,results,periodic;

    public String gethash(){
        return this.hash;
    }
    public String getid(){
        return this.id;
    }
    public String gettime(){
        return this.time;
    }
    public String gethostname(){
        return this.hostname;
    }
    public String gettasks(){
        return this.tasks;
    }
    public String getresults(){
        return this.results;
    }
    public String getperiodic(){
        return this.periodic;
    }
}
