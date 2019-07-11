package id.net.gmedia.semargres2019.Merchant;

import java.util.ArrayList;
import java.util.List;

public class MerchantModel {
    private String id;
    private String nama;
    private String alamat;
    private String foto;

    private double latitude;
    private double longitude;

    private String link = "";
    private boolean iklan = false;

    private List<String> listPromo = new ArrayList<>();

    public MerchantModel(String foto_iklan, String link){
        id = "";
        nama = "";
        alamat = "";
        latitude = 0;
        longitude = 0;

        this.link = link;
        iklan = true;
        this.foto = foto_iklan;
    }

    public MerchantModel(String id, String nama, String foto){
        this.id = id;
        this.nama = nama;
        this.foto = foto;
    }

    public MerchantModel(String id, String nama, String alamat, String foto){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.foto = foto;
    }

    public MerchantModel(String id, String nama, String alamat, String foto, double latitude, double longitude){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.foto = foto;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLink() {
        return link;
    }

    public boolean isIklan() {
        return iklan;
    }

    public void addPromo(String promo){
        listPromo.add(promo);
    }

    public List<String> getListPromo() {
        return listPromo;
    }

    public String getNama() {
        return nama;
    }

    public String getId() {
        return id;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getFoto() {
        return foto;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
