import javax.security.sasl.SaslClient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * DecentralIF
 *
 * Decentralized chat using multicast.
 */
public class DecentralIF {
    public static void main(String[] args)
    {
        System.out.println("Bienvenue dans DecentralIF !");
        try {
            // Multicast Socket Creation
            InetAddress group = InetAddress.getByName("228.5.6.7");
            MulticastSocket socket = null;
            socket = new MulticastSocket(6789);
            socket.joinGroup(group);

            // Ask for usename
            Scanner sc = new Scanner(System.in);
            String username = null;

            while(username == null || username.equals(""))
            {
                System.out.println("Entrez votre nom d'utilisateur :");
                username = sc.nextLine();
            }

            // Starting Listening Thread
            ListeningThread listeningThread = new ListeningThread(socket);
            listeningThread.start();

            // Entry message
            String entryMessage = username + " a rejoint la discussion !";
            DatagramPacket entryPacket = createDataPacket(group, entryMessage);
            socket.send(entryPacket);

            String message;

            // While we want to send message into the discussion.
            while(true)
            {
                message = sc.nextLine();

                // Command to quit the discussion
                if(message.equals("/quit"))
                {
                    listeningThread.setAlive(false);
                    break;
                }

                message = username + ": " + message;
                DatagramPacket messagePacket = createDataPacket(group, message);
                socket.send(messagePacket);
            }

            // Exit message
            String exitMessage = username + " a quitté la discussion !";
            DatagramPacket exitPacket = createDataPacket(group, exitMessage);
            socket.send(exitPacket);

            // Waiting and closing everything.
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

    /**
     * Utility function to create a DatagramPacket with a String.
     *
     * @param group the group to send to.
     * @param message the message that we want to send.
     * @return The DatagramPacket.
     */
    private static DatagramPacket createDataPacket(InetAddress group, String message) {
        return new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8).length, group, 6789);
    }
}
