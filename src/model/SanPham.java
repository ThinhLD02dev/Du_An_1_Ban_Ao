/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

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
    private int id;
    private String maSP;
    private String tenAo;
    private String moTa;
    private int trangThai;
    private Date ngayTao;
    private int chatLieuId;
    private int thuongHieuId;
}
