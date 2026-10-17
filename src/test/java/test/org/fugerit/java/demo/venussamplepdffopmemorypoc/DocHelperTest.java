// generated from template 'DocHelperTest.ftl' on 2026-10-17T23:17:21.848+02:00
package test.org.fugerit.java.demo.venussamplepdffopmemorypoc;

import org.fugerit.java.demo.venussamplepdffopmemorypoc.DocHelper;
import org.fugerit.java.doc.base.config.DocConfig;
import org.fugerit.java.doc.base.process.DocProcessContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import lombok.extern.slf4j.Slf4j;

/**
 * Proof of Concept (POC) test demonstrating Apache FOP heap memory consumption
 * when rendering a large (~30,000 characters) text block inside a table cell.
 *
 * This test compares:
 * - Fixed layout: using format="preserve-line" (XSL-FO linefeed-treatment="preserve")
 * - Original layout: default Knuth-Plass paragraph line-breaking across all 30,000 characters
 *
 * For further documentation:
 * https://github.com/fugerit-org/fj-doc
 */
@Slf4j
class DocHelperTest {

    @Test
    void testDocProcess() throws Exception {
        this.printInfo( "start", true );
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            DocHelper docHelper = new DocHelper();
            String chainId = "document";
            String handlerId = DocConfig.TYPE_PDF;

            // 1. Output generation with fix (format="preserve-line")
            docHelper.getDocProcessConfig().fullProcess(chainId,
                    DocProcessContext.newContext( "fixLineHandling", "1" ), handlerId, baos);
            int fixedSize = baos.size();
            long peakMemoryFixed = this.printInfo( "after pdf generation (fixed)", false );
            this.printInfo( "after pdf generation (fixed) gc", true );

            baos.reset();

            // 2. Output generation with original default behavior
            docHelper.getDocProcessConfig().fullProcess(chainId,
                    DocProcessContext.newContext( "fixLineHandling", "0" ), handlerId, baos);
            int originalSize = baos.size();
            long peakMemoryOriginal = this.printInfo( "after pdf generation (original)", false );
            this.printInfo( "after pdf generation (original) gc", true );

            log.info("{} output size - fixed: {} bytes, original: {} bytes", handlerId, fixedSize, originalSize);
            Assertions.assertNotEquals(0, fixedSize);
            Assertions.assertNotEquals(0, originalSize);
            log.info( "peakMemoryFixed : {} vs peakMemoryOriginal : {}", this.formatMemory( peakMemoryFixed ), this.formatMemory( peakMemoryOriginal ) );
            Assertions.assertTrue( peakMemoryOriginal > peakMemoryFixed,
                    "Expected original unsegmented Knuth-Plass layout to consume more peak heap memory than fixed layout" );
        }
    }


    private String formatMemory( long mem ) {
        return String.format( "%.2f MB", mem / ( 1024.0 * 1024.0 ) );
    }

    private long lastUsedMemory = 0;

    private long lastTime = System.currentTimeMillis();

    private long printInfo( String step, boolean runGc ) {
        if ( runGc ) {
            System.gc();
        }
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        long used = total - free;
        long max = rt.maxMemory();
        long deltaUsed = ( this.lastUsedMemory == 0 ) ? 0 : ( used - this.lastUsedMemory );
        long now = System.currentTimeMillis();
        long elapsedMs = now - this.lastTime;

        long nonHeapUsed = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
        long peakHeapUsed = 0;
        for ( MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans() ) {
            if ( pool.getType() == MemoryType.HEAP && pool.getPeakUsage() != null ) {
                peakHeapUsed += pool.getPeakUsage().getUsed();
                pool.resetPeakUsage();
            }
        }

        log.info( "[{}] time: {} ms | usedHeap: {} (delta: {}{}) | peakHeap: {} | freeHeap: {} | totalHeap: {} | maxHeap: {} | nonHeap: {}",
                step,
                elapsedMs,
                formatMemory( used ),
                ( deltaUsed >= 0 ? "+" : "" ),
                formatMemory( deltaUsed ),
                formatMemory( peakHeapUsed ),
                formatMemory( free ),
                formatMemory( total ),
                formatMemory( max ),
                formatMemory( nonHeapUsed ) );

        this.lastUsedMemory = used;
        this.lastTime = System.currentTimeMillis();
        return peakHeapUsed;
    }

}
