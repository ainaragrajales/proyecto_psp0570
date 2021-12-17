import java.io.Serializable;
import java.util.Objects;

public class Jugador implements Serializable {
    private int id;
    private String nombre;
    private String apellido;
    private int edad;
    private String user;
    private String passwd;

    public Jugador(String nombre, String apellido, int edad, String user, String passwd) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.user = user;
        this.passwd = passwd;
    }

    public Jugador() {
    }

    public Jugador(String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return edad == jugador.edad && nombre.equals(jugador.nombre) && apellido.equals(jugador.apellido) && user.equals(jugador.user) && passwd.equals(jugador.passwd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, apellido, edad, user, passwd);
    }

    @Override
    public String toString() {
        return "Jugador llamado: "+ nombre + " " + apellido +
                " cuya edad es " + edad +
                ", su usuario es " + user +
                " y su passwd es " + passwd;
    }
}
