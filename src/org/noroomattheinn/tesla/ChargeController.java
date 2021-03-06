/*
 * ChargeController.java - Copyright(c) 2013 Joe Pasqua
 * Provided under the MIT License. See the LICENSE file for details.
 * Created: Jul 5, 2013
 */

package org.noroomattheinn.tesla;

/**
 * ChargeController: Control charging parameters and start/stop charging.
 *
 * @author Joe Pasqua <joe at NoRoomAtTheInn dot org>
 */

public class ChargeController extends APICall {
    // Instance Variables - These are effectively constants
    private final String startCommand, stopCommand;
    private final String maxRangeCommand, stdRangeCommand;
    private final String chargePercentFormat;
    
    //
    // Constructors
    //
    
    public ChargeController(Vehicle v) {
        super(v);
        startCommand = Tesla.command(v.getVID(), "charge_start");
        stopCommand = Tesla.command(v.getVID(), "charge_stop");
        maxRangeCommand = Tesla.command(v.getVID(), "charge_max_range");
        stdRangeCommand = Tesla.command(v.getVID(), "charge_standard");
        chargePercentFormat = Tesla.command(v.getVID(), "set_charge_limit?percent=%d");
    }

    
    //
    // Action Methods
    //
    
    public Result setChargeState(boolean charging) {
        setAndRefresh(charging? startCommand : stopCommand);
        return new Result(this);
    }
    
    public Result startCharing() { return setChargeState(true); }

    public Result stopCharing() { return setChargeState(false); }
    
    public Result setChargeRange(boolean max) {
        setAndRefresh(max ? maxRangeCommand : stdRangeCommand);
        return new Result(this);
    }
    
    public Result setChargePercent(int percent) {
        if (percent < 1 || percent > 100)
            return new Result(false, "value out of range");
        setAndRefresh(String.format(chargePercentFormat, percent));
        return new Result(this);
    }
}
