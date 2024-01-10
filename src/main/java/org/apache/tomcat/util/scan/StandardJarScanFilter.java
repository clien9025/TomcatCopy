package org.apache.tomcat.util.scan;

import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StandardJarScanFilter implements JarScanFilter {


    private final ReadWriteLock configurationLock =
            new ReentrantReadWriteLock();

    private static final String defaultSkip;
    private static final String defaultScan;
    private static final Set<String> defaultSkipSet = new HashSet<>();
    private static final Set<String> defaultScanSet = new HashSet<>();
    private static final boolean defaultSkipAll;

    static {
        // Initialize defaults. There are no setter methods for them.
        defaultSkip = System.getProperty(Constants.SKIP_JARS_PROPERTY);
        populateSetFromAttribute(defaultSkip, defaultSkipSet);
        defaultScan = System.getProperty(Constants.SCAN_JARS_PROPERTY);
        populateSetFromAttribute(defaultScan, defaultScanSet);
        defaultSkipAll = (defaultSkipSet.contains("*") || defaultSkipSet.contains("*.jar")) && defaultScanSet.isEmpty();
    }

    private String tldSkip;
    private String tldScan;
    private final Set<String> tldSkipSet;
    private final Set<String> tldScanSet;
    private boolean defaultTldScan = true;

    private String pluggabilitySkip;
    private String pluggabilityScan;
    private final Set<String> pluggabilitySkipSet;
    private final Set<String> pluggabilityScanSet;
    private boolean defaultPluggabilityScan = true;

    /**
     * This is the standard implementation of {@link JarScanFilter}. By default,
     * the following filtering rules are used:
     * <ul>
     * <li>JARs that match neither the skip nor the scan list will be included
     *     in scan results.</li>
     * <li>JARs that match the skip list but not the scan list will be excluded
     *     from scan results.</li>
     * <li>JARs that match the scan list will be included from scan results.
     *     </li>
     * </ul>
     * The default skip list and default scan list are obtained from the system
     * properties {@link Constants#SKIP_JARS_PROPERTY} and
     * {@link Constants#SCAN_JARS_PROPERTY} respectively. These default values
     * may be over-ridden for the {@link JarScanType#TLD} and
     * {@link JarScanType#PLUGGABILITY} scans. The filtering rules may also be
     * modified for these scan types using {@link #setDefaultTldScan(boolean)}
     * and {@link #setDefaultPluggabilityScan(boolean)}. If set to
     * <code>false</code>, the following filtering rules are used for associated
     * type:
     * <ul>
     * <li>JARs that match neither the skip nor the scan list will be excluded
     *     from scan results.</li>
     * <li>JARs that match the scan list but not the skip list will be included
     *     in scan results.</li>
     * <li>JARs that match the skip list will be excluded from scan results.
     *     </li>
     * </ul>
     */
    public StandardJarScanFilter() {
        tldSkip = defaultSkip;
        tldSkipSet = new HashSet<>(defaultSkipSet);
        tldScan = defaultScan;
        tldScanSet = new HashSet<>(defaultScanSet);
        pluggabilitySkip = defaultSkip;
        pluggabilitySkipSet = new HashSet<>(defaultSkipSet);
        pluggabilityScan = defaultScan;
        pluggabilityScanSet = new HashSet<>(defaultScanSet);
    }


    public String getTldSkip() {
        return tldSkip;
    }


    public void setTldSkip(String tldSkip) {
        this.tldSkip = tldSkip;
        Lock writeLock = configurationLock.writeLock();
        writeLock.lock();
        try {
            populateSetFromAttribute(tldSkip, tldSkipSet);
        } finally {
            writeLock.unlock();
        }
    }


    public String getTldScan() {
        return tldScan;
    }


    public void setTldScan(String tldScan) {
        this.tldScan = tldScan;
        Lock writeLock = configurationLock.writeLock();
        writeLock.lock();
        try {
            populateSetFromAttribute(tldScan, tldScanSet);
        } finally {
            writeLock.unlock();
        }
    }


    @Override
    public boolean isSkipAll() {
        return defaultSkipAll;
    }


    public boolean isDefaultTldScan() {
        return defaultTldScan;
    }


    public void setDefaultTldScan(boolean defaultTldScan) {
        this.defaultTldScan = defaultTldScan;
    }


    public String getPluggabilitySkip() {
        return pluggabilitySkip;
    }


    public void setPluggabilitySkip(String pluggabilitySkip) {
        this.pluggabilitySkip = pluggabilitySkip;
        Lock writeLock = configurationLock.writeLock();
        writeLock.lock();
        try {
            populateSetFromAttribute(pluggabilitySkip, pluggabilitySkipSet);
        } finally {
            writeLock.unlock();
        }
    }


    public String getPluggabilityScan() {
        return pluggabilityScan;
    }


    public void setPluggabilityScan(String pluggabilityScan) {
        this.pluggabilityScan = pluggabilityScan;
        Lock writeLock = configurationLock.writeLock();
        writeLock.lock();
        try {
            populateSetFromAttribute(pluggabilityScan, pluggabilityScanSet);
        } finally {
            writeLock.unlock();
        }
    }


    public boolean isDefaultPluggabilityScan() {
        return defaultPluggabilityScan;
    }


    public void setDefaultPluggabilityScan(boolean defaultPluggabilityScan) {
        this.defaultPluggabilityScan = defaultPluggabilityScan;
    }

    /**
     * populateSetFromAttribute 方法的主要作用是从一个以逗号分隔的字符串中提取元素，将它们作为单独的条目添加到提供的 Set 集合中
     * populateSetFromAttribute 方法将不同的输入字符串转换成 Set 集合中的元素。
     *
     * @param attribute
     * @param set
     */
    private static void populateSetFromAttribute(String attribute, Set<String> set) {
        /* 1. 清空集合 */
        set.clear();// 清空传入的 Set 集合，以便存储新的元素
        /* 2. 检查属性是否为 null */
        if (attribute != null) {
            /* 3. 分割字符串 */
            // 使用 StringTokenizer 对 attribute 进行分割，分隔符是逗号 ","
            StringTokenizer tokenizer = new StringTokenizer(attribute, ",");
            /* 4. 提取并添加元素 */
            // 遍历 StringTokenizer，提取每个 token（经过 .trim() 去除空白字符）。如果 token 的长度大于 0，则将其添加到 Set 集合中。
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken().trim();
                if (token.length() > 0) {
                    set.add(token);
                }
            }
        }
    }
}
