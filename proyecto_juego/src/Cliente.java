import javax.crypto.*;
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
            System.out.println("Cliente encendido");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            String nombre, apellido, usuario, contrasenia, comprobacion;
            int edad;
            Jugador jugador;

            String inicioSesion = in.readObject().toString();
            System.out.println(inicioSesion);
            String resp = scanner.nextLine();
            out.writeObject(resp);
            boolean correcto = false;
            if (resp.equalsIgnoreCase("s")) {
                while (!correcto) {
                    String datos = in.readObject().toString();
                    System.out.println(datos);
                    System.out.println("Usuario");
                    usuario = scanner.nextLine();
                    System.out.println("Contraseña");
                    contrasenia = scanner.nextLine();
                    jugador = new Jugador(usuario, contrasenia);
                    out.writeObject(jugador);
                    comprobacion = in.readObject().toString();
                    if (comprobacion.equalsIgnoreCase("true")) {
                        correcto = true;
                    } else {
                        correcto = false;
                    }
                }
            } else if (resp.equalsIgnoreCase("n")) {
                while (!correcto) {
                    String mensaje = in.readObject().toString();
                    //Recibe un mensaje pidiendo los datos del jugador
                    System.out.println(mensaje);

                    System.out.println("Escribe el nombre: ");
                    nombre = scanner.nextLine();
                    out.writeObject(nombre);

                    System.out.println("Escribe el apellido: ");
                    apellido = scanner.nextLine();
                    out.writeObject(apellido);

                    System.out.println("Escribe la edad: ");
                    edad = Integer.parseInt(scanner.nextLine());
                    out.writeObject(edad);

                    System.out.println("Escribe el usuario: ");
                    usuario = scanner.nextLine();
                    out.writeObject(usuario);

                    System.out.println("Escribe la contraseña: ");
                    contrasenia = scanner.nextLine();
                    out.writeObject(contrasenia);


                    comprobacion = in.readObject().toString();
                    correcto = comprobacion.equalsIgnoreCase("true");
                }
            }

            String bienvenida = in.readObject().toString();
            System.out.println(bienvenida);

            String respuesta = "";
            do {
                try {

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

                    if (check) {
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


            try {
                SecretKey key = (SecretKey) in.readObject();
                System.out.println("La clave es --> " + key);
                Cipher descipher = Cipher.getInstance("DES");

                descipher.init(Cipher.DECRYPT_MODE, key);
                byte[] mensaje_cifrado;


                do {
                    //Recibe la operación cifrada y se descifra antes de mostrarlo por consola
                    mensaje_cifrado = (byte[])  in.readObject();
                    operacion = new String(descipher.doFinal(mensaje_cifrado));
                    //operacion = in.readObject().toString();
                    System.out.println(operacion + "\n Tu respuesta es:");
                    resultado = scanner.nextLine();
                    //Envia la respuesta
                    out.writeObject(resultado);

                } while (!resultado.equalsIgnoreCase("end"));

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                System.out.println("Error");
            } catch (InvalidKeyException e) {
                System.out.println("Error con la clave");
            }

            //Recibe la puntuación total
            String puntuacion = in.readObject().toString();

            System.out.println("La puntuación total conseguida es: " + puntuacion);

            System.out.println("Fin de la partida");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
