/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.2.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.openani.anitorrent.binding;

public final class torrent_state_t {
  public final static torrent_state_t unused_enum_for_backwards_compatibility = new torrent_state_t("unused_enum_for_backwards_compatibility");
  public final static torrent_state_t checking_files = new torrent_state_t("checking_files");
  public final static torrent_state_t downloading_metadata = new torrent_state_t("downloading_metadata");
  public final static torrent_state_t downloading = new torrent_state_t("downloading");
  public final static torrent_state_t finished = new torrent_state_t("finished");
  public final static torrent_state_t seeding = new torrent_state_t("seeding");
  public final static torrent_state_t unused_enum_for_backwards_compatibility_allocating = new torrent_state_t("unused_enum_for_backwards_compatibility_allocating");
  public final static torrent_state_t checking_resume_data = new torrent_state_t("checking_resume_data");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static torrent_state_t swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + torrent_state_t.class + " with value " + swigValue);
  }

  private torrent_state_t(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private torrent_state_t(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private torrent_state_t(String swigName, torrent_state_t swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static torrent_state_t[] swigValues = { unused_enum_for_backwards_compatibility, checking_files, downloading_metadata, downloading, finished, seeding, unused_enum_for_backwards_compatibility_allocating, checking_resume_data };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

