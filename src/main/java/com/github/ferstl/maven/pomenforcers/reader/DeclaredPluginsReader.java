/*
 * Copyright (c) 2012 by The Author(s)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.maven.pomenforcers.reader;

import java.util.List;

import org.apache.maven.model.Plugin;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

public class DeclaredPluginsReader extends AbstractPomSectionReader<List<Plugin>> {

  private static final String PLUGINS_ALIAS = "plugins";
  private static final String PLUGIN_ALIAS = "plugin";

  public DeclaredPluginsReader(Document pom) {
    super(pom);
  }

  @Override
  protected void configureXStream(XStream xstream) {
    xstream.alias(PLUGINS_ALIAS, List.class);
    xstream.alias(PLUGIN_ALIAS, Plugin.class);
    xstream.omitField(Plugin.class, "configuration");
    xstream.omitField(Plugin.class, "extensions");
    xstream.omitField(Plugin.class, "goals");
    xstream.omitField(Plugin.class, "executions");
    xstream.omitField(Plugin.class, "dependencies");
  }

  @Override
  protected List<Plugin> getUndeclaredSection() {
    return Lists.newArrayListWithCapacity(0);
  }
}
