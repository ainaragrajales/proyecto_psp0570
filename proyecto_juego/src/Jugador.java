import java.io.Serializable;
import java.util.Objects;

public class Jugador implements Serializable {
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
}
