# aws-lambda-fop
AWS Lambda function for generating PDFs from XML/XSLT using FOP

## Running from the CLI

```
javac -cp 'lib/*:.' PDFGenerator.java
java -cp 'lib/*:.' PDFGenerator samples/data.xml samples/style.xslt samples/output.pdf
```
