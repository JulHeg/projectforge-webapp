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

package org.projectforge.plugins.skillmatrix;


import org.apache.log4j.Logger;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.Hibernate;
import org.projectforge.user.PFUserDO;
import org.projectforge.web.user.UserSelectPanel;
import org.projectforge.web.wicket.AbstractEditForm;
import org.projectforge.web.wicket.bootstrap.GridSize;
import org.projectforge.web.wicket.components.LabelValueChoiceRenderer;
import org.projectforge.web.wicket.components.MaxLengthTextArea;
import org.projectforge.web.wicket.flowlayout.FieldsetPanel;

/**
 * This is the edit formular page.
 * @author Werner Feder (werner.feder@t-online.de)
 */
public class AttendeeEditForm extends AbstractEditForm<AttendeeDO, AttendeeEditPage>
{

  private static final long serialVersionUID = 6814668114853472909L;

  private static final Logger log = Logger.getLogger(AttendeeEditForm.class);

  private FieldsetPanel ratingFs, certificateFs;
  private LabelValueChoiceRenderer<String> ratingChoiceRenderer, certificateChoiceRenderer;

  @SpringBean(name = "attendeeDao")
  private AttendeeDao attendeeDao;

  @SpringBean(name = "skillDao")
  private SkillDao skillDao;

  /**
   * @param parentPage
   * @param data
   */
  public AttendeeEditForm(final AttendeeEditPage parentPage, final AttendeeDO data)
  {
    super(parentPage, data);
  }


  @Override
  public void init()
  {
    super.init();

    gridBuilder.newSplitPanel(GridSize.COL50);

    // Training
    FieldsetPanel fs = gridBuilder.newFieldset(AttendeeDO.class, "training");
    TrainingDO training = data.getTraining();
    if (Hibernate.isInitialized(training) == false) {
      training = attendeeDao.getTraingDao().getOrLoad(training.getId());
      data.setTraining(training);
    }
    final TrainingSelectPanel trainingSelectPanel = new TrainingSelectPanel(fs.newChildId(), new PropertyModel<TrainingDO>(data, "training"),
        parentPage, "trainingId");
    trainingSelectPanel.setDefaultFormProcessing(false);
    trainingSelectPanel.init().withAutoSubmit(true).setRequired(true);
    fs.add(trainingSelectPanel);

    // Attendee
    fs = gridBuilder.newFieldset(AttendeeDO.class, "attendee");
    PFUserDO attendee = data.getAttendee();
    if (Hibernate.isInitialized(attendee) == false) {
      attendee = attendeeDao.getUserDao().getOrLoad(attendee.getId());
      data.setAttendee(attendee);
    }
    final UserSelectPanel attendeeSelectPanel = new UserSelectPanel(fs.newChildId(), new PropertyModel<PFUserDO>(data, "attendee"),
        parentPage, "attendeeId");
    fs.add(attendeeSelectPanel.setRequired(true));
    attendeeSelectPanel.init();

    if (isNew() == true) {
      trainingSelectPanel.setFocus();
    } else {
      attendeeSelectPanel.setFocus();
    }

    { // Rating
      ratingFs = gridBuilder.newFieldset(AttendeeDO.class, "rating");
      ratingChoiceRenderer = new LabelValueChoiceRenderer<String>();
      ratingFs.addDropDownChoice(new PropertyModel<String>(data, "rating"), ratingChoiceRenderer.getValues(), ratingChoiceRenderer)
      .setNullValid(true);
    }

    { // Certificate
      certificateFs = gridBuilder.newFieldset(AttendeeDO.class, "certificate");
      certificateChoiceRenderer = new LabelValueChoiceRenderer<String>();
      certificateFs.addDropDownChoice(new PropertyModel<String>(data, "certificate"), certificateChoiceRenderer.getValues(), certificateChoiceRenderer)
      .setNullValid(true);
    }

    { // Description
      fs = gridBuilder.newFieldset(AttendeeDO.class, "description");
      fs.add(new MaxLengthTextArea(fs.getTextAreaId(), new PropertyModel<String>(data, "description"))).setAutogrow();
    }

  }

  /**
   * @see org.projectforge.web.wicket.AbstractEditForm#onBeforeRender()
   */
  @Override
  public void onBeforeRender()
  {
    super.onBeforeRender();
    final TrainingDO training = data.getTraining();

    if (training != null) {
      if (training.getRatingArray() != null) {
        ratingChoiceRenderer.clear().setValueArray(training.getRatingArray());
        ratingFs.setVisible(true);
      } else
        ratingFs.setVisible(false);

      if (training.getCertificateArray() != null) {
        certificateChoiceRenderer.clear().setValueArray(training.getCertificateArray());
        certificateFs.setVisible(true);
      } else
        certificateFs.setVisible(false);
    }
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
