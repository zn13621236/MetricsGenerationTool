package domain;

public class TestMetrcisEntry {

    private int    testNumber;
    private String summary;
    private String packageName;
    private String preSteps;
    private String testSteps;
    private String expectResults;
    private String priority;
    private String knownBugs;
    private String note;
    private String automationCaseName;

    public String getPreSteps () {
        return preSteps;
    }

    public void setPreSteps (String preSteps) {
        this.preSteps = preSteps;
    }

    public String getAutomationCaseName () {
        return automationCaseName;
    }

    public void setAutomationCaseName (String automationCaseName) {
        this.automationCaseName = automationCaseName;
    }

    public int getTestNumber () {
        return testNumber;
    }

    public void setTestNumber (int testNumber) {
        this.testNumber = testNumber;
    }

    public String getSummary () {
        return summary;
    }

    public void setSummary (String summary) {
        this.summary = summary;
    }

    public String getPackageName () {
        return packageName;
    }

    public void setPackageName (String packageName) {
        this.packageName = packageName;
    }

    public String getTestSteps () {
        return testSteps;
    }

    public void setTestSteps (String testSteps) {
        this.testSteps = testSteps;
    }

    public String getExpectResults () {
        return expectResults;
    }

    public void setExpectResults (String expectResults) {
        this.expectResults = expectResults;
    }

    public String getPriority () {
        return priority;
    }

    public void setPriority (String priority) {
        this.priority = priority;
    }

    public String getKnownBugs () {
        return knownBugs;
    }

    public void setKnownBugs (String knownBugs) {
        this.knownBugs = knownBugs;
    }

    public String getNote () {
        return note;
    }

    public void setNote (String note) {
        this.note = note;
    }

    @Override
    public String toString () {
        return "TestMetrcisEntry [testNumber=" + testNumber + ", summary=" + summary + ", packageName=" + packageName + ", preSteps=" + preSteps + ", testSteps=" + testSteps + ", expectResults="
                + expectResults + ", priority=" + priority + ", knownBugs=" + knownBugs + ", note=" + note + ", automationCaseName=" + automationCaseName + "]";
    }
}
