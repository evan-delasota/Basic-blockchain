package noobchain;

import java.util.Date;

public class Blockchain
{
    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp;

    public Blockchain(String data, String previousHash)
    {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
    }

}
