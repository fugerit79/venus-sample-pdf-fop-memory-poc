// generated from template 'DocHelperTest.ftl' on 2026-10-17T23:17:21.848+02:00
package test.org.fugerit.java.demo.venussamplepdffopmemorypoc;

import org.fugerit.java.demo.venussamplepdffopmemorypoc.DocHelper;
import org.fugerit.java.demo.venussamplepdffopmemorypoc.People;

import org.fugerit.java.doc.base.config.DocConfig;
import org.fugerit.java.doc.base.process.DocProcessContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * This is a basic example of Fugerit Venus Doc usage,
 * running this junit will :
 * - creates data to be used in document model
 * - renders the 'document.ftl' template
 * - print the result in markdown format
 *
 * For further documentation :
 * https://github.com/fugerit-org/fj-doc
 *
 * NOTE: This is a 'Hello World' style example, adapt it to your scenario, especially :
 * - change the doc handler and the output mode (here a ByteArrayOutputStream buffer is used)
 */
@Slf4j
class DocHelperTest {

    @Test
    void testDocProcess() throws Exception {
        this.printInfo( "start", true );
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // creates the doc helper
            DocHelper docHelper = new DocHelper();
            // create custom data for the fremarker template 'document.ftl'
            List<People> listPeople = Arrays.asList(
                    new People("Luthien", "Tinuviel", "Queen"), new People("Thorin", "Oakshield", "King"));

            String chainId = "document";
            // handler id
            String handlerId = DocConfig.TYPE_PDF;
            // output generation fixed
            docHelper.getDocProcessConfig().fullProcess(chainId,
                    DocProcessContext.newContext("listPeople", listPeople).withAtt( "fixLineHandling", "1" ), handlerId, baos);
            long peakMemoryFixed = this.printInfo( "after pdf generation (fixed)", false );
            this.printInfo( "after pdf generation (fixed) gc", true );
            // output generation original
            docHelper.getDocProcessConfig().fullProcess(chainId,
                    DocProcessContext.newContext("listPeople", listPeople).withAtt( "fixLineHandling", "0" ), handlerId, baos);
            long peakMemoryOriginal = this.printInfo( "after pdf generation (original)", false );
            this.printInfo( "after pdf generation (original) gc", true );
            // print the output
            log.info("{} output size : {}", handlerId, baos.size());
            Assertions.assertNotEquals(0, baos.size());
            log.info( "peakMemoryFixed : {} vs peakMemoryOriginal : {}", this.formatMemory( peakMemoryFixed ), this.formatMemory( peakMemoryOriginal ) );
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
