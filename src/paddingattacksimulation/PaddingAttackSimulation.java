/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paddingattacksimulation;

/**
 *
 * @author ahmed
 */
public class PaddingAttackSimulation {
    
   
    private static class Sender {
            private byte[] secretKey;
            private String secretMessage = "Top secret!";
            public Sender(byte[] secretKey) {
                    this.secretKey = secretKey;
            }
            // This will return both iv and ciphertext
            public byte[] encrypt() {
                    return AESDemo.encrypt(secretKey, secretMessage);
            }
	}



	private static class Receiver {
            private byte[] secretKey;
            public Receiver(byte[] secretKey) {
                    this.secretKey = secretKey;
            }
            // Padding Oracle (Notice the return type)
            public boolean isDecryptionSuccessful(byte[] ciphertext) {
                    //System.out.println(new String(AESDemo.decrypt(secretKey, ciphertext)));
                    return AESDemo.decrypt(secretKey, ciphertext) != null;
            }
	}



	public static class Adversary {
            // This is where you are going to develop the attack
            // Assume you cannot access the key. 
            // You shall not add any methods to the Receiver class.
            // You only have access to the receiver's "isDecryptionSuccessful" only. 
            public String extractSecretMessage(Receiver receiver, byte[] ciphertext) {
                    byte[] iv = AESDemo.extractIV(ciphertext);
                    byte[] ciphertextBlocks = AESDemo.extractCiphertextBlocks(ciphertext);
                    System.out.println("Encrypted message: \nIV: " + AESDemo.toHex(iv) + "\nCiphertext Blocks: "+ AESDemo.toHex(ciphertextBlocks));
                    boolean result = receiver.isDecryptionSuccessful(AESDemo.prepareCiphertext(iv, ciphertextBlocks));
                    System.out.println(result); // This is true initially, as the ciphertext was not altered in any way.
                    //
                    String[] paddings = {"01", "0202", "030303", "04040404", "0505050505", "060606060606", "07070707070707", "0808080808080808", "090909090909090909",
    			"0a0a0a0a0a0a0a0a0a0a", "0b0b0b0b0b0b0b0b0b0b0b", "0c0c0c0c0c0c0c0c0c0c0c0c", "0d0d0d0d0d0d0d0d0d0d0d0d0d",
    			"0e0e0e0e0e0e0e0e0e0e0e0e0e0e", "0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f"};
                    int i = 0;
                    byte [] falseinv = new byte[1];
                    falseinv = hexStringToByteArray("fc");
                    byte [] iv1 = new byte[16];
                    System.arraycopy(iv, 0, iv1, 0, iv.length);
                    while(i < 16 && result != false){
                        System.arraycopy(falseinv,0,iv1,i,1);
                        System.out.println("Encrypted message: \nIV: " + AESDemo.toHex(iv1) + "\nCiphertext Blocks: "+ AESDemo.toHex(ciphertextBlocks));
                        result = receiver.isDecryptionSuccessful(AESDemo.prepareCiphertext(iv1, ciphertextBlocks));
                        System.out.println(result);
                        i++;
                    }
                    int padding = 15 - i;
                    System.out.println("Padding: "+ paddings[padding]);
                    char[] message = new char[16];
                    byte[] fcmessage = new byte[16];
                    
                    // TODO: WRITE THE ATTACK HERE. 

                    return null;
            }
            
            public static byte[] hexStringToByteArray(String s) {
                int len = s.length();
                byte[] data = new byte[len / 2];
                for (int i = 0; i < len; i += 2) {
                    data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
                }
                return data;
            }

	}

    
    public static void main(String[] args) {
        byte[] secretKey = AESDemo.keyGen();
        Sender sender = new Sender(secretKey);
        Receiver receiver = new Receiver(secretKey);
        // The adversary does not have the key
        Adversary adversary = new Adversary();
        // Now, let's get some valid encryption from the sender
        byte[] ciphertext = sender.encrypt();
        // The adversary  got the encrypted message from the network.
        // The adversary's goal is to extract the message without knowing the key.
        String message = adversary.extractSecretMessage(receiver, ciphertext);
        System.out.println("Extracted message = " + message);
    }
    
}
