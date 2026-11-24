package vn.nhom.thuvien.dto;

import java.util.List;

public record TrangKetQua<T>(List<T> dsKetQua, long tongSo, int trang, int soTrang) {
}
