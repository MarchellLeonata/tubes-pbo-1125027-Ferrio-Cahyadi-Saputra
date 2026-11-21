package model;

import interfaces.Authenticatable;
public abstract class User extends BaseEntity implements Authenticatable {
    protected String username;
    protected String namaLengkap;
    protected String role;

    public User(int id, String username, String namaLengkap, String role) {
        super(id);
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getRole() { return role; }
}
