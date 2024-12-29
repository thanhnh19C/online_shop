/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.sql.ResultSet;
import view.User;
import model.DAOUser;
import model.EncodeSHA;

/**
 *
 * @author 84395
 */
@WebServlet(name = "changeuserinfo", urlPatterns = {"/ChangeuserinfoURL"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 3, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class ControllerChangeuserinfo extends HttpServlet {

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
            int UserID = Integer.parseInt(request.getParameter("UserID"));
            String service = request.getParameter("service");
            if (service == null || service.equals("")) {
                service = "";
            }
            DAOUser daoU = new DAOUser();
            ResultSet rsProfile = daoU.getData("select * from User where UserID = " + UserID);
            request.setAttribute("rsProfile", rsProfile);
            if (service.equals("upload")) {

            }
            if (service.equals("changepassword")) {

            }
            if (service.equals("changeprofile")) {

            }
            request.getRequestDispatcher("profileUser.jsp").forward(request, response);
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
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        int UserID = Integer.parseInt(request.getParameter("UserID"));
        String service = request.getParameter("service");
        if (service == null || service.equals("")) {
            service = "";
        }
        DAOUser daoU = new DAOUser();
        User user = daoU.getUserByUserID(UserID);
        ResultSet rsProfile = daoU.getData("select * from User where UserID = " + UserID);
        request.setAttribute("rsProfile", rsProfile);
        if (service.equals("upload")) {
            HttpSession session = request.getSession();
            User use = (User) session.getAttribute("account");
            String mess = "";
            String newImageName = "AvatarUser" + UserID;
            Part filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();
            int dotIndex = fileName.lastIndexOf(".");
            String result = newImageName + fileName.substring(dotIndex);
            String path = "images/user/" + result;
            String realFileName = getServletContext().getRealPath(path);
            String realFileName1 = realFileName.replace("\\build", "");
            System.out.println("real = "+realFileName +"real1 = "+realFileName1);
            filePart.write(realFileName1);
            filePart.write(realFileName);
            int n = 0;
            n = daoU.updateUserImage(UserID, result);
            use.setUserImage(result);
            session.setAttribute("account", use);
            if (n > 0) {
                mess = "Tải ảnh lên thành công";
            } else {
                mess = null;
            }
            request.setAttribute("mess", mess);
            try {
                Thread.sleep(2000);
                request.getRequestDispatcher("profileUser.jsp").forward(request, response);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (service.equals("changepassword")) {

            String messa = "";
            String oldPassword = request.getParameter("oldPass");
            String passwordEncode = EncodeSHA.transFer(oldPassword);
            String newPassword = request.getParameter("repass");
            HttpSession session = request.getSession();
            User use = (User) session.getAttribute("account");
            DAOUser dao = new DAOUser();
            User checkOldpassword = dao.check(use.getEmail(), passwordEncode);
            if (checkOldpassword == null) {
                messa = "Nhập sai mật khẩu cũ!!";
                request.setAttribute("messa", messa);
                request.getRequestDispatcher("profileUser.jsp").forward(request, response);
            } else {
                dao.updatepassword(use.getUserID(), EncodeSHA.transFer(newPassword));
                messa = "Thay đổi mật khẩu thành công";
                request.setAttribute("messa", messa);
                request.getRequestDispatcher("profileUser.jsp").forward(request, response);
            }
        }

        if (service.equals("changeprofile")) {
            String messaa = "";
            String firstName1 = request.getParameter("firstName1");
            String lastName1 = request.getParameter("lastName1");
            String address1 = request.getParameter("address1");
            String phoneNumber1 = request.getParameter("phoneNumber1");
            String dateOfBirth1 = request.getParameter("dateOfBirth1");
            boolean gender1 = Boolean.parseBoolean(request.getParameter("gender1"));
            HttpSession session = request.getSession();
            User use = (User) session.getAttribute("account");
            DAOUser dao = new DAOUser();
            use.setFirstName(firstName1);
            use.setAddress(address1);
            use.setDateOfBirth(dateOfBirth1);
            use.setLastName(lastName1);
            use.setPhoneNumber(phoneNumber1);
            use.setGender(gender1);
            int n = dao.updateProfile(use);
            if (n > 0) {
                messaa = "Thay đổi thông tin thành công";
            } else {
                messaa = null;
            }
            request.setAttribute("messaa", messaa);
            request.getRequestDispatcher("profileUser.jsp").forward(request, response);
        }
    }
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
}
