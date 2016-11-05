package lightster.aws.lambda.fop;

class LambdaRequest
{
    private String dataUrl;
    private String xsltUrl;
    private String outputUrl;

    public String getDataUrl()
    {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl)
    {
        this.dataUrl = dataUrl;
    }

    public String getXsltUrl()
    {
        return xsltUrl;
    }

    public void setXsltUrl(String xsltUrl)
    {
        this.xsltUrl = xsltUrl;
    }

    public String getOutputUrl()
    {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl)
    {
        this.outputUrl = outputUrl;
    }
}
