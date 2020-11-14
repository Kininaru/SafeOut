package top.shiftregister.safeout;

public class ListViewItem {
    String machineId;
    String date;
    Boolean granted;
    ListViewItem(boolean g, String date, String machineId) {
        granted = g;
        this.date = date;
        this.machineId = machineId;
    }
}
