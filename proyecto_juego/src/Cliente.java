import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

public class Cliente {

    final static int PUERTO = 5010;

    public static void main(String[] args) {

        //
        try {

            Socket socket = new Socket("localhost", PUERTO);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            String mensaje = in.readObject().toString();
            Scanner scanner = new Scanner(System.in);
            String nombre, apellido, usuario, contrasenia, error;
            int edad;
            //Recibe un mensaje pidiendo los datos del jugador
            System.out.println(mensaje);

            Jugador jugador;
            do {

                System.out.println("Escribe el nombre: \n");
                nombre = scanner.nextLine();
                System.out.println("Escribe el apellido: \n");
                apellido = scanner.nextLine();
                System.out.println("Escribe la edad: \n");
                edad = Integer.parseInt(scanner.nextLine());
                System.out.println("Escribe el usuario: \n");
                usuario = scanner.nextLine();
                System.out.println("Escribe la contraseña: \n");
                contrasenia = scanner.nextLine();

                jugador = new Jugador(nombre, apellido, edad, usuario, contrasenia);
                //Envía los datos del jugador
                out.writeObject(jugador);

                //Recibe si hay algún error con algun dato
                error = in.readObject().toString();

            } while (!error.equalsIgnoreCase("Datos correctos"));

            String respuesta = "";
            do {
                try {
                    //Comprobar si está bien escrito todo
                    //Se recibe la clave pública
                    PublicKey publicKey = (PublicKey) in.readObject();
                    //Recibe las reglas sin firmar
                    String mens = in.readObject().toString();
                    Signature verificarDsa = Signature.getInstance("SHA1withRSA");
                    verificarDsa.initVerify(publicKey);
                    verificarDsa.update(mens.getBytes());

                    //Recibe las reglas firmadas
                    byte[] firma = (byte[]) in.readObject();

                    boolean check = verificarDsa.verify(firma);

                    if (check){
                        System.out.println("Firma verificada");
                        respuesta = "verificado";
                        System.out.println(mens);
                    } else {
                        System.out.println("Firma no verificada");
                        respuesta = "no verificado";
                    }
                    //se envía si se han verificado las reglas
                    out.writeObject(respuesta);
                } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                    e.printStackTrace();
                }
            } while (respuesta.equalsIgnoreCase("no verificado"));
            String resultado, operacion;
            int contador = 0;

            do {
                //Recibe la operación
                operacion = in.readObject().toString();
                System.out.println(operacion + "\n Tu respuesta es:");
                resultado = scanner.nextLine();
                if (!resultado.equalsIgnoreCase("end")){
                    contador += 1;
                }
                //Envia la respuesta
                out.writeObject(resultado);

            } while (!resultado.equalsIgnoreCase("end") || contador >= 10);

            String puntuacion;

            //Recibe la puntuación total
            puntuacion = in.readObject().toString();

            System.out.println(puntuacion);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
