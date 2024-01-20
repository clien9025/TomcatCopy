/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.util;

import java.util.Locale;

/**
 * Utility class to manage context names so there is one place where the
 * conversions between baseName, path and version take place.
 */
public final class ContextName {
    public static final String ROOT_NAME = "ROOT";
    private static final String VERSION_MARKER = "##";
    private static final char FWD_SLASH_REPLACEMENT = '#';

    private final String baseName;
    private final String path;
    private final String version;
    private final String name;


    /**
     * Creates an instance from a context name, display name, base name,
     * directory name, WAR name or context.xml name.
     * <p>
     * 这个构造函数的作用是基于提供的名称（可能是上下文名称、显示名称、基础名称、目录名称、WAR名称或context.xml名称）
     * 来创建一个 ContextName 实例。
     *
     * @param name               The name to use as the basis for this object
     * @param stripFileExtension If a .war or .xml file extension is present
     *                           at the end of the provided name should it be
     *                           removed?
     */
    public ContextName(String name, boolean stripFileExtension) {

        String tmp1 = name;

        // Convert Context names and display names to base names
        /* 1. 去除前导斜杠 */
        // Strip off any leading "/"
        if (tmp1.startsWith("/")) {
            tmp1 = tmp1.substring(1);
        }
        /* 2. 替换剩余斜杠 */
        // 将 tmp1 中的所有斜杠 (/) 替换为 FWD_SLASH_REPLACEMENT（前向斜杠替代字符）
        // Replace any remaining /
        tmp1 = tmp1.replace('/', FWD_SLASH_REPLACEMENT);
        /* 3. 处理根名称 */
        // 如果 tmp1 以版本标记开头或者 tmp1 为空，将根名称（ROOT_NAME）添加到 tmp1 前面
        // Insert the ROOT name if required
        if (tmp1.startsWith(VERSION_MARKER) || tmp1.isEmpty()) {
            tmp1 = ROOT_NAME + tmp1;
        }
        /* 4. 移除文件扩展名 */
        // 如果 stripFileExtension 为真，并且 tmp1 以 .war 或 .xml 结尾（不区分大小写），则移除这些扩展名
        // Remove any file extensions
        if (stripFileExtension &&
                (tmp1.toLowerCase(Locale.ENGLISH).endsWith(".war") ||
                        tmp1.toLowerCase(Locale.ENGLISH).endsWith(".xml"))) {
            tmp1 = tmp1.substring(0, tmp1.length() - 4);
        }
        /* 5. 设置基础名称 */
        // 将处理后的 tmp1 赋值给 baseName 成员变量
        baseName = tmp1;

        String tmp2;
        /* 6. 提取版本号 */
        // 查找 baseName 中的版本标记（VERSION_MARKER）
        // Extract version number
        int versionIndex = baseName.indexOf(VERSION_MARKER);
        // 如果找到，提取版本号并赋值给 version 成员变量，并将 baseName 切割至版本标记之前，赋值给 tmp2
        if (versionIndex > -1) {
            version = baseName.substring(versionIndex + 2);
            tmp2 = baseName.substring(0, versionIndex);
        } else {
            // 如果未找到，将 version 设置为空字符串，tmp2 设置为 baseName
            version = "";
            tmp2 = baseName;
        }
        /* 7. 处理路径 */
        // 如果 tmp2 等于根名称，则将 path 成员变量设置为空字符串
        if (ROOT_NAME.equals(tmp2)) {
            path = "";
        } else {
            // 否则，将 tmp2 中的 FWD_SLASH_REPLACEMENT 替换回斜杠，加上前导斜杠，并赋值给 path
            path = "/" + tmp2.replace(FWD_SLASH_REPLACEMENT, '/');
        }
        /* 8. 设置完整名称 */
        // 如果存在版本号，将路径、版本标记和版本号组合赋值给 this.name。
        // 如果没有版本号，直接将路径赋值给 this.name
        if (versionIndex > -1) {
            this.name = path + VERSION_MARKER + version;
        } else {
            this.name = path;
        }
    }

    /**
     * Construct an instance from a path and version.
     *
     * @param path    Context path to use
     * @param version Context version to use
     */
    public ContextName(String path, String version) {
//        // Path should never be null, '/' or '/ROOT'
//        if (path == null || "/".equals(path) || "/ROOT".equals(path)) {
//            this.path = "";
//        } else {
//            this.path = path;
//        }
//
//        // Version should never be null
//        if (version == null) {
//            this.version = "";
//        } else {
//            this.version = version;
//        }
//
//        // Name is path + version
//        if (this.version.isEmpty()) {
//            name = this.path;
//        } else {
//            name = this.path + VERSION_MARKER + this.version;
//        }
//
//        // Base name is converted path + version
//        StringBuilder tmp = new StringBuilder();
//        if (this.path.isEmpty()) {
//            tmp.append(ROOT_NAME);
//        } else {
//            tmp.append(this.path.substring(1).replace('/',
//                    FWD_SLASH_REPLACEMENT));
//        }
//        if (!this.version.isEmpty()) {
//            tmp.append(VERSION_MARKER);
//            tmp.append(this.version);
//        }
//        this.baseName = tmp.toString();
        throw new UnsupportedOperationException();
    }

    public String getBaseName() {
        return baseName;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        StringBuilder tmp = new StringBuilder();
        if ("".equals(path)) {
            tmp.append('/');
        } else {
            tmp.append(path);
        }

        if (!version.isEmpty()) {
            tmp.append(VERSION_MARKER);
            tmp.append(version);
        }

        return tmp.toString();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }


    /**
     * Extract the final component of the given path which is assumed to be a
     * base name and generate a {@link ContextName} from that base name.
     *
     * @param path The path that ends in a base name
     * @return the {@link ContextName} generated from the given base name
     */
    public static ContextName extractFromPath(String path) {
        // Convert '\' to '/'
        path = path.replace("\\", "/");
        // Remove trailing '/'. Use while just in case a value ends in ///
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        int lastSegment = path.lastIndexOf('/');
        if (lastSegment > 0) {
            path = path.substring(lastSegment + 1);
        }

        return new ContextName(path, true);
    }
}
