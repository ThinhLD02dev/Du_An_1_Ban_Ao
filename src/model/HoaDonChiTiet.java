/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
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
public class HoaDonChiTiet {
    private int id;
    private int soLuong;
    private BigDecimal tongGia;
    private int quanAoChiTietId;
    private int hoaDonId;
}
