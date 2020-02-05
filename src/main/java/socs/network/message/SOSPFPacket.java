package socs.network.message;

import java.io.*;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class SOSPFPacket implements Serializable {

  //for inter-process communication
  public String srcProcessIP;
  public short srcProcessPort;

  //simulated IP address
  public String srcIP;
  public String dstIP;

  //common header
  public short sospfType; //0 - HELLO, 1 - LinkState Update, 2 - Connection refuse
  public String routerID;

  //used by HELLO message to identify the sender of the message
  //e.g. when router A sends HELLO to its neighbor, it has to fill this field with its own
  //simulated IP address
  public String neighborID; //neighbor's simulated IP address

  //used by LSAUPDATE
  public Vector<LSA> lsaArray = null;

  public SOSPFPacket()
  {

  }

  public SOSPFPacket(SOSPFPacket packet)
  {
    srcIP = packet.srcIP;
    srcProcessPort = packet.srcProcessPort;
    srcProcessIP = packet.srcProcessIP;
    dstIP = packet.dstIP;
    sospfType = packet.sospfType;
    routerID = packet.routerID;
    neighborID = packet.neighborID;

    //TODO migh need to add lsaArray to later steps
  }
}
