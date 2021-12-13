import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    final static int PUERTO = 5010;

    public static void main(String[] args) throws IOException {

        //El servidor lanza preguntas a los jugadores que se conecten(más de dos jugadores)
        //Pedir al jugador nombre, apellidos, edad, nick, contraseña y validar a través de patrones
        //Cuando el jugador termine la partida se le devuelven el total de puntos

        //Despúes de que el jugador se conecte, el servidor le envía las reglas del juego firmadas y el cliente las tiene que validar
        //las respuestas del cliente tienen que ir cifradas


        //extra usar más métodos para mayor seguridad

        //Escribir un manual de funcionamiento y enviarlo con el proyecto

        ServerSocket serverSocket = new ServerSocket(PUERTO);
        System.out.println("Servidor encendido");

        for (int i = 0; i < 3; i++) {

            Socket cliente = serverSocket.accept();
            HiloServidor hiloServidor = new HiloServidor(cliente);
            hiloServidor.start();

            if (i == 2){
                System.out.println("No se aceptan a más jugadores");
            }
        }




    }

}
