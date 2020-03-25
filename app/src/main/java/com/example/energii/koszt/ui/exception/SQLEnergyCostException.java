package com.example.energii.koszt.ui.exception;

public class SQLEnergyCostException {
    public static class DuplicationRoom extends Exception {
        public DuplicationRoom(String roomName) {
            super("Istnieje już pokój o nazwie " + roomName);
        }
    }

    public static class DuplicationDevice extends Exception {
        public DuplicationDevice(String deviceName) {
            super("Istnieje już pokój o nazwie " + deviceName);
        }
    }

    public static class EmptyField extends Exception {
        public EmptyField(String fieldName) {
            super("Pole \"" + fieldName +  "\" nie może być puste");
        }

        public EmptyField() {
            super("Wszystkie pola muszą być zapełnione");
        }
    }
}
