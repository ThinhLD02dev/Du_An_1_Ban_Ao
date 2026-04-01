/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author nhocx
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    private int id;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayThanhToan;
    private BigDecimal tongTien;
    private BigDecimal tienThanhToan;
    private BigDecimal tienNhan;
    private BigDecimal tienThua;
    private boolean trangThai;    
    private int khuyenMaiId;
    private int khachHangId;
    private int nhanVienId;
}
