package id.net.gmedia.semargres2019.PromoEvent;

public class PromoModel {
    private String id;
    private String title;
    private String image;
    private String keterangan;
    private String link;

    public PromoModel(String id, String title, String image, String keterangan, String link){
        this.id = id;
        this.title = title;
        this.image = image;
        this.keterangan = keterangan;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getTitle() {
        return title;
    }
}
