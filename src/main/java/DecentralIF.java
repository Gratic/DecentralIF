import javax.security.sasl.SaslClient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DecentralIF {
    public static void main(String[] args)
    {
        System.out.println("Bienvenue dans DecentralIF !");
        try {
            InetAddress group = InetAddress.getByName("228.5.6.7");
            MulticastSocket socket = null;
            socket = new MulticastSocket(6789);
            socket.joinGroup(group);

            Scanner sc = new Scanner(System.in);
            String username = null;

            while(username == null || username.equals(""))
            {
                System.out.println("Entrez votre nom d'utilisateur :");
                username = sc.nextLine();
            }

            ListeningThread listeningThread = new ListeningThread(socket);
            listeningThread.start();

            String entryMessage = username + " a rejoint la discussion !";
            DatagramPacket entryPacket = new DatagramPacket(entryMessage.getBytes(StandardCharsets.UTF_8), entryMessage.getBytes(StandardCharsets.UTF_8).length, group, 6789);
            socket.send(entryPacket);

            String message;

            while(true)
            {
                message = sc.nextLine();

                if(message.equals("/quit"))
                {
                    listeningThread.setAlive(false);
                    break;
                }

                message = username + ": " + message;
                DatagramPacket messagePacket = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8).length, group, 6789);
                socket.send(messagePacket);
            }

            String exitMessage = username + " a quitté la discussion !";
            DatagramPacket exitPacket = new DatagramPacket(exitMessage.getBytes(StandardCharsets.UTF_8), exitMessage.getBytes(StandardCharsets.UTF_8).length, group, 6789);
            socket.send(exitPacket);

            try {
                listeningThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            socket.leaveGroup(group);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
