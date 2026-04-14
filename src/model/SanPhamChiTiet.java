/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author ngocp
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamChiTiet {
    private int id;
    private String maSpct;
    private int soLuong;
    private int mauSacId;
    private int kichThuocId;
    private int quanAoId;
}
