package org.apache.tomcat.util.modeler;


import javax.management.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.buf.StringUtils;

/**
 * <p>Internal configuration information for a managed bean (MBean)
 * descriptor.</p>
 *
 * @author Craig R. McClanahan
 */
public class ManagedBean implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private static final StringManager sm = StringManager.getManager(ManagedBean.class);

    private static final String BASE_MBEAN = "org.apache.tomcat.util.modeler.BaseModelMBean";


    // ----------------------------------------------------- Instance Variables
    static final Class<?>[] NO_ARGS_PARAM_SIG = new Class[0];


    private final ReadWriteLock mBeanInfoLock = new ReentrantReadWriteLock();
    /**
     * The <code>ModelMBeanInfo</code> object that corresponds
     * to this <code>ManagedBean</code> instance.
     */
    private transient volatile MBeanInfo info = null;

    private Map<String,AttributeInfo> attributes = new HashMap<>();

    private Map<String,OperationInfo> operations = new HashMap<>();

    protected String className = BASE_MBEAN;
    protected String description = null;
    protected String domain = null;
    protected String group = null;
    protected String name = null;

//    private NotificationInfo notifications[] = new NotificationInfo[0];
    protected String type = null;

    /**
     * Constructor. Will add default attributes.
     */
    public ManagedBean() {
        AttributeInfo ai=new AttributeInfo();
        ai.setName("modelerType");
        ai.setDescription("Type of the modeled resource. Can be set only once");
        ai.setType("java.lang.String");
        ai.setWriteable(false);
        addAttribute(ai);
    }

    // ------------------------------------------------------------- Properties

    /**
     * The fully qualified name of the Java class of the MBean
     * described by this descriptor.  If not specified, the standard JMX
     * class (<code>javax.management.modelmbean.RequiredModeLMBean</code>)
     * will be utilized.
     * @return the class name
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * @return the name of this managed bean, which must be unique
     *  among all MBeans managed by a particular MBeans server.
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        mBeanInfoLock.writeLock().lock();
        try {
            this.name = name;
            this.info = null;
        } finally {
            mBeanInfoLock.writeLock().unlock();
        }
    }
    /**
     * @return the fully qualified name of the Java class of the resource
     * implementation class described by the managed bean described
     * by this descriptor.
     */
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        mBeanInfoLock.writeLock().lock();
        try {
            this.type = type;
            this.info = null;
        } finally {
            mBeanInfoLock.writeLock().unlock();
        }
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Add a new attribute to the set of attributes for this MBean.
     *
     * @param attribute The new attribute descriptor
     */
    public void addAttribute(AttributeInfo attribute) {
        // private Map<String,AttributeInfo> attributes = new HashMap<>();
        attributes.put(attribute.getName(), attribute);
    }



    /**
     * Add a new operation to the set of operations for this MBean.
     *
     * @param operation The new operation descriptor
     */
    public void addOperation(OperationInfo operation) {
        operations.put(createOperationKey(operation), operation);
    }

    /**
     * Create and return a <code>ModelMBean</code> that has been
     * preconfigured with the <code>ModelMBeanInfo</code> information
     * for this managed bean, and is associated with the specified
     * managed object instance.  The returned <code>ModelMBean</code>
     * will <strong>NOT</strong> have been registered with our
     * <code>MBeanServer</code>.
     *
     * @param instance Instanced of the managed object, or <code>null</code>
     *  for no associated instance
     * @return the MBean
     * @exception InstanceNotFoundException if the managed resource
     *  object cannot be found
     * @exception MBeanException if a problem occurs instantiating the
     *  <code>ModelMBean</code> instance
     * @exception RuntimeOperationsException if a JMX runtime error occurs
     */
    public DynamicMBean createMBean(Object instance)
            throws InstanceNotFoundException,
            MBeanException, RuntimeOperationsException {

        BaseModelMBean mbean = null;

        // Load the ModelMBean implementation class
        if(getClassName().equals(BASE_MBEAN)) {
            // Skip introspection
            mbean = new BaseModelMBean();
        } else {
            Class<?> clazz = null;
            Exception ex = null;
            try {
                clazz = Class.forName(getClassName());
            } catch (Exception e) {
            }

            if( clazz==null ) {
                try {
                    ClassLoader cl= Thread.currentThread().getContextClassLoader();
                    if ( cl != null) {
                        clazz= cl.loadClass(getClassName());
                    }
                } catch (Exception e) {
                    ex=e;
                }
            }

            if( clazz==null) {
                throw new MBeanException
                        (ex, sm.getString("managedMBean.cannotLoadClass", getClassName()));
            }
            try {
                // Stupid - this will set the default minfo first....
                mbean = (BaseModelMBean) clazz.getConstructor().newInstance();
            } catch (RuntimeOperationsException e) {
                throw e;
            } catch (Exception e) {
                throw new MBeanException
                        (e, sm.getString("managedMBean.cannotInstantiateClass", getClassName()));
            }
        }

        mbean.setManagedBean(this);

        // Set the managed resource (if any)
        try {
            if (instance != null) {
                mbean.setManagedResource(instance, "ObjectReference");
            }
        } catch (InstanceNotFoundException e) {
            throw e;
        }

        return mbean;
    }



    private String createOperationKey(OperationInfo operation) {
        StringBuilder key = new StringBuilder(operation.getName());
        key.append('(');
        StringUtils.join(operation.getSignature(), ',', FeatureInfo::getType, key);
        key.append(')');

        return key.toString().intern();
    }


    private String createOperationKey(String methodName, String[] parameterTypes) {
//        StringBuilder key = new StringBuilder(methodName);
//        key.append('(');
//        StringUtils.join(parameterTypes, ',', key);
//        key.append(')');
//
//        return key.toString().intern();
        throw new UnsupportedOperationException();
    }

}
