import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

class ClientHandler implements Runnable {
    Scanner scn = new Scanner(System.in);
    Socket sc;
    final DataInputStream din;
    final DataOutputStream dout;
    boolean isOnline;
    private String name;

    ClientHandler(Socket s, String name, DataOutputStream dout, DataInputStream din) {
        this.sc = s;
        this.name = name;
        this.dout = dout;
        this.din = din;
        this.isOnline = true;
        System.out.println(this.name + "is online");
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        String received;
        while (true) {
            try {
                received = (String) din.readUTF();
                System.out.println(received);

                if (received.equalsIgnoreCase("logout")) {
                    this.isOnline = false;
                    this.sc.close();
                    break;
                }

                StringTokenizer token = new StringTokenizer(received, "#");
                String MsgToSend = token.nextToken();
                String receiver = token.nextToken();

                for (ClientHandler ch : Server.user) {
                    if (ch.name.equalsIgnoreCase(receiver) && ch.isOnline == true) {
                        ch.dout.writeUTF(this.name + " : " + MsgToSend);
                        break;
                    }
                }
            } catch (Exception ex) {
                this.isOnline = false;
                try {
                    this.sc.close();
                    break;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ex.printStackTrace();
            }
        }

        try {
            this.din.close();
            this.dout.close();
            System.out.println(this.name + " is offline now..");
        } catch (Exception ec) {
            ec.printStackTrace();
        }
    }

}

public class Server {
    public static ArrayList<ClientHandler> user = new ArrayList<ClientHandler>();
    private static int i = 0;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(5000);
        Socket s;

        while (true) {
            s = server.accept();
            System.out.println("New Client " + s);

            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating Client Handle ");
            ClientHandler client = new ClientHandler(s, "client-" + i, dout, din);
            Thread t = new Thread(client);

            System.out.println("Adding Client To Active Client List");

            user.add(client);

            t.start();

            i++;

        }
    }
}
