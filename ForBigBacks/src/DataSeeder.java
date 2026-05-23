import java.util.ArrayList;

public class DataSeeder {

    public static void main(String[] args) {
        seedRestaurants();
    }

    public static void seedRestaurants() {

        FileHandler<Restaurant> fileHandler = new FileHandler<>();

        // ================= RESTAURANT 1 =================
        Menu menu1 = new Menu("M001", new ArrayList<>());

        FoodItem f001 = new FoodItem("F001", "Classic Burger", 350, "Burger", 1);
        f001.rate(4.5);
        f001.rate(4.8);
        f001.rate(5.0);
        CustomizationGroup spiceGroup = new CustomizationGroup("Spice Level");
        spiceGroup.addOption("Mild", 0);
        spiceGroup.addOption("Medium", 0);
        spiceGroup.addOption("Hot", 0);
        f001.addCustomizationGroup(spiceGroup);

        CustomizationGroup cheeseGroup = new CustomizationGroup("Extra Cheese");
        cheeseGroup.addOption("No", 0);
        cheeseGroup.addOption("Yes", 50);
        f001.addCustomizationGroup(cheeseGroup);

        FoodItem f002 = new FoodItem("F002", "Fries", 180, "Sides", 1);
        f002.rate(3.8);
        f002.rate(4.0);

        CustomizationGroup sizeGroupFries = new CustomizationGroup("Size");
        sizeGroupFries.addOption("Small", 0);
        sizeGroupFries.addOption("Medium", 50);
        sizeGroupFries.addOption("Large", 100);
        f002.addCustomizationGroup(sizeGroupFries);

        menu1.addItem(f001);
        menu1.addItem(f002);

        Restaurant r1 = new Restaurant(
                "R001",
                "Burger Palace",
                "Gulberg Lahore",
                "American",
                menu1,
                new Location(10, 20));

        r1.rate(4.5);
        r1.rate(4.7);
        r1.rate(4.8);

        RestaurantAdmin a1 = new RestaurantAdmin(
                "A001", "Ahmed", "Lahore", "03001112222",
                "admin1", "123", r1);

        r1.setAdmin(a1);

        // ================= RESTAURANT 2 =================
        Menu menu2 = new Menu("M002", new ArrayList<>());

        FoodItem f003 = new FoodItem("F003", "Pepperoni Pizza", 900, "Pizza", 1);
        f003.rate(4.9);
        f003.rate(4.8);
        CustomizationGroup sizeGroupPizza = new CustomizationGroup("Size");
        sizeGroupPizza.addOption("Small", 0);
        sizeGroupPizza.addOption("Medium", 500);
        sizeGroupPizza.addOption("Large", 700);
        f003.addCustomizationGroup(sizeGroupPizza);

        FoodItem f004 = new FoodItem("F004", "Garlic Bread", 250, "Sides", 1);
        f004.rate(4.0);
        f004.rate(4.1);

        menu2.addItem(f003);
        menu2.addItem(f004);

        Restaurant r2 = new Restaurant(
                "R002",
                "Pizza Hub",
                "DHA Lahore",
                "Italian",
                menu2,
                new Location(15, 25));

        r2.rate(4.6);
        r2.rate(4.7);

        RestaurantAdmin a2 = new RestaurantAdmin(
                "A002", "Bilal", "Lahore", "03112223333",
                "admin2", "123", r2);

        r2.setAdmin(a2);

        // ================= RESTAURANT 3 =================
        Menu menu3 = new Menu("M003", new ArrayList<>());

        FoodItem f005 = new FoodItem("F005", "Chicken Karahi", 1200, "Desi", 1);
        f005.rate(5.0);
        f005.rate(4.9);
        f005.addCustomizationGroup(spiceGroup);

        FoodItem f006 = new FoodItem("F006", "Naan", 60, "Bread", 1);
        f006.rate(4.2);

        menu3.addItem(f005);
        menu3.addItem(f006);

        Restaurant r3 = new Restaurant(
                "R003",
                "Desi Dhaba",
                "Johar Town Lahore",
                "Pakistani",
                menu3,
                new Location(20, 30));

        r3.rate(4.9);
        r3.rate(5.0);

        RestaurantAdmin a3 = new RestaurantAdmin(
                "A003", "Nadia", "Lahore", "03223334444",
                "admin3", "123", r3);

        r3.setAdmin(a3);

        // ================= RESTAURANT 4 =================
        Menu menu4 = new Menu("M004", new ArrayList<>());

        FoodItem f007 = new FoodItem("F007", "Sushi Roll", 1400, "Japanese", 1);
        f007.rate(4.7);
        f007.rate(4.8);

        menu4.addItem(f007);

        Restaurant r4 = new Restaurant(
                "R004",
                "Sushi House",
                "Bahria Town",
                "Japanese",
                menu4,
                new Location(25, 35));

        r4.rate(4.6);
        r4.rate(4.7);

        RestaurantAdmin a4 = new RestaurantAdmin(
                "A004", "Ali", "Lahore", "03334445555",
                "admin4", "123", r4);

        r4.setAdmin(a4);

        // ================= RESTAURANT 5 =================
        Menu menu5 = new Menu("M005", new ArrayList<>());

        FoodItem f008 = new FoodItem("F008", "Tacos", 500, "Mexican", 1);
        f008.rate(4.4);
        f008.rate(4.5);

        menu5.addItem(f008);

        Restaurant r5 = new Restaurant(
                "R005",
                "Taco Fiesta",
                "Model Town",
                "Mexican",
                menu5,
                new Location(30, 40));

        r5.rate(4.3);
        r5.rate(4.4);

        RestaurantAdmin a5 = new RestaurantAdmin(
                "A005", "Hamza", "Lahore", "03445556666",
                "admin5", "123", r5);

        r5.setAdmin(a5);

        // ================= RESTAURANT 6 =================
        Menu menu6 = new Menu("M006", new ArrayList<>());

        FoodItem f009 = new FoodItem("F009", "Butter Chicken", 950, "Indian", 1);
        f009.rate(4.8);
        f009.rate(4.9);

        menu6.addItem(f009);

        Restaurant r6 = new Restaurant(
                "R006",
                "Spice Garden",
                "Cantt",
                "Indian",
                menu6,
                new Location(35, 45));

        r6.rate(4.7);
        r6.rate(4.8);

        RestaurantAdmin a6 = new RestaurantAdmin(
                "A006", "Usman", "Lahore", "03556667777",
                "admin6", "123", r6);

        r6.setAdmin(a6);

        // ================= RESTAURANT 7 =================
        Menu menu7 = new Menu("M007", new ArrayList<>());

        FoodItem f010 = new FoodItem("F010", "BBQ Wings", 700, "BBQ", 1);
        f010.rate(4.6);
        f010.rate(4.7);

        menu7.addItem(f010);

        Restaurant r7 = new Restaurant(
                "R007",
                "BBQ Nation",
                "Wapda Town",
                "BBQ",
                menu7,
                new Location(40, 50));

        r7.rate(4.5);
        r7.rate(4.6);

        RestaurantAdmin a7 = new RestaurantAdmin(
                "A007", "Farhan", "Lahore", "03667778888",
                "admin7", "123", r7);

        r7.setAdmin(a7);

        // ================= RESTAURANT 8 =================
        Menu menu8 = new Menu("M008", new ArrayList<>());

        FoodItem f011 = new FoodItem("F011", "Alfredo Pasta", 850, "Italian", 1);
        f011.rate(4.5);
        f011.rate(4.6);

        menu8.addItem(f011);

        Restaurant r8 = new Restaurant(
                "R008",
                "Pasta Point",
                "Iqbal Town",
                "Italian",
                menu8,
                new Location(45, 55));

        r8.rate(4.4);
        r8.rate(4.5);

        RestaurantAdmin a8 = new RestaurantAdmin(
                "A008", "Hassan", "Lahore", "03778889999",
                "admin8", "123", r8);

        r8.setAdmin(a8);

        // ================= RESTAURANT 9 =================
        Menu menu9 = new Menu("M009", new ArrayList<>());

        FoodItem f012 = new FoodItem("F012", "Cappuccino", 400, "Cafe", 1);
        f012.rate(4.3);
        f012.rate(4.4);

        menu9.addItem(f012);

        Restaurant r9 = new Restaurant(
                "R009",
                "Cafe Mocha",
                "MM Alam Road",
                "Cafe",
                menu9,
                new Location(50, 60));

        r9.rate(4.2);
        r9.rate(4.3);

        RestaurantAdmin a9 = new RestaurantAdmin(
                "A009", "Sara", "Lahore", "03889990000",
                "admin9", "123", r9);

        r9.setAdmin(a9);

        // ================= RESTAURANT 10 =================
        Menu menu10 = new Menu("M010", new ArrayList<>());

        FoodItem f013 = new FoodItem("F013", "Chicken Shawarma", 300, "Arabic", 1);
        f013.rate(4.7);
        f013.rate(4.8);

        menu10.addItem(f013);

        Restaurant r10 = new Restaurant(
                "R010",
                "Shawarma Express",
                "Township",
                "Arabic",
                menu10,
                new Location(55, 65));

        r10.rate(4.5);
        r10.rate(4.6);

        RestaurantAdmin a10 = new RestaurantAdmin(
                "A010", "Omer", "Lahore", "03990001111",
                "admin10", "123", r10);

        r10.setAdmin(a10);

        // ================= RESTAURANT 11 =================
        Menu menu11 = new Menu("M011", new ArrayList<>());

        FoodItem f014 = new FoodItem("F014", "Kung Pao Chicken", 1000, "Chinese", 1);
        f014.rate(4.6);
        f014.rate(4.7);

        menu11.addItem(f014);

        Restaurant r11 = new Restaurant(
                "R011",
                "China Town",
                "Valencia",
                "Chinese",
                menu11,
                new Location(60, 70));

        r11.rate(4.4);
        r11.rate(4.5);

        RestaurantAdmin a11 = new RestaurantAdmin(
                "A011", "Areeba", "Lahore", "03010001111",
                "admin11", "123", r11);

        r11.setAdmin(a11);

        // ================= RESTAURANT 12 =================
        Menu menu12 = new Menu("M012", new ArrayList<>());

        FoodItem f015 = new FoodItem("F015", "Turkish Kebab", 1300, "Turkish", 1);
        f015.rate(4.8);
        f015.rate(4.9);

        menu12.addItem(f015);

        Restaurant r12 = new Restaurant(
                "R012",
                "Turkish Grill",
                "Garden Town",
                "Turkish",
                menu12,
                new Location(65, 75));

        r12.rate(4.7);
        r12.rate(4.8);

        RestaurantAdmin a12 = new RestaurantAdmin(
                "A012", "Zain", "Lahore", "03120002222",
                "admin12", "123", r12);

        r12.setAdmin(a12);

        // ================= RESTAURANT 13 =================
        Menu menu13 = new Menu("M013", new ArrayList<>());

        FoodItem f016 = new FoodItem("F016", "Grilled Fish", 1600, "Seafood", 1);
        f016.rate(4.9);
        f016.rate(5.0);

        menu13.addItem(f016);

        Restaurant r13 = new Restaurant(
                "R013",
                "Sea Food Bay",
                "Askari",
                "Seafood",
                menu13,
                new Location(70, 80));

        r13.rate(4.8);
        r13.rate(4.9);

        RestaurantAdmin a13 = new RestaurantAdmin(
                "A013", "Muneeb", "Lahore", "03230003333",
                "admin13", "123", r13);

        r13.setAdmin(a13);

        // ================= RESTAURANT 14 =================
        Menu menu14 = new Menu("M014", new ArrayList<>());

        FoodItem f017 = new FoodItem("F017", "Healthy Salad", 450, "Healthy", 1);
        f017.rate(4.4);
        f017.rate(4.5);

        menu14.addItem(f017);

        Restaurant r14 = new Restaurant(
                "R014",
                "Healthy Bites",
                "Paragon City",
                "Healthy",
                menu14,
                new Location(75, 85));

        r14.rate(4.3);
        r14.rate(4.4);

        RestaurantAdmin a14 = new RestaurantAdmin(
                "A014", "Noor", "Lahore", "03340004444",
                "admin14", "123", r14);

        r14.setAdmin(a14);

        // ================= RESTAURANT 15 =================
        Menu menu15 = new Menu("M015", new ArrayList<>());

        FoodItem f018 = new FoodItem("F018", "Zinger Burger", 500, "Fast Food", 1);
        f018.rate(4.8);
        f018.rate(4.9);
        f018.rate(5.0);

        menu15.addItem(f018);

        Restaurant r15 = new Restaurant(
                "R015",
                "Hot n Spicy",
                "Ferozepur Road",
                "Fast Food",
                menu15,
                new Location(80, 90));

        r15.rate(4.7);
        r15.rate(4.8);
        r15.rate(4.9);

        RestaurantAdmin a15 = new RestaurantAdmin(
                "A015", "Daniyal", "Lahore", "03450005555",
                "admin15", "123", r15);

        r15.setAdmin(a15);

        // ================= SAVE =================

        Restaurant[] restaurants = {
                r1, r2, r3, r4, r5,
                r6, r7, r8, r9, r10,
                r11, r12, r13, r14, r15
        };

        fileHandler.saveArray(restaurants, "restaurants.dat");

        System.out.println(restaurants.length
                + " restaurants saved to restaurants.dat");
    }
}