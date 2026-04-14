/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.util.Date;
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
public class SanPham {
    private Integer id;
    private String maSP;
    private String tenAo;
    private String moTa;
    private Integer trangThai;    
    private BigDecimal giaBan;
    private Date ngayTao;
    private Integer dotGiamGiaId;
    private Integer chatLieuId;
    private Integer thuongHieuId;
}
