/*
 * LocationHandler.java - Copyright(c) 2013 Joe Pasqua
 * Provided under the MIT License. See the LICENSE file for details.
 * Created: Jul 6, 2013
 */

package org.noroomattheinn.tesla.test;

import org.noroomattheinn.utils.Handler;
import java.util.Date;
import org.noroomattheinn.tesla.DrivingState;
import org.noroomattheinn.tesla.Vehicle;
import org.noroomattheinn.utils.BrowserUtils;
import org.noroomattheinn.utils.GeoUtils;

/**
 * LocationHandler
 *
 * @author Joe Pasqua <joe at NoRoomAtTheInn dot org>
 */

public class LocationHandler extends TeslaHandler  {
    // Private Constants
    private static final String Description = "View vehicle location";
    private static final String Name = "location";
    private static final String Alias = "loc";
    
    // Private Instance Variables
    private DrivingState driving;

    
    //
    // Constructors
    //
    
    LocationHandler(Vehicle v) {
        super(Name, Description, Alias, v);
        driving = new DrivingState(v);
        repl.addHandler(new LocationHandler.DisplayHandler());
        repl.addHandler(new LocationHandler.MapHandler());
    }
    
    
    //
    // Handler classes
    //
    
    class DisplayHandler extends Handler {
        DisplayHandler() { super("display", "Display Location State", "d"); }
        @Override public boolean execute() {
            System.out.print("Getting updated location information...");
            if (!driving.refresh()) {
                System.err.println("Problem communicating with Tesla");
                return true;
            }
            System.out.println("done");
            System.out.print("Consulting Google to get an address...");
            String address = GeoUtils.getAddrForLatLong(
                    String.valueOf(driving.state.latitude),
                    String.valueOf(driving.state.longitude));
            System.out.println("done");
            System.out.println("The vehicle is near " + address);
            System.out.println("It is facing " + GeoUtils.headingToString(driving.state.heading));
            System.out.println("It was last located at " + 
                    new Date(((long)driving.state.gpsAsOf*1000)));
            return true;
        }
    }

    class MapHandler extends Handler {
        MapHandler() { super("map", "Display a Google map", "m"); }
        @Override public boolean execute() {
            System.out.print("Getting updated location information...");
            if (!driving.refresh()) {
                System.err.println("Problem communicating with Tesla");
                return true;
            }
            System.out.println("done");
            String url = String.format(
                    "https://maps.google.com/maps?q=%f,%f(Tesla)&z=18&output=embed",
                    driving.state.latitude, driving.state.longitude);
            BrowserUtils.popupWithURL(url);
            return true;
        }
    }
 
}
