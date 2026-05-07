class Person {
    private String personID;
    private String name;
    private String address;
    private String phoneNumber;

    public Person(){}
    public Person(String personID, String name, String address, String phoneNumber){
        this.personID = personID;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    public String getPersonID() {
        return personID;
    }
    public void setPersonID(String personID) {
        this.personID = personID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}  

class Customer extends Person {
    public Customer(){}
    public Customer(String personID, String name, String address, String phoneNumber){
        super(personID, name, address, phoneNumber);
    }
}

class Rider extends Person {
    private String vehicleType;
    private boolean isAvailable;
    private boolean isAssigned;
    public Rider(){}
    public Rider(String personID, String name, String address, String phoneNumber, String vehicleType, boolean isAvailable){
        super(personID, name, address, phoneNumber);
        this.vehicleType = vehicleType;
        this.isAvailable = isAvailable;
    }

    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public void assignOrder(){
        if(isAvailable){
            isAssigned = true;
            return;
        }
        isAssigned = false;
    }
    public boolean getStatus(){
       return isAvailable;
    }
}


