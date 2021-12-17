import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    final static int PUERTO = 5010;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PUERTO);
        System.out.println("Servidor encendido");
        for (int i = 0; i < 3; i++) {
            Socket cliente = serverSocket.accept();
            System.out.println("Cliente conectado");
            HiloServidor hiloServidor = new HiloServidor(cliente);
            hiloServidor.start();
            if (i == 2){
                System.out.println("No se aceptan a más jugadores");
            }
        }
    }
}
