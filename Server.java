import java.io.*;
import java.util.*;
import java.net.*;

public class Server {
    
    static Vector<ClientHandler> ar = new Vector<>();

    static int i = 0;

    public static void main(final String[] args) throws IOException {
        final Scanner scn = new Scanner(System.in);

        
        final Thread setServer = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket s;
                String client = "";
                
                try{
                    final ServerSocket ss = new ServerSocket(1234);
                    while (true) {
                        
                        s = ss.accept();
    
                        System.out.println("New client request received : " + s);
                        //add new input here
                        final DataInputStream dis = new DataInputStream(s.getInputStream());
                        final DataOutputStream dos = new DataOutputStream(s.getOutputStream());
    
                        System.out.println("Creating a new handler for this client...");
                        //output invalid
                        if(i==0){
                            client = "server";
                        }else{
                            client = "client " + i;
                        }
                        final ClientHandler mtch = new ClientHandler(s, client, dis, dos);
    
                        final Thread t = new Thread(mtch);
    
                        System.out.println("Adding this client to active client list");
    
                        ar.add(mtch);
    
                        t.start();
    
                        i++;
                    }
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        });
        setServer.start();

        InetAddress ip = InetAddress.getByName("localhost");

        Socket s_master = new Socket(ip, 1234);

        DataInputStream dis = new DataInputStream(s_master.getInputStream());
        DataOutputStream dos = new DataOutputStream(s_master.getOutputStream());

        try {
            System.out.println("CHAT-APP");
            String input = dis.readUTF();
            System.out.println("Server logged on\n");
            System.out.println("Enter Message to send\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (true) {
                        String msg = scn.nextLine(); 
                          
                        try { 
                            dos.writeUTF(msg); 
                        } catch (IOException e) { 
                            e.printStackTrace(); 
                        } 
                    } 
                }
            }
        });

        sendMessage.start();
    }
}

class ClientHandler implements Runnable {
    Scanner scn = new Scanner(System.in);
    private final String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    public ClientHandler(final Socket s, final String name, final DataInputStream dis, final DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
        try {
            this.dos.writeUTF(this.name);
        } catch (final Exception e) {
            System.out.println(e);
        }

    }

    @Override
    public void run() {

        String received;
        while (true) {
            try {
   
                received = dis.readUTF();

                System.out.println(received);

                if (received.equals("logout")) {
                    this.isloggedin = false;
                    this.s.close();
                    break;
                }

                final StringTokenizer st = new StringTokenizer(received, "=>");
                final String recipient = st.nextToken();
                final String MsgToSend = st.nextToken();
                for (final ClientHandler mc : Server.ar) {
                    if (recipient.equals("broadcast") && mc.isloggedin == true) {
                        mc.dos.writeUTF(this.name + "<=" + MsgToSend);
                    }
                    if (mc.name.equals(recipient) && mc.isloggedin == true) {
                        mc.dos.writeUTF(this.name + "<=" + MsgToSend);
                        break;
                    }
                }
            } catch (final IOException e) {

                e.printStackTrace();
            }

        }
        try {
            this.dis.close();
            this.dos.close();

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
