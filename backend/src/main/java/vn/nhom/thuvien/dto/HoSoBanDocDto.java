package vn.nhom.thuvien.dto;

import java.util.List;

public record HoSoBanDocDto(BanDocDto banDoc, List<LuotMuonDto> dangMuon, List<LuotMuonDto> lichSu,
                            List<PhieuPhatDto> phieuPhat, List<DatTruocDto> datTruoc) {
}
