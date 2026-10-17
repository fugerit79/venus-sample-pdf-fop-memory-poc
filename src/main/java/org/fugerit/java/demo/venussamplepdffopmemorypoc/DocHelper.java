package org.fugerit.java.demo.venussamplepdffopmemorypoc;

import org.fugerit.java.doc.freemarker.process.FreemarkerDocProcessConfig;
import org.fugerit.java.doc.freemarker.process.FreemarkerDocProcessConfigFacade;

/**
 * DocHelper, version : auto generated on 2026-10-17 23:17:21.846
 */
public class DocHelper {

     /*
      * FreemarkerDocProcessConfig is thread-safe and should be initialized once for each config file.
      * 
      * Consider using a @ApplicationScoped or Singleton approach.
      */
     private final FreemarkerDocProcessConfig docProcessConfig = FreemarkerDocProcessConfigFacade
            .loadConfigSafe("cl://venus-sample-pdf-fop-memory-poc/fm-doc-process-config.xml");

     /**
      * Accessor for FreemarkerDocProcessConfig configuration.
      *
      * @return the FreemarkerDocProcessConfig instance associated with this helper.
      */
     public FreemarkerDocProcessConfig getDocProcessConfig() {
         return this.docProcessConfig;
     }

}
