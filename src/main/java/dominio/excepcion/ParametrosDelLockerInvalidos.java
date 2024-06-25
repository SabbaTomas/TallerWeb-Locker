package dominio.excepcion;

public class ParametrosDelLockerInvalidos extends RuntimeException{

    public ParametrosDelLockerInvalidos(String message){
        super(message);
    }
}
