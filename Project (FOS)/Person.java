public class Person {
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
