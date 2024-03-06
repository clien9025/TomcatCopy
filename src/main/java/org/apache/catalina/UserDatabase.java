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

import java.util.Iterator;

/**
 * Abstract representation of a database of {@link User}s and {@link Group}s
 * that can be maintained by an application, along with definitions of
 * corresponding {@link Role}s, and referenced by a {@link Realm} for
 * authentication and access control.
 *
 * @author Craig R. McClanahan
 * @since 4.1
 */
public interface UserDatabase {

    // ------------------------------------------------------------- Properties

    /**
     * @return the set of {@link Group}s defined in this user database.
     */
    default Iterator<Group> getGroups() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the unique global identifier of this user database.
     */
    default String getId() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of {@link Role}s defined in this user database.
     */
    default Iterator<Role> getRoles() {
        throw new UnsupportedOperationException();
    }


    /**
     * @return the set of {@link User}s defined in this user database.
     */
    default Iterator<User> getUsers() {
        throw new UnsupportedOperationException();
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Finalize access to this user database.
     *
     * @throws Exception if any exception is thrown during closing
     */
    default void close() throws Exception {
        throw new UnsupportedOperationException();
    }


    /**
     * Create and return a new {@link Group} defined in this user database.
     *
     * @param groupname   The group name of the new group (must be unique)
     * @param description The description of this group
     * @return The new group
     */
    default Group createGroup(String groupname, String description) {
        throw new UnsupportedOperationException();
    }


    /**
     * Create and return a new {@link Role} defined in this user database.
     *
     * @param rolename    The role name of the new role (must be unique)
     * @param description The description of this role
     * @return The new role
     */
    default Role createRole(String rolename, String description) {
        throw new UnsupportedOperationException();
    }


    /**
     * Create and return a new {@link User} defined in this user database.
     *
     * @param username The logon username of the new user (must be unique)
     * @param password The logon password of the new user
     * @param fullName The full name of the new user
     * @return The new user
     */
    default User createUser(String username, String password, String fullName) {
        throw new UnsupportedOperationException();
    }


    /**
     * @param groupname Name of the group to return
     * @return the {@link Group} with the specified group name, if any;
     * otherwise return <code>null</code>.
     */
    default Group findGroup(String groupname) {
        throw new UnsupportedOperationException();
    }


    /**
     * @param rolename Name of the role to return
     * @return the {@link Role} with the specified role name, if any; otherwise
     * return <code>null</code>.
     */
    default Role findRole(String rolename) {
        throw new UnsupportedOperationException();
    }


    /**
     * @param username Name of the user to return
     * @return the {@link User} with the specified user name, if any; otherwise
     * return <code>null</code>.
     */
    default User findUser(String username) {
        throw new UnsupportedOperationException();
    }


    /**
     * Initialize access to this user database.
     *
     * @throws Exception if any exception is thrown during opening
     */
    default void open() throws Exception {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified {@link Group} from this user database.
     *
     * @param group The group to be removed
     */
    default void removeGroup(Group group) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified {@link Role} from this user database.
     *
     * @param role The role to be removed
     */
    default void removeRole(Role role) {
        throw new UnsupportedOperationException();
    }


    /**
     * Remove the specified {@link User} from this user database.
     *
     * @param user The user to be removed
     */
    default void removeUser(User user) {
        throw new UnsupportedOperationException();
    }


    /**
     * Signal the specified {@link Group} from this user database has been
     * modified.
     *
     * @param group The group that has been modified
     */
    default void modifiedGroup(Group group) {}


    /**
     * Signal the specified {@link Role} from this user database has been
     * modified.
     *
     * @param role The role that has been modified
     */
    default void modifiedRole(Role role) {}


    /**
     * Signal the specified {@link User} from this user database has been
     * modified.
     *
     * @param user The user that has been modified
     */
    default void modifiedUser(User user) {}


    /**
     * Save any updated information to the persistent storage location for this
     * user database.
     *
     * @throws Exception if any exception is thrown during saving
     */
    default void save() throws Exception {
        throw new UnsupportedOperationException();
    }


    /**
     * Perform any background processing (e.g. checking for changes in persisted
     * storage) required for the user database.
     */
    default void backgroundProcess() {
        // NO-OP by default
    }


    /**
     * Is the database available.
     *
     * @return true
     */
    default boolean isAvailable() {
        return true;
    }


    /**
     * Is the database data loaded on demand. This is used to avoid eager
     * loading of the full database data, for example for JMX registration of
     * all objects.
     *
     * @return false
     */
    default boolean isSparse() {
        return false;
    }
}
