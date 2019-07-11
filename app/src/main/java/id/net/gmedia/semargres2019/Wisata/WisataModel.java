package id.net.gmedia.semargres2019.Wisata;

public class WisataModel {
    private String id;
    private String nama;
    private String foto;
    private double latitude;
    private double longitude;
    private String keterangan;

    public WisataModel(String id, String nama, String foto, double latitude, double longitude, String keterangan){
        this.id = id;
        this.nama = nama;
        this.foto = foto;
        this.latitude = latitude;
        this.longitude = longitude;
        this.keterangan = keterangan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getFoto() {
        return foto;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }
}
