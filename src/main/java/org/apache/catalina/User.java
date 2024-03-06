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
 * Abstract representation of a user in a {@link UserDatabase}.  Each user is
 * optionally associated with a set of {@link Group}s through which they inherit
 * additional security roles, and is optionally assigned a set of specific
 * {@link Role}s.
 *
 * @author Craig R. McClanahan
 * @since 4.1
 */
public interface User extends Principal {


    // ------------------------------------------------------------- Properties


    /**
     * @return the full name of this user.
     */
    default String getFullName() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the full name of this user.
     *
     * @param fullName The new full name
     */
    default void setFullName(String fullName) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of {@link Group}s to which this user belongs.
     */
    default Iterator<Group> getGroups() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the logon password of this user, optionally prefixed with the
     * identifier of an encoding scheme surrounded by curly braces, such as
     * <code>{md5}xxxxx</code>.
     */
    default String getPassword() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the logon password of this user, optionally prefixed with the
     * identifier of an encoding scheme surrounded by curly braces, such as
     * <code>{md5}xxxxx</code>.
     *
     * @param password The new logon password
     */
    default void setPassword(String password) {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of {@link Role}s assigned specifically to this user.
     */
    default Iterator<Role> getRoles() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the {@link UserDatabase} within which this User is defined.
     */
    default UserDatabase getUserDatabase() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the logon username of this user, which must be unique
     * within the scope of a {@link UserDatabase}.
     */
    default String getUsername() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the logon username of this user, which must be unique within
     * the scope of a {@link UserDatabase}.
     *
     * @param username The new logon username
     */
    default void setUsername(String username) {
        throw new UnsupportedOperationException();
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Add a new {@link Group} to those this user belongs to.
     *
     * @param group The new group
     */
    default void addGroup(Group group) {
        throw new UnsupportedOperationException();
    }


    /**
     * Add a {@link Role} to those assigned specifically to this user.
     *
     * @param role The new role
     */
    default void addRole(Role role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Is this user in the specified {@link Group}?
     *
     * @param group The group to check
     * @return <code>true</code> if the user is in the specified group
     */
    default boolean isInGroup(Group group) {
        throw new UnsupportedOperationException();
    }


    /**
     * Is this user specifically assigned the specified {@link Role}?  This
     * method does <strong>NOT</strong> check for roles inherited based on
     * {@link Group} membership.
     *
     * @param role The role to check
     * @return <code>true</code> if the user has the specified role
     */
    default boolean isInRole(Role role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove a {@link Group} from those this user belongs to.
     *
     * @param group The old group
     */
    default void removeGroup(Group group) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove all {@link Group}s from those this user belongs to.
     */
    default void removeGroups() {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove a {@link Role} from those assigned to this user.
     *
     * @param role The old role
     */
    default void removeRole(Role role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove all {@link Role}s from those assigned to this user.
     */
    default void removeRoles() {
        throw new UnsupportedOperationException();
    }


}
