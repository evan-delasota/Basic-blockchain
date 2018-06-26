package noobchain;

import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class Main
{
    public static ArrayList<Blockchain> blockchain = new ArrayList<Blockchain>();

    public static void main(String[] args)
    {
        Blockchain genesisBlock = new Blockchain("Hi i'm the first block,", "0");
        System.out.println("Hash for block 1: " + genesisBlock.hash);

        Blockchain secondBlock = new Blockchain("And i'm the second block", genesisBlock.hash);
        System.out.println("Hash for block 2: " + secondBlock.hash);

        Blockchain thirdBlock = new Blockchain("Suh dude i'm the third block", secondBlock.hash);
        System.out.println("Hash for block 3: " + thirdBlock.hash);

    }

    public static Boolean isChainValid()
    {
        Blockchain currentBlock;
        Blockchain previousBlock;

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
            if (!previousBlock.hash.equals(currentBlock.previousHash))
            {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }

        return true;
    }
}
