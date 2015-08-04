//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantaci�n de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perder�n si se vuelve a compilar el esquema de origen. 
// Generado el: 2015.08.01 a las 07:06:23 PM CEST 
//

package gcmserver.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Clase Java para anonymous complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Group" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RegistrationId" maxOccurs="20" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="notificationKey" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="notificationKeyName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlType(name = "", propOrder = { "groupList" })
@XmlRootElement(name = "Groups")
public class Groups {

	@XmlElement(name = "Group")
	protected List<Groups.Group> groupList;

	/**
	 * Gets the value of the group property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the group property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getGroup().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Groups.Group }
	 * 
	 * 
	 */
	public List<Groups.Group> getGroupList() {
		if (groupList == null) {
			groupList = new ArrayList<Groups.Group>();
		}
		return this.groupList;
	}

	/**
	 * <p>
	 * Clase Java para anonymous complex type.
	 * 
	 * <p>
	 * El siguiente fragmento de esquema especifica el contenido que se espera
	 * que haya en esta clase.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="RegistrationId" maxOccurs="20" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;simpleContent>
	 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
	 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *               &lt;/extension>
	 *             &lt;/simpleContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/sequence>
	 *       &lt;attribute name="notificationKey" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *       &lt;attribute name="notificationKeyName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "registrationIdList" })
	public static class Group {

		@XmlElement(name = "RegistrationId")
		protected List<Groups.Group.RegistrationId> registrationIdList;
		@XmlAttribute(name = "notificationKey", required = true)
		protected String notificationKey;
		@XmlAttribute(name = "notificationKeyName", required = true)
		protected String notificationKeyName;

		/**
		 * Gets the value of the registrationId property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the registrationId property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getRegistrationId().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link Groups.Group.RegistrationId }
		 * 
		 * 
		 */
		public List<Groups.Group.RegistrationId> getRegistrationIdList() {
			if (registrationIdList == null) {
				registrationIdList = new ArrayList<Groups.Group.RegistrationId>();
			}
			return this.registrationIdList;
		}

		/**
		 * Obtiene el valor de la propiedad notificationKey.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getNotificationKey() {
			return notificationKey;
		}

		/**
		 * Define el valor de la propiedad notificationKey.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setNotificationKey(String value) {
			this.notificationKey = value;
		}

		/**
		 * Obtiene el valor de la propiedad notificationKeyName.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getNotificationKeyName() {
			return notificationKeyName;
		}

		/**
		 * Define el valor de la propiedad notificationKeyName.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setNotificationKeyName(String value) {
			this.notificationKeyName = value;
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
			return this.notificationKey.equals(((Group) objeto)
					.getNotificationKey());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("Group( ");
			if (notificationKeyName != null && !notificationKeyName.isEmpty()) {
				builder.append("notificationKeyName=")
						.append(notificationKeyName).append(", ");
			}
			if (notificationKey != null && !notificationKey.isEmpty()) {
				builder.append("notificationKey=").append(notificationKey)
						.append(", ");
			}
			if (registrationIdList != null && !registrationIdList.isEmpty()) {
				builder.append("registrationId=")
						.append(registrationIdList.toString()).append(", ");
			}

			if (builder.charAt(builder.length() - 1) == ' ') {
				builder.delete(builder.length() - 2, builder.length());
			}
			builder.append(")");
			return builder.toString();
		}

		/**
		 * <p>
		 * Clase Java para anonymous complex type.
		 * 
		 * <p>
		 * El siguiente fragmento de esquema especifica el contenido que se
		 * espera que haya en esta clase.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;simpleContent>
		 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
		 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *     &lt;/extension>
		 *   &lt;/simpleContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "value" })
		public static class RegistrationId {

			@XmlValue
			protected String value;
			@XmlAttribute(name = "name", required = true)
			protected String name;

			/**
			 * Obtiene el valor de la propiedad value.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getValue() {
				return value;
			}

			/**
			 * Define el valor de la propiedad value.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setValue(String value) {
				this.value = value;
			}

			/**
			 * Obtiene el valor de la propiedad name.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getName() {
				return name;
			}

			/**
			 * Define el valor de la propiedad name.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setName(String value) {
				this.name = value;
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
				return this.value.equals(((RegistrationId) objeto).getValue());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				StringBuilder builder = new StringBuilder("RegistrationId( ");
				if (name != null && !name.isEmpty()) {
					builder.append("name=").append(name).append(", ");
				}
				if (value != null && !value.isEmpty()) {
					builder.append("value=").append(value).append(", ");
				}

				if (builder.charAt(builder.length() - 1) == ' ') {
					builder.delete(builder.length() - 2, builder.length());
				}
				builder.append(")");
				return builder.toString();
			}
		}

	}

}
