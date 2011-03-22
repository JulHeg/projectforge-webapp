/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2011 Kai Reinhard (k.reinhard@me.com)
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

package org.projectforge.web.gwiki;

import org.apache.wicket.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.projectforge.web.wicket.AbstractSecuredPage;


/**
 * Standard error page should be shown in production mode.
 * 
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
public class GWikiContainerPage extends AbstractSecuredPage
{
  public GWikiContainerPage(final PageParameters parameters)
  {
    super(parameters);
    
    String pageId = parameters.getString("pageId");
    
    if (Strings.isEmpty(pageId)) {
      pageId = "index";
    }
    
    body.add(new GWikiLabel("text", pageId));
  }
  
  @Override
  protected String getTitle()
  {
    return "GWiki";
  }
}
