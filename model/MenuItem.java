package model;

public class MenuItem extends BaseEntity {
    private String namaMenu;
    private double harga;

    public MenuItem(int id, String namaMenu, double harga) {
        super(id);
        this.namaMenu = namaMenu;
        this.harga = harga;
    }

    public String getNamaMenu() { return namaMenu; }
    public double getHarga() { return harga; }

    @Override
    public String getDeskripsi() {
        return id + " - " + namaMenu + " (Rp " + harga + ")";
    }

    @Override
    public String toString() {
        return getDeskripsi();
    }
}