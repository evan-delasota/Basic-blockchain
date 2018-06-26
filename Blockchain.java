package noobchain;

import java.util.Date;

public class Blockchain
{
    public String hash; // contains the block data signature.
    public String previousHash;
    private String data; // Dummy data
    private long timeStamp;

    public Blockchain(String data, String previousHash)
    {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash()
    {
        String calculatedHash = StringUtil.applySha256(
                              previousHash +
                                    Long.toString(timeStamp) +
                                    data
                                    );
        return calculatedHash;
    }


}
