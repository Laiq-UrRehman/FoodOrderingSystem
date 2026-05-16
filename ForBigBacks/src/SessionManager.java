public class SessionManager {

    private static SessionManager instance;
    private Customer currentCustomer;
    private RestaurantAdmin currentAdmin;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer c) {
        this.currentCustomer = c;
    }

    public RestaurantAdmin getCurrentAdmin() {
        return currentAdmin;
    }

    public void setCurrentAdmin(RestaurantAdmin a) {
        this.currentAdmin = a;
    }

    public void logout() {
        currentCustomer = null;
        currentAdmin = null;
    }

    public boolean isCustomerLoggedIn() {
        return currentCustomer != null;
    }

    public boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }
}
