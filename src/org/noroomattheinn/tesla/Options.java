/*
 * Options.java - Copyright(c) 2013 Joe Pasqua
 * Provided under the MIT License. See the LICENSE file for details.
 * Created: Jul 5, 2013
 */

package org.noroomattheinn.tesla;

import org.noroomattheinn.utils.Utils;
import java.util.HashMap;
import java.util.Map;

/**
 * Options: This class parses and contains the many options made available in
 * the Tesla vehicle description. Many of the options are represented by
 * enum's of the known types. All of those enum's have an "Unknown" instance
 * to handle unexpected or new option types. For example, if Tesla adds a new
 * paint color, it will be reported as Unknown until the code is updated.
 *
 * @author Joe Pasqua <joe at NoRoomAtTheInn dot org>
 */

public class Options {
    // Instance Variables
    private Map<String,String> optionsFound;
    
    //
    // Constructors
    //
    
    public Options(String optionsString) {
        optionsFound = new HashMap<>();

        if (optionsString == null) {
            return;
        }

        // Deal with the one 3 letter prefix in the options: PBT. Turn this into BT
        optionsString = optionsString.replace("PBT", "BT");
        
        String[] tokens = optionsString.split(",");
        for (String token : tokens) {
            String prefix = token.substring(0,2);
            // X0 options are handled differently. Speculation is that these are
            // the old way Tesla handled things. In this case we store the whole
            // token as the key and the value.
            if (prefix.equals("X0"))
                prefix = token;
            optionsFound.put(prefix, token);
        }
    }
    
    //
    // Option Handling Methods
    //

    private boolean hasOption(String optionName) {
        String option = optionsFound.get(optionName);
        if (option == null)
            return false;
        
        // 'X' options are either there or not, they aren't a prefix
        // followed by 00 or 01. Treat them specially.
        if (optionName.startsWith("X"))
            return true;
        return option.equals(optionName + "01");
    }
        
    /*
     * Returns a member of an Enumeration corresponding to a particular option.
     * found in the optionsFound Map.
     * <P>
     * For example, lets say we want to create an
     * instance of the Region enum that corresponds to whatever Region was
     * actually returned by the REST API. We'd pass in Region as the Type
     * parameter and "RE" as the option name (All regions start with RE).
     * <p>
     * This code will look up "RE" in the optionsFound Map and find the actual
     * option code that was returned for the "RE" prefix. Let's say that it is
     * "RENA". The code will then instantiate a Region instance with the value
     * corresponding to the String "RENA". If an error occurs during instantiation,
     * an Unknown value is returned.
     * <P>
     * Note that all Enum types used with this method must have an "Unknown"
     * instance for this to work.
     * <P>
     * This code is a little weird because it is both parameterized by the Enum
     * Type and the class is alo passed in. This is required to handle some
     * oddities of dealing with Generic Enums.
     * 
     * @param eClass    The class of the Enum for which we want an instance
     * @param prefix    A variable length list of one or more prefixes
     *                  that we'll look for in turn. Some options (like 
     *                  PaintColor) use multiple prefixes to encode them
     *                  so we need to look through all of them.
     * @return          An instance of the specified Enum type corresponding
     *                  to the specified prefix
     */
    private <T extends Enum<T>> T optionToEnum(Class<T> eClass, String... prefix) {
        String option = null;
        for (String p : prefix) {
            if ( (option = optionsFound.get(p)) != null)
                break;
        }
        return Utils.stringToEnum(eClass, option);
    }

    
    //
    // Field Accessor Methods
    //
    
    public Region region() { return optionToEnum(Region.class, "RE"); }
    public TrimLevel trimLevel() { return optionToEnum(TrimLevel.class, "TM"); }
    public DriveSide driveSide() { return optionToEnum(DriveSide.class, "DR"); }
    public BatteryType batteryType() { return optionToEnum(BatteryType.class, "BT"); }
    public RoofType roofType() { return optionToEnum(RoofType.class, "RF"); }
    public WheelType wheelType() { return optionToEnum(WheelType.class, "WT"); }
    public DecorType decorType() { return optionToEnum(DecorType.class, "ID"); }
    public AdapterType adapterType() { return optionToEnum(AdapterType.class, "AD"); }
    public PaintColor paintColor() { 
        return optionToEnum(PaintColor.class, "PB", "PM", "PP"); }
    public SeatType seatType() {
        return optionToEnum(SeatType.class, "IB", "IP", "IZ", "IS"); }


    public boolean isPerformance() { return hasOption("PF"); }
    public boolean isPerfPlus() { return hasOption("PX") || wheelType() == WheelType.WTSG; }
    public boolean hasThirdRow() { return hasOption("TR"); }
    public boolean hasAirSuspension() { return hasOption("SU"); }
    public boolean hasSupercharger() { return hasOption("SC"); }    
    public boolean hasTechPackage() { return hasOption("TP"); }
    public boolean hasAudioUpgrade() { return hasOption("AU"); }
    public boolean hasTwinCharger() { return hasOption("CH"); }
    public boolean hasHPWC() { return hasOption("HP"); }
    public boolean hasPaintArmor() { return hasOption("PA"); }
    public boolean hasParcelShelf() { return hasOption("PS"); }
    public boolean hasPowerLiftgate() { return hasOption("X001"); }
    public boolean hasNavSystem() { return hasOption("X003"); }
    public boolean hasPremiumLighting() { return hasOption("X007"); }
    public boolean hasHomeLink() { return hasOption("X011"); }
    public boolean hasSatRadio() { return hasOption("X013"); }
    public boolean hasPerfExterior() { return hasOption("X019"); }
    public boolean hasPerfPowertrain() { return hasOption("X024"); }
    // Speculative at this point
    public boolean hasParkingSensors() { return hasOption("PK"); }
    public boolean hasLightingPackage() { return hasOption("LP"); }
    public boolean hasSecurityPackage() { return hasOption("SP"); }
    public boolean hasColdWeather() { return hasOption("CW"); }
    
    //
    // Override Methods
    //
    
    @Override
    public String toString() {
        return String.format(
                "    Region: %s\n" +
                "    Trim: %s\n" +
                "    Drive Side: %s\n" +
                "    Performance Options: [\n" +
                "       Performance: %b\n" +
                "       Performance+: %b\n" +
                "       Performance Exterior: %b\n" +
                "       Performance Powertrain: %b\n" +
                "    ]\n" +
                "    Battery: %s\n" +
                "    Color: %s\n" +
                "    Roof: %s\n" +
                "    Wheels: %s\n" +
                "    Seats: %s\n" +
                "    Decor: %s\n" +
                "    Air Suspension: %b\n" +
                "    Tech Upgrades: [\n" +
                "        Tech Package: %b\n" +
                "        Power Liftgate: %b\n" +
                "        Premium Lighting: %b\n" +
                "        HomeLink: %b\n" +
                "        Navigation: %b\n" +
                "    ]\n" +
                "    Audio: [\n" +
                "        Upgraded: %b\n" +
                "        Sat Radio: %b\n" +
                "    ]\n" +
                "    Charging: [\n" +
                "        Supercharger: %b\n" +
                "        Twin Chargers: %b\n" +
                "        HPWC: %b\n" +
                "    ]\n" +
                "    Options: [\n" +
                "        Parcel Shelf: %b\n" +
                "        Paint Armor: %b\n" +
                "        Third Row Seating: %b\n" +
                "    ]\n" +
                "    Newer Options: [\n" +
                "        Parking Sensors: %b\n" +
                "        Lighting Package: %b\n" + 
                "        Security Package: %b\n" +
                "        Cold Weather Package: %b\n" +
                "    ]\n",
                region(), trimLevel(), driveSide(),
                isPerformance(), isPerfPlus(),
                hasPerfExterior(), hasPerfPowertrain(),
                batteryType(), paintColor(), roofType(), wheelType(),
                seatType(), decorType(), hasAirSuspension(),
                hasTechPackage(), hasPowerLiftgate(), hasPremiumLighting(),
                hasHomeLink(), hasNavSystem(),
                hasAudioUpgrade(), hasSatRadio(),
                hasSupercharger(), hasTwinCharger(), hasHPWC(),
                hasParcelShelf(), hasPaintArmor(), hasThirdRow(),
                hasParkingSensors(), hasLightingPackage(), hasSecurityPackage(),
                hasColdWeather());
    }

    //
    // Nested Classes - Enums for the various options
    //
    
    public enum WheelType {
        WT1P("Silver 19\""),
        WTX1("Silver 19\""),
        WT19("Silver 19\""),
        WT21("Silver 21\""),
        WTSP("Gray 21\""),
        WTSG("Gray Perf+ 21\""),
        WTAE("Aero 19\""),
        WTTB("Cyclone 19\""),
        Unknown("Unknown");
        
        private String descriptiveName;

        WheelType(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }
    }

    public enum TrimLevel  {
        TM00("Standard Production Trim"),
        TM02("Signature Performance Trim"),
        Unknown("Unknown");

        private String descriptiveName;

        TrimLevel(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }

    }

    public enum SeatType {
        IBMB("Base Textile, Black"),
        IPMB("Leather, Black"),
        IPMG("Leather, Gray"),
        IPMT("Leather, Tan"),
        IZZW("Perf Leather with Grey Piping, White"),
        QZMB("Perf Leather with Piping, Black"),
        IZMB("Perf Leather with Piping, Black"),
        IZMG("Perf Leather with Piping, Gray"),
        IZMT("Perf Leather with Piping, Tan"),
        ISZW("Signature Perforated Leather, White"),
        ISZT("Signature Perforated Leather, Tan"),
        ISZB("Signature Perforated Leather, Black"),
        Unknown("Unknown");

        private String descriptiveName;

        SeatType(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }

    }

    public enum RoofType {
        RFBC("Body Color"),
        RFPO("Panoramic"),
        RFBK("Black"),
        Unknown("Unknown");

        private String descriptiveName;

        RoofType(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }

    }

    public enum Region {
        RENA("United States"),
        RENC("Canada"),
        REEU("Europe"),
        Unknown("Unknown");

        private String descriptiveName;

        Region(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }
    }

    public enum PaintColor {
        PBSB("Black"),
        PBCW("Solid White"),
        PMSS("Silver"),
        PMTG("Metallic Dolphin Gray"),
        PMAB("Metallic Brown"),
        PMMB("Metallic Blue"),
        PMSG("Metallic Green"),
        PPSW("Pearl White"),
        PPMR("Premium Multicoat Red"),
        PPSR("Premium Signature Red"),
        Unknown("Unknown");

        private String descriptiveName;

        PaintColor(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }

    }
    
    public enum DriveSide  {
        DRLH("Left Hand"),
        DRRH("Right Hand"),
        Unknown("Unknown");

        private String descriptiveName;

        DriveSide(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }

    }

    public enum DecorType {
        IDCF("Carbon Fiber"),
        IDLW("Lacewood"),
        IDOM("Obeche Matte"),
        IDOG("Obeche Gloss"),
        IDPB("Piano Black"),
        Unknown("Unknown");

        private String descriptiveName;

        DecorType(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }

    }
    
    public enum BatteryType  {
        BT85("85kWh"),
        BT60("60kWh"),
        BT40("40kWh (Software Limited)"),
        Unknown("Unknown");

        private String descriptiveName;

        BatteryType(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }

    }

    public enum AdapterType  {
        AD02("NEMA 14-50"),
        Unknown("Unknown");

        private String descriptiveName;

        AdapterType(String name) { this.descriptiveName = name; }

        @Override
        public String toString() { return descriptiveName; }

    }

}
    