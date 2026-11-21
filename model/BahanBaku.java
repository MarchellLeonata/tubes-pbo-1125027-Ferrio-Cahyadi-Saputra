package model;

public class BahanBaku extends BaseEntity {
    private String namaBahan;
    private double stok;
    private String satuan;

    public BahanBaku(int id, String namaBahan, double stok, String satuan) {
        super(id);
        this.namaBahan = namaBahan;
        this.stok = stok;
        this.satuan = satuan;
    }

    public String getNamaBahan() { return namaBahan; }
    public double getStok() { return stok; }
    public String getSatuan() { return satuan; }

    @Override
    public String getDeskripsi() {
        return namaBahan + " - " + stok + " " + satuan;
    }
}
