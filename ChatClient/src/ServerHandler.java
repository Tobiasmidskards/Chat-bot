import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ServerHandler {

    private Socket socket = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    Boolean connected = false;
    
    public ServerHandler(String username, String address, String port) {
        try {
            int intPort = Integer.parseInt(port);

            socket = new Socket(address, intPort);
            
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF(username);
            if (inputStream.readUTF().equals("J_OK")) {
                this.connected = true;
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }
    
    public void close() throws IOException{
        connected = false;
        outputStream.close();
        socket.close();

    }
    
    public void send(String msg) throws SocketException, IOException {
        outputStream.writeUTF(msg);
    }

    public void read() throws SocketException, IOException{
        System.out.println(inputStream.readUTF());
    }

    public String readReturn() throws IOException {
        return inputStream.readUTF();
    }
}
