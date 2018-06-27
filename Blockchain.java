package noobchain;
import java.util.Date;
import java.util.ArrayList;

public class Blockchain
{
    public String hash; // contains the block data signature.
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private long timeStamp;
    private int nonce;

    public Blockchain(String previousHash)
    {
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
                        merkleRoot
                        );

        return calculatedHash;
    }

    public void mineBlock(int difficulty)
    {
        merkleRoot = StringUtil.getMerkelRoot(transactions);
        // Creates a string with difficulty * "0".
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0 , difficulty).equals(target))
        {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!: " + hash);

    }

    // Add transactions to this block
    public boolean addTransaction(Transaction transaction)
    {
        // Process transaction and check if it's valid - unless block is genesis block then ignore.
        if (transaction == null) return false;
        if ((previousHash != "0"))
        {
            if ((transaction.processTransaction() != true))
            {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to Block.");
        return true;

    }
}
