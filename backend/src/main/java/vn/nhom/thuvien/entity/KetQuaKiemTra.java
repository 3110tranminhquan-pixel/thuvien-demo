package vn.nhom.thuvien.entity;

// Kết quả kiểm tra một điều kiện nghiệp vụ: đạt hay không, nếu không thì vì sao
public class KetQuaKiemTra {

    private final boolean dat;
    private final String lyDo;

    private KetQuaKiemTra(boolean dat, String lyDo) {
        this.dat = dat;
        this.lyDo = lyDo;
    }

    public static KetQuaKiemTra dat() {
        return new KetQuaKiemTra(true, null);
    }

    public static KetQuaKiemTra khongDat(String lyDo) {
        return new KetQuaKiemTra(false, lyDo);
    }

    public boolean isDat() {
        return dat;
    }

    public String getLyDo() {
        return lyDo;
    }
}
