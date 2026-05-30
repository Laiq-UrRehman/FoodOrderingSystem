import java.util.ArrayList;

public class DataSeeder {

        public static void main(String[] args) {
                seedRestaurants();
        }

        public static void seedRestaurants() {

                FileHandler<Restaurant> fileHandler = new FileHandler<>();

                // ── Shared customization groups ──────────────────────────────────────────

        
                CustomizationGroup spiceGroup = new CustomizationGroup("Spice Level");
                spiceGroup.addOption("Mild", 0);
                spiceGroup.addOption("Medium", 0);
                spiceGroup.addOption("Hot", 0);

                CustomizationGroup cheeseGroup = new CustomizationGroup("Extra Cheese");
                cheeseGroup.addOption("No", 0);
                cheeseGroup.addOption("Yes", 50);

                CustomizationGroup cupSizeGroup = new CustomizationGroup("Cup Size");
                cupSizeGroup.addOption("Small", 0);
                cupSizeGroup.addOption("Medium", 50);
                cupSizeGroup.addOption("Large", 100);

                FoodItem f001 = new FoodItem("F001", "Chicken Burger", 400, "Burger", 1);
                f001.addCustomizationGroup(spiceGroup);
                f001.addCustomizationGroup(cheeseGroup);
                FoodItem f002 = new FoodItem("F002", "Fries", 180, "Sides", 1);
                f002.rate(3.8);
                f002.rate(4.0);

                CustomizationGroup sizeGroupFries = new CustomizationGroup("Size");
                sizeGroupFries.addOption("Small", 0);
                sizeGroupFries.addOption("Medium", 50);
                sizeGroupFries.addOption("Large", 100);
                f002.addCustomizationGroup(sizeGroupFries);

                FoodItem f001b = new FoodItem("F001B", "Double Smash Burger", 550, "Burger", 1);
                f001b.rate(4.7);
                f001b.rate(4.9);
                f001b.addCustomizationGroup(cheeseGroup);
                f001b.addCustomizationGroup(spiceGroup);

                FoodItem f001c = new FoodItem("F001C", "Onion Rings", 200, "Sides", 1);
                f001c.rate(4.1);
                f001c.rate(4.3);

                FoodItem f001d = new FoodItem("F001D", "Chocolate Milkshake", 280, "Drinks", 1);
                f001d.rate(4.6);
                f001d.rate(4.8);

                CustomizationGroup milkshakeSizeGroup = new CustomizationGroup("Size");
                milkshakeSizeGroup.addOption("Regular", 0);
                milkshakeSizeGroup.addOption("Large", 80);
                f001d.addCustomizationGroup(milkshakeSizeGroup);

                FoodItem f001e = new FoodItem("F001E", "Soft Drink", 120, "Drinks", 1);
                f001e.rate(4.0);
                f001e.addCustomizationGroup(cupSizeGroup);

                CustomizationGroup softDrinkGroup = new CustomizationGroup("Flavor");
                softDrinkGroup.addOption("Cola", 0);
                softDrinkGroup.addOption("Lemon Lime", 0);
                softDrinkGroup.addOption("Orange", 0);
                f001e.addCustomizationGroup(softDrinkGroup);

                Menu menu1 = new Menu("M001", new ArrayList<>());
                menu1.addItem(f001);
                menu1.addItem(f002);
                menu1.addItem(f001b);
                menu1.addItem(f001c);
                menu1.addItem(f001d);
                menu1.addItem(f001e);

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

                FoodItem f003b = new FoodItem("F003B", "BBQ Chicken Pizza", 950, "Pizza", 1);
                f003b.rate(4.7);
                f003b.rate(4.8);
                f003b.addCustomizationGroup(sizeGroupPizza);

                FoodItem f003c = new FoodItem("F003C", "Caesar Salad", 400, "Salads", 1);
                f003c.rate(4.3);
                f003c.rate(4.4);

                FoodItem f003d = new FoodItem("F003D", "Lemonade", 150, "Drinks", 1);
                f003d.rate(4.2);
                f003d.rate(4.4);

                CustomizationGroup lemonadeGroup = new CustomizationGroup("Sugar Level");
                lemonadeGroup.addOption("Regular", 0);
                lemonadeGroup.addOption("Less Sugar", 0);
                lemonadeGroup.addOption("No Sugar", 0);
                f003d.addCustomizationGroup(lemonadeGroup);

                FoodItem f003e = new FoodItem("F003E", "Iced Tea", 160, "Drinks", 1);
                f003e.rate(4.1);
                f003e.rate(4.3);
                f003e.addCustomizationGroup(cupSizeGroup);

                menu2.addItem(f003);
                menu2.addItem(f004);
                menu2.addItem(f003b);
                menu2.addItem(f003c);
                menu2.addItem(f003d);
                menu2.addItem(f003e);

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

                FoodItem f005b = new FoodItem("F005B", "Mutton Biryani", 550, "Desi", 1);
                f005b.rate(4.8);
                f005b.rate(4.9);
                f005b.addCustomizationGroup(spiceGroup);

                FoodItem f005c = new FoodItem("F005C", "Dal Makhani", 350, "Desi", 1);
                f005c.rate(4.5);
                f005c.rate(4.6);

                FoodItem f005d = new FoodItem("F005D", "Lassi", 180, "Drinks", 1);
                f005d.rate(4.7);
                f005d.rate(4.9);

                CustomizationGroup lassiGroup = new CustomizationGroup("Type");
                lassiGroup.addOption("Sweet", 0);
                lassiGroup.addOption("Salty", 0);
                lassiGroup.addOption("Mango", 50);
                f005d.addCustomizationGroup(lassiGroup);

                FoodItem f005e = new FoodItem("F005E", "Rooh Afza", 100, "Drinks", 1);
                f005e.rate(4.4);
                f005e.addCustomizationGroup(cupSizeGroup);

                menu3.addItem(f005);
                menu3.addItem(f006);
                menu3.addItem(f005b);
                menu3.addItem(f005c);
                menu3.addItem(f005d);
                menu3.addItem(f005e);

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

                FoodItem f007b = new FoodItem("F007B", "Ramen", 1100, "Japanese", 1);
                f007b.rate(4.6);
                f007b.rate(4.8);
                f007b.addCustomizationGroup(spiceGroup);

                FoodItem f007c = new FoodItem("F007C", "Edamame", 400, "Sides", 1);
                f007c.rate(4.3);
                f007c.rate(4.5);

                FoodItem f007d = new FoodItem("F007D", "Miso Soup", 300, "Sides", 1);
                f007d.rate(4.4);

                FoodItem f007e = new FoodItem("F007E", "Green Tea", 200, "Drinks", 1);
                f007e.rate(4.5);
                f007e.rate(4.7);

                CustomizationGroup teaTempGroup = new CustomizationGroup("Temperature");
                teaTempGroup.addOption("Hot", 0);
                teaTempGroup.addOption("Iced", 50);
                f007e.addCustomizationGroup(teaTempGroup);

                FoodItem f007f = new FoodItem("F007F", "Yuzu Lemonade", 280, "Drinks", 1);
                f007f.rate(4.6);
                f007f.rate(4.7);
                f007f.addCustomizationGroup(cupSizeGroup);

                menu4.addItem(f007);
                menu4.addItem(f007b);
                menu4.addItem(f007c);
                menu4.addItem(f007d);
                menu4.addItem(f007e);
                menu4.addItem(f007f);

                Restaurant r4 = new Restaurant(
                                "R004",
                                "Sushi House",
                                "Bahria Town",
                                "Japanese",
                                menu4,
                                new Location(25, 28));

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

                FoodItem f008b = new FoodItem("F008B", "Nachos", 450, "Sides", 1);
                f008b.rate(4.3);
                f008b.rate(4.5);

                CustomizationGroup nachosGroup = new CustomizationGroup("Extras");
                nachosGroup.addOption("Plain", 0);
                nachosGroup.addOption("With Salsa", 50);
                nachosGroup.addOption("With Guacamole", 80);
                f008b.addCustomizationGroup(nachosGroup);

                FoodItem f008c = new FoodItem("F008C", "Quesadilla", 600, "Mexican", 1);
                f008c.rate(4.4);
                f008c.rate(4.6);

                FoodItem f008d = new FoodItem("F008D", "Horchata", 200, "Drinks", 1);
                f008d.rate(4.5);
                f008d.rate(4.6);
                f008d.addCustomizationGroup(cupSizeGroup);

                FoodItem f008e = new FoodItem("F008E", "Agua Fresca", 180, "Drinks", 1);
                f008e.rate(4.2);
                f008e.rate(4.4);

                CustomizationGroup aguaGroup = new CustomizationGroup("Flavor");
                aguaGroup.addOption("Watermelon", 0);
                aguaGroup.addOption("Hibiscus", 0);
                aguaGroup.addOption("Cucumber Lime", 0);
                f008e.addCustomizationGroup(aguaGroup);

                menu5.addItem(f008);
                menu5.addItem(f008b);
                menu5.addItem(f008c);
                menu5.addItem(f008d);
                menu5.addItem(f008e);

                Restaurant r5 = new Restaurant(
                                "R005",
                                "Taco Fiesta",
                                "Model Town",
                                "Mexican",
                                menu5,
                                new Location(28, 26));

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

                FoodItem f009b = new FoodItem("F009B", "Paneer Tikka", 750, "Indian", 1);
                f009b.rate(4.6);
                f009b.rate(4.7);
                f009b.addCustomizationGroup(spiceGroup);

                FoodItem f009c = new FoodItem("F009C", "Garlic Naan", 120, "Bread", 1);
                f009c.rate(4.5);
                f009c.rate(4.6);

                FoodItem f009d = new FoodItem("F009D", "Mango Lassi", 220, "Drinks", 1);
                f009d.rate(4.8);
                f009d.rate(4.9);
                f009d.addCustomizationGroup(cupSizeGroup);

                FoodItem f009e = new FoodItem("F009E", "Masala Chai", 100, "Drinks", 1);
                f009e.rate(4.7);
                f009e.rate(4.8);

                CustomizationGroup chaiGroup = new CustomizationGroup("Sugar");
                chaiGroup.addOption("Regular", 0);
                chaiGroup.addOption("Less Sweet", 0);
                chaiGroup.addOption("Extra Sweet", 0);
                f009e.addCustomizationGroup(chaiGroup);

                menu6.addItem(f009);
                menu6.addItem(f009b);
                menu6.addItem(f009c);
                menu6.addItem(f009d);
                menu6.addItem(f009e);

                Restaurant r6 = new Restaurant(
                                "R006",
                                "Spice Garden",
                                "Cantt",
                                "Indian",
                                menu6,
                                new Location(12, 18));

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

                FoodItem f010b = new FoodItem("F010B", "BBQ Ribs", 1500, "BBQ", 1);
                f010b.rate(4.7);
                f010b.rate(4.8);
                f010b.addCustomizationGroup(spiceGroup);

                FoodItem f010c = new FoodItem("F010C", "Coleslaw", 200, "Sides", 1);
                f010c.rate(4.2);
                f010c.rate(4.3);

                FoodItem f010d = new FoodItem("F010D", "Corn on the Cob", 180, "Sides", 1);
                f010d.rate(4.4);

                FoodItem f010e = new FoodItem("F010E", "Lemonade Iced Tea", 190, "Drinks", 1);
                f010e.rate(4.4);
                f010e.rate(4.5);
                f010e.addCustomizationGroup(cupSizeGroup);

                FoodItem f010f = new FoodItem("F010F", "Sparkling Water", 100, "Drinks", 1);
                f010f.rate(4.0);
                f010f.addCustomizationGroup(cupSizeGroup);

                menu7.addItem(f010);
                menu7.addItem(f010b);
                menu7.addItem(f010c);
                menu7.addItem(f010d);
                menu7.addItem(f010e);
                menu7.addItem(f010f);

                Restaurant r7 = new Restaurant(
                                "R007",
                                "BBQ Nation",
                                "Wapda Town",
                                "BBQ",
                                menu7,
                                new Location(14, 22));

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

                FoodItem f011b = new FoodItem("F011B", "Penne Arrabbiata", 750, "Italian", 1);
                f011b.rate(4.4);
                f011b.rate(4.5);
                f011b.addCustomizationGroup(spiceGroup);

                FoodItem f011c = new FoodItem("F011C", "Bruschetta", 350, "Sides", 1);
                f011c.rate(4.3);
                f011c.rate(4.5);

                FoodItem f011d = new FoodItem("F011D", "Tiramisu", 450, "Desserts", 1);
                f011d.rate(4.7);
                f011d.rate(4.8);

                FoodItem f011e = new FoodItem("F011E", "Sparkling Lemon Water", 130, "Drinks", 1);
                f011e.rate(4.2);
                f011e.rate(4.3);
                f011e.addCustomizationGroup(cupSizeGroup);

                FoodItem f011f = new FoodItem("F011F", "Italian Soda", 200, "Drinks", 1);
                f011f.rate(4.4);
                f011f.rate(4.5);

                CustomizationGroup sodaFlavorGroup = new CustomizationGroup("Flavor");
                sodaFlavorGroup.addOption("Peach", 0);
                sodaFlavorGroup.addOption("Strawberry", 0);
                sodaFlavorGroup.addOption("Raspberry", 0);
                f011f.addCustomizationGroup(sodaFlavorGroup);

                menu8.addItem(f011);
                menu8.addItem(f011b);
                menu8.addItem(f011c);
                menu8.addItem(f011d);
                menu8.addItem(f011e);
                menu8.addItem(f011f);

                Restaurant r8 = new Restaurant(
                                "R008",
                                "Pasta Point",
                                "Iqbal Town",
                                "Italian",
                                menu8,
                                new Location(16, 24));

                r8.rate(4.4);
                r8.rate(4.5);

                RestaurantAdmin a8 = new RestaurantAdmin(
                                "A008", "Hassan", "Lahore", "03778889999",
                                "admin8", "123", r8);

                r8.setAdmin(a8);

                // ================= RESTAURANT 9 =================
                Menu menu9 = new Menu("M009", new ArrayList<>());

                FoodItem f012 = new FoodItem("F012", "Cappuccino", 400, "Drinks", 1);
                f012.rate(4.3);
                f012.rate(4.4);
                f012.addCustomizationGroup(cupSizeGroup);

                FoodItem f012b = new FoodItem("F012B", "Caramel Latte", 450, "Drinks", 1);
                f012b.rate(4.5);
                f012b.rate(4.6);
                f012b.addCustomizationGroup(cupSizeGroup);

                CustomizationGroup milkGroup = new CustomizationGroup("Milk");
                milkGroup.addOption("Full Fat", 0);
                milkGroup.addOption("Skimmed", 0);
                milkGroup.addOption("Oat Milk", 80);
                f012b.addCustomizationGroup(milkGroup);

                FoodItem f012c = new FoodItem("F012C", "Avocado Toast", 550, "Snacks", 1);
                f012c.rate(4.4);
                f012c.rate(4.5);

                FoodItem f012d = new FoodItem("F012D", "Blueberry Muffin", 280, "Snacks", 1);
                f012d.rate(4.3);
                f012d.rate(4.4);

                FoodItem f012e = new FoodItem("F012E", "Cold Brew Coffee", 420, "Drinks", 1);
                f012e.rate(4.6);
                f012e.rate(4.7);
                f012e.addCustomizationGroup(cupSizeGroup);

                FoodItem f012f = new FoodItem("F012F", "Fresh Juice", 300, "Drinks", 1);
                f012f.rate(4.5);
                f012f.rate(4.6);
                f012f.addCustomizationGroup(cupSizeGroup);

                CustomizationGroup juiceGroup = new CustomizationGroup("Flavor");
                juiceGroup.addOption("Orange", 0);
                juiceGroup.addOption("Watermelon", 0);
                juiceGroup.addOption("Mixed Fruit", 50);
                f012f.addCustomizationGroup(juiceGroup);

                menu9.addItem(f012);
                menu9.addItem(f012b);
                menu9.addItem(f012c);
                menu9.addItem(f012d);
                menu9.addItem(f012e);
                menu9.addItem(f012f);

                Restaurant r9 = new Restaurant(
                                "R009",
                                "Cafe Mocha",
                                "MM Alam Road",
                                "Cafe",
                                menu9,
                                new Location(18, 26));

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

                FoodItem f013b = new FoodItem("F013B", "Meat Shawarma", 380, "Arabic", 1);
                f013b.rate(4.6);
                f013b.rate(4.8);
                f013b.addCustomizationGroup(spiceGroup);

                FoodItem f013c = new FoodItem("F013C", "Hummus with Pita", 350, "Sides", 1);
                f013c.rate(4.5);
                f013c.rate(4.6);

                FoodItem f013d = new FoodItem("F013D", "Fattoush Salad", 300, "Sides", 1);
                f013d.rate(4.4);
                f013d.rate(4.5);

                FoodItem f013e = new FoodItem("F013E", "Jallab Juice", 200, "Drinks", 1);
                f013e.rate(4.6);
                f013e.rate(4.7);
                f013e.addCustomizationGroup(cupSizeGroup);

                FoodItem f013f = new FoodItem("F013F", "Mint Lemonade", 180, "Drinks", 1);
                f013f.rate(4.5);
                f013f.rate(4.7);
                f013f.addCustomizationGroup(cupSizeGroup);

                menu10.addItem(f013);
                menu10.addItem(f013b);
                menu10.addItem(f013c);
                menu10.addItem(f013d);
                menu10.addItem(f013e);
                menu10.addItem(f013f);

                Restaurant r10 = new Restaurant(
                                "R010",
                                "Shawarma Express",
                                "Township",
                                "Arabic",
                                menu10,
                                new Location(20, 28));

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

                FoodItem f014b = new FoodItem("F014B", "Fried Rice", 600, "Chinese", 1);
                f014b.rate(4.5);
                f014b.rate(4.6);

                CustomizationGroup riceProteinGroup = new CustomizationGroup("Protein");
                riceProteinGroup.addOption("Chicken", 0);
                riceProteinGroup.addOption("Prawn", 150);
                riceProteinGroup.addOption("Vegetable", 0);
                f014b.addCustomizationGroup(riceProteinGroup);

                FoodItem f014c = new FoodItem("F014C", "Spring Rolls", 400, "Sides", 1);
                f014c.rate(4.4);
                f014c.rate(4.5);

                FoodItem f014d = new FoodItem("F014D", "Hot and Sour Soup", 350, "Soups", 1);
                f014d.rate(4.5);
                f014d.rate(4.6);

                FoodItem f014e = new FoodItem("F014E", "Chinese Tea", 150, "Drinks", 1);
                f014e.rate(4.3);
                f014e.rate(4.4);

                CustomizationGroup chineseTeaGroup = new CustomizationGroup("Type");
                chineseTeaGroup.addOption("Jasmine", 0);
                chineseTeaGroup.addOption("Oolong", 0);
                chineseTeaGroup.addOption("Chrysanthemum", 0);
                f014e.addCustomizationGroup(chineseTeaGroup);

                FoodItem f014f = new FoodItem("F014F", "Bubble Tea", 350, "Drinks", 1);
                f014f.rate(4.6);
                f014f.rate(4.7);

                CustomizationGroup bubbleTeaGroup = new CustomizationGroup("Flavor");
                bubbleTeaGroup.addOption("Taro", 0);
                bubbleTeaGroup.addOption("Matcha", 0);
                bubbleTeaGroup.addOption("Brown Sugar", 0);
                f014f.addCustomizationGroup(bubbleTeaGroup);

                menu11.addItem(f014);
                menu11.addItem(f014b);
                menu11.addItem(f014c);
                menu11.addItem(f014d);
                menu11.addItem(f014e);
                menu11.addItem(f014f);

                Restaurant r11 = new Restaurant(
                                "R011",
                                "China Town",
                                "Valencia",
                                "Chinese",
                                menu11,
                                new Location(22, 10));

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

                FoodItem f015b = new FoodItem("F015B", "Lahmacun", 700, "Turkish", 1);
                f015b.rate(4.7);
                f015b.rate(4.8);
                f015b.addCustomizationGroup(spiceGroup);

                FoodItem f015c = new FoodItem("F015C", "Baklava", 400, "Desserts", 1);
                f015c.rate(4.8);
                f015c.rate(4.9);

                FoodItem f015d = new FoodItem("F015D", "Turkish Bread", 150, "Bread", 1);
                f015d.rate(4.5);

                FoodItem f015e = new FoodItem("F015E", "Turkish Tea", 120, "Drinks", 1);
                f015e.rate(4.6);
                f015e.rate(4.7);
                f015e.addCustomizationGroup(cupSizeGroup);

                FoodItem f015f = new FoodItem("F015F", "Ayran", 180, "Drinks", 1);
                f015f.rate(4.5);
                f015f.rate(4.6);
                f015f.addCustomizationGroup(cupSizeGroup);

                menu12.addItem(f015);
                menu12.addItem(f015b);
                menu12.addItem(f015c);
                menu12.addItem(f015d);
                menu12.addItem(f015e);
                menu12.addItem(f015f);

                Restaurant r12 = new Restaurant(
                                "R012",
                                "Turkish Grill",
                                "Garden Town",
                                "Turkish",
                                menu12,
                                new Location(24, 12));

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

                FoodItem f016b = new FoodItem("F016B", "Prawn Tempura", 1200, "Seafood", 1);
                f016b.rate(4.8);
                f016b.rate(4.9);

                FoodItem f016c = new FoodItem("F016C", "Fish and Chips", 900, "Seafood", 1);
                f016c.rate(4.7);
                f016c.rate(4.8);

                FoodItem f016d = new FoodItem("F016D", "Clam Chowder", 700, "Soups", 1);
                f016d.rate(4.6);
                f016d.rate(4.8);

                FoodItem f016e = new FoodItem("F016E", "Virgin Mojito", 250, "Drinks", 1);
                f016e.rate(4.7);
                f016e.rate(4.8);
                f016e.addCustomizationGroup(cupSizeGroup);

                FoodItem f016f = new FoodItem("F016F", "Coconut Water", 200, "Drinks", 1);
                f016f.rate(4.5);
                f016f.rate(4.6);
                f016f.addCustomizationGroup(cupSizeGroup);

                menu13.addItem(f016);
                menu13.addItem(f016b);
                menu13.addItem(f016c);
                menu13.addItem(f016d);
                menu13.addItem(f016e);
                menu13.addItem(f016f);

                Restaurant r13 = new Restaurant(
                                "R013",
                                "Sea Food Bay",
                                "Askari",
                                "Seafood",
                                menu13,
                                new Location(26, 14));

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

                FoodItem f017b = new FoodItem("F017B", "Grilled Chicken Bowl", 650, "Healthy", 1);
                f017b.rate(4.6);
                f017b.rate(4.7);

                FoodItem f017c = new FoodItem("F017C", "Quinoa Salad", 500, "Healthy", 1);
                f017c.rate(4.5);
                f017c.rate(4.6);

                FoodItem f017d = new FoodItem("F017D", "Fruit Bowl", 350, "Snacks", 1);
                f017d.rate(4.4);
                f017d.rate(4.5);

                FoodItem f017e = new FoodItem("F017E", "Green Detox Smoothie", 300, "Drinks", 1);
                f017e.rate(4.5);
                f017e.rate(4.6);

                CustomizationGroup smoothieGroup = new CustomizationGroup("Base");
                smoothieGroup.addOption("Almond Milk", 0);
                smoothieGroup.addOption("Coconut Water", 0);
                smoothieGroup.addOption("Water", 0);
                f017e.addCustomizationGroup(smoothieGroup);

                FoodItem f017f = new FoodItem("F017F", "Infused Water", 100, "Drinks", 1);
                f017f.rate(4.2);
                f017f.rate(4.3);

                CustomizationGroup infusedWaterGroup = new CustomizationGroup("Infusion");
                infusedWaterGroup.addOption("Cucumber Mint", 0);
                infusedWaterGroup.addOption("Lemon Ginger", 0);
                infusedWaterGroup.addOption("Berry", 0);
                f017f.addCustomizationGroup(infusedWaterGroup);

                menu14.addItem(f017);
                menu14.addItem(f017b);
                menu14.addItem(f017c);
                menu14.addItem(f017d);
                menu14.addItem(f017e);
                menu14.addItem(f017f);

                Restaurant r14 = new Restaurant(
                                "R014",
                                "Healthy Bites",
                                "Paragon City",
                                "Healthy",
                                menu14,
                                new Location(28, 16));

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

                FoodItem f018b = new FoodItem("F018B", "Crispy Chicken Strips", 450, "Fast Food", 1);
                f018b.rate(4.7);
                f018b.rate(4.8);
                f018b.addCustomizationGroup(spiceGroup);

                FoodItem f018c = new FoodItem("F018C", "Loaded Fries", 350, "Sides", 1);
                f018c.rate(4.6);
                f018c.rate(4.7);

                CustomizationGroup loadedFriesGroup = new CustomizationGroup("Topping");
                loadedFriesGroup.addOption("Cheese Sauce", 0);
                loadedFriesGroup.addOption("Jalapeno", 50);
                loadedFriesGroup.addOption("BBQ Sauce", 0);
                f018c.addCustomizationGroup(loadedFriesGroup);

                FoodItem f018d = new FoodItem("F018D", "Coleslaw", 150, "Sides", 1);
                f018d.rate(4.3);

                FoodItem f018e = new FoodItem("F018E", "Soft Drink", 120, "Drinks", 1);
                f018e.rate(4.2);
                f018e.rate(4.3);
                f018e.addCustomizationGroup(cupSizeGroup);

                CustomizationGroup fastFoodDrinkGroup = new CustomizationGroup("Flavor");
                fastFoodDrinkGroup.addOption("Cola", 0);
                fastFoodDrinkGroup.addOption("Lemon Lime", 0);
                fastFoodDrinkGroup.addOption("Orange", 0);
                f018e.addCustomizationGroup(fastFoodDrinkGroup);

                FoodItem f018f = new FoodItem("F018F", "Strawberry Milkshake", 300, "Drinks", 1);
                f018f.rate(4.7);
                f018f.rate(4.8);

                menu15.addItem(f018);
                menu15.addItem(f018b);
                menu15.addItem(f018c);
                menu15.addItem(f018d);
                menu15.addItem(f018e);
                menu15.addItem(f018f);

                Restaurant r15 = new Restaurant(
                                "R015",
                                "Hot n Spicy",
                                "Ferozepur Road",
                                "Fast Food",
                                menu15,
                                new Location(30, 18));

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

                try {
                        fileHandler.saveArray(restaurants, "restaurants.dat");
                        System.out.println(restaurants.length + " restaurants saved to restaurants.dat");
                } catch (FileHandler.FileOperationException e) {
                        System.out.println("[DataSeeder] Failed to save restaurants: " + e.getMessage());
                }
        }
}