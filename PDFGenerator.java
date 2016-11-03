import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.MimeConstants;

public class PDFGenerator
{
    public static void main(String[] args)
    {
        if (args.length < 3) {
            System.err.println("Usage: PDFGenerator data.xml style.xslt output.pdf");
            System.exit(1);
        }

        try {
            generatePdf(args[0], args[1], args[2]);
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    /**
     * @param String dataPath
     * @param String xsltPath
     * @param String outputPath
     */
    public static void generatePdf(String dataPath, String xsltPath, String outputPath)
        throws Exception
    {
        File dataFile = new File(dataPath);
        File xsltFile = new File(xsltPath);
        File outputFile = new File(outputPath);

        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

        OutputStream outputBuffer = new BufferedOutputStream(
            new FileOutputStream(outputFile)
        );

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputBuffer);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

            Source source = new StreamSource(dataFile);
            Result result = new SAXResult(fop.getDefaultHandler());

            transformer.transform(source, result);
        } finally {
            outputBuffer.close();
        }
    }
}
