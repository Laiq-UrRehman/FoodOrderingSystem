import java.util.ArrayList;
import java.util.List;

public class Menu {
    private String menuID;
    private List<FoodItem> items;

    public Menu() {
        items = new ArrayList<>();
    }

    public Menu(String menuID, List<FoodItem> items) {
        this.menuID = menuID;
        this.items = items;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }

    public List<FoodItem> getItems() {
        return items;
    }

    public void setItems(List<FoodItem> items) {
        this.items = items;
    }

    public void addItem(FoodItem item) {
        items.add(item);
    }

    public void removeItem(FoodItem item) {
        items.remove(item);
    }
}