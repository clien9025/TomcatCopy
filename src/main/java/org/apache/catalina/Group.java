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
package org.apache.catalina;

import java.security.Principal;
import java.util.Iterator;

/**
 * <p>Abstract representation of a group of {@link User}s in a
 * {@link UserDatabase}.  Each user that is a member of this group
 * inherits the {@link Role}s assigned to the group.</p>
 *
 * @author Craig R. McClanahan
 * @since 4.1
 */
public interface Group extends Principal {

    // ------------------------------------------------------------- Properties

    /**
     * @return the description of this group.
     */
    default String getDescription() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the description of this group.
     *
     * @param description The new description
     */
    default void setDescription(String description) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the group name of this group, which must be unique
     * within the scope of a {@link UserDatabase}.
     */
    default String getGroupname() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the group name of this group, which must be unique
     * within the scope of a {@link UserDatabase}.
     *
     * @param groupname The new group name
     */
    default void setGroupname(String groupname) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of {@link Role}s assigned specifically to this group.
     */
    default Iterator<Role> getRoles() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the {@link UserDatabase} within which this Group is defined.
     */
    default UserDatabase getUserDatabase() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of {@link User}s that are members of this group.
     */
    default Iterator<User> getUsers() {
        throw new UnsupportedOperationException();
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Add a new {@link Role} to those assigned specifically to this group.
     *
     * @param role The new role
     */
    default void addRole(Role role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Is this group specifically assigned the specified {@link Role}?
     *
     * @param role The role to check
     * @return <code>true</code> if the group is assigned to the specified role
     * otherwise <code>false</code>
     */
    default boolean isInRole(Role role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove a {@link Role} from those assigned to this group.
     *
     * @param role The old role
     */
    default void removeRole(Role role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove all {@link Role}s from those assigned to this group.
     */
    default void removeRoles() {
        throw new UnsupportedOperationException();
    }


}
