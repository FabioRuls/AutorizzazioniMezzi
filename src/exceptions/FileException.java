package exceptions;

/**
 * Eccezione lanciata quando un'operazione su file fallisce.
 * Include errori di lettura, scrittura, creazione e validazione percorso.
 */
public class FileException extends Exception {

    public FileException() {
        super();
    }

    public FileException(String message) {
        super(message);
    }
}
