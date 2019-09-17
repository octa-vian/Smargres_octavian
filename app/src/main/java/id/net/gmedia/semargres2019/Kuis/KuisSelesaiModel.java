package id.net.gmedia.semargres2019.Kuis;

import java.util.Date;

public class KuisSelesaiModel extends KuisModel{
    private String nama_pemenang;
    private String jawaban_pemenang;
    private String email_pemenang;

    public KuisSelesaiModel(String id, String merchant, String pertanyaan, Date mulai, Date selesai, String hadiah,
                            String nama_pemenang, String jawaban_pemenang, String email_pemenang) {
        super(id, merchant, pertanyaan, hadiah, mulai, selesai);
        this.nama_pemenang = nama_pemenang;
        this.jawaban_pemenang = jawaban_pemenang;
        this.email_pemenang = email_pemenang;
    }

    public String getNama_pemenang() {
        return nama_pemenang;
    }

    public String getJawaban_pemenang() {
        return jawaban_pemenang;
    }

    public String getEmail_pemenang() {
        return email_pemenang;
    }
}
