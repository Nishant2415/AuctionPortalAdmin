package in.nishant.auctionportaladmin.model;

public class ProductsModel {
    private String productName,endDate,endTime,productImage,minimalPrice,highestBid;

    public ProductsModel() {
    }

    public String getProductName() {
        return productName;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getMinimalPrice() { return minimalPrice; }

    public String getHighestBid() {
        return highestBid;
    }
}
