//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.21 at 03:37:18 PM CEST 
//

package gcmserver.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Device" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="RegistrationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "deviceList" })
@XmlRootElement(name = "Devices")
public class Devices {

	@XmlElement(name = "Device")
	protected List<Devices.Device> deviceList;

	/**
	 * Gets the value of the device property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the device property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDevice().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Devices.Device }
	 * 
	 * 
	 */
	public List<Devices.Device> getDeviceList() {
		if (deviceList == null) {
			deviceList = new ArrayList<Devices.Device>();
		}
		return this.deviceList;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="RegistrationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "name", "registrationId" })
	public static class Device {

		@XmlElement(name = "Name", required = true)
		protected String name;
		@XmlElement(name = "RegistrationId", required = true)
		protected String registrationId;

		public Device() {

		}

		/**
		 * 
		 * @param device
		 */
		public Device(Device device) {
			this.name = new String(device.getName());
			this.registrationId = new String(device.getRegistrationId());
		}

		/**
		 * @param name
		 * @param registrationId
		 */
		public Device(String name, String registrationId) {
			this.name = new String(name);
			this.registrationId = new String(registrationId);
		}

		/**
		 * Gets the value of the name property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the value of the name property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setName(String value) {
			this.name = value;
		}

		/**
		 * Gets the value of the registrationId property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getRegistrationId() {
			return registrationId;
		}

		/**
		 * Sets the value of the registrationId property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setRegistrationId(String value) {
			this.registrationId = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object objeto) {
			// If the registrationId is the same, then it should be the same
			// device
			return this.registrationId.equals(((Device) objeto)
					.getRegistrationId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("Device[");
			if (name != null) {
				builder.append(" name=").append(name);
			}
			if (registrationId != null) {
				builder.append(" registrationId=").append(registrationId);
			}

			builder.append(" ]");
			return builder.toString();
		}

	}

}