package exceptions;

/**
 * Eccezione lanciata quando un'operazione su un'autorizzazione fallisce.
 * Include ricerca, rimozione e operazioni di validazione.
 */
public class AutorizzazioneException extends Exception {

    public AutorizzazioneException() {
    }

    public AutorizzazioneException(String message) {
        super(message);
    }
}
