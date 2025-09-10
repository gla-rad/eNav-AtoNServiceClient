package org.grad.eNav.atonServiceClient.utils;

import _int.iho.s_125.gml.cs0._1.*;

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
        if(Objects.equals(s125Class, CardinalBeacon.class)) {
            return "beacon_cardinal";
        }
        else if(Objects.equals(s125Class, LateralBeacon.class)) {
            return "beacon_lateral";
        }
        else if(Objects.equals(s125Class, IsolatedDangerBeacon.class)) {
            return "beacon_isolated_danger";
        }
        else if(Objects.equals(s125Class, SafeWaterBeacon.class)) {
            return "beacon_safe_water";
        }
        else if(Objects.equals(s125Class, SpecialPurposeGeneralBeacon.class)) {
            return "beacon_special_purpose";
        }
        else if(Objects.equals(s125Class, CardinalBuoy.class)) {
            return "buoy_cardinal";
        }
        else if(Objects.equals(s125Class, LateralBuoy.class)) {
            return "buoy_lateral";
        }
        else if(Objects.equals(s125Class, InstallationBuoy.class)) {
            return "buoy_installation";
        }
        else if(Objects.equals(s125Class, IsolatedDangerBuoy.class)) {
            return "buoy_isolated_danger";
        }
        else if(Objects.equals(s125Class, SafeWaterBuoy.class)) {
            return "buoy_safe_water";
        }
        else if(Objects.equals(s125Class, SpecialPurposeGeneralBuoy.class)) {
            return "buoy_special_purpose";
        }
        else if(Objects.equals(s125Class, Lighthouse.class)) {
            return "light_major";
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
