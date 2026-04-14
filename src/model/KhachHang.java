package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    private Integer id;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private String diaChi;
}
