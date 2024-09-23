package org.grad.eNav.atonServiceClient.utils;

import _int.iho.s125.gml.cs0._1.*;

import java.util.Objects;

public class AtonTypeConverter {

    /**
     * A simple switch converter to translate between the S125 AtoN type names
     * and the seamark definitions used by Open-SeaMarks and Niord.
     *
     * @param s125Class the S-125 AtoN types
     * @return the Open-SeaMarks types
     */
    public static String convertToSeamarkType(Class<?> s125Class) {
        if(Objects.equals(s125Class, BeaconCardinal.class)) {
            return "beacon_cardinal";
        }
        else if(Objects.equals(s125Class, BeaconLateral.class)) {
            return "beacon_lateral";
        }
        else if(Objects.equals(s125Class, BeaconIsolatedDanger.class)) {
            return "beacon_isolated_danger";
        }
        else if(Objects.equals(s125Class, BeaconSafeWater.class)) {
            return "beacon_safe_water";
        }
        else if(Objects.equals(s125Class, BeaconSpecialPurposeGeneral.class)) {
            return "beacon_special_purpose";
        }
        else if(Objects.equals(s125Class, BuoyCardinal.class)) {
            return "cardinal_buoy";
        }
        else if(Objects.equals(s125Class, BuoyLateral.class)) {
            return "buoy_lateral";
        }
        else if(Objects.equals(s125Class, BuoyInstallation.class)) {
            return "buoy_installation";
        }
        else if(Objects.equals(s125Class, BuoyIsolatedDanger.class)) {
            return "buoy_isolated_danger";
        }
        else if(Objects.equals(s125Class, BuoySafeWater.class)) {
            return "buoy_safe_water";
        }
        else if(Objects.equals(s125Class, BuoySpecialPurposeGeneral.class)) {
            return "buoy_special_purpose";
        }
        else if(Objects.equals(s125Class, LandmarkType.class)) {
            return "landmark";
        }
        else if(Objects.equals(s125Class, LightVessel.class)) {
            return "light_vessel";
        }
        else if(Objects.equals(s125Class, VirtualAISAidToNavigation.class)) {
            return "virtual_aton";
        } else {
            return "";
        }
    }
}
