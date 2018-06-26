package noobchain;
import java.util.Date;

public class Blockchain
{
    public String hash; // contains the block data signature.
    public String previousHash;
    private String data; // Dummy data
    private long timeStamp;
    private int nonce;

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
                        Integer.toString(nonce) +
                        data
                        );

        return calculatedHash;
    }

    public void mineBlock(int difficulty)
    {
        // Creates a string with difficulty * "0".
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0 , difficulty).equals(target))
        {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!: " + hash);

    }
}
