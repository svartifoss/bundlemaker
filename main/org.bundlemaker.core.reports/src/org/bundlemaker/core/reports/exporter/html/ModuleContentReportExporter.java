package org.bundlemaker.core.reports.exporter.html;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.bundlemaker.analysis.model.IDependency;
import org.bundlemaker.core.analysis.IArtifactModelConfiguration;
import org.bundlemaker.core.projectdescription.ContentType;
import org.bundlemaker.core.resource.IResource;

public class ModuleContentReportExporter extends AbstractSingleModuleHtmlReportExporter {

  /**
   * {@inheritDoc}
   */
  protected String getReportName() {
    return getCurrentModule().getModuleIdentifier().toString() + "_Content";
  }

  /**
   * <p>
   * </p>
   * 
   * @param bufferedWriter
   * @throws IOException
   */
  protected void writeHtmlBody(BufferedWriter bw) throws IOException {

    bw.write("<table>\n");

    bw.write("  <tr>\n");
    bw.write("     <th>Content</th>\n");
    bw.write("  </tr>\n");

    //
    boolean odd = true;

    //
    List<IResource> sources = new LinkedList<IResource>(getCurrentModule().getResources(ContentType.SOURCE));
    Collections.sort(sources, new Comparator<IResource>() {
      @Override
      public int compare(IResource arg0, IResource arg1) {
        return arg0.getPath().compareTo(arg1.getPath());
      }
    });

    //
    for (IResource resource : sources) {

      //

      if (odd) {
        bw.write("  <tr class=\"oddrow\">\n");
        odd = false;
      } else {
        bw.write("  <tr class=\"evenrow\">\n");
        odd = true;
      }

      bw.write("     <td>");
      bw.write(resource.getPath());
      bw.write("     </td>");

      bw.write("  </tr>\n");
    }

    bw.write("</table>\n");
  }

  @Override
  protected IArtifactModelConfiguration getModelConfiguration() {
    return IArtifactModelConfiguration.SOURCE_RESOURCES_CONFIGURATION;
  }
}