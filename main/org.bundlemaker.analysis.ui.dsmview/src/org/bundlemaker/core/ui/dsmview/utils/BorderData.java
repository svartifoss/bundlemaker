package org.bundlemaker.core.ui.dsmview.utils;

public class BorderData {

  private String _msg;

  private BorderData(String msg) {
    _msg = msg;
  }

  public static final BorderData NORTH  = new BorderData("NORTH");

  public static final BorderData SOUTH  = new BorderData("SOUTH");

  public static final BorderData EAST   = new BorderData("EAST");

  public static final BorderData WEST   = new BorderData("WEST");

  public static final BorderData CENTER = new BorderData("CENTER");

}