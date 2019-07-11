package id.net.gmedia.semargres2019.Voucher;

import com.leonardus.irfan.Converter;

import java.util.Date;

public class VoucherModel {
    public final static int RUPIAH = 0;
    public final static int PERSEN = 1;

    private String id;
    private String nama;
    private double nominal;
    private Date start;
    private Date end;
    private int TYPE;

    public VoucherModel(String id, String nama, double nominal,
                        Date start, Date end, int type){
        this.id = id;
        this.nama = nama;
        this.nominal = nominal;
        this.start = start;
        this.end = end;
        this.TYPE = type;
    }

    public int getTYPE() {
        return TYPE;
    }

    public String getId() {
        return id;
    }

    public double getNominal() {
        return nominal;
    }

    public String getNama() {
        return nama;
    }

    public String getPeriode() {
        return Converter.DToStringInverse(start) +
                " - " + Converter.DToStringInverse(end);
    }
}
