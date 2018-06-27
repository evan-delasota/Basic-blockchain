package noobchain;
import java.util.ArrayList;
import java.util.Base64;
import java.security.Security;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class Main
{
    public static ArrayList<Blockchain> blockchain = new ArrayList<Blockchain>();
    // Unspent transactions list.
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args)
    {
        // Using Bouncy Castle as a security provider.
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Creating the new wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        // Create genesis transaction, which sends 100 noobcoin to walletA:
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput(
                            genesisTransaction.recipient
                            , genesisTransaction.value
                            , genesisTransaction.transactionId
                            )); // Manually adding a transactions output.
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis block... ");
        Blockchain genesis = new Blockchain("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //Testing:
        Blockchain block1 = new Blockchain(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());

        System.out.println("\nWalletA is attempting to send funds (40) to walletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Blockchain block2 = new Blockchain(block1.hash);
        System.out.println("\nWalletA is attempting to send more funds (1000) than it has to walletB...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Blockchain block3 = new Blockchain(block2.hash);
        System.out.println("\nWalletA is attempting to send funds (20) to walletB...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();

    }

    public static Boolean isChainValid()
    {
        Blockchain currentBlock;
        Blockchain previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        // A temp list of working transactions at a given block state.
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

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

            // Loop through blockchain transactions:
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++)
            {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature())
                {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid.");
                    return false;
                }

                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue())
                {
                    System.out.print("#Inputs are not equal to outputs on transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input: currentTransaction.inputs)
                {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null)
                    {
                        System.out.println("#Referenced input on Transaction(" + t + ") is missing.");
                        return false;
                    }

                    if (input.UTXO.value != tempOutput.value)
                    {
                        System.out.println("#Referenced input Transaction(" + t + ") value is invalid.");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output: currentTransaction.outputs)
                {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).recipient != currentTransaction.recipient)
                {
                    System.out.println("#Transaction(" + t + ") output recipient is not who it should be.");
                    return false;
                }

                if (currentTransaction.outputs.get(1).recipient != currentTransaction.sender)
                {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid.");
        return true;

    }

    public static void addBlock(Blockchain newBlock)
    {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

}
