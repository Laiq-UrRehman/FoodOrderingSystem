import java.io.Serializable;

public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    private String restaurantID;
    private String name;
    private String address;
    private Menu menu;
    private RestaurantAdmin admin;

    public Restaurant() {
    }

    public Restaurant(String restaurantID, String name, String address, Menu menu) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.address = address;
        this.menu = menu;
        // admin is set separately via setAdmin()
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public RestaurantAdmin getAdmin() {
        return admin;
    }

    public void setAdmin(RestaurantAdmin admin) {
        this.admin = admin;
    }

    public void updateMenu() {
        System.out.println("Menu updated.");
    }

    public void acceptOrder() {
        System.out.println("Order accepted.");
    }
}