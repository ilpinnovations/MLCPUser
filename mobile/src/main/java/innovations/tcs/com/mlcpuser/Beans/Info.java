package innovations.tcs.com.mlcpuser.Beans;

import java.util.ArrayList;

public class Info {

    String emp_vehicle_number;
    String emp_full_name;
    String mlcp_location;
    ArrayList<String> vehicleNumberList;

    public Info() {
    }

    public Info(String emp_vehicle_number, String emp_full_name, String mlcp_location) {
        this.emp_vehicle_number = emp_vehicle_number;
        this.emp_full_name = emp_full_name;
        this.mlcp_location = mlcp_location;

    }

    public Info(String emp_vehicle_number, ArrayList<String> vehicleNumberList, String emp_full_name, String mlcp_location) {
        this.emp_vehicle_number = emp_vehicle_number;
        this.vehicleNumberList = vehicleNumberList;
        this.emp_full_name = emp_full_name;
        this.mlcp_location = mlcp_location;

    }

    // getting ID
    public String getVehicleNumber() {
        return this.emp_vehicle_number;
    }

    // setting id
    public void setVehicleNumber(String id) {
        this.emp_vehicle_number = id;
    }

    public ArrayList<String> getVehicleNumberList() {
        return vehicleNumberList;
    }

    public void setVehicleNumberList(ArrayList<String> vehicleNumberList) {
        this.vehicleNumberList = vehicleNumberList;
    }

    // getting name
    public String getName() {
        return this.emp_full_name;
    }

    // setting name
    public void setName(String name) {
        this.emp_full_name = name;
    }

    // getting loc
    public String getLocation() {
        return this.mlcp_location;
    }

    // setting loc
    public void setLocation(String loc) {
        this.mlcp_location = loc;
    }
}
