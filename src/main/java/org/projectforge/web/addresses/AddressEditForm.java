/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2013 Kai Reinhard (k.reinhard@micromata.de)
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

package org.projectforge.web.addresses;


import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.projectforge.address.FormOfAddress;
import org.projectforge.addresses.Address2DO;
import org.projectforge.addresses.Address2Dao;
import org.projectforge.web.wicket.AbstractEditForm;
import org.projectforge.web.wicket.bootstrap.GridSize;
import org.projectforge.web.wicket.components.DatePanel;
import org.projectforge.web.wicket.components.DatePanelSettings;
import org.projectforge.web.wicket.components.LabelValueChoiceRenderer;
import org.projectforge.web.wicket.components.MaxLengthTextField;
import org.projectforge.web.wicket.components.RequiredMaxLengthTextField;
import org.projectforge.web.wicket.flowlayout.FieldProperties;
import org.projectforge.web.wicket.flowlayout.FieldsetPanel;

/**
 * This is the edit formular page.
 * @author Werner Feder (werner.feder@t-online.de)
 */
public class AddressEditForm extends AbstractEditForm<Address2DO, AddressEditPage>
{

  private static final long serialVersionUID = 7930242750045989712L;

  private static final Logger log = Logger.getLogger(AddressEditForm.class);

  @SpringBean(name = "address2Dao")
  private Address2Dao address2Dao;

  /**
   * @param parentPage
   * @param data
   */
  public AddressEditForm(final AddressEditPage parentPage, final Address2DO data)
  {
    super(parentPage, data);
  }


  @Override
  public void init()
  {
    super.init();

    gridBuilder.newSplitPanel(GridSize.COL50);

    // name
    FieldsetPanel fs = gridBuilder.newFieldset(Address2DO.class, "name");
    final RequiredMaxLengthTextField name = new RequiredMaxLengthTextField(fs.getTextFieldId(), new PropertyModel<String>(data,
        "name"));
    fs.add(name);

    // firstname
    fs = gridBuilder.newFieldset(Address2DO.class, "firstname");
    final MaxLengthTextField firstname = new RequiredMaxLengthTextField(fs.getTextFieldId(), new PropertyModel<String>(data, "firstname"));
    fs.add(firstname);

    // form
    final FieldProperties<FormOfAddress> props = new FieldProperties<FormOfAddress>("address.form", new PropertyModel<FormOfAddress>(data, "form"));
    fs = gridBuilder.newFieldset(props);
    final LabelValueChoiceRenderer<FormOfAddress> formChoiceRenderer = new LabelValueChoiceRenderer<FormOfAddress>(parentPage, FormOfAddress.values());
    fs.addDropDownChoice(props.getModel(), formChoiceRenderer.getValues(), formChoiceRenderer).setRequired(true).setNullValid(false);

    // title
    fs = gridBuilder.newFieldset(Address2DO.class, "title");
    final MaxLengthTextField title = new RequiredMaxLengthTextField(fs.getTextFieldId(), new PropertyModel<String>(data, "title"));
    fs.add(title);

    // birthday
    fs = gridBuilder.newFieldset(Address2DO.class, "birthday");
    fs.add(new DatePanel(fs.newChildId(), new PropertyModel<Date>(data, "birthday"), DatePanelSettings.get().withTargetType(
        java.sql.Date.class)));

  }

  /**
   * @see org.projectforge.web.wicket.AbstractEditForm#getLogger()
   */
  @Override
  protected Logger getLogger()
  {
    return log;
  }

}
