package com.example.laptor.rxcoaster.utils;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by laptor on 10/31/17.
 */

public class BluetoothInfo {

    // UUID for the UART BTLE client characteristic which is necessary for notifications.
    private static UUID DESCRIPTOR_CONFIG;
    private static UUID DESCRIPTOR_USER_DESC;

    private static UUID SERVICE_UUID = UUID.fromString("f000ba55-0451-4000-b000-000000000000");;
    private static UUID CHARACTERISTIC_REFILL_UUID = UUID.fromString("00002cab-0000-1000-8000-00805f9b34fb");
    private static UUID CHARACTERISTIC_INTERACTOR_UUID = UUID.fromString("00002bad-0000-1000-8000-8000-00805f9b34fb");

    //TODO: Make constructer to set all the UUID's in order to have multiple bluetooth connections in the future
    public BluetoothInfo(UUID DESCRIPTOR_CONFIG, UUID DESCRIPTOR_USER_DESC, UUID SERVICE_UUID, UUID CHARACTERISTIC_REFILL_UUID, UUID CHARACTERISTIC_INTERACTOR_UUID){
        this.DESCRIPTOR_CONFIG = DESCRIPTOR_CONFIG;
        this.DESCRIPTOR_USER_DESC = DESCRIPTOR_USER_DESC;
        this.SERVICE_UUID = SERVICE_UUID;
        this.CHARACTERISTIC_REFILL_UUID = CHARACTERISTIC_REFILL_UUID;
        this.CHARACTERISTIC_INTERACTOR_UUID = CHARACTERISTIC_INTERACTOR_UUID;
    }

    public static UUID getDescriptorConfig() {
        return DESCRIPTOR_CONFIG;
    }

    public static void setDescriptorConfig(UUID descriptorConfig) {
        DESCRIPTOR_CONFIG = descriptorConfig;
    }

    public static UUID getDescriptorUserDesc() {
        return DESCRIPTOR_USER_DESC;
    }

    public static void setDescriptorUserDesc(UUID descriptorUserDesc) {
        DESCRIPTOR_USER_DESC = descriptorUserDesc;
    }

    public static UUID getServiceUuid() {
        return SERVICE_UUID;
    }

    public static void setServiceUuid(UUID serviceUuid) {
        SERVICE_UUID = serviceUuid;
    }

    public static UUID getCharacteristicRefillUuid() {
        return CHARACTERISTIC_REFILL_UUID;
    }

    public static void setCharacteristicRefillUuid(UUID characteristicRefillUuid) {
        CHARACTERISTIC_REFILL_UUID = characteristicRefillUuid;
    }

    public static UUID getCharacteristicInteractorUuid() {
        return CHARACTERISTIC_INTERACTOR_UUID;
    }

    public static void setCharacteristicInteractorUuid(UUID characteristicInteractorUuid) {
        CHARACTERISTIC_INTERACTOR_UUID = characteristicInteractorUuid;
    }

    public static byte[] getUserDescription(UUID characteristicUUID) {
        String desc;

        if (CHARACTERISTIC_REFILL_UUID.equals(characteristicUUID)) {
            desc = "Refill status of this coaster";
        } else if (CHARACTERISTIC_INTERACTOR_UUID.equals(characteristicUUID)) {
            desc = "Write any value here -- PLACEHOLDER";
        } else {
            desc = "";
        }

        return desc.getBytes(Charset.forName("UTF-8"));
    }
}
