package de.bangle_bridge.bangle_bridge;
/**  Interface for BLE  serial connection and serial read with their correspondent error listeners.
 * @author Jorge
 * @version 1.5
 * @since 1.0
 */
interface BtListener {
    void onSerialConnect      ();
    void onSerialConnectError (Exception e);
    void onSerialRead         (byte[] data);
    void onSerialIoError      (Exception e);
}
