package noobchain;

public class Main
{
    public static void main(String[] args)
    {
        Blockchain genesisBlock = new Blockchain("Hi i'm the first block,", "0");
        System.out.println("Hash for block 1: " + genesisBlock.hash);

        Blockchain secondBlock = new Blockchain("And i'm the second block", genesisBlock.hash);
        System.out.println("Hash for block 2: " + secondBlock.hash);

        Blockchain thirdBlock = new Blockchain("Suh dude i'm the third block", secondBlock.hash);
        System.out.println("Hash for block 3: " + thirdBlock.hash);

    }
}
