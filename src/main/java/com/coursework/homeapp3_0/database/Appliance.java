package com.coursework.homeapp3_0.database;

public class Appliance {
    private String type;
    private String model;
    private String company;
    private int power;
    private String status;

    public Appliance(String type, String model, String company, int power, String status) {
        this.type = type;
        this.model = model;
        this.company = company;
        this.power = power;
        this.status = status;
    }

    public String showCharacteristic(){
        return "CHARACTERISTIC:\n" + type + "\n" + "MODEL: " + model + "\nCOMPANY: " + company + "\nPOWER: "
                + power + " W\n" + "STATUS: [" + status + "]";
    }

    @Override
    public String toString() {
        return type + " [" + model + "]" + "[" + status + "]";
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getModel() {
        return model;
    }

    public String getCompany() {
        return company;
    }

    public int getPower() {
        return power;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
