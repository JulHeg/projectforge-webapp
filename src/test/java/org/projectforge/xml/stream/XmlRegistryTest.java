/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2012 Kai Reinhard (k.reinhard@micromata.de)
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

package org.projectforge.xml.stream;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.projectforge.common.DateHolder;
import org.projectforge.test.TestBase;
import org.projectforge.xml.stream.converter.ISODateConverter;

public class XmlRegistryTest extends TestBase
{
  @Test
  public void testWrite()
  {
    final XmlObjectWriter writer = new XmlObjectWriter();
    TestObject obj = new TestObject();
    final DateHolder dh = new DateHolder();
    dh.setDate(2010, Calendar.AUGUST, 29, 23, 8, 17, 123);
    obj.date = dh.getDate();
    assertEquals("<test d1=\"0.0\" i1=\"0\" date=\"1283116097123\"/>", writer.writeToXml(obj));
    final XmlRegistry reg = new XmlRegistry();
    reg.registerConverter(Date.class, new ISODateConverter());
    writer.setXmlRegistry(reg);
    assertEquals("<test d1=\"0.0\" i1=\"0\" date=\"2010-08-29 23:08:17.123\"/>", writer.writeToXml(obj));
    obj.date = dh.setMilliSecond(0).getDate();
    assertEquals("<test d1=\"0.0\" i1=\"0\" date=\"2010-08-29 23:08:17\"/>", writer.writeToXml(obj));
    obj.date = dh.setSecond(0).getDate();
    assertEquals("<test d1=\"0.0\" i1=\"0\" date=\"2010-08-29 23:08\"/>", writer.writeToXml(obj));
    obj.date = dh.setMinute(0).getDate();
    assertEquals("<test d1=\"0.0\" i1=\"0\" date=\"2010-08-29 23:00\"/>", writer.writeToXml(obj));
    obj.date = dh.setHourOfDay(0).getDate();
    assertEquals("<test d1=\"0.0\" i1=\"0\" date=\"2010-08-29\"/>", writer.writeToXml(obj));
  }
}
