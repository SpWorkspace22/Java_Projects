import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        try {
            Socket sc = new Socket("127.0.0.1", 5000);
            DataOutputStream dout = new DataOutputStream(sc.getOutputStream());
            DataInputStream din = new DataInputStream(sc.getInputStream());

            Thread sendMessage = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (true) {
                        String message = scn.nextLine();

                        try {
                            dout.writeUTF(message);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            Thread readMessage = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (true) {
                        try {
                            // read the message sent to this client
                            String msg = din.readUTF();
                            System.out.println(msg);
                        } catch (IOException e) {
                            System.out.println("Server Down... ");
                            break;
                            // e.printStackTrace();
                        }
                    }
                }
            });

            sendMessage.start();
            readMessage.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
