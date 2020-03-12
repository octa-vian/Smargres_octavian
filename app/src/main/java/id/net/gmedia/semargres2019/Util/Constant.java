package id.net.gmedia.semargres2019.Util;

import android.content.Context;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import id.net.gmedia.semargres2019.R;

public class Constant {

    //Header Request
    public final static Map<String, String> HEADER_AUTH = new HashMap<String, String>(){{
            put("Auth-Key", "gmedia_semargress");
            put("Client-Service", "frontend-client");
        }
    };

    //Token heaader dengan enkripsi
    public static Map<String, String> getTokenHeader(Context context){
        Map<String, String> header = new HashMap<>();
        header.put("Auth-Key", "gmedia_semargress");
        header.put("Client-Service", "frontend-client");
        header.put("Username", AppSharedPreferences.getEmail(context));
        header.put("Uid", AppSharedPreferences.getUid(context));
        header.put("Token", AppSharedPreferences.getToken(context));
        return header;
    }

    public static final String TAG = "semargres_log";

    //MERCHANT LAYOUT TYPE
    public static final int LAYOUT_TERDEKAT = 123;
    public static final int LAYOUT_POPULER = 124;
    public static final int LAYOUT_KATEGORI = 125;

    //EXTRA
    public static final String EXTRA_MERCHANT_ID = "id_merchant";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_KATEGORI_ID = "id_kategori";
    public static final String EXTRA_KATEGORI_NAMA = "nama_kategori";
    public static final String EXTRA_ID_KUIS = "id_kuis";
    public static final String EXTRA_START_KUIS = "kuis_start";
    public static final String EXTRA_NOTIF = "notif_flag";
    public static final String EXTRA_PROMO = "promo";
    public static final String EXTRA_PROMO_ID = "id_promo";
    public static final String EXTRA_EVENT = "event";
    public static final String EXTRA_WISATA = "wisata";
    public static final String EXTRA_DENAH = "denah";

    //URL
    private static final String baseURL = "https://semargres.gmedia.id/";

    public static final String URL_LOGIN = baseURL + "auth";
    public static final String URL_REGISTER = baseURL + "register";
    public static final String URL_GENDER = baseURL + "gender";
    public static final String URL_AGAMA = baseURL + "agama";
    public static final String URL_MARRIAGE = baseURL + "marriage";
    public static final String URL_PEKERJAAN = baseURL + "pekerjaan";
    public static final String URL_PROFIL = baseURL + "profile/view";
    public static final String URL_PROFIL_EDIT = baseURL + "profile/edit";
    public static final String URL_TEMPAT_WISATA = baseURL + "api/tempat_wisata";
    public static final String URL_TEMPAT_WISATA_DETAIL = baseURL + "api/tempat_wisata/view";
    public static final String URL_QR_SCAN_MERCHANT = baseURL + "user/scan_qrcode";
    public static final String URL_CARA_BAYAR = baseURL + "master/cara_bayar";
    public static final String URL_VOUCHER_LIST = baseURL + "user/voucher";
    public static final String URL_VOUCHER_USE = baseURL + "user/voucher/use";
    public static final String URL_KUIS_LIST = baseURL + "user/quiz/on_progress";
    public static final String URL_KUIS_DIJAWAB = baseURL + "user/quiz/answered";
    public static final String URL_KUIS_SELESAI = baseURL + "user/quiz/expired";
    public static final String URL_KUIS_JAWAB = baseURL + "user/quiz/send_answer";
    public static final String URL_KUIS_DETAIL = baseURL + "user/quiz/view_answered";
    public static final String URL_KUIS_CHECK = baseURL + "user/quiz/check_unread";
    public static final String URL_KUIS_MENANG_CHECK = baseURL + "user/quiz/check_unread_win";
    public static final String URL_FCM_UPDATE = baseURL + "auth/update_fcm_id";
    public static final String URL_CHECK_VERSION = baseURL + "latest_version/user";
    public static final String URL_TIKET_KONSER = baseURL + "user/tiket/konser";
    public static final String URL_TIKET_KONSER_RIWAYAT = baseURL + "user/tiket/konser/history";
    public static final String URL_TIKET_BELI = baseURL + "user/tiket/konser/buy";
    public static final String URL_TIKET_CEK_PROMO = baseURL + "user/tiket/konser/check_promo";

    public static final String URL_PROMO = baseURL + "promo/";
    public static final String URL_IKLAN_HOME_SGM = baseURL + "ads/sgm";
    public static final String URL_PROMO_DETAIL = baseURL + "merchant/view_promo_user";
    public static final String URL_QR = baseURL + "qrcode";
    public static final String URL_KATEGORI = baseURL + "kategori";
    public static final String URL_IKLAN = baseURL + "iklan/";
    public static final String URL_KUPON_RIWAYAT = baseURL + "kupon/history";
    public static final String URL_KUPON_TERJUAL = baseURL + "kupon/terjual";

    public static final String URL_MERCHANT = baseURL + "merchant/all/";
    public static final String URL_MERCHANT_TERDEKAT = baseURL + "merchant/nearby";
    public static final String URL_MERCHANT_TERDEKAT_IKLAN = baseURL + "merchant/nearby_with_ads";
    public static final String URL_MERCHANT_TERDEKAT_BARU = baseURL + "/merchant/nearby_filter_order/";
    public static final String URL_MERCHANT_POPULER = baseURL + "merchant/populer";
    public static final String URL_MERCHANT_KATEGORI = baseURL + "merchant/kategori";

    public static final String URL_REQUEST_OTP = baseURL + "auth/request_otp";
    public static final String URL_LOGIN_NUMBER = baseURL + "auth/request_otp_sms";
    public static final String URL_KIRIM_OTP= baseURL + "auth/login_check_otp";
    public static final String URL_CEK_OTP = baseURL + "auth/check_otp";
    public static final String URL_REGISTER_OTP = baseURL + "register_from_otp";
    public static final String URL_JUMLAH_KUPON = baseURL + "kupon/total";

    public static final String URL_EVENT = baseURL + "event";
    public static final String URL_NEWS = baseURL + "news_promo";
    public static final String URL_LATEST_VERSION = baseURL + "latest_version/user";

    public static String getPathfromDrawable(int res_int){
        return Uri.parse("android.resource://"+ R.class.getPackage().getName()+"/" + res_int).toString();
    }
}
