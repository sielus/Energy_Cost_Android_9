package com.example.energii.koszt.ui.exception;

import android.content.Context;
import com.example.energii.koszt.R;

public class SQLEnergyCostException {
    public static class DuplicationRoom extends Exception {
        public DuplicationRoom(String roomName, Context context) {
            super(roomName + " " +context.getResources().getString(R.string.sql_exception_DuplicationRoom));
        }
    }

    public static class DuplicationDevice extends Exception {
        public DuplicationDevice(String deviceName, Context context) {
            super(deviceName + " " + context.getResources().getString(R.string.sql_exception_DuplicationDevice));
        }
    }

    public static class WrongTime extends Exception {
        public WrongTime(Context context) {
            super(context.getResources().getString(R.string.sql_exception_WrongTime));
        }
    }

    public static class EmptyField extends Exception {
        public EmptyField(String fieldName, Context context) {
            super("Pole \"" + fieldName +  "\" nie może być puste");
        }

        public EmptyField(Context context) {
            super(context.getResources().getString(R.string.sql_exception_EmptyField));
        }
    }
}
