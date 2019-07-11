package id.net.gmedia.semargres2019.TiketKonser;

import java.util.Date;

public class TiketRiwayatModel {
    private String id;
    private String nama;
    private String jenis;
    private int jumlah;
    private Date waktu;
    /*private Date batas_pembayaran;
    private String rekening;*/
    private String keterangan;
    private String status;
    private double total;
    private boolean download;
    private String link_download;
    private String kodepromo;

    public TiketRiwayatModel(String id, String nama, String jenis, int jumlah, double total, String keterangan,
                             Date waktu, String link_download, String status, boolean download, String kodepromo){
        this.id = id;
        this.nama = nama;
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.total = total;
        this.keterangan = keterangan;
        this.waktu = waktu;
        this.link_download = link_download;
        this.status = status;
        this.download = download;
        this.kodepromo = kodepromo;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public double getTotal() {
        return total;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public int getJumlah() {
        return jumlah;
    }

    public Date getWaktu() {
        return waktu;
    }

    public String getJenis() {
        return jenis;
    }

    public String getLink_download() {
        return link_download;
    }

    public String getStatus() {
        return status;
    }

    public boolean isDownload() {
        return download;
    }

    public String getKodepromo() {
        return kodepromo;
    }
}
