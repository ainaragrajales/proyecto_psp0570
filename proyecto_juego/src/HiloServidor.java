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

        // igual se puede hacer un login con base de datos sqlite y se pregunta si tiene ya cuenta o quiere crear una,
        // además de comprobar que el mismo jugador no esté en dos partidas a la vez
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            int contador = 0;

            String conectar = "Devuelve los siguientes datos: nombre, apellido, edad, usuario y contraseña";
            //Envía la petición de los datos del jugador
            outputStream.writeObject(conectar);

            //Comprobar los datos del jugador
            Jugador jugador = (Jugador) inputStream.readObject();
            String bienvenida = "Bienvenido jugador " + jugador.getNombre();
            outputStream.writeObject(bienvenida);
            /*do {
                //Recibe los datos del jugador

                if (!comprobarNombre(jugador.getNombre()) || !comprobarApellido(jugador.getApellido()) || !comprobarEdad(jugador.getEdad()) || !comprobarUsuario(jugador.getUser()) || !comprobarPasswd(jugador.getPasswd())){
                    err = false;
                } else {
                    err = true;
                }
                *//*if (!comprobarNombre(jugador.getNombre())){
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
                if (error.equalsIgnoreCase("")){
                    error = "Datos correctos";
                }
                //Envía si hay algun error con algún dato
                outputStream.writeObject(error);*//*
            } *//*while (error.equalsIgnoreCase("Datos correctos"));*//*
            while (!err);
*/
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
                    //Se envían las reglas sin firmar
                    outputStream.writeObject(reglas);

                    Signature dsa = Signature.getInstance("SHA1withRSA");
                    dsa.initSign(privateKey);
                    dsa.update(reglas.getBytes());
                    byte[] firma = dsa.sign();
                    //Se envían las reglas firmadas
                    outputStream.writeObject(firma);
                    //recibe si se han verificado las reglas
                    result = inputStream.readObject().toString();

                } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
                    e.printStackTrace();
                }


            } while (!result.equalsIgnoreCase("verificado"));

            //Enviar preguntas hasta que el jugador responda 'end'
            String resultado = "", pregunta;
            int operacion;
            Random r = new Random();
            int num1, num2, res, dev = 0, total = 0;


            do {
                operacion = r.nextInt(2);
                switch (operacion){
                    case 0:
                        System.out.println("Suma");
                        num1 = r.nextInt(150);
                        num2 = r.nextInt(150);
                        System.out.println("Suma " + num1 + " + " + num2 + " y envía el resultado");
                        res = num1 + num2;
                        pregunta = "Suma " + num1 + " + " + num2 + " y envía el resultado";
                        //Envia la operación
                        outputStream.writeObject(pregunta);
                        //Recibe el resultado
                        resultado = inputStream.readObject().toString();
                        if (!resultado.equalsIgnoreCase("end")){
                            dev = Integer.parseInt(resultado);
                            if ( res == dev){
                                total += 10;
                            } else {
                                total -= 5;
                            }
                            contador += 1;
                        }

                        break;
                    case 1:
                        System.out.println("Resta");
                        num1 = r.nextInt(100);
                        num2 = r.nextInt(100);
                        System.out.println("Resta " + num1 + " - " + num2 + " y envía el resultado");
                        res = num1 - num2;
                        pregunta = "Resta " + num1 + " - " + num2 + " y envía el resultado";
                        //Envia la operación
                        outputStream.writeObject(pregunta);
                        //Recibe el resultado
                        resultado = inputStream.readObject().toString();
                        if (!resultado.equalsIgnoreCase("end")){
                            dev = Integer.parseInt(resultado);
                            if ( res == dev){
                                total += 10;
                            } else {
                                total -= 5;
                            }
                            contador += 1;
                        }
                        break;
                    case 2:
                        System.out.println("Multiplicación");
                        num1 = r.nextInt(50);
                        num2 = r.nextInt(50);
                        System.out.println("Multiplica " + num1 + " * " + num2 + " y envía el resultado");
                        res = num1 * num2;
                        pregunta = "Multiplica " + num1 + " * " + num2 + " y envía el resultado";
                        //Envia la operación
                        outputStream.writeObject(pregunta);
                        //Recibe el resultado
                        resultado = inputStream.readObject().toString();
                        if (!resultado.equalsIgnoreCase("end")){
                            dev = Integer.parseInt(resultado);
                            if ( res == dev){
                                total += 10;
                            } else {
                                total -= 5;
                            }
                            contador += 1;
                        }
                        break;
                    default:
                        break;
                }

            } while (!resultado.equalsIgnoreCase("end") || contador >= 10);


            System.out.println("Puntuación total --> " + total);
            //Enviar la puntuación total
            outputStream.writeObject(total);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
    public boolean comprobarNombre(String nombre){
        Pattern pattern = Pattern.compile("[a-zA-z]{4,20}");
        Matcher matcher;
        matcher = pattern.matcher(nombre);
        return matcher.find();
    }
    public boolean comprobarApellido(String apellido){
        Pattern pattern = Pattern.compile("[a-zA-z]{4,40}");
        Matcher matcher;
        matcher = pattern.matcher(apellido);
        return matcher.find();
    }
    public boolean comprobarEdad(int edad){
        Pattern pattern = Pattern.compile("[0-9]{2}");
        Matcher matcher;
        matcher = pattern.matcher(String.valueOf(edad));
        return matcher.find();
    }
    public boolean comprobarUsuario(String nick){
        Pattern pattern = Pattern.compile("[a-zA-z0-9]{4,20}");
        Matcher matcher;
        matcher = pattern.matcher(nick);
        return matcher.find();
    }
    public boolean comprobarPasswd(String passwd){
        Pattern pattern = Pattern.compile("[a-zA-z0-9]{10,25}");
        Matcher matcher;
        matcher = pattern.matcher(passwd);
        return matcher.find();
    }
}
