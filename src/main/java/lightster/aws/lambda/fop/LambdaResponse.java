package lightster.aws.lambda.fop;

class LambdaResponse
{
    private String outputUrl;
    private boolean hasError = false;
    private String errorMessage;

    public String getOutputUrl()
    {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl)
    {
        this.outputUrl = outputUrl;
    }

    public boolean getHasError()
    {
        return this.hasError;
    }

    public void setHasError(boolean hasError)
    {
        this.hasError = hasError;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
