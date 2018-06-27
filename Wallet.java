package noobchain;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet
{
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); // Only UTXOs owned by this wallet


    public Wallet()
    {
        generateKeyPair();
    }

    public void generateKeyPair()
    {
        try
        {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key gen and generate a KeyPair
            keyGen.initialize(ecSpec, random); // 256 bytes provides a satisfiable security level.
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // Returns balance / Stores the UTXO's owned by this wallet in this.UTXOs
    public float getBalance()
    {
        float total = 0;

        for (Map.Entry<String, TransactionOutput> item: Main.UTXOs.entrySet())
        {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) // If the outputs belongs to me
            {
                UTXOs.put(UTXO.id, UTXO); // Add it to the list of unspent transactions.
                total += UTXO.value;
            }
        }
        return total;

    }

    // Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey _recipient, float value)
    {
        if (getBalance() < value)
        {
            System.out.println("#Not enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        // Create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet())
        {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input: inputs)
        {
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;

    }


}
