/**
 * Autogenerated by Thrift Compiler (1.0.0-dev)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.fxmind.global;

import org.apache.thrift.EncodingUtils;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

//import javax.annotation.processing.Generated;
import javax.annotation.Generated;
import java.util.*;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (1.0.0-dev)", date = "2014-5-29")
public class TechIndicator implements org.apache.thrift.TBase<TechIndicator, TechIndicator._Fields>, java.io.Serializable, Cloneable, Comparable<TechIndicator> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TechIndicator");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("ID", org.apache.thrift.protocol.TType.I16, (short)1);
  private static final org.apache.thrift.protocol.TField INDICATOR_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("IndicatorName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField ENABLED_FIELD_DESC = new org.apache.thrift.protocol.TField("Enabled", org.apache.thrift.protocol.TType.BOOL, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TechIndicatorStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TechIndicatorTupleSchemeFactory());
  }

  public short ID; // required
  public String IndicatorName; // required
  public boolean Enabled; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "ID"),
    INDICATOR_NAME((short)2, "IndicatorName"),
    ENABLED((short)3, "Enabled");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ID
          return ID;
        case 2: // INDICATOR_NAME
          return INDICATOR_NAME;
        case 3: // ENABLED
          return ENABLED;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __ID_ISSET_ID = 0;
  private static final int __ENABLED_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("ID", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I16)));
    tmpMap.put(_Fields.INDICATOR_NAME, new org.apache.thrift.meta_data.FieldMetaData("IndicatorName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ENABLED, new org.apache.thrift.meta_data.FieldMetaData("Enabled", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TechIndicator.class, metaDataMap);
  }

  public TechIndicator() {
  }

  public TechIndicator(
    short ID,
    String IndicatorName,
    boolean Enabled)
  {
    this();
    this.ID = ID;
    setIDIsSet(true);
    this.IndicatorName = IndicatorName;
    this.Enabled = Enabled;
    setEnabledIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TechIndicator(TechIndicator other) {
    __isset_bitfield = other.__isset_bitfield;
    this.ID = other.ID;
    if (other.isSetIndicatorName()) {
      this.IndicatorName = other.IndicatorName;
    }
    this.Enabled = other.Enabled;
  }

  public TechIndicator deepCopy() {
    return new TechIndicator(this);
  }

  @Override
  public void clear() {
    setIDIsSet(false);
    this.ID = 0;
    this.IndicatorName = null;
    setEnabledIsSet(false);
    this.Enabled = false;
  }

  public short getID() {
    return this.ID;
  }

  public TechIndicator setID(short ID) {
    this.ID = ID;
    setIDIsSet(true);
    return this;
  }

  public void unsetID() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ID_ISSET_ID);
  }

  /** Returns true if field ID is set (has been assigned a value) and false otherwise */
  public boolean isSetID() {
    return EncodingUtils.testBit(__isset_bitfield, __ID_ISSET_ID);
  }

  public void setIDIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ID_ISSET_ID, value);
  }

  public String getIndicatorName() {
    return this.IndicatorName;
  }

  public TechIndicator setIndicatorName(String IndicatorName) {
    this.IndicatorName = IndicatorName;
    return this;
  }

  public void unsetIndicatorName() {
    this.IndicatorName = null;
  }

  /** Returns true if field IndicatorName is set (has been assigned a value) and false otherwise */
  public boolean isSetIndicatorName() {
    return this.IndicatorName != null;
  }

  public void setIndicatorNameIsSet(boolean value) {
    if (!value) {
      this.IndicatorName = null;
    }
  }

  public boolean isEnabled() {
    return this.Enabled;
  }

  public TechIndicator setEnabled(boolean Enabled) {
    this.Enabled = Enabled;
    setEnabledIsSet(true);
    return this;
  }

  public void unsetEnabled() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ENABLED_ISSET_ID);
  }

  /** Returns true if field Enabled is set (has been assigned a value) and false otherwise */
  public boolean isSetEnabled() {
    return EncodingUtils.testBit(__isset_bitfield, __ENABLED_ISSET_ID);
  }

  public void setEnabledIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ENABLED_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetID();
      } else {
        setID((Short)value);
      }
      break;

    case INDICATOR_NAME:
      if (value == null) {
        unsetIndicatorName();
      } else {
        setIndicatorName((String)value);
      }
      break;

    case ENABLED:
      if (value == null) {
        unsetEnabled();
      } else {
        setEnabled((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return Short.valueOf(getID());

    case INDICATOR_NAME:
      return getIndicatorName();

    case ENABLED:
      return Boolean.valueOf(isEnabled());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetID();
    case INDICATOR_NAME:
      return isSetIndicatorName();
    case ENABLED:
      return isSetEnabled();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TechIndicator)
      return this.equals((TechIndicator)that);
    return false;
  }

  public boolean equals(TechIndicator that) {
    if (that == null)
      return false;

    boolean this_present_ID = true;
    boolean that_present_ID = true;
    if (this_present_ID || that_present_ID) {
      if (!(this_present_ID && that_present_ID))
        return false;
      if (this.ID != that.ID)
        return false;
    }

    boolean this_present_IndicatorName = true && this.isSetIndicatorName();
    boolean that_present_IndicatorName = true && that.isSetIndicatorName();
    if (this_present_IndicatorName || that_present_IndicatorName) {
      if (!(this_present_IndicatorName && that_present_IndicatorName))
        return false;
      if (!this.IndicatorName.equals(that.IndicatorName))
        return false;
    }

    boolean this_present_Enabled = true;
    boolean that_present_Enabled = true;
    if (this_present_Enabled || that_present_Enabled) {
      if (!(this_present_Enabled && that_present_Enabled))
        return false;
      if (this.Enabled != that.Enabled)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_ID = true;
    list.add(present_ID);
    if (present_ID)
      list.add(ID);

    boolean present_IndicatorName = true && (isSetIndicatorName());
    list.add(present_IndicatorName);
    if (present_IndicatorName)
      list.add(IndicatorName);

    boolean present_Enabled = true;
    list.add(present_Enabled);
    if (present_Enabled)
      list.add(Enabled);

    return list.hashCode();
  }

  @Override
  public int compareTo(TechIndicator other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetID()).compareTo(other.isSetID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ID, other.ID);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIndicatorName()).compareTo(other.isSetIndicatorName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndicatorName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.IndicatorName, other.IndicatorName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEnabled()).compareTo(other.isSetEnabled());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEnabled()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.Enabled, other.Enabled);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TechIndicator(");
    boolean first = true;

    sb.append("ID:");
    sb.append(this.ID);
    first = false;
    if (!first) sb.append(", ");
    sb.append("IndicatorName:");
    if (this.IndicatorName == null) {
      sb.append("null");
    } else {
      sb.append(this.IndicatorName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("Enabled:");
    sb.append(this.Enabled);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TechIndicatorStandardSchemeFactory implements SchemeFactory {
    public TechIndicatorStandardScheme getScheme() {
      return new TechIndicatorStandardScheme();
    }
  }

  private static class TechIndicatorStandardScheme extends StandardScheme<TechIndicator> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TechIndicator struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I16) {
              struct.ID = iprot.readI16();
              struct.setIDIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // INDICATOR_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.IndicatorName = iprot.readString();
              struct.setIndicatorNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // ENABLED
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.Enabled = iprot.readBool();
              struct.setEnabledIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TechIndicator struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID_FIELD_DESC);
      oprot.writeI16(struct.ID);
      oprot.writeFieldEnd();
      if (struct.IndicatorName != null) {
        oprot.writeFieldBegin(INDICATOR_NAME_FIELD_DESC);
        oprot.writeString(struct.IndicatorName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(ENABLED_FIELD_DESC);
      oprot.writeBool(struct.Enabled);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TechIndicatorTupleSchemeFactory implements SchemeFactory {
    public TechIndicatorTupleScheme getScheme() {
      return new TechIndicatorTupleScheme();
    }
  }

  private static class TechIndicatorTupleScheme extends TupleScheme<TechIndicator> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TechIndicator struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetID()) {
        optionals.set(0);
      }
      if (struct.isSetIndicatorName()) {
        optionals.set(1);
      }
      if (struct.isSetEnabled()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetID()) {
        oprot.writeI16(struct.ID);
      }
      if (struct.isSetIndicatorName()) {
        oprot.writeString(struct.IndicatorName);
      }
      if (struct.isSetEnabled()) {
        oprot.writeBool(struct.Enabled);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TechIndicator struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.ID = iprot.readI16();
        struct.setIDIsSet(true);
      }
      if (incoming.get(1)) {
        struct.IndicatorName = iprot.readString();
        struct.setIndicatorNameIsSet(true);
      }
      if (incoming.get(2)) {
        struct.Enabled = iprot.readBool();
        struct.setEnabledIsSet(true);
      }
    }
  }

}

