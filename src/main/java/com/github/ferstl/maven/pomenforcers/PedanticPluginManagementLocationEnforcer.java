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
package com.github.ferstl.maven.pomenforcers;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;

import com.github.ferstl.maven.pomenforcers.artifact.ArtifactInfo;
import com.github.ferstl.maven.pomenforcers.artifact.ArtifactInfoTransformer;
import com.github.ferstl.maven.pomenforcers.util.CommaSeparatorUtils;
import com.github.ferstl.maven.pomenforcers.util.EnforcerRuleUtils;
import com.github.ferstl.maven.pomenforcers.util.XmlUtils;

/**
 * Enforces that only a well-defined set of POMs may declare plugin management.
 * <pre>
 * ### Example
 *     &lt;rules&gt;
 *       &lt;pluginManagemenLocation implementation=&quot;com.github.ferstl.maven.pomenforcers.PedanticPluginManagementLocationEnforcer&quot;&gt;
 *         &lt;!-- Only these POMs may declare plugin management --&gt;
 *         &lt;pluginManagingPoms&gt;com.example.myproject:parent,com.example.myproject:subparent&lt;/pluginManagingPoms&gt;
 *       &lt;/pluginManagemenLocation&gt;
 *     &lt;/rules&gt;
 * </pre>
 * @id {@link PedanticEnforcerRule#PLUGIN_MANAGEMENT_LOCATION}
 */
public class PedanticPluginManagementLocationEnforcer extends AbstractPedanticEnforcer {

  private final Set<ArtifactInfo> pluginManagingPoms;

  public PedanticPluginManagementLocationEnforcer() {
    this.pluginManagingPoms = new HashSet<>();
  }

  @Override
  protected void doEnforce(EnforcerRuleHelper helper, Document pom) throws EnforcerRuleException {
    MavenProject mavenProject = EnforcerRuleUtils.getMavenProject(helper);
    if (containsPluginManagement(pom) && !isPluginManagementAllowed(mavenProject)) {
      throw new EnforcerRuleException("One does not simply declare plugin management. " +
      		"Only these POMs are allowed to manage plugins: " + this.pluginManagingPoms);
    }
  }

  /**
   * Comma separated list of POMs that may declare <code>&lt;pluginManagement&gt;</code>. Each POM has
   * to be defined in the format <code>groupId:artifactId</code>.
   * @param pluginManagingPoms Comma separated list of POMs that may declare plugin management.
   * @configParam
   * @default n/a
   */
  public void setPluginManagingPoms(String pluginManagingPoms) {
    ArtifactInfoTransformer artifactInfoTransformer = new ArtifactInfoTransformer();
    CommaSeparatorUtils.splitAndAddToCollection(pluginManagingPoms, this.pluginManagingPoms, artifactInfoTransformer);
  }

  private boolean containsPluginManagement(Document pom) {
    return XmlUtils.evaluateXPathAsElement("/project/build/pluginManagement", pom) != null;
  }

  private boolean isPluginManagementAllowed(MavenProject project) {
    ArtifactInfo projectInfo = new ArtifactInfo(project.getGroupId(), project.getArtifactId());
    return this.pluginManagingPoms.contains(projectInfo);
  }

  @Override
  protected void accept(PedanticEnforcerVisitor visitor) {
    visitor.visit(this);
  }
}
