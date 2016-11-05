# aws-lambda-fop
AWS Lambda function for generating PDFs from XML/XSLT using FOP

## Running via Gradle

[Install Gradle](https://gradle.org/gradle-download/).



```
gradle installDist
./build/install/aws-lambda-fop/bin/aws-lambda-fop s3://lightster-test/lambda-fop/data.xml s3://lightster-test/lambda-fop/style.xslt s3://lightster-test/lambda-fop/output.pdf
```
