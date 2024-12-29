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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DAOCategories;
import model.DAOProduct;
import view.Categories;
import view.Product;

/**
 *
 * @author admin
 */
@WebServlet(name = "ControllerProductList", urlPatterns = {"/ProductListURL"})
public class ControllerProductList extends HttpServlet {

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
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ControllerProductList</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ControllerProductList at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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
        try {
            DAOProduct dao = new DAOProduct();
            DAOCategories daoCate = new DAOCategories();
            String service = request.getParameter("service");
            String type = request.getParameter("type");
            double minValue;
            double maxValue;
            String maxValue_raw = request.getParameter("inputMaxPrice");
            String minValue_raw = request.getParameter("inputMinPrice");
            if (maxValue_raw == null) {
                maxValue = dao.getMaxUnitPrice();
            } else {
                maxValue = Double.parseDouble(maxValue_raw);
            }
            if (minValue_raw == null) {
                minValue = dao.getMinUnitPrice();
            } else {
                minValue = Double.parseDouble(minValue_raw);

            }
            String CategoryID_raw = request.getParameter("CategoryID");
            String TotalRate_raw = request.getParameter("TotalRate");
            String filterChoice = request.getParameter("filterChoice");
            if (filterChoice == null) {
                filterChoice = "createDate desc";
            }
            if (service == null) {
                if (CategoryID_raw == null || CategoryID_raw.equals("")) {
                    service = "ShowAllProduct";
                } else {
                    service = "ShowCategory";
                }
            }
            String pageSetting = "";
            ResultSet rsSettingPage = dao.getData("Select * from Setting where SettingID=2");
            if (rsSettingPage.next()) {
                pageSetting = rsSettingPage.getString("SettingValue");
            }
            //Start get All product----------------------------------------------------------------
            if (service.equals("ShowAllProduct")) {
                String index_raw = request.getParameter("index");
                int index = 1;
                if (index_raw != null) {
                    index = Integer.parseInt(index_raw);
                }
                System.out.println("index = " + index);
                if (filterChoice.equals("priceasc") || filterChoice.equals("(UnitPrice*(100-UnitDiscount)/100) asc")) {
                    filterChoice = "(UnitPrice*(100-UnitDiscount)/100) asc";
                }
                if (filterChoice.equals("pricedesc") || filterChoice.equals("(UnitPrice*(100-UnitDiscount)/100) desc")) {
                    filterChoice = "(UnitPrice*(100-UnitDiscount)/100) desc";
                }
                if (filterChoice.equals("createDate asc") || filterChoice.equals("p.CreateDate asc")) {
                    filterChoice = "p.CreateDate asc";
                }
                if (filterChoice.equals("createDate desc") || filterChoice.equals("p.CreateDate desc")) {
                    filterChoice = "p.CreateDate desc";
                }
                if (type == null || type.equals("")) {
                    //paging
                    System.out.println("IN HERE");
                    int count = dao.getTotalProduct(minValue, maxValue);
                    System.out.println("count = " + count);
                    int endPage = count / 12;
                    if (count % 12 != 0) {
                        endPage++;
                    }
                    request.setAttribute("endPage", endPage);

                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                            + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                    request.setAttribute("rsPaging", rsPaging);
                    request.setAttribute("index", index);
                } else {
                    int count = dao.getTotalTypeProduct(type, minValue, maxValue);
                    int endPage = count / 12;
                    if (count % 12 != 0) {
                        endPage++;
                    }
                    request.setAttribute("endPage", endPage);
                    if (type.equals("showSale")) {
                        ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                        request.setAttribute("rsPaging", rsPaging);
                        request.setAttribute("index", index);
                    }
                }
                System.out.println("Set attribute rsPaging success!");
            }
            // End get All Product----------------------------------------------------------------

            //Start get Product from CategoryID
            if (service.equals("ShowCategory")) {
                String index_raw = request.getParameter("index");
                int index = 1;
                if (index_raw != null) {
                    index = Integer.parseInt(index_raw);
                }
                int CategoryID = Integer.parseInt(CategoryID_raw);
                Categories cateName = daoCate.getCategoriesById(CategoryID);
                request.setAttribute("catename", cateName);
                if (filterChoice.equals("createDate asc") || filterChoice.equals("p.CreateDate asc")) {
                    filterChoice = "p.CreateDate asc";
                    if (type == null || type.equals("")) {
                        int count = dao.getTotalProductByCateID(CategoryID, minValue, maxValue);
                        int endPage = count / 12;
                        if (count % 12 != 0) {
                            endPage++;
                        }
                        request.setAttribute("endPage", endPage);
                        ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                + "and p.CategoryID = " + CategoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                        request.setAttribute("rsPaging", rsPaging);
                        request.setAttribute("index", index);
                    } else {
                        int count = dao.getTotalTypeAndCategoryIDProduct(type, CategoryID, minValue, maxValue);
                        int endPage = count / 12;
                        if (count % 12 != 0) {
                            endPage++;
                        }
                        request.setAttribute("endPage", endPage);
                        if (type.equals("showSale")) {
                            ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                    + "and p.UnitDiscount != 0 and p.CategoryID = " + CategoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                            request.setAttribute("rsPaging", rsPaging);
                            request.setAttribute("index", index);
                        }
                    }

                }
                if (filterChoice.equals("createDate desc") || filterChoice.equals("p.CreateDate desc")) {
                    filterChoice = "p.CreateDate desc";
                    if (type == null || type.equals("")) {
                        int count = dao.getTotalProductByCateID(CategoryID, minValue, maxValue);
                        int endPage = count / 12;
                        if (count % 12 != 0) {
                            endPage++;
                        }
                        request.setAttribute("endPage", endPage);
                        ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                + "and p.CategoryID = " + CategoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                        request.setAttribute("rsPaging", rsPaging);
                        request.setAttribute("index", index);
                    } else {
                        int count = dao.getTotalTypeAndCategoryIDProduct(type, CategoryID, minValue, maxValue);
                        int endPage = count / 12;
                        if (count % 12 != 0) {
                            endPage++;
                        }
                        request.setAttribute("endPage", endPage);
                        if (type.equals("showSale")) {
                            ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                    + "and p.UnitDiscount != 0 and p.CategoryID = " + CategoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                            request.setAttribute("rsPaging", rsPaging);
                            request.setAttribute("index", index);
                        }
                    }
                }
                if (filterChoice.equals("priceasc") || filterChoice.equals("(UnitPrice*(100-UnitDiscount)/100) asc")) {
                    filterChoice = "(UnitPrice*(100-UnitDiscount)/100) asc";
                    if (type == null || type.equals("")) {
                        int count = dao.getTotalProductByCateID(CategoryID, minValue, maxValue);
                        int endPage = count / 12;
                        if (count % 12 != 0) {
                            endPage++;
                        }
                        request.setAttribute("endPage", endPage);
                        ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                + "and p.CategoryID = " + CategoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                        request.setAttribute("rsPaging", rsPaging);
                        request.setAttribute("index", index);
                    } else {
                        int count = dao.getTotalTypeAndCategoryIDProduct(type, CategoryID, minValue, maxValue);
                        int endPage = count / 12;
                        if (count % 12 != 0) {
                            endPage++;
                        }
                        request.setAttribute("endPage", endPage);
                        if (type.equals("showSale")) {
                            ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                    + "and p.UnitDiscount != 0 and p.CategoryID = " + CategoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                            request.setAttribute("rsPaging", rsPaging);
                            request.setAttribute("index", index);
                        }
                    }

                }
                if (filterChoice.equals("pricedesc") || filterChoice.equals("(UnitPrice*(100-UnitDiscount)/100) desc")) {
                    filterChoice = "(UnitPrice*(100-UnitDiscount)/100) desc";
                    if (type == null || type.equals("")) {
                        int count = dao.getTotalProductByCateID(CategoryID, minValue, maxValue);
                        int endPage = count / 12;
                        if (count % 12 != 0) {
                            endPage++;
                        }
                        request.setAttribute("endPage", endPage);
                        ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                + "and p.CategoryID = " + CategoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                        request.setAttribute("rsPaging", rsPaging);
                        request.setAttribute("index", index);
                    } else {
                        int count = dao.getTotalTypeAndCategoryIDProduct(type, CategoryID, minValue, maxValue);
                        int endPage = count / 12;
                        if (count % 12 != 0) {
                            endPage++;
                        }
                        request.setAttribute("endPage", endPage);
                        if (type.equals("showSale")) {
                            ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                    + "and p.UnitDiscount != 0 and p.CategoryID = " + CategoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                            request.setAttribute("rsPaging", rsPaging);
                            request.setAttribute("index", index);
                        }
                    }
                }
            }

            if (service.equals("filter")) {
                if (filterChoice == null) {
                    filterChoice = "createDate desc";
                }
                String index_raw = request.getParameter("index");
                int index = 1;
                if (index_raw != null) {
                    index = Integer.parseInt(index_raw);
                }
                System.out.println("index in here = " + index);
                //orderDate giam dan
                if (filterChoice.equals("createDate asc") || filterChoice.equals("p.CreateDate asc")) {
                    filterChoice = "p.CreateDate asc";
                    if (CategoryID_raw == null || CategoryID_raw.equals("")) {
                        if (TotalRate_raw == null || TotalRate_raw.equals("")) {
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProduct(minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeProduct(type, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }

                        } else {
                            if (type == null || type.equals("")) {
                                int TotalRate = Integer.parseInt(TotalRate_raw);
                                String resultRate = "";
                                if (TotalRate == 0) {
                                    resultRate = "and p.TotalRate between 0 and 5 ";
                                } else {
                                    resultRate = "and p.TotalRate =" + TotalRate + " ";
                                }
                                int count = dao.getTotalProductByRate(TotalRate, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + resultRate + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int TotalRate = Integer.parseInt(TotalRate_raw);
                                String resultRate = "";
                                if (TotalRate == 0) {
                                    resultRate = "and p.TotalRate between 0 and 5 ";
                                } else {
                                    resultRate = "and p.TotalRate =" + TotalRate + " ";
                                }
                                int count = dao.getTotalTypeAndRateProduct(type, TotalRate, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + resultRate + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        }
                    } else {
                        int categoryID = Integer.parseInt(CategoryID_raw);
                        if (TotalRate_raw == null || TotalRate_raw.equals("")) {
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProductByCateID(categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + "and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID = " + categoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeAndCategoryIDProduct(type, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID = " + categoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        } else {
                            if (type == null || type.equals("")) {
                                int TotalRate = Integer.parseInt(TotalRate_raw);
                                String resultRate = "";
                                if (TotalRate == 0) {
                                    resultRate = "and p.TotalRate between 0 and 5 ";
                                } else {
                                    resultRate = "and p.TotalRate =" + TotalRate + " ";
                                }
                                int count = dao.getTotalProductByRateAndCategoryID(TotalRate, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + resultRate + "and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID = " + categoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int TotalRate = Integer.parseInt(TotalRate_raw);
                                String resultRate = "";
                                if (TotalRate == 0) {
                                    resultRate = "and p.TotalRate between 0 and 5 ";
                                } else {
                                    resultRate = "and p.TotalRate =" + TotalRate + " ";
                                }
                                int count = dao.getTotalTypeAndRateAndCategoryID(type, TotalRate, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + resultRate + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID = " + categoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        }
                    }
                }
                //orderDate tang dan
                if (filterChoice.equals("createDate desc") || filterChoice.equals("p.CreateDate desc")) {
                    filterChoice = "p.CreateDate desc";
                    if (CategoryID_raw == null || CategoryID_raw.equals("")) {
                        if (TotalRate_raw == null || TotalRate_raw.equals("")) {
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProduct(minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeProduct(type, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        } else {
                            if (type == null || type.equals("")) {
                                int TotalRate = Integer.parseInt(TotalRate_raw);
                                String resultRate = "";
                                if (TotalRate == 0) {
                                    resultRate = "and p.TotalRate between 0 and 5 ";
                                } else {
                                    resultRate = "and p.TotalRate =" + TotalRate + " ";
                                }
                                int count = dao.getTotalProductByRate(TotalRate, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + resultRate + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int TotalRate = Integer.parseInt(TotalRate_raw);
                                String resultRate = "";
                                if (TotalRate == 0) {
                                    resultRate = "and p.TotalRate between 0 and 5 ";
                                } else {
                                    resultRate = "and p.TotalRate =" + TotalRate + " ";
                                }
                                int count = dao.getTotalTypeAndRateProduct(type, TotalRate, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + resultRate + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }

                        }
                    } else {
                        int categoryID = Integer.parseInt(CategoryID_raw);
                        if (TotalRate_raw == null || TotalRate_raw.equals("")) {
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProductByCateID(categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                System.out.println("End page in here = " + endPage);
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + "and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + categoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeAndCategoryIDProduct(type, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + categoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }

                        } else {
                            int TotalRate = Integer.parseInt(TotalRate_raw);
                            String resultRate = "";
                            if (TotalRate == 0) {
                                resultRate = "and p.TotalRate between 0 and 5 ";
                            } else {
                                resultRate = "and p.TotalRate =" + TotalRate + " ";
                            }
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProductByRateAndCategoryID(TotalRate, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + resultRate + "and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + categoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeAndRateAndCategoryID(type, TotalRate, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + resultRate + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + categoryID + " order by " + filterChoice + " limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        }
                    }
                    request.setAttribute("index", index);
                }
                //UnitPrice giam dan
                if (filterChoice.equals("priceasc") || filterChoice.equals("(UnitPrice*(100-UnitDiscount)/100) asc")) {
                    if (CategoryID_raw == null || CategoryID_raw.equals("")) {
                        if (TotalRate_raw == null || TotalRate_raw.equals("")) {
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProduct(minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by (UnitPrice*(100-UnitDiscount)/100) asc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeProduct(type, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by (UnitPrice*(100-UnitDiscount)/100) asc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        } else {
                            if (type == null || type.equals("")) {
                                int TotalRate = Integer.parseInt(TotalRate_raw);
                                String resultRate = "";
                                if (TotalRate == 0) {
                                    resultRate = "and p.TotalRate between 0 and 5 ";
                                } else {
                                    resultRate = "and p.TotalRate =" + TotalRate + " ";
                                }
                                int count = dao.getTotalProductByRate(TotalRate, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + resultRate + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by (UnitPrice*(100-UnitDiscount)/100) asc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int TotalRate = Integer.parseInt(TotalRate_raw);
                                String resultRate = "";
                                if (TotalRate == 0) {
                                    resultRate = "and p.TotalRate between 0 and 5 ";
                                } else {
                                    resultRate = "and p.TotalRate =" + TotalRate + " ";
                                }
                                int count = dao.getTotalTypeAndRateProduct(type, TotalRate, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + resultRate + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by (UnitPrice*(100-UnitDiscount)/100) asc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }

                        }
                    } else {
                        int categoryID = Integer.parseInt(CategoryID_raw);
                        if (TotalRate_raw == null || TotalRate_raw.equals("")) {
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProductByCateID(categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + "and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + CategoryID_raw + " order by (UnitPrice*(100-UnitDiscount)/100) asc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeAndCategoryIDProduct(type, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + CategoryID_raw + " order by (UnitPrice*(100-UnitDiscount)/100) asc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }

                        } else {
                            int TotalRate = Integer.parseInt(TotalRate_raw);
                            String resultRate = "";
                            if (TotalRate == 0) {
                                resultRate = "and p.TotalRate between 0 and 5 ";
                            } else {
                                resultRate = "and p.TotalRate =" + TotalRate + " ";
                            }
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProductByRateAndCategoryID(TotalRate, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + resultRate + "and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + CategoryID_raw + " order by (UnitPrice*(100-UnitDiscount)/100) asc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeAndRateAndCategoryID(type, TotalRate, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    request.setAttribute("endPage", endPage);
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + resultRate + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + CategoryID_raw + " order by (UnitPrice*(100-UnitDiscount)/100) asc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        }

                    }

                }
                //UnitPrice tang dan
                if (filterChoice.equals("pricedesc") || filterChoice.equals("UnitPrice*(100-UnitDiscount)/100) asc")) {
                    if (CategoryID_raw == null || CategoryID_raw.equals("")) {
                        if (TotalRate_raw == null || TotalRate_raw.equals("")) {
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProduct(minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by(UnitPrice*(100-UnitDiscount)/100) desc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeProduct(type, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by(UnitPrice*(100-UnitDiscount)/100) desc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        } else {
                            int TotalRate = Integer.parseInt(TotalRate_raw);
                            String resultRate = "";
                            if (TotalRate == 0) {
                                resultRate = "and p.TotalRate between 0 and 5 ";
                            } else {
                                resultRate = "and p.TotalRate =" + TotalRate + " ";
                            }
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProductByRate(TotalRate, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + resultRate + "and p.UnitPrice between " + minValue + " and " + maxValue + " order by(UnitPrice*(100-UnitDiscount)/100) desc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeAndRateProduct(type, TotalRate, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + resultRate + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " order by(UnitPrice*(100-UnitDiscount)/100) desc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        }
                    } else {
                        int categoryID = Integer.parseInt(CategoryID_raw);
                        if (TotalRate_raw == null || TotalRate_raw.equals("")) {
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProductByCateID(categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + "and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + CategoryID_raw + " order by(UnitPrice*(100-UnitDiscount)/100) desc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeAndCategoryIDProduct(type, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + CategoryID_raw + " order by(UnitPrice*(100-UnitDiscount)/100) desc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        } else {
                            int TotalRate = Integer.parseInt(TotalRate_raw);
                            String resultRate = "";
                            if (TotalRate == 0) {
                                resultRate = "and p.TotalRate between 0 and 5 ";
                            } else {
                                resultRate = "and p.TotalRate =" + TotalRate + " ";
                            }
                            if (type == null || type.equals("")) {
                                int count = dao.getTotalProductByRateAndCategoryID(TotalRate, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                        + resultRate + "and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + CategoryID_raw + " order by(UnitPrice*(100-UnitDiscount)/100) desc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                request.setAttribute("rsPaging", rsPaging);
                                request.setAttribute("index", index);
                            } else {
                                int count = dao.getTotalTypeAndRateAndCategoryID(type, TotalRate, categoryID, minValue, maxValue);
                                int endPage = count / 12;
                                if (count % 12 != 0) {
                                    endPage++;
                                }
                                request.setAttribute("endPage", endPage);
                                if (type.equals("showSale")) {
                                    ResultSet rsPaging = dao.getData("select * from product as p join ProductImage as pi on p.ProductID = pi.ProductID where p.ProductStatus=0 and pi.ProductURL = pi.ProductURLShow \n"
                                            + resultRate + "and p.UnitDiscount != 0 and p.UnitPrice between " + minValue + " and " + maxValue + " and p.CategoryID= " + CategoryID_raw + " order by(UnitPrice*(100-UnitDiscount)/100) desc limit " + pageSetting + "  offset " + ((index - 1) * 12));
                                    request.setAttribute("rsPaging", rsPaging);
                                    request.setAttribute("index", index);
                                }
                            }
                        }
                    }
                }
                request.setAttribute("filterChoice", filterChoice);
            }

            //End get Product from CategoryID-------------------------------------------------------
            //Startget All category----------------------------------------------------------------
            ResultSet rsCategory = dao.getData("Select * from Categories");
            request.setAttribute("CategoryResult", rsCategory);
            //End get All Category----------------------------------------------------------------
            ResultSet rsMax = dao.getData("select * from Product order by (UnitPrice*(100-UnitDiscount)/100) desc limit 1");
            ResultSet rsMin = dao.getData("select * from Product order by (UnitPrice*(100-UnitDiscount)/100) asc limit 1");
            Double maxPrice = 0.0;
            Double minPrice = 0.0;
            if (rsMax.next() && rsMin.next()) {
                maxPrice = rsMax.getDouble(6);
                minPrice = rsMin.getDouble(6);
            }
            System.out.println("in servlet: maxPrice = " + maxPrice + " and minPrice = " + minPrice);
            request.setAttribute("maxPrice", maxPrice);
            request.setAttribute("minPrice", minPrice);
            request.setAttribute("oldMaxPrice", maxValue);
            request.setAttribute("oldMinPrice", minValue);
            request.setAttribute("filterChoice", filterChoice);
            request.setAttribute("TotalRate", TotalRate_raw);
            request.setAttribute("service", service);
            request.setAttribute("type", type);
            request.setAttribute("categoryID", CategoryID_raw);
            request.getRequestDispatcher("productList.jsp").forward(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(ControllerProductList.class.getName()).log(Level.SEVERE, null, ex);
        }
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
