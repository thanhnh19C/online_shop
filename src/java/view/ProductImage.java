/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author admin
 */
public class ProductImage {

    private int ProductID;
    private String ProductURL;
    private String ProductURLShow;

    public ProductImage() {
    }

    public ProductImage(int ProductID, String ProductURL, String ProductURLShow) {
        this.ProductID = ProductID;
        this.ProductURL = ProductURL;
        this.ProductURLShow = ProductURLShow;
    }

    public ProductImage(int ProductID, String ProductURL) {
        this.ProductID = ProductID;
        this.ProductURL = ProductURL;
    }

    public String getProductURLShow() {
        return ProductURLShow;
    }

    public void setProductURLShow(String ProductURLShow) {
        this.ProductURLShow = ProductURLShow;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int ProductID) {
        this.ProductID = ProductID;
    }

    public String getProductURL() {
        return ProductURL;
    }

    public void setProductURL(String ProductURL) {
        this.ProductURL = ProductURL;
    }

    public String getNoBackgroundImage(String productURL) {
        if (productURL.contains("_1")) {
            return productURL;
        }
        return null;
    }

    @Override
    public String toString() {
        return "ProductImage{" + "ProductID=" + ProductID + ", ProductURL=" + ProductURL + '}';
    }

}
