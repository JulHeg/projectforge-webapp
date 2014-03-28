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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.projectforge.excel.ContentProvider;
import org.projectforge.excel.ExportColumn;
import org.projectforge.excel.PropertyMapping;
import org.projectforge.export.DOListExcelExporter;
import org.projectforge.user.PFUserContext;
import org.projectforge.web.calendar.DateTimeFormatter;
import org.projectforge.web.user.UserFormatter;
import org.projectforge.web.user.UserPropertyColumn;
import org.projectforge.web.wicket.AbstractListPage;
import org.projectforge.web.wicket.CellItemListener;
import org.projectforge.web.wicket.CellItemListenerPropertyColumn;
import org.projectforge.web.wicket.IListPageColumnsCreator;
import org.projectforge.web.wicket.ListPage;
import org.projectforge.web.wicket.ListSelectActionPanel;

/**
 * @author Billy Duong (b.duong@micromata.de)
 * 
 */
@ListPage(editPage = SkillRatingEditPage.class)
public class SkillRatingListPage extends AbstractListPage<SkillRatingListForm, SkillRatingDao, SkillRatingDO> implements
IListPageColumnsCreator<SkillRatingDO>
{

  private static final long serialVersionUID = 3262800972072452074L;

  @SpringBean(name = "skillRatingDao")
  private SkillRatingDao skillRatingDao;

  @SpringBean(name = "userFormatter")
  private UserFormatter userFormatter;

  public SkillRatingListPage(final PageParameters parameters)
  {
    super(parameters, "plugins.skillmatrix.rating");
  }

  /**
   * @see org.projectforge.web.wicket.IListPageColumnsCreator#createColumns(org.apache.wicket.markup.html.WebPage, boolean)
   */
  @SuppressWarnings("serial")
  @Override
  public List<IColumn<SkillRatingDO, String>> createColumns(final WebPage returnToPage, final boolean sortable)
  {
    final List<IColumn<SkillRatingDO, String>> columns = new ArrayList<IColumn<SkillRatingDO, String>>();
    final CellItemListener<SkillRatingDO> cellItemListener = new CellItemListener<SkillRatingDO>() {
      public void populateItem(final Item<ICellPopulator<SkillRatingDO>> item, final String componentId,
          final IModel<SkillRatingDO> rowModel)
      {
        final SkillRatingDO skillRating = rowModel.getObject();
        appendCssClasses(item, skillRating.getId(), skillRating.isDeleted());
      }
    };

    final CellItemListenerPropertyColumn<SkillRatingDO> created = new CellItemListenerPropertyColumn<SkillRatingDO>(new Model<String>(
        getString("created")), getSortable("created", sortable), "created", cellItemListener) {
      @SuppressWarnings({ "unchecked", "rawtypes"})
      @Override
      public void populateItem(final Item item, final String componentId, final IModel rowModel)
      {
        final SkillRatingDO skillRating = (SkillRatingDO) rowModel.getObject();
        item.add(new ListSelectActionPanel(componentId, rowModel, SkillRatingEditPage.class, skillRating.getId(), returnToPage,
            DateTimeFormatter.instance().getFormattedDateTime(skillRating.getCreated())));
        // Only the owner can click / edit his entries
        if (ObjectUtils.equals(PFUserContext.getUserId(), skillRating.getUserId())) {
          addRowClick(item);
        }
        cellItemListener.populateItem(item, componentId, rowModel);
      }
    };

    final CellItemListenerPropertyColumn<SkillRatingDO> modified = new CellItemListenerPropertyColumn<SkillRatingDO>(getString("modified"),
        getSortable("lastUpdate", sortable), "lastUpdate", cellItemListener);

    final CellItemListenerPropertyColumn<SkillRatingDO> user = new UserPropertyColumn<SkillRatingDO>(SkillRatingDO.class, getSortable(
        "userId", sortable), "user", cellItemListener).withUserFormatter(userFormatter);

    // TODO: Workaround with get (hardcoded I18N), needs a better solution.
    // Commented lines don't work!
    // final CellItemListenerPropertyColumn<SkillRatingDO> skillTitle = new
    // CellItemListenerPropertyColumn<SkillRatingDO>(SkillRatingDO.class,
    // getSortable("skill.title", sortable), "skill.title", cellItemListener);
    final CellItemListenerPropertyColumn<SkillRatingDO> skillTitle = new CellItemListenerPropertyColumn<SkillRatingDO>(
        getString("plugins.skillmatrix.skill.title"), getSortable("skill.title", sortable), "skill.title", cellItemListener);

    final CellItemListenerPropertyColumn<SkillRatingDO> experience = new CellItemListenerPropertyColumn<SkillRatingDO>(SkillRatingDO.class,
        getSortable("skillRating", sortable), "skillRating", cellItemListener);

    final CellItemListenerPropertyColumn<SkillRatingDO> sinceYear = new CellItemListenerPropertyColumn<SkillRatingDO>(SkillRatingDO.class,
        getSortable("sinceYear", sortable), "sinceYear", cellItemListener);

    final CellItemListenerPropertyColumn<SkillRatingDO> certificates = new CellItemListenerPropertyColumn<SkillRatingDO>(
        SkillRatingDO.class, getSortable("certificates", sortable), "certificates", cellItemListener);

    final CellItemListenerPropertyColumn<SkillRatingDO> trainingCourses = new CellItemListenerPropertyColumn<SkillRatingDO>(
        SkillRatingDO.class, getSortable("trainingCourses", sortable), "trainingCourses", cellItemListener);

    final CellItemListenerPropertyColumn<SkillRatingDO> description = new CellItemListenerPropertyColumn<SkillRatingDO>(
        SkillRatingDO.class, getSortable("description", sortable), "description", cellItemListener);

    final CellItemListenerPropertyColumn<SkillRatingDO> comment = new CellItemListenerPropertyColumn<SkillRatingDO>(SkillRatingDO.class,
        getSortable("comment", sortable), "comment", cellItemListener);

    columns.add(created);
    columns.add(modified);
    columns.add(user);
    columns.add(skillTitle);
    columns.add(experience);
    columns.add(sinceYear);
    columns.add(certificates);
    columns.add(trainingCourses);
    columns.add(description);
    columns.add(comment);

    return columns;
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#init()
   */
  @Override
  protected void init()
  {
    dataTable = createDataTable(createColumns(this, true), "lastUpdate", SortOrder.DESCENDING);
    form.add(dataTable);
    addExcelExport(getString("plugins.skillmatrix.skillrating.menu"), getString("plugins.skillmatrix.skillrating.menu"));
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#createExcelExporter(java.lang.String)
   */
  @Override
  protected DOListExcelExporter createExcelExporter(final String filenameIdentifier)
  {
    return new DOListExcelExporter(filenameIdentifier) {
      /**
       * @see org.projectforge.excel.ExcelExporter#onBeforeSettingColumns(java.util.List)
       */
      @Override
      protected List<ExportColumn> onBeforeSettingColumns(final ContentProvider sheetProvider, final List<ExportColumn> columns)
      {
        final List<ExportColumn> sortedColumns = reorderColumns(columns, "skill", "user",  "sinceYear", "skillRating", "certificates",
            "trainingCourses", "description", "comment");
        //       I18nExportColumn col = new I18nExportColumn("kontoBezeichnung", "fibu.konto.bezeichnung", MyXlsContentProvider.LENGTH_STD);
        //       sortedColumns.add(2, col);
        //       col = new I18nExportColumn("netSum", "fibu.common.netto");
        //       putCurrencyFormat(sheetProvider, col);
        //       sortedColumns.add(7, col);
        //       col = new I18nExportColumn("grossSum", "fibu.common.brutto");
        //       putCurrencyFormat(sheetProvider, col);
        //       sortedColumns.add(8, col);
        return sortedColumns;
      }

      /**
       * @see org.projectforge.excel.ExcelExporter#addMapping(org.projectforge.excel.PropertyMapping, java.lang.Object,
       *      java.lang.reflect.Field)
       */
      @Override
      public void addMapping(final PropertyMapping mapping, final Object entry, final Field field)
      {
        if ("skill".equals(field.getName()) == true) {
          final SkillDO skill = ((SkillRatingDO) entry).getSkill();
          mapping.add(field.getName(), skill != null ? skill.getTitle() : "");
        } else if ("sinceYear".equals(field.getName()) == true) {
          final Integer i = ((SkillRatingDO) entry).getSinceYear();
          String s = null;
          if ( i != null)
            s = i.toString();
          mapping.add(field.getName(), s != null ? s : "");
        } else {
          super.addMapping(mapping, entry, field);
        }
      }
    };
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#getBaseDao()
   */
  @Override
  protected SkillRatingDao getBaseDao()
  {
    return skillRatingDao;
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#newListForm(org.projectforge.web.wicket.AbstractListPage)
   */
  @Override
  protected SkillRatingListForm newListForm(final AbstractListPage< ? , ? , ? > parentPage)
  {
    return new SkillRatingListForm(this);
  }

  /*
   * @see org.projectforge.web.wicket.AbstractListPage#select(java.lang.String, java.lang.Object)
   */
  @Override
  public void select(final String property, final Object selectedValue)
  {
    if ("skillId".equals(property) == true) {
      form.getSearchFilter().setSkillId((Integer) selectedValue);
      refresh();
    } else if ("userId".equals(property) == true) {
      form.getSearchFilter().setUserId((Integer) selectedValue);
      refresh();
    } else
      super.select(property, selectedValue);
  }

  /**
   * 
   * @see org.projectforge.web.fibu.ISelectCallerPage#unselect(java.lang.String)
   */
  @Override
  public void unselect(final String property)
  {
    if ("skillId".equals(property) == true) {
      form.getSearchFilter().setSkillId(null);
      refresh();
    } else if ("userId".equals(property) == true) {
      form.getSearchFilter().setUserId(null);
      refresh();
    }
    super.unselect(property);
  }
}
