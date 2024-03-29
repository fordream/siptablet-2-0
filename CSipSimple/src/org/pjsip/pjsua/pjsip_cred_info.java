/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.pjsip.pjsua;

public class pjsip_cred_info {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  public pjsip_cred_info(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(pjsip_cred_info obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        pjsuaJNI.delete_pjsip_cred_info(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setRealm(pj_str_t value) {
    pjsuaJNI.pjsip_cred_info_realm_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getRealm() {
    long cPtr = pjsuaJNI.pjsip_cred_info_realm_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setScheme(pj_str_t value) {
    pjsuaJNI.pjsip_cred_info_scheme_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getScheme() {
    long cPtr = pjsuaJNI.pjsip_cred_info_scheme_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setUsername(pj_str_t value) {
    pjsuaJNI.pjsip_cred_info_username_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getUsername() {
    long cPtr = pjsuaJNI.pjsip_cred_info_username_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public void setData_type(int value) {
    pjsuaJNI.pjsip_cred_info_data_type_set(swigCPtr, this, value);
  }

  public int getData_type() {
    return pjsuaJNI.pjsip_cred_info_data_type_get(swigCPtr, this);
  }

  public void setData(pj_str_t value) {
    pjsuaJNI.pjsip_cred_info_data_set(swigCPtr, this, pj_str_t.getCPtr(value), value);
  }

  public pj_str_t getData() {
    long cPtr = pjsuaJNI.pjsip_cred_info_data_get(swigCPtr, this);
    return (cPtr == 0) ? null : new pj_str_t(cPtr, false);
  }

  public pjsip_cred_info() {
    this(pjsuaJNI.new_pjsip_cred_info(), true);
  }

}
