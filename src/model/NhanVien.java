package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    private Integer id;
    private String tenNhanVien;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private Integer trangThai;
}
