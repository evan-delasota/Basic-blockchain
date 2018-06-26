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
}
