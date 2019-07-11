package id.net.gmedia.semargres2019.Kuis;

import java.util.Date;

public class KuisDijawabModel extends KuisModel {
    private String jawaban;
    private boolean menang;
    private Date dijawab;

    public KuisDijawabModel(String id, String merchant, String pertanyaan,
                            String jawaban, Date dijawab, boolean menang) {
        super(id, merchant, pertanyaan);
        this.jawaban = jawaban;
        this.dijawab = dijawab;
        this.menang = menang;
    }

    public String getJawaban() {
        return jawaban;
    }

    public Date getDijawab() {
        return dijawab;
    }

    public boolean isMenang() {
        return menang;
    }
}
