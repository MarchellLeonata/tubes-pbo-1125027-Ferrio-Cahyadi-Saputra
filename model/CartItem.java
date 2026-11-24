package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartItem {
    private int menuId;
    private String namaMenu;
    private double hargaDasar;
    private int jumlah;
    private List<KustomisasiOpsi> opsiTerpilih;

    public CartItem(int menuId, String namaMenu, double hargaDasar, int jumlah,
                    List<KustomisasiOpsi> opsiTerpilih) {
        this.menuId = menuId;
        this.namaMenu = namaMenu;
        this.hargaDasar = hargaDasar;
        this.jumlah = jumlah;
        this.opsiTerpilih = opsiTerpilih != null ? new ArrayList<>(opsiTerpilih) : new ArrayList<>();
    }

    public int getMenuId() { return menuId; }
    public String getNamaMenu() { return namaMenu; }
    public double getHargaDasar() { return hargaDasar; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
    public List<KustomisasiOpsi> getOpsiTerpilih() { return opsiTerpilih; }

    public double getBiayaKustomisasiPerItem() {
        double biaya = 0;
        for (KustomisasiOpsi opsi : opsiTerpilih) {
            if (opsi != null) {
                biaya += opsi.getHargaTambahan();
            }
        }
        return biaya;
    }

    public double getHargaSatuanTotal() {
        return hargaDasar + getBiayaKustomisasiPerItem();
    }

    public double getSubtotal() {
        return getHargaSatuanTotal() * jumlah;
    }

    public String getDeskripsiKustomisasi() {
        if (opsiTerpilih.isEmpty()) {
            return "Standar";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < opsiTerpilih.size(); i++) {
            KustomisasiOpsi opsi = opsiTerpilih.get(i);
            if (opsi == null) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append(opsi.getNamaOpsi());
        }
        return sb.length() == 0 ? "Standar" : sb.toString();
    }

    public String getNamaMenuLengkap() {
        return namaMenu + " (" + getDeskripsiKustomisasi() + ")";
    }

    public boolean isSameConfiguration(int menuId, List<KustomisasiOpsi> opsiLain) {
        if (this.menuId != menuId) return false;
        if (this.opsiTerpilih.size() != opsiLain.size()) return false;
        for (int i = 0; i < this.opsiTerpilih.size(); i++) {
            Integer id1 = this.opsiTerpilih.get(i) != null ? this.opsiTerpilih.get(i).getId() : null;
            Integer id2 = opsiLain.get(i) != null ? opsiLain.get(i).getId() : null;
            if (!Objects.equals(id1, id2)) {
                return false;
            }
        }
        return true;
    }
}