package hanu.a2_2001040223.models;

public class Product {
    private long id;
    private String thumbnail;
    private String name;
    private String category;
    private long unitPrice;
    private int quantity;
    private long payEachItem;

    public Product(){};

    public Product(long id, String thumbnail, String name, String category, long unitPrice, int quantity, long payEachItem) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.payEachItem = payEachItem;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getPayEachItem() {
        return payEachItem;
    }

    public void setPayEachItem(long payEachItem) {
        this.payEachItem = payEachItem;
    }
}
