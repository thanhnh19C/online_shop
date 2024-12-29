/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import static model.DAOProduct.getCurrentTimestamp;
import view.ProductImage;

/**
 *
 * @author admin
 */
public class DAOProductImage extends DBConnect {

    public ProductImage getProductImageByProductID(int ProductID) {
        String sql = "select * from ProductImage where ProductID = ?";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, ProductID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ProductImage pro = new ProductImage(
                        rs.getInt("ProductID"),
                        rs.getString("ProductURL"),
                        rs.getString("ProductURLShow")
                );
                return pro;
            }
        } catch (SQLException e) {
            System.out.println("sql error at line 36 in DAOProductImage: "+e);
        }
        return null;
    }

    public int insertImage(ProductImage productImage) {
        int n = 0;
        String sql = "INSERT INTO `online_shop_system`.`productimage`\n"
                + "(`ProductID`,\n"
                + "`ProductURL`,\n"
                + "`ProductURLShow`)\n"
                + "VALUES\n"
                + "(?,\n"
                + "?,\n"
                + "?);";
        try {
            PreparedStatement pre = conn.prepareStatement(sql);
            pre.setInt(1, productImage.getProductID());
            pre.setString(2, productImage.getProductURL());
            pre.setString(3, productImage.getProductURLShow());
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAOProductImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }

    public int updateImage(String newURL, int ProductID, String oldURL) {
        int n = 0;
        String sql = "UPDATE `online_shop_system`.`productimage`\n"
                + "SET\n"
                + "`ProductURL` =?\n"
                + "WHERE ProductID = ? and ProductURL = ?";
        try {
            PreparedStatement pre = conn.prepareStatement(sql);
            pre.setString(1, newURL);
            pre.setInt(2, ProductID);
            pre.setString(3, oldURL);
            n = pre.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return n;
    }

    public int updateProductImage(int ProductID, String ProductURLShow) {
        int n = 0;
        String sql = "UPDATE `online_shop_system`.`ProductImage`\n"
                + "SET\n"
                + "ProductURLShow= '" + ProductURLShow + "' WHERE ProductID = " + ProductID;
        try {
            PreparedStatement pre = conn.prepareStatement(sql);
            n = pre.executeUpdate();
        } catch (SQLException e) {
            System.out.println("line 93 in daoProductImage: " + e);
        }
        return n;
    }

    public int updateAllProductImage(ProductImage pro) {
        int n = 0;
        String sql = "UPDATE `online_shop_system`.`ProductImage`\n"
                + "SET\n"
                + "ProductURL ='?',\n"
                + "ProductURLShow = '?',\n"
                + "WHERE ProductID = ?";
        try {
            PreparedStatement pre = conn.prepareStatement(sql);
            pre.setString(1, pro.getProductURL());
            pre.setString(2, pro.getProductURLShow());
            pre.setInt(3, pro.getProductID());
            n = pre.executeUpdate();
        } catch (SQLException e) {
            System.out.println("line 112 in daoProductImage: " + e);
        }
        return n;
    }
}
