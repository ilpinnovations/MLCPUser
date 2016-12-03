package innovations.tcs.com.mlcpuser.Beans;

/**
 * Created by 1115394 on 11/18/2016.
 */
public class ParkingStatsBean2 {
    String available;
    String total;

    public ParkingStatsBean2() {
    }

    public ParkingStatsBean2(String available, String total) {
        this.available = available;
        this.total = total;
    }

    public String getAvailable() {
        return available;
    }

    public String getTotal() {
        return total;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
