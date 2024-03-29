/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public enum pjsua_state {
  PJSUA_STATE_NULL,
  PJSUA_STATE_CREATED,
  PJSUA_STATE_INIT,
  PJSUA_STATE_STARTING,
  PJSUA_STATE_RUNNING,
  PJSUA_STATE_CLOSING;

  public final int swigValue() {
    return swigValue;
  }

  public static pjsua_state swigToEnum(int swigValue) {
    pjsua_state[] swigValues = pjsua_state.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (pjsua_state swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + pjsua_state.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private pjsua_state() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private pjsua_state(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private pjsua_state(pjsua_state swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

