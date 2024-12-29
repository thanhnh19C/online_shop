/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.Receiver;

/**
 *
 * @author trant
 */
public class DAOReceiver extends DBConnect {

    public int insertReceiverByPrepared(Receiver receiver) {
        int n = 0;
        String sql = "INSERT INTO `online_shop_system`.`receiver`\n"
                + "(`OrderID`,\n"
                + "`ReceiverName`,\n"
                + "`ReceiverPhoneNumber`,\n"
                + "`ReceiverAddress`,\n"
                + "`ReceiverEmail`,\n"
                + "`ReceiverGender`)\n"
                + "VALUES\n"
                + "(?,?,?,?,?,?);";
        try {
            PreparedStatement pre = conn.prepareStatement(sql);
            pre.setInt(1, receiver.getOrderID());
            pre.setString(2, receiver.getReceiverName());
            pre.setString(3, receiver.getReceiverPhoneNumber());
            pre.setString(4, receiver.getRceiverAddress());
            pre.setString(5, receiver.getReceiverEmail());
            pre.setBoolean(6, receiver.getReceiverGender());
            n = pre.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAOReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }
    public String getEmailById(int orderId){
        String sql = "select * from receiver where orderId =?";
        String email; 
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, orderId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                email = rs.getString("ReceiverEmail");        
                return email;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
}
