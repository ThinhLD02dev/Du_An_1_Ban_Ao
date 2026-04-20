/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

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
public class DotGiamGia {
    private Integer id;
    private String tenDot;
    private Integer giaTri;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Integer trangThai;
}
