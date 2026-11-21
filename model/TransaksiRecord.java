package model;

public class TransaksiRecord extends BaseEntity {
    private String tanggal;
    private String kasirUsername;
    private String namaMenu;
    private int jumlahBeli;
    private double totalHarga;

    public TransaksiRecord(int id, String tanggal, String kasirUsername, String namaMenu, int jumlahBeli, double totalHarga) {
        super(id);
        this.tanggal = tanggal;
        this.kasirUsername = kasirUsername;
        this.namaMenu = namaMenu;
        this.jumlahBeli = jumlahBeli;
        this.totalHarga = totalHarga;
    }

    public String getTanggal() { return tanggal; }
    public String getKasirUsername() { return kasirUsername; }
    public String getNamaMenu() { return namaMenu; }
    public int getJumlahBeli() { return jumlahBeli; }
    public double getTotalHarga() { return totalHarga; }

    @Override
    public String getDeskripsi() {
        return "Transaksi #" + id + " - " + namaMenu + " x" + jumlahBeli;
    }
}