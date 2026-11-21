package model;

public class Karyawan extends User {
    public Karyawan(int id, String username, String namaLengkap, String role) {
        super(id, username, namaLengkap, role);
    }

    @Override
    public boolean bolehAksesKasir() {
        return role.equals("KASIR") || role.equals("ADMIN");
    }

    @Override
    public boolean bolehAksesGudang() {
        return role.equals("MANAGER") || role.equals("ADMIN");
    }

    @Override
    public String getDeskripsi() {
        return namaLengkap + " (" + role + ")";
    }
}
