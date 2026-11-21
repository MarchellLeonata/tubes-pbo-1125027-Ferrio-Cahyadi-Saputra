package exception;

public class StokTidakCukupException extends CafeException {
    public StokTidakCukupException(String namaBahan) {
        super("Stok bahan baku tidak mencukupi: " + namaBahan);
    }
}

