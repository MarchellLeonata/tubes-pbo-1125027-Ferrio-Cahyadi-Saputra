package model;

import java.text.NumberFormat;
import java.util.Locale;

public class KustomisasiOpsi extends BaseEntity {
    private String kategori;
    private String tipeOpsi;
    private String namaOpsi;
    private double hargaTambahan;
    private Integer bahanId;
    private double jumlahDibutuhkan;

    public KustomisasiOpsi(int id, String kategori, String tipeOpsi, String namaOpsi,
                           double hargaTambahan, Integer bahanId, double jumlahDibutuhkan) {
        super(id);
        this.kategori = kategori;
        this.tipeOpsi = tipeOpsi;
        this.namaOpsi = namaOpsi;
        this.hargaTambahan = hargaTambahan;
        this.bahanId = bahanId;
        this.jumlahDibutuhkan = jumlahDibutuhkan;
    }

    public String getKategori() { return kategori; }
    public String getTipeOpsi() { return tipeOpsi; }
    public String getNamaOpsi() { return namaOpsi; }
    public double getHargaTambahan() { return hargaTambahan; }
    public Integer getBahanId() { return bahanId; }
    public double getJumlahDibutuhkan() { return jumlahDibutuhkan; }

    @Override
    public String getDeskripsi() {
        if (hargaTambahan > 0) {
            NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            return namaOpsi + " (+Rp " + fmt.format(hargaTambahan) + ")";
        }
        return namaOpsi;
    }

    @Override
    public String toString() {
        return getDeskripsi();
    }
}