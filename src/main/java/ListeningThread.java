import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.channels.MulticastChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ListeningThread extends Thread {
    private MulticastSocket socket;
    private boolean alive = false;

    public ListeningThread(MulticastSocket socket)
    {
        this.socket = socket;
        alive = true;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public void run() {
        while(alive)
        {
            byte[] buf = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String message = new String(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()), StandardCharsets.UTF_8);
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}