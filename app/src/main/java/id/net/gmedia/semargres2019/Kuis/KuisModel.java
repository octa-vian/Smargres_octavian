package id.net.gmedia.semargres2019.Kuis;

import java.util.Date;

public class KuisModel {
    private String id;
    private String merchant;
    private String pertanyaan = "";
    private Date mulai = new Date(0);
    private Date selesai = new Date(0);
    private String hadiah = "";

    public KuisModel(String id, String merchant, String pertanyaan, Date mulai,
                     Date selesai, String hadiah){
        this.id = id;
        this.merchant = merchant;
        this.pertanyaan = pertanyaan;
        this.mulai = mulai;
        this.selesai = selesai;
        this.hadiah = hadiah;
    }

    public KuisModel(String id, String merchant, String pertanyaan){
        this.id = id;
        this.merchant = merchant;
        this.pertanyaan = pertanyaan;
    }

    public String getMerchant() {
        return merchant;
    }

    public String getId() {
        return id;
    }

    public Date getMulai() {
        return mulai;
    }

    public Date getSelesai() {
        return selesai;
    }

    public String getHadiah() {
        return hadiah;
    }

    public String getPertanyaan() {
        return pertanyaan;
    }
}
