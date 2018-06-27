package noobchain;
import java.security.*;
import java.util.ArrayList;

public class Transaction
{
    public String transactionId;  // This is also the has of the transaction.
    public PublicKey sender;      // Sender's address key.
    public PublicKey recipient;   // Receiver's address key.
    public float value;
    public byte[] signature;      // Prevents anyone else from spending funds.

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public static int sequence = 0; // a rough count of how many transactions have been generated.

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs)
    {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;

    }

    // Calculates the transaction hash / ID
    private String calculateHash()
    {
        sequence++;
        return StringUtil.applySha256(
                        StringUtil.getStringFromKey(sender) +
                               StringUtil.getStringFromKey(recipient) +
                               Float.toString(value) + sequence
                               );

    }

    //Signs any data we don't want tampered with.
    public void generateSignature(PrivateKey privateKey)
    {
        String data = StringUtil.getStringFromKey(sender)
                        + StringUtil.getStringFromKey(recipient)
                        + Float.toString(value);

        signature = StringUtil.applyECDSASig(privateKey, data);

    }

    //Verifies that the data we signed hasn't been tampered with.
    public boolean verifySignature()
    {
        String data = StringUtil.getStringFromKey(sender)
                        + StringUtil.getStringFromKey(recipient)
                        + Float.toString(value);

        return StringUtil.verifyECDSASig(sender, data, signature);

    }

    public boolean processTransaction()
    {
        if (verifySignature() == false)
        {
            System.out.println("#Transaction Signature failed to verify.");
            return false;
        }
        // Gather unspent transaction inputs
        for (TransactionInput i : inputs)
        {
            i.UTXO = Main.UTXOs.get(i.transactionOutputId);
        }

        // Check if transaction is valid:
        if (getInputsValue() < Main.minimumTransaction)
        {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        // Generate transaction outputs:
        float leftOver = getInputsValue() - value; // Get value of inputs then the left over change.
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId)); // Send value to recipient.
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // Send the left over "change" back.

        // Add outputs to unspent list.
        for (TransactionOutput o : outputs)
        {
            Main.UTXOs.put(o.id, o);
        }

        // Remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs)
        {
            if (i.UTXO == null) continue; // If transaction can't be found skip it.
            Main.UTXOs.remove(i.UTXO.id);
        }
        return true;

    }

    public float getInputsValue()
    {
        float total = 0;
        for (TransactionInput i : inputs)
        {
            if (i.UTXO == null) continue; // If transaction can't be found skip it.
            total += i.UTXO.value;
        }
        return total;

    }

    public float getOutputsValue()
    {
        float total = 0;
        for (TransactionOutput o : outputs)
        {
            total += o.value;
        }
        return total;

    }
}
