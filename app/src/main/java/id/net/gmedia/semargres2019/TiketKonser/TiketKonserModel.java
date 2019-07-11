package id.net.gmedia.semargres2019.TiketKonser;

public class TiketKonserModel {
    private String id;
    private String nama;
    private double harga;
    private boolean sold_out;

    public TiketKonserModel(String id, String nama, double harga, boolean sold_out){
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.sold_out = sold_out;
    }

    public String getNama() {
        return nama;
    }

    public String getId() {
        return id;
    }

    public double getHarga() {
        return harga;
    }

    public boolean isSold_out() {
        return sold_out;
    }

    @Override
    public String toString() {
        return nama;
    }
}
