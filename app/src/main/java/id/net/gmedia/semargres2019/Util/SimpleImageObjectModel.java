package id.net.gmedia.semargres2019.Util;

public class SimpleImageObjectModel {
    private String id;
    private String nama;
    private String gambar;

    public SimpleImageObjectModel(String id, String nama, String gambar){
        this.id = id;
        this.nama = nama;
        this.gambar = gambar;
    }

    public String getId() {
        return id;
    }

    public String getGambar() {
        return gambar;
    }

    public String getNama() {
        return nama;
    }
}
