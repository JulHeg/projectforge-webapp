/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2014 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.Hibernate;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.projectforge.common.StringHelper;
import org.projectforge.core.DefaultBaseDO;
import org.projectforge.core.ShortDisplayNameCapable;

/**
 * 
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
@Entity
@Indexed
@Table(name = "T_GROUP", uniqueConstraints = { @UniqueConstraint(columnNames = { "name"})})
public class GroupDO extends DefaultBaseDO implements ShortDisplayNameCapable
{
  // private static final Logger log = Logger.getLogger(GroupDO.class);

  private static final long serialVersionUID = 5044537226571167954L;

  @Field(index = Index.TOKENIZED, store = Store.NO)
  private String name;

  private boolean localGroup;

  //private boolean nestedGroupsAllowed = true;

  //private String nestedGroupIds;

  @Field(index = Index.TOKENIZED, store = Store.NO)
  private String organization;

  @Field(index = Index.TOKENIZED, store = Store.NO)
  private String description;

  private String usernames;

  @ContainedIn
  @IndexedEmbedded(depth = 1)
  private Set<PFUserDO> assignedUsers;

  @ManyToMany(targetEntity = org.projectforge.user.PFUserDO.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinTable(name = "T_GROUP_USER", joinColumns = @JoinColumn(name = "GROUP_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
  public Set<PFUserDO> getAssignedUsers()
  {
    return assignedUsers;
  }

  /**
   * Returns the collection of assigned users only if initialized. Avoids a LazyInitializationException.
   * @return
   */
  @Transient
  public Set<PFUserDO> getSafeAssignedUsers()
  {
    if (this.assignedUsers == null || Hibernate.isInitialized(this.assignedUsers) == false) {
      return null;
    }
    return this.assignedUsers;
  }

  @Transient
  public String getUsernames()
  {
    if (usernames != null) {
      return usernames;
    }
    if (getSafeAssignedUsers() == null) {
      return "";
    }
    final List<String> list = new ArrayList<String>();
    for (final PFUserDO user : getAssignedUsers()) {
      if (user != null) {
        list.add(user.getUsername());
      }
    }
    usernames = StringHelper.listToString(list, ", ", true);
    return usernames;
  }

  public void setAssignedUsers(final Set<PFUserDO> assignedUsers)
  {
    this.assignedUsers = assignedUsers;
    this.usernames = null;
  }

  public void addUser(final PFUserDO user)
  {
    if (this.assignedUsers == null) {
      this.assignedUsers = new HashSet<PFUserDO>();
    }
    this.assignedUsers.add(user);
    this.usernames = null;
  }

  @Column(length = 1000)
  public String getDescription()
  {
    return description;
  }

  /**
   * @param description
   * @return this for chaining.
   */
  public GroupDO setDescription(final String description)
  {
    this.description = description;
    return this;
  }

  @Column(length = 100)
  public String getName()
  {
    return name;
  }

  /**
   * @param name
   * @return this for chaining.
   */
  public GroupDO setName(final String name)
  {
    this.name = name;
    return this;
  }

  /**
   * A local group will not be published or shared with any external user management system (such as LDAP).
   * @return the localGroup
   */
  @Column(name = "local_group", nullable = false)
  public boolean isLocalGroup()
  {
    return localGroup;
  }

  /**
   * @param localGroup the localGroup to set
   * @return this for chaining.
   */
  public GroupDO setLocalGroup(final boolean localGroup)
  {
    this.localGroup = localGroup;
    return this;
  }

  // /**
  // * Default is true.
  // * @return the nestedGroupsAllowed
  // */
  // @Column(name = "nested_groups_allowed", nullable = false)
  // public boolean isNestedGroupsAllowed()
  // {
  // return nestedGroupsAllowed;
  // }
  //
  // /**
  // * @param nestedGroupsAllowed the nestedGroupsAllowed to set
  // * @return this for chaining.
  // */
  // public GroupDO setNestedGroupsAllowed(final boolean nestedGroupsAllowed)
  // {
  // this.nestedGroupsAllowed = nestedGroupsAllowed;
  // return this;
  // }
  //
  // /**
  // * Comma separated id's of nested groups.
  // * @return the nestedGroups
  // */
  // @Column(name = "nested_group_ids", length = 1000)
  // public String getNestedGroupIds()
  // {
  // return nestedGroupIds;
  // }
  //
  // /**
  // * @param nestedGroupIds the nestedGroups to set
  // * @return this for chaining.
  // */
  // public GroupDO setNestedGroupIds(final String nestedGroupIds)
  // {
  // this.nestedGroupIds = nestedGroupIds;
  // return this;
  // }

  @Column(length = 100)
  public String getOrganization()
  {
    return organization;
  }

  public void setOrganization(final String organization)
  {
    this.organization = organization;
  }

  @Override
  public String toString()
  {
    final ToStringBuilder builder = new ToStringBuilder(this);
    builder.append("name", getName());
    builder.append("organization", getOrganization());
    builder.append("description", getDescription());
    if (getSafeAssignedUsers() != null) {
      builder.append("assignedUsers", this.assignedUsers);
    } else {
      builder.append("assignedUsers", "LazyCollection");
    }
    return builder.toString();
  }

  @Override
  public boolean equals(final Object o)
  {
    if (o instanceof GroupDO) {
      final GroupDO other = (GroupDO) o;
      if (ObjectUtils.equals(this.getName(), other.getName()) == false)
        return false;
      return true;
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    final HashCodeBuilder hcb = new HashCodeBuilder();
    hcb.append(this.getName());
    return hcb.toHashCode();
  }

  @Transient
  public String getShortDisplayName()
  {
    return this.getName();
  }
}
