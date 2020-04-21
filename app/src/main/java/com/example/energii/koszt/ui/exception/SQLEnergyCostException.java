package com.example.energii.koszt.ui.exception;

public class SQLEnergyCostException {
    public static class DuplicationRoom extends Exception {
        public DuplicationRoom(String roomName) {
            super("Pokój o nazwie " + roomName + " już istnieje");
        }
    }

    public static class DuplicationDevice extends Exception {
        public DuplicationDevice(String deviceName) {
            super("Urządzenie o nazwie " + deviceName + " już istnieje");
        }
    }

    public static class WrongTime extends Exception {
        public WrongTime() {
            super("Proszę wybrać czas pracy urządzenia");
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
