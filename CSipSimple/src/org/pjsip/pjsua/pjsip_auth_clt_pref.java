/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public class pjsip_auth_clt_pref {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public pjsip_auth_clt_pref(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(pjsip_auth_clt_pref obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsuaJNI.delete_pjsip_auth_clt_pref(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setInitial_auth(int value) {
    pjsuaJNI.pjsip_auth_clt_pref_initial_auth_set(swigCPtr, this, value);
  }

  public int getInitial_auth() {
    return pjsuaJNI.pjsip_auth_clt_pref_initial_auth_get(swigCPtr, this);
  }

  public void setAlgorithm(pj_str_t value) {
    pjsuaJNI.pjsip_auth_clt_pref_algorithm_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getAlgorithm() {
    long cPtr = pjsuaJNI.pjsip_auth_clt_pref_algorithm_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public pjsip_auth_clt_pref() {
    this(pjsuaJNI.new_pjsip_auth_clt_pref(), true);
  }

}
