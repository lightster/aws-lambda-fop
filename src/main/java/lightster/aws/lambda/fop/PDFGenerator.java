package lightster.aws.lambda.fop;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
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

public class PDFGenerator implements RequestHandler<LambdaRequest, LambdaResponse>
{
    public static void main(String[] args)
    {
        if (args.length < 3) {
            System.err.println("Usage: PDFGenerator data.xml style.xslt output.pdf");
            System.exit(1);
        }

        try {
            generatePdfUsingS3(args[0], args[1], args[2]);
            // generatePdf(args[0], args[1], args[2]);
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

        generatePdf(dataFile, xsltFile, outputFile);
    }

    /**
     * @param File dataFile
     * @param File xsltFile
     * @param File outputFile
     */
    public static void generatePdf(File dataFile, File xsltFile, File outputFile)
        throws Exception
    {
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

    public static void generatePdfUsingS3(String dataPath, String xsltPath, String outputPath)
        throws Exception
    {
        AmazonS3URI dataUri = new AmazonS3URI(dataPath);
        AmazonS3URI xsltUri = new AmazonS3URI(xsltPath);
        AmazonS3URI outputUri = new AmazonS3URI(outputPath);

        File dataFile = new File("/tmp/data.xml");
        File xsltFile = new File("/tmp/style.xslt");
        File outputFile = new File("/tmp/output.pdf");

        AmazonS3 s3;
        try {
            s3 = new AmazonS3Client(new ProfileCredentialsProvider().getCredentials());
        } catch (Exception exception) {
            s3 = new AmazonS3Client();
        }

        s3.getObject(
            new GetObjectRequest(dataUri.getBucket(), dataUri.getKey()),
            dataFile
        );
        s3.getObject(
            new GetObjectRequest(xsltUri.getBucket(), xsltUri.getKey()),
            xsltFile
        );

        generatePdf(dataFile, xsltFile, outputFile);

        s3.putObject(outputUri.getBucket(), outputUri.getKey(), outputFile);
    }

    /**
     * @param LambdaRequest request
     * @param Context context
     * @return LambdaResponse
     */
    public LambdaResponse handleRequest(LambdaRequest request, Context context)
    {
        LambdaResponse response = new LambdaResponse();

        try {
            generatePdfUsingS3(
                request.getDataUrl(),
                request.getXsltUrl(),
                request.getOutputUrl()
            );

            response.setOutputUrl(request.getOutputUrl());
        } catch (Exception exception) {
            response.setErrorMessage(exception.getMessage());
            response.setHasError(true);

            exception.printStackTrace(System.err);
        }

        return response;
    }
}
