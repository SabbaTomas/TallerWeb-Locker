package dominio.excepcion;

public class LockerNoEncontrado extends RuntimeException{

    public LockerNoEncontrado(String message){
        super(message);
    }

}
