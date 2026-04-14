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
    private Integer id;
    private String maSpct;
    private Integer soLuong;
    private Integer mauSacId;
    private Integer kichThuocId;
    private Integer quanAoId;
}
