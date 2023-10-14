//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.libs.pidgin.packet.handler;

public class PacketExceptionHandler {
    public PacketExceptionHandler() {
    }

    public void onException(Exception e) {
        System.out.println("Failed to send packet");
        e.printStackTrace();
    }
}
