/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public enum pjsip_cred_data_type {
  PJSIP_CRED_DATA_PLAIN_PASSWD(pjsuaJNI.PJSIP_CRED_DATA_PLAIN_PASSWD_get()),
  PJSIP_CRED_DATA_DIGEST(pjsuaJNI.PJSIP_CRED_DATA_DIGEST_get()),
  PJSIP_CRED_DATA_EXT_AKA(pjsuaJNI.PJSIP_CRED_DATA_EXT_AKA_get());

  public final int swigValue() {
    return swigValue;
  }

  public static pjsip_cred_data_type swigToEnum(int swigValue) {
    pjsip_cred_data_type[] swigValues = pjsip_cred_data_type.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (pjsip_cred_data_type swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + pjsip_cred_data_type.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private pjsip_cred_data_type() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private pjsip_cred_data_type(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private pjsip_cred_data_type(pjsip_cred_data_type swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

