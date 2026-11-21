package exception;

public class AksesDitolakException extends CafeException {
    public AksesDitolakException(String role) {
        super("Akses ditolak untuk role: " + role);
    }
}
