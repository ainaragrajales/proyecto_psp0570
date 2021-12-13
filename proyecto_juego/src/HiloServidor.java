import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HiloServidor extends Thread{

    Socket socket;

    public HiloServidor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            int contador = 0;

            String conectar = "Devuelve los siguientes datos: nombre, apellido, edad, usuario y contraseña";
            outputStream.writeObject(conectar);
            String error = "Introduce de nuevo: ";
            //Comprobar los datos del jugador
            do {

                Jugador jugador = (Jugador) inputStream.readObject();
                if (!comprobarNombre(jugador.getNombre())){
                    error += "el nombre ";
                }
                if (!comprobarApellido(jugador.getApellido())){
                    error += "el apellido ";
                }
                if (!comprobarEdad(jugador.getEdad())){
                    error += "la edad ";
                }
                if (!comprobarUsuario(jugador.getUser())){
                    error += "el usuario ";
                }
                if (!comprobarPasswd(jugador.getPasswd())){
                    error += "la contraseña";
                }
                if (error.equalsIgnoreCase("Introduce de nuevo: ")){
                    error = "Datos correctos";
                }
                outputStream.writeObject(error);
            } while (error.equalsIgnoreCase("Datos correctos"));

            String result = "";
            //Enviar las reglas del juego firmadas, do while(!firmaVerificada)
            do {
                String reglas = "Las reglas del juego son las siguientes:\n" +
                        "   --> Vas a recibir una pregunta de matemáticas (una suma o una resta o una multiplicación), \n" +
                        "       vas a tener que  responder con el resultado de la operación, si la respuesta es correcta obtendrás 10 puntos, si es incorrecta se restarán 5 puntos.\n" +
                        "   --> En cualquier momento puedes escribir por teclado 'end' para terminar la partida.\n" +
                        "   --> Una partida consta de 10 rondas de preguntas.\n" +
                        "¡Disfruta del juego!!!\n";

                try {
                    KeyPairGenerator generador;

                    generador = KeyPairGenerator.getInstance("RSA");

                    //System.out.println("Generando par de claves");
                    KeyPair par = generador.generateKeyPair();

                    PublicKey publicKey= par.getPublic();
                    PrivateKey privateKey = par.getPrivate();

                    //Se envía la clave pública
                    outputStream.writeObject(publicKey);

                    Signature dsa = Signature.getInstance("SHA1withRSA");
                    dsa.initSign(privateKey);
                    dsa.update(reglas.getBytes());
                    byte[] firma = dsa.sign();
                    //Se envían las reglas firmadas
                    outputStream.writeObject(firma);

                    result = inputStream.readObject().toString();

                } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
                    e.printStackTrace();
                }


            } while (!result.equalsIgnoreCase("verificado"));

            //Enviar preguntas hasta que el jugador responda 'end'
            String resultado = "";
            int operacion = -1;
            Random r = new Random();
            int num1 = 0, num2 = 0, res = 0, dev = 0, total = 0;


            do {
                operacion = r.nextInt(2);
                switch (operacion){
                    case 0:
                        System.out.println("Suma");
                        num1 = r.nextInt(150);
                        num2 = r.nextInt(150);
                        System.out.println("Suma " + num1 + " + " + num2 + " y envía el resultado");
                        res = num1 + num2;
                        if ( res == dev){
                            total += 10;
                        } else {
                            total -= 5;
                        }
                        contador += 1;
                        break;
                    case 1:
                        System.out.println("Resta");
                        num1 = r.nextInt(100);
                        num2 = r.nextInt(100);
                        System.out.println("Resta " + num1 + " - " + num2 + " y envía el resultado");
                        res = num1 - num2;
                        if ( res == dev){
                            total += 10;
                        } else {
                            total -= 5;
                        }
                        contador += 1;
                        break;
                    case 2:
                        System.out.println("Multiplicación");
                        num1 = r.nextInt(50);
                        num2 = r.nextInt(50);
                        System.out.println("Multiplica " + num1 + " * " + num2 + " y envía el resultado");
                        res = num1 * num2;
                        if ( res == dev){
                            total += 10;
                        } else {
                            total -= 5;
                        }
                        contador += 1;
                        break;
                    default:
                        break;
                }

            } while (!resultado.equalsIgnoreCase("end") || contador >= 10);

            //Enviar la puntuación total
            System.out.println("Puntuación total --> " + total);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
    public boolean comprobarNombre(String nombre){
        Pattern pattern = Pattern.compile("[a-zA-z]{4,20}");
        Matcher matcher = null;
        matcher = pattern.matcher(nombre);
        return matcher.find();
    }
    public boolean comprobarApellido(String apellido){
        Pattern pattern = Pattern.compile("[a-zA-z]{4,40}");
        Matcher matcher = null;
        matcher = pattern.matcher(apellido);
        return matcher.find();
    }
    public boolean comprobarEdad(int edad){
        Pattern pattern = Pattern.compile("[0-9]{2}");
        Matcher matcher = null;
        matcher = pattern.matcher(String.valueOf(edad));
        return matcher.find();
    }
    public boolean comprobarUsuario(String nick){
        Pattern pattern = Pattern.compile("[a-zA-z0-9]{4,20}");
        Matcher matcher = null;
        matcher = pattern.matcher(nick);
        return matcher.find();
    }
    public boolean comprobarPasswd(String passwd){
        Pattern pattern = Pattern.compile("[a-zA-z0-9]{10,25}");
        Matcher matcher = null;
        matcher = pattern.matcher(passwd);
        return matcher.find();
    }
}
