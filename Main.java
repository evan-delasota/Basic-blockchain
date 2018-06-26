package noobchain;
import java.util.ArrayList;
import java.util.Base64;
import java.security.Security;
import com.google.gson.GsonBuilder;

public class Main
{
    public static ArrayList<Blockchain> blockchain = new ArrayList<Blockchain>();
    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args)
    {
        // Using Bouncy Castle as a security provider.
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Creating the new wallets
        walletA = new Wallet();
        walletB = new Wallet();

        // Testing public/private keys
        System.out.println("Private and public keys: ");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

        // Creating a test transaction using the two wallets
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        // Verifying if the signature is working using the public key.
        System.out.println("Is signature verified? ");
        System.out.println(transaction.verifySignature());


        blockchain.add(new Blockchain("Hi i am the first block,", "0"));
        System.out.println("Trying to mine block 1...");
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Blockchain("Hey i am the second block,", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to mine block 2...");
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Blockchain("Suh dude i am the third block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Trying to mine block 3...");
        blockchain.get(2).mineBlock(difficulty);

        System.out.println("\nBlockchain is Valid: " + isChainValid());

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid()
    {
        Blockchain currentBlock;
        Blockchain previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // Loops through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++)
        {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            // Compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash()))
            {
                System.out.println("Current Hashes not equal");
                return false;
            }
            // Compare previous hash and registered previous hash.
            if (!previousBlock.hash.equals(currentBlock.previousHash))
            {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            // Check if hash is solved.
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget))
            {
                System.out.println("This block hasn't been mined.");
                return false;
            }
        }
        return true;

    }
}
