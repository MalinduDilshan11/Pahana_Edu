package Utils;

public class Customer {
    private int customerId;
    private String name;
    private String address;
    private String accountNumber;
    private String telephoneNumber;

    public Customer() {}

    public Customer(String name, String address, String accountNumber, String telephoneNumber) {
        this.name = name;
        this.address = address;
        this.accountNumber = accountNumber;
        this.telephoneNumber = telephoneNumber;
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getTelephoneNumber() { return telephoneNumber; }
    public void setTelephoneNumber(String telephoneNumber) { this.telephoneNumber = telephoneNumber; }
}