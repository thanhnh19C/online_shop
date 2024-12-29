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
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import model.DAOBlog;
import model.DAOFeedBack;
import model.DAOProduct;
import model.DAOUser;

/**
 *
 * @author admin
 */
@WebServlet(name = "ControllerMarketingDashBoard", urlPatterns = {"/marketingDashBoardURL"})
public class ControllerMarketingDashBoard extends HttpServlet {

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
            DAOProduct daoPro = new DAOProduct();
            DAOBlog daoBlog = new DAOBlog();
            DAOUser daoUser = new DAOUser();
            DAOFeedBack daoFeedBack = new DAOFeedBack();
            ResultSet rsProductSold;
            ResultSet rsUserList;
            String weekFrom = request.getParameter("weekFrom");
            String userWeekFrom = request.getParameter("userWeekFrom");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (weekFrom == null || weekFrom.equals("")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -6); // Subtract 6 days from the current date
                weekFrom = sdf.format(calendar.getTime());
                rsProductSold = daoPro.getData("SELECT dates.date, COALESCE(earning, 0) AS earning\n"
                        + "	FROM (\n"
                        + "	  SELECT DATE_SUB(curdate(), INTERVAL 6 DAY) + INTERVAL (t4.i * 10000 + t3.i * 1000 + t2.i * 100 + t1.i * 10 + t0.i) DAY AS date\n"
                        + "	  FROM\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t0,\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t1,\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t2,\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t3,\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t4\n"
                        + "	) AS dates LEFT JOIN (\n"
                        + "	  SELECT DATE_FORMAT(p.OrderDate, '%Y-%m-%d') AS date, SUM(p.TotalPrice) AS earning\n"
                        + "	  FROM `Order` AS p where p.StatusID = 4\n"
                        + "	  GROUP BY date\n"
                        + "	) AS earnings ON dates.date = earnings.date\n"
                        + "	WHERE dates.date BETWEEN (curdate() - INTERVAL 6 DAY) AND NOW()\n"
                        + "	ORDER BY dates.date;");
                request.setAttribute("rsProductSold", rsProductSold);
                request.setAttribute("formatWeekFrom", weekFrom);
            } else {
                LocalDate dateWeekFrom = LocalDate.parse(weekFrom, formatter);
                String formatWeekFrom = dateWeekFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                rsProductSold = daoPro.getData("SELECT dates.date, COALESCE(earning, 0) AS earning\n"
                        + "	FROM (\n"
                        + "	  SELECT DATE_SUB('" + formatWeekFrom + "'+INTERVAL 7 DAY, INTERVAL 7 DAY) + INTERVAL (t4.i * 10000 + t3.i * 1000 + t2.i * 100 + t1.i * 10 + t0.i) DAY AS date\n"
                        + "	  FROM\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t0,\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t1,\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t2,\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t3,\n"
                        + "		(SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS t4\n"
                        + "	) AS dates LEFT JOIN (\n"
                        + "	  SELECT DATE_FORMAT(p.OrderDate, '%Y-%m-%d') AS date, SUM(p.TotalPrice) AS earning\n"
                        + "	  FROM `Order` AS p where p.StatusID = 4\n"
                        + "	  GROUP BY date\n"
                        + "	) AS earnings ON dates.date = earnings.date\n"
                        + "	WHERE dates.date BETWEEN ('" + formatWeekFrom + "' - INTERVAL 7 DAY) AND NOW()\n"
                        + "	ORDER BY dates.date;");
                request.setAttribute("rsProductSold", rsProductSold);
                request.setAttribute("formatWeekFrom", formatWeekFrom);
            }
            if (userWeekFrom == null || userWeekFrom.equals("")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -6);
                userWeekFrom = sdf.format(calendar.getTime());
                rsUserList = daoUser.getData("select * from User where RoleID = 1 and CreateDate between now() - interval 6 day and now() ORDER BY CreateDate");
                request.setAttribute("rsUserList", rsUserList);
                request.setAttribute("formatUserWeekFrom", userWeekFrom);
            } else {
                LocalDate dateUserWeekFrom = LocalDate.parse(userWeekFrom, formatter);
                String formatUserWeekFrom = dateUserWeekFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                rsUserList = daoUser.getData("select * from User WHERE RoleID = 1 and CreateDate >= '" + formatUserWeekFrom + " 00:00:00' \n"
                        + "AND CreateDate < '" + formatUserWeekFrom + " 00:00:00' + interval 7 day\n"
                        + "ORDER BY CreateDate;");
                request.setAttribute("rsUserList", rsUserList);
                request.setAttribute("formatUserWeekFrom", formatUserWeekFrom);
            }
            //Blog section
            ResultSet rsBlogCount = daoBlog.getData("select count(BlogID) from Blog");
            request.setAttribute("rsBlogCount", rsBlogCount);

            //product section
            ResultSet rsProductCount = daoBlog.getData("select count(ProductID) from Product");
            request.setAttribute("rsProductCount", rsProductCount);
            //User section
            ResultSet rsUserCount = daoUser.getData("select count(UserID) from User where UserStatus = 1 and RoleID = 1");
            request.setAttribute("rsUserCount", rsUserCount);
            //FeedBack section
            ResultSet rsFeedBackCount = daoFeedBack.getData("select count(FeedBackID) from FeedBack");
            request.setAttribute("rsFeedBackCount", rsFeedBackCount);
            //productSold section
            ResultSet rsSellProduct = daoPro.getData("select productName,(totalStock - UnitInStock) as sold,totalStock from Product \n"
                    + "where (totalStock - UnitInStock) > 0 order by sold desc limit 5;");
            request.setAttribute("rsSellProduct", rsSellProduct);
            //feedBackTrend section
            ResultSet rsNewFeedBack = daoFeedBack.getData("select f.FeedBackID, f.ProductID, f.UserID, CONCAT(FirstName,\" \",LastName) as fullName, \n"
                    + "f.FeedBackContent, f.FeedBackRate, f.FeedBackDate from FeedBack as f \n"
                    + "join User as u on f.UserID = u.UserID order by f.FeedBackDate limit 5");
            request.setAttribute("rsNewFeedBack", rsNewFeedBack);

            request.getRequestDispatcher("marketingDashboard.jsp").forward(request, response);
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
