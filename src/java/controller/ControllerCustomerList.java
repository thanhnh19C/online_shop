/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Logger;
import model.DAOUser;
import view.User;

/**
 *
 * @author trant
 */
@WebServlet(name = "ControllerCustomerList", urlPatterns = {"/marketingCustomerlist"})
public class ControllerCustomerList extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            HttpSession session = request.getSession();
            String service = request.getParameter("service");
            DAOUser dao = new DAOUser();
            String message = "";
            if (service == null) {
                service = "showList";
            }
            if (service.equals("showList")) {
                Vector<User> list = dao.getUser("SELECT * FROM online_shop_system.user where roleID = 1");
                String status = "3";
                request.setAttribute("status", status);
                request.setAttribute("data", list);
                request.getRequestDispatcher("customerlist.jsp").forward(request, response);
            }
            if (service.equals("addnewuser")) {
                String Fname = request.getParameter("FName");
                String Lname = request.getParameter("LName");
                String Adress = request.getParameter("adress");
                String Email = request.getParameter("email");
                String Phone = request.getParameter("phone");
                String Pass = request.getParameter("pass");
                String gender_str = request.getParameter("gender");
                int checkEmail = dao.checkEmail(Email);
                if (checkEmail == 1) {
                    message = "Email đã tồn tại";
                } else {
                    boolean gender;
                    if (gender_str.equals("male")) {
                        gender = true;
                    } else {
                        gender = false;
                    }
                    User newUser = new User(Fname, Lname, Adress, Phone, gender, Pass, Email);
                    int n = dao.addNewUserByMKT(newUser);
                    if (n > 0) {
                        message = "Thêm thành công";
                    }
                }
                session.setAttribute("message", message);
                response.sendRedirect("marketingCustomerlist");
            }
            if (service.equals("showDetail")) {
                String cusID = request.getParameter("uid");
                ResultSet rs = dao.getData("SELECT * FROM `user` where userID = " + cusID);
                ResultSet log = dao.getData("SELECT * FROM loghistory as log join `user` as u on log.UserId = u.UserID where u.UserId = " + cusID+" order by updateAt desc ");
                request.setAttribute("data", rs);
                request.setAttribute("log", log);
                request.getRequestDispatcher("customerdetails.jsp").forward(request, response);
            }
            if (service.equals("fillterStatus")) {
                String status = request.getParameter("status");
                String sql = "SELECT * FROM online_shop_system.user where roleID = 1";
                if (status.equals("1")) {
                    sql += " and UserStatus = 1";
                    status = "1";
                }
                if (status.equals("2")) {
                    sql += " and UserStatus = 2";
                    status = "2";
                }
                if (status.equals("0")) {
                    sql += " and UserStatus = 0";
                    status = "0";
                }
                if (status.equals("3")) {
                    sql = "SELECT * FROM online_shop_system.user where roleID = 1";
                    status = "3";
                }
                Vector<User> list = dao.getUser(sql);
                request.setAttribute("data", list);
                request.setAttribute("status", status);
                request.getRequestDispatcher("customerlist.jsp").forward(request, response);
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
