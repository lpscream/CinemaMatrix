package net.matrixhome.kino.data;

import java.util.ArrayList;
import java.util.Calendar;

public class Years {
    private final String TAG = "Years_log";
    public ArrayList<String> years;

    public Years() {
        years = new ArrayList<>();
        for (int i = 1922; i <= Calendar.getInstance().get(Calendar.YEAR); i++) {
            years.add(String.valueOf(i));
        }
    }
}
