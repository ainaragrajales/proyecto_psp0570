import java.sql.*;
import java.util.ArrayList;

public class BBDD {
    public static final String driver = "org.sqlite.JDBC";
    public static final String url = "jdbc:sqlite:juego.db";

    public ArrayList<Jugador> listaJugadores(){
        ArrayList<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT * FROM jugadores";
        try {
            Class.forName(driver);

            Connection conexion = DriverManager.getConnection(url);

            Statement sentencia = conexion.createStatement();
            // hace la consulta
            ResultSet resul = sentencia.executeQuery(sql);

            while (resul.next()) {
                // Creo un objeto 'Jugador' vacío
                Jugador jugador = new Jugador();

                // voy pasáandole los atributos al objeto 'jugador'
                jugador.setId(resul.getInt(1));
                jugador.setNombre(resul.getString(2));
                jugador.setApellido(resul.getString(3));
                jugador.setEdad(resul.getInt(4));
                jugador.setUser(resul.getString(5));
                jugador.setPasswd(resul.getString(6));


                // Añado el objeto 'jugador' al ArrayList jugadores
                jugadores.add(jugador);
            }

            // Cerrar ResultSet
            resul.close();
            // Cerrar Statement
            sentencia.close();
            //conexion.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return jugadores;
    }

    public String mirarPassword(String usuario){
        String password = "";
        Jugador jugador = new Jugador(usuario,password);
        try {
            //Cargar el driver
            Class.forName(driver);
            Connection conexion = DriverManager.getConnection(url);
            Statement sentencia = conexion.createStatement();
            // hace la consulta
            ResultSet resul = sentencia.executeQuery("select passwd from jugadores where user='"+usuario+"'");

            while (resul.next()) {
                // Creo un objeto 'Jugador' con el jugador que me pasan por parámetro de entrada voy pasándole los atributos al objeto 'jugador'
                jugador.setPasswd(resul.getString(1));
            }
            // Cerrar ResultSet
            resul.close();
            // Cerrar Statement
            sentencia.close();
            // Cerrar conexion
            conexion.close();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error en la bbdd");
        }
        return jugador.getPasswd();
    }
    public void crearJugadorNuevo(Jugador jugador){
        PreparedStatement ps;
        String sql;
        try {
            Class.forName(driver);
            Connection conexion =  DriverManager.getConnection(url);
            Statement sentencia =  conexion.createStatement();
            sql = "insert into jugadores (nombre, apellido, edad, user, passwd) values (?,?,?,?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, jugador.getNombre());
            ps.setString(2, jugador.getApellido());
            ps.setInt(3, jugador.getEdad());
            ps.setString(4, jugador.getUser());
            ps.setString(5, jugador.getPasswd());
            ps.executeUpdate();
            sentencia.close();
            conexion.close();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error en la conexión");
        }
    }
}
