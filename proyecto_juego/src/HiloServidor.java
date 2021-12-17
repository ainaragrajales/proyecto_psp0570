import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HiloServidor extends Thread {

    Socket socket;

    public HiloServidor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        String usuarioJugador = "";
        // igual se puede hacer un login con base de datos sqlite y se pregunta si tiene ya cuenta o quiere crear una,
        // además de comprobar que el mismo jugador no esté en dos partidas a la vez
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            Jugador jugador = new Jugador();
            int num1, num2, res, dev, total = 0;


            String inicioSesion = "¿Tienes cuenta creada? s/n";
            outputStream.writeObject(inicioSesion);
            String resp = inputStream.readObject().toString();
            boolean correcto = false;
            if (resp.equalsIgnoreCase("s")) {
                while (!correcto) {
                    String datos = "Introduce el usuario y la contraseña";
                    outputStream.writeObject(datos);

                    jugador = (Jugador) inputStream.readObject();
                    String contr = new BBDD().mirarPassword(jugador.getUser());
                    if (contr.equalsIgnoreCase(jugador.getPasswd())) {
                        correcto = true;
                    } else {
                        correcto = false;
                    }
                    outputStream.writeObject(correcto);
                }
                //Comprobar contraseña
                //Preguntar mientras la contraseña sea incorrecta
                usuarioJugador = jugador.getUser();
            } else if (resp.equalsIgnoreCase("n")) {
                while (!correcto) {
                    String conectar = "Devuelve los siguientes datos: nombre, apellido, edad, usuario y contraseña";
                    outputStream.writeObject(conectar);

                    jugador.setNombre(inputStream.readObject().toString());
                    System.out.println(jugador.getNombre());

                    jugador.setApellido(inputStream.readObject().toString());
                    System.out.println(jugador.getApellido());

                    jugador.setEdad(Integer.parseInt(inputStream.readObject().toString()));
                    System.out.println(jugador.getEdad());

                    jugador.setUser(inputStream.readObject().toString());
                    System.out.println(jugador.getUser());

                    jugador.setPasswd(inputStream.readObject().toString());
                    System.out.println(jugador.getPasswd());

                    //jugador = (Jugador) inputStream.readObject();

                    //Comprobar con los patrones si los datos son correctos
                    correcto = comprobarString(jugador.getNombre()) && comprobarString(jugador.getApellido()) &&
                            comprobarString(jugador.getUser()) && comprobarEdad(jugador.getEdad()) && comprobarPasswd(jugador.getPasswd());
                    System.out.println(correcto);
                    outputStream.writeObject(correcto);
                }
                //Almacenar los datos en la bd
                new BBDD().crearJugadorNuevo(jugador);
                usuarioJugador = jugador.getUser();
            }


            String bienvenida = "Bienvenido jugador " + usuarioJugador;
            outputStream.writeObject(bienvenida);
            System.out.println("        -->Conectado el jugador " + usuarioJugador);


            String result = "";
            //Enviar las reglas del juego firmadas
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

                    PublicKey publicKey = par.getPublic();
                    PrivateKey privateKey = par.getPrivate();

                    System.out.println("        -->Enviando la clave pública al jugador " + usuarioJugador);
                    //Se envía la clave pública
                    outputStream.writeObject(publicKey);
                    System.out.println("        -->Enviando las reglas sin firmar al jugador " + usuarioJugador);
                    //Se envían las reglas sin firmar
                    outputStream.writeObject(reglas);

                    Signature dsa = Signature.getInstance("SHA1withRSA");
                    dsa.initSign(privateKey);
                    dsa.update(reglas.getBytes());
                    byte[] firma = dsa.sign();
                    System.out.println("        -->Enviando las reglas firmadas al jugador " + usuarioJugador);
                    //Se envían las reglas firmadas
                    outputStream.writeObject(firma);
                    //recibe si se han verificado las reglas
                    result = inputStream.readObject().toString();
                    System.out.println("        -->El jugador " + usuarioJugador + " responde " + result + " las reglas");

                } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
                    e.printStackTrace();
                }


            } while (!result.equalsIgnoreCase("verificado"));

            //Enviar preguntas hasta que el jugador responda 'end'
            String resultado = "", pregunta;
            int operacion;
            Random r = new Random();


            try {
                KeyGenerator keyGenerator;
                Cipher cipher;
                byte[] mensaje_cifrado;


                keyGenerator = KeyGenerator.getInstance("DES");

                SecretKey key = keyGenerator.generateKey();

                cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, key);

                System.out.println("        -->Enviando la clave"+ key +" al jugador " + usuarioJugador );

                outputStream.writeObject(key);

                do {
                    operacion = r.nextInt(3);
                    switch (operacion) {
                        case 0:
                            num1 = r.nextInt(150);
                            num2 = r.nextInt(150);
                            res = num1 + num2;
                            System.out.println("        -->Enviando pregunta al jugador " + usuarioJugador + "\n" +
                                    usuarioJugador + " Suma " + num1 + " + " + num2 + " el rsultado correcto es " + res);
                            pregunta = "Suma " + num1 + " + " + num2 + " y envía el resultado, para salir escribe 'end'";
                            //Cifrar el mensaje
                            mensaje_cifrado = cipher.doFinal(pregunta.getBytes());
                            //Envia la operación
                            outputStream.writeObject(mensaje_cifrado);
                            //Recibe el resultado
                            resultado = inputStream.readObject().toString();
                            if (!resultado.equalsIgnoreCase("end")) {
                                dev = Integer.parseInt(resultado);
                                if (res == dev) {
                                    System.out.println("        --> La respuesta del jugador " + usuarioJugador + " es correcta");
                                    total += 10;
                                } else {
                                    System.out.println("        --> La respuesta del jugador " + usuarioJugador + " es incorrecta");
                                    total -= 5;
                                }
                            }

                            break;
                        case 1:
                            num1 = r.nextInt(100);
                            num2 = r.nextInt(100);
                            res = num1 - num2;
                            System.out.println("        -->Enviando pregunta al jugador " + usuarioJugador + "\n" +
                                    usuarioJugador + " Resta " + num1 + " - " + num2 + " el resultado correcto es " + res);
                            pregunta = "Resta " + num1 + " - " + num2 + " y envía el resultado, para salir escribe 'end'";
                            //Cifrar el mensaje
                            mensaje_cifrado = cipher.doFinal(pregunta.getBytes());
                            //Envia la operación
                            outputStream.writeObject(mensaje_cifrado);
                            //Recibe el resultado
                            resultado = inputStream.readObject().toString();
                            if (!resultado.equalsIgnoreCase("end")) {
                                dev = Integer.parseInt(resultado);
                                if (res == dev) {
                                    System.out.println("        --> La respuesta del jugador " + usuarioJugador + " es correcta");
                                    total += 10;
                                } else {
                                    System.out.println("        --> La respuesta del jugador " + usuarioJugador + " es incorrecta");
                                    total -= 5;
                                }
                            }
                            break;
                        case 2:
                            num1 = r.nextInt(50);
                            num2 = r.nextInt(50);
                            res = num1 * num2;
                            System.out.println("        -->Enviando pregunta al jugador " + usuarioJugador + "\n" +
                                    usuarioJugador + " Multiplica " + num1 + " * " + num2 + " el resultado correcto es " + res);
                            pregunta = "Multiplica " + num1 + " * " + num2 + " y envía el resultado, para salir escribe 'end'";
                            //Cifrar el mensaje
                            mensaje_cifrado = cipher.doFinal(pregunta.getBytes());
                            //Envia la operación
                            outputStream.writeObject(mensaje_cifrado);
                            //Recibe el resultado
                            resultado = inputStream.readObject().toString();
                            if (!resultado.equalsIgnoreCase("end")) {
                                dev = Integer.parseInt(resultado);
                                if (res == dev) {
                                    System.out.println("        --> La respuesta del jugador " + usuarioJugador + " es correcta");
                                    total += 10;
                                } else {
                                    System.out.println("        --> La respuesta del jugador " + usuarioJugador + " es incorrecta");
                                    total -= 5;
                                }
                            }
                            break;
                        default:
                            break;
                    }

                } while (!resultado.equalsIgnoreCase("end"));

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                System.out.println("Error");
            } catch (InvalidKeyException e) {
                System.out.println("Error con la clave");
            }

            System.out.println("        -->Puntuación total del jugador " + usuarioJugador + " --> " + total);
            //Enviar la puntuación total
            outputStream.writeObject(total);

            System.out.println("        -->Terminada la partida del jugador " + usuarioJugador);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public boolean comprobarString(String nombre) {
        Pattern pattern = Pattern.compile("[a-zA-z]{4,40}");
        Matcher matcher;
        matcher = pattern.matcher(nombre);
        return matcher.find();
    }

    public boolean comprobarEdad(int edad) {
        Pattern pattern = Pattern.compile("[0-9]{1,2}");
        Matcher matcher;
        matcher = pattern.matcher(String.valueOf(edad));
        return matcher.find();
    }

    public boolean comprobarPasswd(String passwd) {
        Pattern pattern = Pattern.compile("[a-zA-z0-9]{4,25}");
        Matcher matcher;
        matcher = pattern.matcher(passwd);
        return matcher.find();
    }
}
