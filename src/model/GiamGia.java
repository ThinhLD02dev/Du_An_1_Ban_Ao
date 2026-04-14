/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author nhocx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiamGia {
    private Integer id;
    private String maGiamGia;
    private LocalDate ngayHieuLuc;
    private LocalDate ngayKetThuc;
    private boolean trangThai;
    private Integer soLanApDung;
    private Integer kieuGiamId;
    
    public Boolean isValid() {
        LocalDate today = LocalDate.now();
        return trangThai &&
               (soLanApDung > 0) &&
               (ngayHieuLuc == null || !today.isBefore(ngayHieuLuc)) &&
               (ngayKetThuc == null || !today.isAfter(ngayKetThuc));
    }

}
